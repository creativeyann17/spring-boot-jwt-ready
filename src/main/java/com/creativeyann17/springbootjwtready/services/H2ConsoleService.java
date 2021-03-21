package com.creativeyann17.springbootjwtready.services;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "h2.console", name = "enabled", havingValue = "true")
public class H2ConsoleService {

  private Server webServer;

  @Value("${h2.console.port:8081}")
  private Integer port;

  @EventListener(ContextRefreshedEvent.class)
  public void start() throws java.sql.SQLException {
    log.info("Starting h2 console on port " + port);
    this.webServer = org.h2.tools.Server.createWebServer("-webPort", port.toString()).start();
  }

  @EventListener(ContextClosedEvent.class)
  public void stop() {
    log.info("Stopping h2 console on port " + port);
    this.webServer.stop();
  }
}
