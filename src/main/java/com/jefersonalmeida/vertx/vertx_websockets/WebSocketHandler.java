package com.jefersonalmeida.vertx.vertx_websockets;

import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketHandler implements Handler<ServerWebSocket> {
  private static final Logger LOG = LoggerFactory.getLogger(WebSocketHandler.class);

  @Override
  public void handle(ServerWebSocket ws) {
    LOG.info("Opening web socket connection: {}, {}", ws.path(), ws.textHandlerID());
    ws.accept();
    ws.endHandler(onClose -> LOG.info("closed: {}", ws.textHandlerID()));
    ws.exceptionHandler(err -> LOG.error("Failed: ", err));
    ws.writeTextMessage("Connected!");

  }
}
