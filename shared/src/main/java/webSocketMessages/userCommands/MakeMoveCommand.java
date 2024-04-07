package webSocketMessages.userCommands;

public class MakeMoveCommand extends UserGameCommand{
    public MakeMoveCommand(String authToken, int gameID) {
        super(authToken, gameID);
        this.commandType = CommandType.MAKE_MOVE;
    }

}
