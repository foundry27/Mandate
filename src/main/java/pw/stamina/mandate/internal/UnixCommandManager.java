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

package pw.stamina.mandate.internal;

import pw.stamina.mandate.api.CommandManager;
import pw.stamina.mandate.api.annotations.Executes;
import pw.stamina.mandate.api.component.SyntaxComponent;
import pw.stamina.mandate.api.execution.CommandExecutable;
import pw.stamina.mandate.api.execution.argument.ArgumentHandler;
import pw.stamina.mandate.api.execution.argument.CommandArgument;
import pw.stamina.mandate.api.execution.result.Execution;
import pw.stamina.mandate.api.execution.result.ExitCode;
import pw.stamina.mandate.api.io.CommandInput;
import pw.stamina.mandate.api.io.CommandOutput;
import pw.stamina.mandate.api.io.IODescriptor;
import pw.stamina.mandate.internal.component.SyntaxComponentFactory;
import pw.stamina.mandate.internal.execution.argument.handlers.BooleanArgumentHandler;
import pw.stamina.mandate.internal.execution.argument.handlers.EnumArgumentHandler;
import pw.stamina.mandate.internal.execution.argument.handlers.NumberArgumentHandler;
import pw.stamina.mandate.internal.execution.argument.handlers.StringArgumentHandler;
import pw.stamina.mandate.internal.io.StandardErrorStream;
import pw.stamina.mandate.internal.io.StandardInputStream;
import pw.stamina.mandate.internal.io.StandardOutputStream;
import pw.stamina.mandate.internal.parsing.InputToArgumentParser;
import pw.stamina.mandate.internal.utils.Primitives;
import pw.stamina.parsor.exceptions.ParseException;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Foundry
 */
public class UnixCommandManager implements CommandManager {
    private final Map<String, SyntaxComponent> registeredCommands = new LinkedHashMap<>();

    private final Set<ArgumentHandler> argumentHandlers = new HashSet<>();

    private final CommandInput stdin;

    private final CommandOutput stdout;

    private final CommandOutput stderr;

    public UnixCommandManager() {
        this(StandardInputStream.get(), StandardOutputStream.get(), StandardErrorStream.get());
    }

    public UnixCommandManager(CommandInput stdin, CommandOutput stdout, CommandOutput stderr) {
        this.stdin = stdin;
        this.stderr = stderr;
        this.stdout = stdout;

        Arrays.asList(
                new StringArgumentHandler(),
                new NumberArgumentHandler(),
                new BooleanArgumentHandler(),
                new EnumArgumentHandler()
        ).forEach(argumentHandlers::add);
    }

    @Override
    public boolean register(Object container) {
        boolean registered = false;
        for (Method method : container.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Executes.class)) {
                registered |= register0(method, container);
            }
        }
        return registered;
    }

    @Override
    public Execution execute(String input) {
        if (input == null) {
            stderr.write("Invalid input: input cannot be empty");
            return Execution.complete(ExitCode.INVALID);
        } else if (input.length() == 0) {
            stderr.write("Invalid input: input cannot be empty");
            return Execution.complete(ExitCode.INVALID);
        }

        List<CommandArgument> consumedArgs = new ArrayList<>();

        Deque<CommandArgument> arguments; SyntaxComponent component; CommandArgument currentArgument; int depth = 0;
        if ((component = registeredCommands.get((currentArgument = (arguments = InputToArgumentParser.getInstance().parse(input)).getFirst()).getRaw())) != null) {
            while ((currentArgument = arguments.poll()) != null) {
                consumedArgs.add(currentArgument);
                if (component.findExecutables().isPresent()) {
                    int lowestConsumed = Integer.MAX_VALUE; ParseException lastException = null;
                    for (CommandExecutable executable : component.findExecutables().get()) {
                        if (arguments.size() >= executable.minimumArguments() && arguments.size() <= executable.maximumArguments()) {
                            try {
                                return executable.execute(arguments, IODescriptor.from(stdin, stdout, stderr));
                            } catch (ParseException e) {
                                lastException = e;
                            }
                        } else {
                            lowestConsumed = lowestConsumed > executable.minimumArguments() ? executable.minimumArguments() : lowestConsumed;
                        }
                    }
                    if (lastException != null) {
                        stderr.write(lastException.getLocalizedMessage());
                        return Execution.complete(ExitCode.INVALID);
                    } else if (lowestConsumed != Integer.MAX_VALUE) {
                        depth += lowestConsumed;
                    }
                }
                if (!arguments.isEmpty() && component.getChild(arguments.getFirst().getRaw()) != null) {
                    depth++;
                    component = component.getChild(arguments.getFirst().getRaw());
                } else {
                    consumedArgs.addAll(arguments);
                    if (++depth <= consumedArgs.size()) {
                        stderr.write(String.format("Invalid argument(s) '%s' passed to command '%s'", consumedArgs.subList(depth, consumedArgs.size()), consumedArgs.subList(0, depth)));
                    } else {
                        stderr.write(String.format("Missing %d argument(s) for command '%s'", depth - consumedArgs.size(), consumedArgs));
                    }
                    return Execution.complete(ExitCode.INVALID);
                }
            }
            return Execution.complete(ExitCode.INVALID);
        } else {
            stderr.write(String.format("'%s' is not a valid command", currentArgument));
            return Execution.complete(ExitCode.INVALID);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<ArgumentHandler<T>> findArgumentHandler(Class<T> type) {
        if (type.isPrimitive()) {
            type = Primitives.wrap(type);
        }
        for (ArgumentHandler argumentHandler : argumentHandlers) {
            for (Class handledType : argumentHandler.getHandledTypes()) {
                if (handledType.isAssignableFrom(type)) {
                    return Optional.of(argumentHandler);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection<SyntaxComponent> getCommands() {
        return Collections.unmodifiableCollection(registeredCommands.values());
    }

    private boolean register0(Method backingMethod, Object container) {
        String syntax; SyntaxComponent old;
        for (SyntaxComponent component : SyntaxComponentFactory.getComponents(backingMethod, container, this)) {
            if ((old = registeredCommands.get((syntax = component.getSyntax()))) == null) {
                registeredCommands.put(syntax, component);
            } else {
                mergeSyntaxComponent(component, old);
            }
        }
        return true;
    }

    private static void mergeSyntaxComponent(SyntaxComponent newComponent, SyntaxComponent oldComponent) {
        SyntaxComponent lookup;
        newComponent.findExecutables().ifPresent(set -> set.forEach(oldComponent::addExecutable));
        if (newComponent.findChildren().isPresent()) {
            for (SyntaxComponent component : newComponent.findChildren().get()) {
                if ((lookup = oldComponent.getChild(component.getSyntax())) != null) {
                    mergeSyntaxComponent(component, lookup);
                } else {
                    oldComponent.addChild(component);
                }
            }
        }
    }
}