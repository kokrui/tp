package seedu.address.logic.commands;

import seedu.address.model.Model;

/**
 * Does nothing.
 */
public class DoNothingCommand extends Command {

    public static final String COMMAND_WORD = "donothing";
    public static final String MESSAGE_SUCCESS = "Nothing has been done!";

    @Override
    public CommandResult execute(Model model) {
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
