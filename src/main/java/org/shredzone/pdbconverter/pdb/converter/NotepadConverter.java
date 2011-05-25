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

import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.PdbFile;
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo;
import org.shredzone.pdbconverter.pdb.record.NotepadRecord;

/*
 * This code bases on analyzing the hex dump of a single notepad file. The result might
 * be utterly broken on other notepad files, so be careful. If you get wrong results,
 * please open a bug ticket!
 * 
 * Notepad v1 files are not supported. If you need it, feel free to send a patch.
 */

/**
 * An {@link Converter} that handles notepad entries.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 563 $
 */
public class NotepadConverter implements Converter<NotepadRecord, CategoryAppInfo> {

    private static final int FLAG_TITLE = 0x0002;
    private static final int FLAG_ALARM = 0x0004;
    
    @Override
    public boolean isAcceptable(PdbDatabase<NotepadRecord, CategoryAppInfo> database) {
        return "npadDB".equals(database.getName())
                && "npad".equals(database.getCreator());
    }
    
    @Override
    public NotepadRecord convert(PdbFile reader, int record, int size, int attribute,
            PdbDatabase<NotepadRecord, CategoryAppInfo> database) throws IOException {
        long current = reader.getFilePointer();

        NotepadRecord result = new NotepadRecord(attribute);
        if (result.isDelete()) {
            return null;
        }
        
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
    
    @Override
    public CategoryAppInfo convertAppInfo(PdbFile reader, int size,
            PdbDatabase<NotepadRecord, CategoryAppInfo> database) throws IOException {
        CategoryAppInfo result = new CategoryAppInfo();
        reader.readCategories(result);
        return result;
    }

}
