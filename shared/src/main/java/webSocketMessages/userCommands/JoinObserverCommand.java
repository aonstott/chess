package webSocketMessages.userCommands;

public class JoinObserverCommand extends UserGameCommand{
    public JoinObserverCommand(String authToken) {
        super(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
    }
}
