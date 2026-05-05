package fr.dssi.phishingawareness.shared.health;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class HealthServiceTest {

    @Test
    void returnsApplicationHealthStatus() {
        Clock clock = Clock.fixed(Instant.parse("2026-05-05T10:15:30Z"), ZoneOffset.UTC);
        HealthService service = new HealthService(clock);

        HealthStatusDto status = service.currentStatus();

        assertThat(status.status()).isEqualTo("UP");
        assertThat(status.application()).isEqualTo("dssi-phishing-awareness");
        assertThat(status.checkedAt()).isEqualTo(Instant.parse("2026-05-05T10:15:30Z"));
    }
}
