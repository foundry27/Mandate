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

package pw.stamina.mandate.internal.parsing.argument.implicit;

import pw.stamina.mandate.parsing.argument.ArgumentProvider;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Mark Johnson
 */
public class ForkedArgumentProvider implements ArgumentProvider {

    private final ArgumentProvider forkedProvider;

    private final ArgumentProvider thisProvider;

    public ForkedArgumentProvider(final ArgumentProvider forkedProvider, final ArgumentProvider thisProvider) {
        this.forkedProvider = forkedProvider;
        this.thisProvider = thisProvider;
    }

    @Override
    public void registerProvider(final Type valueType, final Supplier<?> valueProvider) {
        if (thisProvider.isProviderPresent(valueType)) {
            throw new IllegalStateException(String.format("Top-level argument provider already mapped for arguments of type %s", valueType.getTypeName()));
        } else {
            thisProvider.registerProvider(valueType, valueProvider);
        }
    }

    @Override
    public Optional<Supplier<?>> findProvider(final Type valueType) {
        final Optional<Supplier<?>> lookup = thisProvider.findProvider(valueType);
        return lookup.isPresent() ? lookup : forkedProvider.findProvider(valueType);
    }

    @Override
    public boolean isProviderPresent(final Type valueType) {
        return thisProvider.isProviderPresent(valueType) || forkedProvider.isProviderPresent(valueType);
    }
}
