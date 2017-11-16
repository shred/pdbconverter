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
 * An {@link ExportFilter} that consists of a chain of other
 * {@link ExportFilter}. This filter only accepts a record if all the chained
 * {@link ExportFilter} accepted the record.
 *
 * @author Richard "Shred" Körber
 */
public class ChainedExportFilter<T extends Record> implements ExportFilter<T> {

    private final ExportFilter<T>[] filterList;

    /**
     * Creates a new {@link ChainedExportFilter}.
     *
     * @param filter
     *            Filter chain
     */
    public ChainedExportFilter(ExportFilter<T>[] filter) {
        this.filterList = filter;
    }

    @Override
    public boolean accepts(T record) {
        for (ExportFilter<T> filter : filterList) {
            if (!filter.accepts(record)) {
                return false;
            }
        }

        return true;
    }

}
