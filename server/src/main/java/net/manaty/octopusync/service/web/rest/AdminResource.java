package net.manaty.octopusync.service.web.rest;

import net.manaty.octopusync.model.Trigger;
import net.manaty.octopusync.service.EventListener;
import net.manaty.octopusync.service.grpc.OctopuSyncGrpcService;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Set;

@Path("admin")
public class AdminResource {

    private final OctopuSyncGrpcService grpcService;
    private final Set<EventListener> eventListeners;
    private boolean experienceStarted;
    private boolean musicOn;

    public AdminResource(OctopuSyncGrpcService grpcService, Set<EventListener> eventListeners) {
        this.grpcService = grpcService;
        this.eventListeners = eventListeners;
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

    @POST
    @Path("trigger")
    public Response triggerEvent(String message) {
        if (message == null || message.isEmpty()) {
            return Response.status(Status.BAD_REQUEST.getStatusCode(), "Missing message")
                    .build();
        }
        Trigger trigger = Trigger.message(0, System.currentTimeMillis(), message);
        eventListeners.forEach(l -> l.onAdminTrigger(trigger));
        return Response.ok()
                .build();
    }

    @POST
    @Path("music/on")
    public synchronized Response startMusic(String message) {
        if (!musicOn) {
            eventListeners.forEach(EventListener::onMusicOn);
            musicOn = true;
            return Response.ok()
                    .build();
        } else {
            return Response.status(Status.BAD_REQUEST.getStatusCode(), "Music already on")
                    .build();
        }
    }

    @POST
    @Path("music/off")
    public synchronized Response stopMusic(String message) {
        if (musicOn) {
            eventListeners.forEach(EventListener::onMusicOff);
            musicOn = false;
            return Response.ok()
                    .build();
        } else {
            return Response.status(Status.BAD_REQUEST.getStatusCode(), "Music not on yet")
                    .build();
        }
    }
}
