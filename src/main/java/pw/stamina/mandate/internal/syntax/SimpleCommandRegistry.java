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

package pw.stamina.mandate.internal.syntax;

import pw.stamina.mandate.execution.ExecutionContext;
import pw.stamina.mandate.execution.executable.CommandExecutable;
import pw.stamina.mandate.internal.syntax.component.ImmutableSyntaxTreeDecorator;
import pw.stamina.mandate.parsing.argument.CommandArgument;
import pw.stamina.mandate.parsing.InputParsingException;
import pw.stamina.mandate.syntax.CommandRegistry;
import pw.stamina.mandate.syntax.ExecutableLookup;
import pw.stamina.mandate.syntax.SyntaxTree;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Mark Johnson
 */
public class SimpleCommandRegistry implements CommandRegistry {

    private final Map<String, SyntaxTree> registeredCommands = new HashMap<>();

    @Override
    public Set<SyntaxTree> getCommands() {
        return Collections.unmodifiableSet(registeredCommands.values().stream().map(ImmutableSyntaxTreeDecorator::new).collect(Collectors.toSet()));
    }

    @Override
    public void addCommand(final SyntaxTree component) {
        final SyntaxTree old;
        if ((old = registeredCommands.get(component.getSyntax())) == null) {
            registeredCommands.put(component.getSyntax(), component);
        } else {
            mergeSyntaxComponent(component, old);
        }
    }

    @Override
    public ExecutableLookup findExecutable(final Deque<CommandArgument> arguments, final ExecutionContext executionContext) {
        int depth = 0;
        final List<CommandArgument> consumedArgs = new ArrayList<>();
        SyntaxTree currentComponent;
        CommandArgument currentArgument;
        if ((currentComponent = registeredCommands.get((currentArgument = arguments.getFirst()).getRaw())) != null) {

            while ((currentArgument = arguments.poll()) != null) {
                consumedArgs.add(currentArgument);
                if (currentComponent.findExecutables().isPresent()) {

                    int lowestConsumed = Integer.MAX_VALUE;
                    for (final CommandExecutable executable : currentComponent.findExecutables().get()) {
                        if (arguments.size() >= executable.minimumArguments() && arguments.size() <= executable.maximumArguments()) {
                            return new SimpleExecutableLookup(executable);
                        } else {
                            lowestConsumed = lowestConsumed > executable.minimumArguments() ? executable.minimumArguments() : lowestConsumed;
                        }
                    }
                    if (lowestConsumed != Integer.MAX_VALUE) {
                        depth += lowestConsumed;
                    }
                }
                final Optional<SyntaxTree> tokenLookup;
                if (!arguments.isEmpty() && (tokenLookup = currentComponent.findChild(arguments.getFirst().getRaw())).isPresent()) {
                    depth++;
                    currentComponent = tokenLookup.get();
                } else {
                    consumedArgs.addAll(arguments);
                    if (++depth <= consumedArgs.size()) {
                        return new SimpleExecutableLookup(null, new InputParsingException(String.format("Invalid argument(s) '%s' passed to command '%s'", consumedArgs.subList(depth, consumedArgs.size()), consumedArgs.subList(0, depth))));
                    } else {
                        return new SimpleExecutableLookup(null, new InputParsingException(String.format("Missing %d argument(s) for command '%s'", depth - consumedArgs.size(), consumedArgs)));
                    }
                }
            }
            return new SimpleExecutableLookup(null, new IllegalStateException("No possible arguments available to parse"));
        } else {
            return new SimpleExecutableLookup(null, new InputParsingException(String.format("'%s' is not a valid command", currentArgument)));
        }
    }

    @Override
    public boolean commandPresent(final String command) {
        return registeredCommands.containsKey(command);
    }

    private static void mergeSyntaxComponent(final SyntaxTree newComponent, final SyntaxTree oldComponent) {
        Optional<SyntaxTree> lookup;
        newComponent.findExecutables().ifPresent(set -> set.forEach(oldComponent::addExecutable));
        if (newComponent.findChildren().isPresent()) {
            for (final SyntaxTree component : newComponent.findChildren().get()) {
                if ((lookup = oldComponent.findChild(component.getSyntax())).isPresent()) {
                    mergeSyntaxComponent(component, lookup.get());
                } else {
                    oldComponent.addChild(component);
                }
            }
        }
    }
}
