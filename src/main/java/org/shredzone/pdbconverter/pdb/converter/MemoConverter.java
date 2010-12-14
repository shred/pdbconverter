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
import org.shredzone.pdbconverter.pdb.record.MemoRecord;

/**
 * A {@link Converter} that handles memo records.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 497 $
 */
public class MemoConverter implements Converter<MemoRecord, CategoryAppInfo> {

    @Override
    public boolean isAcceptable(PdbDatabase<MemoRecord, CategoryAppInfo> database) {
        return "MemoDB".equals(database.getName())
                && "memo".equals(database.getCreator());
    }
    
    @Override
    public MemoRecord convert(PdbFile reader, int record, int size, byte attribute,
            PdbDatabase<MemoRecord, CategoryAppInfo> database) throws IOException {
        MemoRecord result = new MemoRecord(attribute);
        result.setMemo(reader.readTerminatedString());
        return result;
    }
    
    @Override
    public CategoryAppInfo convertAppInfo(PdbFile reader, int size,
            PdbDatabase<MemoRecord, CategoryAppInfo> database) throws IOException {
        CategoryAppInfo result = new CategoryAppInfo();
        reader.readCategories(result);
        return result;
    }

}
