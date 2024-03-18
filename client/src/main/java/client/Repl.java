package client;
import ui.EscapeSequences;

import java.util.Scanner;

public class Repl {
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private int state = 0;

    public Repl(String serverUrl) {
        preLoginClient = new PreLoginClient(serverUrl);
        postLoginClient = new PostLoginClient(serverUrl, preLoginClient.getAuth());
    }

    public void run() {
        System.out.println(" ♕ Welcome to 240 Online Chess" + EscapeSequences.BLACK_QUEEN);
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
                    }
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + result);
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
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + result);
                } catch (Throwable e) {
                    var msg = e.toString();
                    System.out.print(msg);
                }
            }
        }
        System.out.println();
    }



    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.RESET_TEXT_COLOR + ">>> " + EscapeSequences.SET_TEXT_COLOR_GREEN);
    }

}
