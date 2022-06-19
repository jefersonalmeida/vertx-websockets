package com.jefersonalmeida.vertx.websockets;

import io.vertx.core.Vertx;
import io.vertx.core.http.WebSocketConnectOptions;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {
  private static final Logger LOG = LoggerFactory.getLogger(TestMainVerticle.class);
  public static final int EXPECTED_MESSAGES = 5;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext context) {
    vertx.deployVerticle(new MainVerticle(), context.succeeding(id -> context.completeNow()));
  }

  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  @Test
  void can_connect_to_web_socket_server(Vertx vertx, VertxTestContext context) throws Throwable {
    final var client = vertx.createHttpClient();

    client.webSocket(8900, "localhost", WebSocketHandler.PATH)
      .onFailure(context::failNow)
      .onComplete(context.succeeding(ws -> {
        ws.handler(data -> {

          final var receiveData = data.toString();
          LOG.debug("Received message {}", receiveData);

          assertEquals("Connected!", receiveData);

          client.close();
          context.completeNow();
        });
      }));
  }

  @Timeout(value = 10, timeUnit = TimeUnit.SECONDS)
  @Test
  void can_receive_multiple_messages(Vertx vertx, VertxTestContext context) throws Throwable {
    final var client = vertx.createHttpClient();

    final var counter = new AtomicInteger(0);

    client.webSocket(
        new WebSocketConnectOptions()
          .setHost("localhost")
          .setPort(8900)
          .setURI(WebSocketHandler.PATH)
      )
      .onFailure(context::failNow)
      .onComplete(context.succeeding(ws -> {
        ws.handler(data -> {

          final var receiveData = data.toString();
          LOG.debug("Received message {}", receiveData);

          final var currentValue = counter.getAndIncrement();
          if (currentValue >= EXPECTED_MESSAGES) {
            client.close();
            context.completeNow();
          } else {
            LOG.debug("not enough messages yet... ({}/{})", currentValue, EXPECTED_MESSAGES);
          }
        });
      }));
  }
}
