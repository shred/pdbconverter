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

import java.io.IOException;
import java.io.OutputStream;

import org.shredzone.pdbconverter.export.filter.ExportFilter;
import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.appinfo.AppInfo;
import org.shredzone.pdbconverter.pdb.record.Record;

/**
 * Generic interface for a database exporter.
 *
 * @author Richard "Shred" Körber
 */
public interface Exporter<T extends Record, U extends AppInfo> {

    /**
     * Sets a filter to only export certain records.
     *
     * @param filter ExportFilter to be used. {@code null} means to use no filter.
     */
    void setFilter(ExportFilter<T> filter);

    /**
     * Exports the database to the given stream.
     *
     * @param database
     *            {@link PdbDatabase} to be exported
     * @param out
     *            {@link OutputStream} to write to.
     */
    void export(PdbDatabase<T, U> database, OutputStream out) throws IOException;

}
