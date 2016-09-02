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

package pw.stamina.mandate.internal.syntax.component;

/**
 * @author Foundry
 */
public class MissingSyntaxException extends RuntimeException {

    private static final long serialVersionUID = 1054337915116756139L;

    MissingSyntaxException() {}

    MissingSyntaxException(final String message) {
        super(message);
    }

    MissingSyntaxException(final Throwable cause) {
        super(cause);
    }

    MissingSyntaxException(final String message, final Throwable cause) {
        super(message, cause);
    }

    MissingSyntaxException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}