/**
 * pdbconverter - Convert Palm PDB files into more common formats
 *
 * Copyright (C) 2011 Richard "Shred" Körber
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
package org.shredzone.pdbconverter.mdb;

import java.io.File;
import java.io.IOException;

import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.appinfo.AppInfo;
import org.shredzone.pdbconverter.pdb.record.Record;

/**
 * Interface for a reading MDB files.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 523 $
 */
public interface MdbReader<T extends Record, U extends AppInfo> {

    /**
     * Opens an MDB database.
     * 
     * @param mdbFile
     *            MDB database file to open
     */
    void open(File mdbFile) throws IOException;

    /**
     * Closes a MDB database.
     */
    void close() throws IOException;

    /**
     * Reads the MDB database as {@link PdbDatabase}.
     * 
     * @return {@link PdbDatabase} that was read.
     */
    PdbDatabase<T, U> read() throws IOException;
    
}
