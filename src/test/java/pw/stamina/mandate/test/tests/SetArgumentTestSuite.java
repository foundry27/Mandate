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

package pw.stamina.mandate.test.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import pw.stamina.mandate.Mandate;
import pw.stamina.mandate.annotations.Executes;
import pw.stamina.mandate.annotations.Implicit;
import pw.stamina.mandate.annotations.Syntax;
import pw.stamina.mandate.execution.CommandContext;
import pw.stamina.mandate.execution.result.Execution;
import pw.stamina.mandate.execution.result.ExitCode;
import pw.stamina.mandate.io.IODescriptor;

import java.util.*;

/**
 * @author Foundry
 */
@Syntax(tree = "run")
public class SetArgumentTestSuite {

    private final Queue<Object> commandErrors = new ArrayDeque<>();

    private final Queue<Object> commandOutput = new ArrayDeque<>();

    private final CommandContext commandContext = Mandate.newContextBuilder()
            .usingIOEnvironment(Mandate.newIOBuilder()
                    .usingOutputStream(() -> commandOutput::add)
                    .usingErrorStream(() -> commandErrors::add)
                    .build())
            .build();

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void failed(final Throwable e, final Description description) {
            commandErrors.forEach(System.out::println);
        }
    };

    @Before
    public void setup() {
        commandContext.register(this);
    }

    @Test
    public void testPassingToStringSet() {
        final Execution result = commandContext.execute("run strings [foo, foo, bar, bar]");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertTrue(commandOutput.peek() instanceof Set);

        Assert.assertEquals(new HashSet<>(Arrays.asList("foo", "bar")), commandOutput.poll());
    }

    @Test
    public void testPassingZeroLengthSetAsArgument() {
        final Execution result = commandContext.execute("run strings []");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertTrue(commandOutput.peek() instanceof Set);

        Assert.assertEquals(Collections.emptySet(), commandOutput.poll());
    }

    @Test
    public void testPassingTo2DStringSet() {
        final Execution result = commandContext.execute("run 2dstrings [[foo, bar, bar], [baz, baz, quz]]");

        Assert.assertTrue(result.result() == ExitCode.SUCCESS);

        Assert.assertTrue(commandOutput.peek() instanceof Set);

        Assert.assertEquals(new HashSet<>(Arrays.asList(new HashSet<>(Arrays.asList("foo", "bar")), new HashSet<>(Arrays.asList("baz", "quz")))), commandOutput.poll());
    }

    @Executes(tree = "strings")
    public ExitCode runStrings(@Implicit final IODescriptor io, final Set<String> strings) {
        io.out().write(strings);
        return ExitCode.SUCCESS;
    }

    @Executes(tree = "2dstrings")
    public ExitCode run2DStrings(@Implicit final IODescriptor io, final Set<Set<String>> strings) {
        io.out().write(strings);
        return ExitCode.SUCCESS;
    }
}