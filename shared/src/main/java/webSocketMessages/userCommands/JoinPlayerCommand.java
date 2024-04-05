package webSocketMessages.userCommands;

public class JoinPlayerCommand extends UserGameCommand{
    public JoinPlayerCommand(String authToken) {
        super(authToken);
        this.commandType = CommandType.JOIN_PLAYER;
    }
}
