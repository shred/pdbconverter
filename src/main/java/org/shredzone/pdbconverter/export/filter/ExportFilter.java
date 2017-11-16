/**
 * pdbconverter - Convert Palm PDB files into more common formats
 *
 * Copyright (C) 2009 Richard "Shred" Körber
 *   http://pdbconverter.shredzone.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
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
package org.shredzone.pdbconverter.export.filter;

import org.shredzone.commons.pdb.record.Record;

/**
 * A filter tells if a {@link Record} is accepted.
 *
 * @author Richard "Shred" Körber
 */
public interface ExportFilter<T extends Record> {

    /**
     * Checks if the filter accepts the {@link Record}.
     *
     * @param record
     *            {@link Record} that is checked
     * @return {@code true}: Filter accepts this {@link Record}
     */
    boolean accepts(T record);

}
