
package client.websocket;

import webSocketMessages.serverMessages.ServerMessage;

public interface ServerMessageHandler {
    void handle(String message);
}

