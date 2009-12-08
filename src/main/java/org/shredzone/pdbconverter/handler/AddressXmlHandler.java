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

import org.shredzone.pdbconverter.export.AddressXmlExporter;
import org.shredzone.pdbconverter.export.Exporter;
import org.shredzone.pdbconverter.pdb.appinfo.AddressAppInfo;
import org.shredzone.pdbconverter.pdb.converter.AddressConverter;
import org.shredzone.pdbconverter.pdb.converter.Converter;
import org.shredzone.pdbconverter.pdb.record.AddressRecord;

/**
 * {@link ExportHandler} that reads an Address pdb file and writes an XML file.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 400 $
 */
public class AddressXmlHandler extends AbstractCategoryExportHandler<AddressRecord, AddressAppInfo> {
    
    public String getName() {
        return "address";
    }

    public String getDescription() {
        return "AddressDB to XML";
    }

    @Override
    protected Converter<AddressRecord, AddressAppInfo> createConverter() {
        return new AddressConverter();
    }

    @Override
    protected Exporter<AddressRecord, AddressAppInfo> createExporter() {
        return new AddressXmlExporter();
    }

}
