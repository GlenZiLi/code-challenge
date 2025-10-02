package ie.dpd.resource;

import ie.dpd.model.TaskSummary;
import ie.dpd.repository.SummaryRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * @Author ZI LI
 * @Date 2025/10/2 7:02
 * @comment base on summary
 */
@RequestScoped
@Path("/summaries")
public class SummaryResource {


    @Inject
    private SummaryRepository summaryRepository;

    // Create a new task summary
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSummary() {
        try {
            TaskSummary summary = summaryRepository.generateSummary();
            return Response.status(Response.Status.CREATED).entity(summary).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error generating summary").build();
        }
    }

    // Get all historical summaries (optional helper endpoint, not part of core requirements)
    // read-only, no new data is created, used to view existing snapshots
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSummaries() {
        try {
            List<TaskSummary> summaries = summaryRepository.findAllSummaries();
            return Response.ok(summaries).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError().entity("Error fetching summaries").build();
        }
    }
}
