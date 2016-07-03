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

package pw.stamina.mandate.api;

import pw.stamina.mandate.api.execution.argument.ArgumentHandler;
import pw.stamina.mandate.api.execution.result.CommandResult;
import pw.stamina.mandate.api.execution.result.ExecutableResultHandler;
import pw.stamina.mandate.internal.component.SyntaxComponent;

import java.util.Collection;
import java.util.Optional;

/**
 * @author Foundry
 */
public interface CommandManager {
    boolean register(Object container);

    Optional<CommandResult> execute(String input);

    <T> Optional<ArgumentHandler<T>> findArgumentHandler(Class<T> type);

    <T> Optional<ExecutableResultHandler<T>> findOutputParser(Class<T> type);

    Collection<SyntaxComponent> getCommands();
}