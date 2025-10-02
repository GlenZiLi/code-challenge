import http from 'k6/http';
import { check, sleep } from 'k6';

// The base URL of the API running in Docker
const API_BASE_URL = 'http://localhost:8199/task-manager/api';

// --- Test Configuration ---
export const options = {
  // Define the stages of the test.
  stages: [
    { duration: '10s', target: 5 },  // Ramp-up to 5 virtual users over 10 seconds
    { duration: '20s', target: 5 },  // Stay at 5 users for 20 seconds
    { duration: '5s', target: 0 },   // Ramp-down to 0 users
  ],
  // Define thresholds. If these are crossed, the test will fail.
  thresholds: {
    //'http_req_failed': ['rate<0.9'], // Fail if more than 90% of requests fail.
	'http_req_failed': ['rate<0.01'],  //Failure rate < 1% (i.e., Success rate ≥ 99%)
    'http_req_duration': ['p(95)<2000'], // 95% of requests must complete below 2s
    'checks': ['rate>0.6'], // More than 60% of checks must pass
	'checks{scenario:post_summary}': ['rate>0.9'], // POST /summaries success rate > 90%
    'checks{scenario:get_summaries}': ['rate>0.9'], // GET /summaries success rate > 90%
  },
};

// --- Test Scenario ---
export default function () {
  // 1. Get all tasks
  http.get(`${API_BASE_URL}/tasks`);
  sleep(1);

  // 2. Test the "equals vs ==" bug
  const res1 = http.get(`${API_BASE_URL}/tasks/9`);
  check(res1, {
    'GET /tasks/9 returns 200 OK': (r) => r.status === 200,
  });
  sleep(1);

  const res2 = http.get(`${API_BASE_URL}/tasks/130`);
   check(res2, {
    'GET /tasks/130 should return 200 OK (currently returns 404 due to bug)': (r) => r.status === 200,
   });
   sleep(1);

  // 3. Test the "silent audit failure" bug
  const res3 = http.get(`${API_BASE_URL}/tasks/11`);
  check(res3, {
    'GET /tasks/11 (long title) returns 200 OK': (r) => r.status === 200,
  });
  sleep(1);

  // 4. Test connection pool exhaustion
  http.get(`${API_BASE_URL}/tasks/1`);
  sleep(1);
  
  
  // 5. Test Summaries API
  // 5.1 Create a new summary (POST)
  const postRes = http.post(`${API_BASE_URL}/summaries`);
  check(postRes, {
    'POST /summaries returns 201': (r) => r.status === 201,
    'POST /summaries contains id': (r) => r.json('id') !== undefined,
  }, { scenario: 'post_summary' });
  sleep(1);

  // 5.2 Get all summaries (GET)
  const getRes = http.get(`${API_BASE_URL}/summaries`);
  check(getRes, {
    'GET /summaries returns 200': (r) => r.status === 200,
    'GET /summaries response is array': (r) => Array.isArray(r.json()),
  }, { scenario: 'get_summaries' });
  sleep(1);
}