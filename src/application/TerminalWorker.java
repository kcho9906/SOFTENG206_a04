package application;

import javafx.concurrent.Task;

/**
 * Worker which runs bash commands in the background thread.
 */
public class TerminalWorker extends Task<String> {

    private String _command;
    private static MethodHelper methodHelper = Main.getMethodHelper();

    public TerminalWorker(String command) {

        this._command = command;
    }

    @Override
    protected String call() throws Exception {

        String output = methodHelper.command(_command);
        output = " " + output;

        if (isCancelled()) {
            return null;
        }
        return output;
    }

}
