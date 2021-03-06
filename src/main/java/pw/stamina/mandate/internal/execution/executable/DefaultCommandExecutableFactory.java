/*
 * Mandate - A flexible annotation-based command parsing and execution system
 * Copyright (C) 2017 Mark Johnson
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

package pw.stamina.mandate.internal.execution.executable;

import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.executable.CommandExecutable;
import pw.stamina.mandate.execution.executable.CommandExecutableCreationStrategy;

import java.lang.reflect.Method;

/**
 * @author Mark Johnson
 */
public enum DefaultCommandExecutableFactory implements CommandExecutableCreationStrategy {
    INSTANCE;

    public CommandExecutable newExecutable(final Method backingMethod, final Object methodParent, final CommandContext commandContext) {
        return new SimpleExecutable(backingMethod, methodParent, commandContext);
    }

    public static DefaultCommandExecutableFactory getInstance() {
        return INSTANCE;
    }
}
