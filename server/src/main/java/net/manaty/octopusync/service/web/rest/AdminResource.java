package net.manaty.octopusync.service.web.rest;

import net.manaty.octopusync.service.grpc.OctopuSyncGrpcService;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("admin")
public class AdminResource {

    private final OctopuSyncGrpcService grpcService;
    private boolean experienceStarted;

    public AdminResource(OctopuSyncGrpcService grpcService) {
        this.grpcService = grpcService;
    }

    @POST
    @Path("experience/start")
    public synchronized Response startExperience() {
        if (!experienceStarted) {
            grpcService.onExperienceStarted();
            experienceStarted = true;
            return Response.ok()
                    .build();
        } else {
            return Response.status(Status.BAD_REQUEST.getStatusCode(), "Already started")
                    .build();
        }
    }

    @POST
    @Path("experience/stop")
    public synchronized Response stopExperience() {
        if (experienceStarted) {
            grpcService.onExperienceStopped();
            experienceStarted = false;
            return Response.ok()
                    .build();
        } else {
            return Response.status(Status.BAD_REQUEST.getStatusCode(), "Not started yet")
                    .build();
        }
    }
}
