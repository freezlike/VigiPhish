package fr.dssi.phishingawareness.tracking.controller;

import fr.dssi.phishingawareness.tracking.dto.TrackingDtos.PublicTrackingEventRequest;
import fr.dssi.phishingawareness.tracking.dto.TrackingDtos.PublicTrackingEventResponse;
import fr.dssi.phishingawareness.tracking.service.TrackingEventService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/tracking")
public class PublicTrackingController {

    private final TrackingEventService trackingEventService;

    public PublicTrackingController(TrackingEventService trackingEventService) {
        this.trackingEventService = trackingEventService;
    }

    @PostMapping("/events")
    public PublicTrackingEventResponse record(@Valid @RequestBody PublicTrackingEventRequest request) {
        return trackingEventService.recordPublicEvent(request);
    }
}
