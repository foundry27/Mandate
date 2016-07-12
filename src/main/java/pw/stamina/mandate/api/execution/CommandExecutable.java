/*
 * Mandate - A flexible annotation-based command parsing and execution system
 * Copyright (C) 2016 Foundry
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pw.stamina.mandate.api.execution;

import pw.stamina.mandate.api.execution.argument.CommandArgument;
import pw.stamina.mandate.api.execution.result.Execution;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.parsor.exceptions.ParseException;

import java.util.Deque;
import java.util.List;

/**
 * An command that can be executed with a stack of arguments and an {@link IODescriptor IODescriptor}, providing an {@link pw.stamina.mandate.api.execution.result.ExitCode ExitCode} as a result of that execution
 *
 * @author Foundry
 */
public interface CommandExecutable {

    /**
     * Invokes the command that this executable is the terminal operation for, returning a possibly
     * asynchronous {@link Execution execution} representing a running invocation of this executable
     *
     * @param arguments the un-parsed user input tokens that should be parsed as arguments to this executable
     * @param io the descriptor for the IO streams available to the running command
     * @return a possibly asynchronous execution representing a running invocation of this executable
     * @throws ParseException
     */
    Execution execute(Deque<CommandArgument> arguments, IODescriptor io) throws ParseException;

    /**
     * @return a list of the parameters that this executable is defined as having
     */
    List<CommandParameter> getParameters();

    /**
     * @return a friendly description of this executable
     */
    String getDescription();

    /**
     * Returns the minimum number of arguments that can be provided to this executable for an execution to be valid.
     * <p>
     * This should be equal to the number of mandatory parameters for this executable, discounting any optional parameters or flags.
     *
     * @return the minimum number of arguments that can be provided to this executable for an execution to be valid
     */
    int minimumArguments();

    /**
     * Returns the maximum number of arguments that can be provided to this executable for an execution to be valid.
     * <p>
     * This should be equal to the total number of parameters for this executable, discounting any flags that are incompatible with any previously found flag
     *
     * @return the maximum number of arguments that can be provided to this executable for an execution to be valid
     */
    int maximumArguments();
}
