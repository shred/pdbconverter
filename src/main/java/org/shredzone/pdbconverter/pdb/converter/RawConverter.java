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
import org.shredzone.pdbconverter.pdb.appinfo.RawAppInfo;
import org.shredzone.pdbconverter.pdb.record.RawRecord;

/**
 * An {@link Converter} that handles only the raw content of a record.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 368 $
 */
public class RawConverter implements Converter<RawRecord, RawAppInfo> {

    public boolean isAcceptable(PdbDatabase<RawRecord, RawAppInfo> database) {
        // Raw accepts everything
        return true;
    }
    
    public RawRecord convert(PdbFile reader, int size, byte attribute,
            PdbDatabase<RawRecord, RawAppInfo> database) throws IOException {
        byte[] data = new byte[size];
        reader.readFully(data);
        return new RawRecord(data, attribute);
    }

    public RawAppInfo convertAppInfo(PdbFile reader, int size,
            PdbDatabase<RawRecord, RawAppInfo> database) throws IOException {
        byte[] data = new byte[size];
        reader.readFully(data);
        return new RawAppInfo(data);
    }
    
}
