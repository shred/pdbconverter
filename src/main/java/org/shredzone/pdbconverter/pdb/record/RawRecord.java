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
package org.shredzone.pdbconverter.pdb.record;



/**
 * An {@link Record} implementation that just contains the raw database record.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 368 $
 */
public class RawRecord extends Record {
    
    private final byte[] data;

    /**
     * Creates a new {@link RawRecord} for the given data and attribute.
     * 
     * @param data
     *            Record data
     * @param attribute
     *            Record attribute
     */
    public RawRecord(byte[] data, byte attribute) {
        super(attribute);
        this.data = data;
    }

    /**
     * Gets the raw content of the record.
     * 
     * @return Raw content, as byte array
     */
    public byte[] getRaw() {
        return data;
    }
    
}
