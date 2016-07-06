package tests;

import org.junit.*;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.execution.result.ResultCode;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.AnnotatedCommandManager;
import pw.stamina.mandate.internal.annotations.Executes;
import pw.stamina.mandate.internal.annotations.Syntax;
import pw.stamina.mandate.internal.io.StandardInputStream;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author Foundry
 */
@Syntax(syntax = {"execute", "exec", "do"})
public class ClassSyntaxInheritanceTest {
    private Queue<Object> commandErrors = new ArrayDeque<>();

    private Queue<Object> commandOutput = new ArrayDeque<>();

    private CommandManager commandManager = new AnnotatedCommandManager(StandardInputStream.get(), commandOutput::add, commandErrors::add);

    @Rule
    public TestWatcher watcher = new TestWatcher() {
        @Override
        protected void failed(Throwable e, Description description) {
            commandErrors.forEach(System.out::println);
        }
    };

    @Before
    public void setup() {
        commandManager.register(this);
    }

    @Test
    public void testSyntaxInheritance() {
        ResultCode result = commandManager.execute("execute sum 500 250");

        Assert.assertTrue(result == ResultCode.COMPLETED);

        Assert.assertEquals(750, commandOutput.poll());
    }

    @Test
    public void testAliasUsage() {
        ResultCode result = commandManager.execute("do add 1000 500");

        Assert.assertTrue(result == ResultCode.COMPLETED);

        Assert.assertEquals(1500, commandOutput.poll());
    }

    @Executes(tree = "sum|add")
    public ResultCode sum(IODescriptor io, int augend, int addend) {
        io.out().write(augend + addend);
        return ResultCode.COMPLETED;
    }
}