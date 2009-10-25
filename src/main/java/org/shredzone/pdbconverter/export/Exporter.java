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
import java.io.PrintStream;

import org.shredzone.pdbconverter.pdb.Entry;
import org.shredzone.pdbconverter.pdb.PdbDatabase;

/**
 * Generic interface for a database exporter.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 359 $
 */
public interface Exporter<T extends Entry> {

    /**
     * Exports the database to the given stream.
     * 
     * @param database
     *            {@link PdbDatabase} to be exported
     * @param out
     *            {@link PrintStream} to write to.
     */
    void export(PdbDatabase<T> database, PrintStream out) throws IOException;

}
