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
package org.shredzone.pdbconverter.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.appinfo.AddressAppInfo;
import org.shredzone.pdbconverter.pdb.record.AddressRecord;
import org.shredzone.pdbconverter.pdb.record.AddressRecord.Field;
import org.shredzone.pdbconverter.pdb.record.AddressRecord.Label;

/**
 * Writes a {@link AddressRecord} database as a single XML file.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 490 $
 */
public class AddressXmlExporter extends AbstractExporter<AddressRecord, AddressAppInfo> {

    /**
     * Writes the {@link AddressRecord} database XML to the given {@link OutputStream}.
     * 
     * @param database
     *            {@link AddressRecord} {@link PdbDatabase} to write
     * @param out
     *            {@link OutputStream} to write to
     */
    @Override
    public void export(PdbDatabase<AddressRecord, AddressAppInfo> database, OutputStream out)
    throws IOException {
        XmlHelper xh = new XmlHelper();
        xh.openXmlWriter(out, "addressdb");
        
        xh.writeDatabase(database);
        xh.writeValue("country", database.getAppInfo().getCountry());
        xh.writeCategories(database);
        
        writeLabelNames(database.getAppInfo(), xh);

        xh.startElement("addresses");
        List<AddressRecord> records = database.getRecords();
        for (int ix = 0; ix < records.size(); ix++) {
            AddressRecord record = records.get(ix);
            if (isAccepted(record)) {
                xh.startElement("address",
                        "id", ix,
                        "category", record.getCategoryIndex(),
                        "secret", record.isSecret()
                );
                writeAddress(record, xh);
                xh.endElement();
            }
        }
        xh.endElement();
        
        xh.closeXmlWriter();
    }

    /**
     * Writes an {@link AddressRecord}.
     * 
     * @param address
     *            {@link AddressRecord} to be written
     * @param xh
     *            {@link XmlHelper} for the output
     */
    private void writeAddress(AddressRecord address, XmlHelper xh) throws IOException {
        int pref = address.getDisplayPhone();
        
        for (Field field : Field.values()) {
            String value = address.getField(field);
            Label label = address.getLabel(field);
            
            boolean preferred = false;
            if (   label == Label.PHONE1 || label == Label.PHONE2 || label == Label.PHONE3
                || label == Label.PHONE4 || label == Label.PHONE5 || label == Label.PHONE6
                || label == Label.PHONE7 || label == Label.PHONE8) {
                preferred = (pref == 0);
                pref--;
            }
            
            if (value != null) {
                if (preferred) {
                    xh.startElement(
                        "field",
                        "label",
                        label.name(),
                        "preferred",
                        "true"
                    );
                } else {
                    xh.startElement(
                        "field",
                        "label",
                        label.name()
                    );
                }
                xh.writeContent(value);
                xh.endElement();
            }
        }
    }

    /**
     * Writes the label names.
     * 
     * @param appinfo
     *            {@link AddressAppInfo} with the label definitions
     * @param xh
     *            {@link XmlHelper} for the output
     */
    private void writeLabelNames(AddressAppInfo appinfo, XmlHelper xh) throws IOException {
        xh.startElement("labels");
        for (Label label : Label.values()) {
            xh.startElement("label", "id", label.name());
            xh.writeContent(appinfo.getLabel(label));
            xh.endElement();
        }
        xh.endElement();
    }
    
}
