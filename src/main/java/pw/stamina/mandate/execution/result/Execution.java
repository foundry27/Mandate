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

package pw.stamina.mandate.execution.result;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * An Execution represents a possibly asynchronous command execution taking place
 * <p>
 * Methods are provided to check if the execution is
 * complete, to wait for its completion, and to retrieve the result of
 * the execution.  The result can only be retrieved using method
 * {@link #result result} when the execution has completed, blocking if
 * necessary until it is ready. Cancellation is performed by the
 * {@link #kill kill} method.
 *
 * @author Mark Johnson
 */
public interface Execution {
    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     *
     * @return the terminal exit code delivered by the executed command
     */
    ExitCode result();

    /**
     * Waits if necessary for at most the given time for the computation
     * to complete, and then retrieves its result, if available.
     *
     * @param timeout how long, in the provided timeunit, to wait for the execution to finish
     * @param unit the unit of time in which the timeout is presented
     * @return the terminal exit code delivered by the executed command
     * @throws TimeoutException if the execution times out before returning a result
     */
    ExitCode result(long timeout, TimeUnit unit) throws TimeoutException;

    /**
     * Attempts to cancel execution of this task.
     * <p>
     * This attempt will fail if the task has already completed, has already been cancelled,
     * or could not be cancelled for some other reason.
     * <p>
     * If successful, and this task has not started when this method is called,
     * this task should never run.
     *
     * @return {@code false} if the execution could not be cancelled,
     * typically because it has already completed normally;
     * {@code true} otherwise
     */
    boolean kill();

    /**
     * Returns {@code true} if this execution completed.
     * <p>
     * Completion may be due to normal termination, an exception, or
     * cancellation -- in all of these cases, this method will return
     * {@code true}.
     *
     * @return {@code true} if this execution completed
     */
    boolean completed();

    /**
     * Returns a new Execution that is already completed with the specified exit code
     * @param exitCode the exit code that this execution should be defined as having finished with
     * @return a new Execution that is already completed with the specified exit code
     */
    static Execution complete(ExitCode exitCode) {
        return new Complete(exitCode);
    }

    /**
     * An Execution that is already completed with a specified exit code
     */
    class Complete implements Execution {
        private final ExitCode exitCode;

        Complete(ExitCode exitCode) {
            this.exitCode = exitCode;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ExitCode result() {
            return exitCode;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ExitCode result(long timeout, TimeUnit unit) throws TimeoutException {
            return exitCode;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean kill() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean completed() {
            return true;
        }
    }
}
