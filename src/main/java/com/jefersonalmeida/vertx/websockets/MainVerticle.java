package com.jefersonalmeida.vertx.websockets;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.createHttpServer()
      .webSocketHandler(new WebSocketHandler(vertx))
      .listen(8900, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        LOG.info("HTTP server started on port 8900");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}
