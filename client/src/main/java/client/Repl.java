package client;
import ui.EscapeSequences;

import java.util.Scanner;

public class Repl {
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GameplayClient gameplayClient;

    private int state = 0;

    public Repl(String serverUrl) {
        preLoginClient = new PreLoginClient(serverUrl);
        postLoginClient = new PostLoginClient(serverUrl, preLoginClient.getAuth());
        gameplayClient = new GameplayClient(serverUrl, postLoginClient.getAuthData());
    }

    public void run() {
        System.out.println(EscapeSequences.SET_TEXT_BOLD + EscapeSequences.SET_BG_COLOR_LIGHT_GREY +
                EscapeSequences.SET_TEXT_COLOR_BLACK + " ♕ Welcome to 240 Online Chess " + EscapeSequences.BLACK_QUEEN);
        System.out.println(EscapeSequences.RESET_BG_COLOR + EscapeSequences.SET_TEXT_COLOR_MAGENTA + EscapeSequences.RESET_TEXT_BOLD_FAINT);
        System.out.print(preLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            while (state == 0 && !result.equals("quit")) {
                printPrompt();
                String line = scanner.nextLine();

                try {
                    result = preLoginClient.eval(line);
                    state = preLoginClient.getState();
                    if (state == 1)
                    {
                        postLoginClient.setAuthData(preLoginClient.getAuth());
                        preLoginClient.setState(0);
                        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + postLoginClient.eval("help"));
                    }
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_MAGENTA + result);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
            while (state == 1 && !result.equals("quit"))
            {
                printPrompt();
                String line = scanner.nextLine();

                try {
                    result = postLoginClient.eval(line);
                    state = postLoginClient.getState();
                    if (state == 0)
                    {
                        postLoginClient.setState(1);
                        System.out.println(EscapeSequences.SET_TEXT_COLOR_MAGENTA + preLoginClient.eval("help"));
                    }
                    if (state == 2)
                    {
                        postLoginClient.setState(1);
                        System.out.println(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + gameplayClient.eval("draw"));
                    }
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + result);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
            while (state == 2 && !result.equals("quit"))
            {
                printPrompt();
                String line = scanner.nextLine();

                try {
                    result = gameplayClient.eval(line);
                    state = gameplayClient.getState();
                    if (state == 0 || state == 1)
                    {
                        gameplayClient.setState(2);
                    }
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.SET_TEXT_COLOR_BLACK + result);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
        }
        System.out.println();
    }

    public String evalState(int state)
    {
        if (state == 0)
        {
            return "SIGNED OUT";
        }
        else if (state == 1)
        {
            return "LOGGED IN";
        }
        else if (state == 2)
        {
            return "IN GAME";
        }
        return "ERROR";
    }



    private void printPrompt() {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN + "\n[" + this.evalState(this.state) + "] " +
                EscapeSequences.SET_TEXT_COLOR_WHITE + ">>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
    }

}