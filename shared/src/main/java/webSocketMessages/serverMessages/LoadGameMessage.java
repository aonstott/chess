package webSocketMessages.serverMessages;

public class LoadGameMessage extends ServerMessage{
    public LoadGameMessage(ServerMessageType type) {
        super(type);
        this.serverMessageType = ServerMessageType.LOAD_GAME;
    }
}
