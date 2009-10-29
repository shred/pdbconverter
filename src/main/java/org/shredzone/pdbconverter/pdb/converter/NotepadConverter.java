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

import org.shredzone.pdbconverter.pdb.CategoryAppInfo;
import org.shredzone.pdbconverter.pdb.CategoryAppInfoHelper;
import org.shredzone.pdbconverter.pdb.NotepadEntry;
import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.PdbFile;

/*
 * This code bases on analyzing the hex dump of a single notepad file. The result might
 * be utterly broken on other notepad files, so be careful. If you get wrong results,
 * please open a bug ticket!
 * 
 * Notepad v1 files are not supported. If you need it, feel free to send a patch.
 */

/**
 * An {@link EntryConverter} that handles notepad entries.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 366 $
 */
public class NotepadConverter implements EntryConverter<NotepadEntry, CategoryAppInfo> {

    private static final int FLAG_TITLE = 0x0002;
    private static final int FLAG_ALARM = 0x0004;
    
    public boolean isAcceptable(PdbDatabase<NotepadEntry, CategoryAppInfo> database) {
        return "npadDB".equals(database.getName())
                && "npad".equals(database.getCreator());
    }
    
    public NotepadEntry convert(PdbFile reader, int size, byte attribute,
            PdbDatabase<NotepadEntry, CategoryAppInfo> database) throws IOException {
        long current = reader.getFilePointer();

        NotepadEntry result = new NotepadEntry(attribute);
        
        result.setCreated(reader.readDateTimeWords());
        result.setModified(reader.readDateTimeWords());
        int flags = reader.readUnsignedShort();
        
        if ((flags & FLAG_ALARM) != 0) {
            result.setAlarm(reader.readDateTimeWords());
        }
        
        if ((flags & FLAG_TITLE) != 0) {
            long start = reader.getFilePointer();
            result.setTitle(reader.readTerminatedString());
            long end = reader.getFilePointer();

            // If we're on an odd position, read one padding byte to make it even
            if (((end - start) % 2) == 1) {
                reader.readByte();
            }
        }
        
        reader.readUnsignedInt();           // Offset to the image's end (?)
        reader.readUnsignedInt();           // Full image width
        reader.readUnsignedInt();           // Full image height
        reader.readUnsignedInt();           // Always 1 (?)
        reader.readUnsignedInt();           // Always 2 (?)
        reader.readUnsignedInt();           // Offset to the image's end (?)
        
        // Read image data
        int fileSize = size - (int) (reader.getFilePointer() - current);
        byte[] pngData = new byte[fileSize];
        reader.readFully(pngData);
        result.setImagePng(pngData);
        
        return result;
    }
    
    public CategoryAppInfo convertAppInfo(PdbFile reader, int size,
            PdbDatabase<NotepadEntry, CategoryAppInfo> database) throws IOException {
        CategoryAppInfo result = new CategoryAppInfo();
        CategoryAppInfoHelper.readCategories(reader, result);
        return result;
    }

}