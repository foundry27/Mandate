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

package pw.stamina.mandate.internal.execution.executable.transformer;

/**
 * @author Foundry
 */
public class InvokerProxyGenerationException extends RuntimeException {

    private static final long serialVersionUID = -9003638433958538032L;

    InvokerProxyGenerationException() {}

    InvokerProxyGenerationException(String message) {
        super(message);
    }

    InvokerProxyGenerationException(Throwable cause) {
        super(cause);
    }

    InvokerProxyGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    InvokerProxyGenerationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}