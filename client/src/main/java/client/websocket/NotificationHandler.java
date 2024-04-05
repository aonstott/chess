
package client.websocket;

import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.UserGameCommand;

public interface NotificationHandler {
    void notify(ServerMessage notification);
}

