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
 * Abstract {@link Record} implementation that represents a single database
 * entry. Subclasses will give detailed methods for reading the entry's content.
 * 
 * @author Richard "Shred" Körber
 * @version $Revision: 405 $
 */
public abstract class AbstractRecord implements Record {
    
    public static final int ATTR_SECRET = 0x10;
    public static final int ATTR_BUSY = 0x20;
    public static final int ATTR_DIRTY = 0x40;
    public static final int ATTR_DELETE = 0x80;
    
    private final byte attribute;
    
    /**
     * Create a new Entry.
     * 
     * @param attribute Entry attributes (see ATTR constants)
     */
    public AbstractRecord(byte attribute) {
        this.attribute = attribute;
    }

    @Override
    public boolean isSecret() {
        return (attribute & ATTR_SECRET) != 0;
    }

    @Override
    public boolean isBusy() {
        return (attribute & ATTR_BUSY) != 0;
    }

    @Override
    public boolean isDirty() {
        return (attribute & ATTR_DIRTY) != 0;
    }

    @Override
    public boolean isDelete() {
        return (attribute & ATTR_DELETE) != 0;
    }

    @Override
    public int getCategoryIndex() {
        return attribute & 0x0F;
    }
    
}
