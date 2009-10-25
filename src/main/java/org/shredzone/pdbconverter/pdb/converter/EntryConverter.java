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
package org.shredzone.pdbconverter.pdb.converter;

import java.io.IOException;

import org.shredzone.pdbconverter.pdb.Entry;
import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.PdbFile;

/**
 * Converts a PDB record into an {@link Entry} object.
 * 
 * @author Richard "Shred" Körber
 * @version $Revision: 356 $
 */
public interface EntryConverter<T extends Entry> {

    /**
     * Checks if this entry converter is able to convert entries for the PdbDatabase.
     * 
     * @param database
     *            The {@link PdbDatabase} that is currently generated. Note that the
     *            database is still being read. The attributes and categories are already
     *            read and may be used, but the entries are still incomplete. You would
     *            usually access the database to read the database name or the category
     *            map.
     * @return {@code true} if the converter is able to process the database records.
     */
    boolean isAcceptable(PdbDatabase<T> database);

    /**
     * @param reader
     *            {@link PdbFile} with the file cursor at the beginning of the record
     * @param size
     *            Size of this record, in bytes
     * @param attribute
     *            Attributes of this record
     * @param database
     *            The {@link PdbDatabase} that is currently generated. Note that the
     *            database is still being read. The attributes and categories are already
     *            read and may be used, but the entries are still incomplete. You would
     *            usually access the database to read the database name or the category
     *            map.
     * @return {@link Entry} object containing the data of this record
     */
    T convert(PdbFile reader, int size, byte attribute, PdbDatabase<T> database)
        throws IOException;

}
