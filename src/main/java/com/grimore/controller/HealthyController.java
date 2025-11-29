package com.grimore.controller;

import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.micrometer.metrics.actuate.endpoint.MetricsEndpoint;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


@RestController
@RequestMapping("/healthy")
public class HealthyController {

    private final HealthEndpoint healthEndpoint;
    private final MetricsEndpoint metricsEndpoint;

    HealthyController(HealthEndpoint healthyEndpoint, MetricsEndpoint metricsEndpoint) {
        this.healthEndpoint = healthyEndpoint;
        this.metricsEndpoint = metricsEndpoint;
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> healthy(ZoneId zoneId) {
        String html = """
                <!DOCTYPE html>
                <html>
                    <head>
                    <title>Health Check</title>
                           <style>
                                body { font-family: Arial; padding: 50px; background: #f0f0f0; }
                                .container { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                                .status { color: #28a745; font-size: 24px; font-weight: bold; }
                                .info { margin-top: 20px; color: #666; }
                            </style>
                    </head>
                <body>
                     <div class="container">
                         <div class="status">✓ Service is Healthy</div>
                         <div class="info">
                             <p><strong>Service:</strong> Grimore API</p>
                             <p><strong>Status:</strong> %s</p>
                             <p><strong>Start time: </strong> %s</p>
                             <p><strong>Uptime: </strong> %s</p>
                         </div>
                     </div>
                    </body>
                </html>
                """;

        MetricsEndpoint.MetricDescriptor uptimeMetric = metricsEndpoint.metric("process.uptime", null);
        MetricsEndpoint.MetricDescriptor startTimeMetric = metricsEndpoint.metric("process.start.time", null);

        long startTimeInLong = startTimeMetric.getMeasurements().getFirst().getValue().longValue();        LocalDateTime startTime = Instant.ofEpochSecond(startTimeInLong).atZone(ZoneId.of("UTC")).toLocalDateTime();

        Double uptimeInSeconds = uptimeMetric.getMeasurements().getFirst().getValue();
        Duration uptime = Duration.ofSeconds(uptimeInSeconds.longValue());

        String appStatus = healthEndpoint.health().getStatus().toString();
        String readableUptime = String.format("%d dias, %d horas, %d minutos e %d segundos",
                uptime.toDays(),
                uptime.toHoursPart(),
                uptime.toMinutesPart(),
                uptime.toSecondsPart()
        );


        return ResponseEntity.ok(html.formatted(appStatus, startTime, readableUptime));
    }
}
