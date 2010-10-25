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
package org.shredzone.pdbconverter.handler;

import org.shredzone.pdbconverter.export.Exporter;
import org.shredzone.pdbconverter.export.MemoXmlExporter;
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo;
import org.shredzone.pdbconverter.pdb.converter.Converter;
import org.shredzone.pdbconverter.pdb.converter.MemoConverter;
import org.shredzone.pdbconverter.pdb.record.MemoRecord;

/**
 * {@link ExportHandler} that reads a Memo pdb file and writes an XML file.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 490 $
 */
public class MemoXmlHandler extends AbstractCategoryExportHandler<MemoRecord, CategoryAppInfo> {
    
    @Override
    public String getName() {
        return "memo";
    }

    @Override
    public String getDescription() {
        return "MemoDB to XML";
    }

    @Override
    protected Converter<MemoRecord, CategoryAppInfo> createConverter() {
        return new MemoConverter();
    }

    @Override
    protected Exporter<MemoRecord, CategoryAppInfo> createExporter() {
        return new MemoXmlExporter();
    }

}
