package com.jefersonalmeida.vertx.vertx_websockets;

import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.http.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketHandler implements Handler<ServerWebSocket> {
  private static final Logger LOG = LoggerFactory.getLogger(WebSocketHandler.class);
  public static final String PATH = "/ws/simple/prices";

  @Override
  public void handle(ServerWebSocket ws) {

    if (!PATH.equalsIgnoreCase(ws.path())) {
      LOG.info("Rejected wrong path: {}", ws.path());
      ws.writeFinalTextFrame("Wrong path, Only %s is accepted!".formatted(PATH));
      closeClient(ws);
      return;
    }

    LOG.info("Opening web socket connection: {}, {}", ws.path(), ws.textHandlerID());
    ws.accept();
    ws.frameHandler(frameHandler(ws));
    ws.endHandler(onClose -> LOG.info("closed: {}", ws.textHandlerID()));
    ws.exceptionHandler(err -> LOG.error("Failed: ", err));
    ws.writeTextMessage("Connected!");
  }

  private Handler<WebSocketFrame> frameHandler(ServerWebSocket ws) {
    return received -> {
      final var message = received.textData();

      LOG.debug("Received message: {} from client {}", message, ws.textHandlerID());

      if ("disconnect me".equalsIgnoreCase(message)) {
        LOG.info("Client close requested!");
        closeClient(ws);
      } else {
        ws.writeTextMessage("Not supported => (%s)".formatted(message));
      }
    };
  }

  private void closeClient(ServerWebSocket ws) {
    ws.close((short) 1000, "Normal Closure");
  }
}
