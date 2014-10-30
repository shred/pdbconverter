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
package org.shredzone.pdbconverter.export;

import org.shredzone.pdbconverter.export.filter.ExportFilter;
import org.shredzone.pdbconverter.pdb.appinfo.AppInfo;
import org.shredzone.pdbconverter.pdb.record.Record;

/**
 * An abstract implementation of {@link Exporter} that handles filtering.
 *
 * @author Richard "Shred" Körber
 */
public abstract class AbstractExporter<T extends Record, U extends AppInfo> implements Exporter<T, U> {

    private ExportFilter<T> filter;

    @Override
    public void setFilter(ExportFilter<T> filter) {
        this.filter = filter;
    }

    /**
     * Checks if the current filter accepts the given record.
     *
     * @param record
     *            {@link Record} to test
     * @return {@code true} if the record is accepted
     */
    protected boolean isAccepted(T record) {
        return (filter == null || filter.accepts(record));
    }

}
