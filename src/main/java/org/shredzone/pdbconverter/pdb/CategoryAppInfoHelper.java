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
package org.shredzone.pdbconverter.pdb;

import java.io.IOException;

/**
 * A helper for reading the categories from a standard appinfo area.
 * 
 * @author Richard "Shred" Körber
 * @version $Revision:$
 */
public final class CategoryAppInfoHelper {
    
    private static final int NUM_CATEGORIES = 16;

    /**
     * Utility class cannot be constructed.
     */
    private CategoryAppInfoHelper() {
    }

    /**
     * Reads the categories from a standard appinfo area and fills them into a
     * {@link CategoryAppInfo} object. After invocation, the {@link PdbFile}
     * file pointer points after the category part, where further application
     * information may be stored.
     * 
     * @param reader
     *            {@link PdbFile} that is located at the beginning of the
     *            appinfo area.
     * @param appInfo
     *            {@link CategoryAppInfo} where the categories are stored.
     * @return Bytes that were actually read from the appinfo area. The file
     *         pointer is located at the beginning of the appinfo area plus the
     *         result of this method.
     */
    public static int readCategories(PdbFile reader, CategoryAppInfo appInfo)
    throws IOException {
        long startPos = reader.getFilePointer();
        
        // This is a bitmask about renamed categories. It is ignored for now.
        reader.readShort();
        
        for (int ix = 0; ix < NUM_CATEGORIES; ix++) {
            String catName = reader.readTerminatedFixedString(16);
            if (catName.length() > 0) {
                appInfo.getCategories().add(catName);
            }
        }

        // Read the category keys
        for (int ix = 0; ix < NUM_CATEGORIES; ix++) {
            reader.readByte();
        }

        long endPos = reader.getFilePointer();
        
        return (int) (endPos - startPos);
    }

}
