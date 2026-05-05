package fr.dssi.phishingawareness.landingpages.controller;

import fr.dssi.phishingawareness.landingpages.dto.LandingPageDtos.PublicAwarenessPageResponse;
import fr.dssi.phishingawareness.landingpages.service.LandingPageService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/awareness")
public class PublicAwarenessController {

    private final LandingPageService service;

    public PublicAwarenessController(LandingPageService service) {
        this.service = service;
    }

    @GetMapping("/{token}")
    public PublicAwarenessPageResponse get(@PathVariable String token) {
        return service.getPublicAwarenessPage(token);
    }
}
