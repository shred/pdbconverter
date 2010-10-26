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
import org.shredzone.pdbconverter.pdb.record.TodoRecord;

/**
 * A {@link Converter} that handles todo records.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 491 $
 */
public class TodoConverter implements Converter<TodoRecord, CategoryAppInfo> {

    @Override
    public boolean isAcceptable(PdbDatabase<TodoRecord, CategoryAppInfo> database) {
        return "ToDoDB".equals(database.getName())
                && "todo".equals(database.getCreator());
    }
    
    @Override
    public TodoRecord convert(PdbFile reader, int size, byte attribute,
            PdbDatabase<TodoRecord, CategoryAppInfo> database) throws IOException {
        TodoRecord result = new TodoRecord(attribute);
        
        result.setDate(reader.readPackedDate());

        int flags = reader.readUnsignedByte();
        result.setCompleted((flags & 0x80) != 0);
        result.setPriority(flags & 0x7F);
        
        result.setDescription(reader.readTerminatedString());
        
        String note = reader.readTerminatedString();
        if (note != null && note.length() > 0) {
            result.setNote(note);
        }
        
        return result;
    }
    
    @Override
    public CategoryAppInfo convertAppInfo(PdbFile reader, int size,
            PdbDatabase<TodoRecord, CategoryAppInfo> database) throws IOException {
        CategoryAppInfo result = new CategoryAppInfo();
        reader.readCategories(result);
        return result;
    }

}
