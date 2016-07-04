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

import pw.stamina.mandate.api.annotations.Executes;
import pw.stamina.mandate.api.annotations.Syntax;

import java.util.Optional;

/**
 * @author Foundry
 */
@Syntax(syntax = {"execute", "exec", "do"})
class TestCommandBody {

    @Executes(tree = "greet|gr")
    public String greetSomeone(Optional<String> greeter, String greeting, Optional<String> greeted) {
        return greeter.orElse("Someone") + " greeted " + greeted.orElse("someone else") + " with " + greeting;
    }

    @Executes(tree = "thing|th")
    public String doThing(
            Optional<String> arg1,
            String arg2,
            Optional<String> arg3,
            Optional<String> arg4,
            String arg5
    ) {
        return String.format("1='%s', 2='%s', 3='%s', 4='%s', 5='%s'", arg1.orElse("Default"), arg2, arg3.orElse("Default"), arg4.orElse("Default"), arg5);
    }

}
