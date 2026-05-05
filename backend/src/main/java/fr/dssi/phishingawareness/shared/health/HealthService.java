package fr.dssi.phishingawareness.shared.health;

import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    private final Clock clock;

    public HealthService() {
        this(Clock.systemUTC());
    }

    HealthService(Clock clock) {
        this.clock = clock;
    }

    public HealthStatusDto currentStatus() {
        return new HealthStatusDto("UP", "dssi-phishing-awareness", Instant.now(clock));
    }
}
