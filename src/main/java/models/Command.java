package models;

import java.util.HashMap;

public abstract class Command {

    protected HashMap<String, String> args;

    public Command(HashMap<String, String> args) {
        this.args = args;
    }

    /**
     * Execute the command
     * @return The output (if any) of the command
     */
    public abstract String execute();
}
