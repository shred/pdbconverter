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
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo;
import org.shredzone.pdbconverter.pdb.record.MemoRecord;

/**
 * Writes a {@link MemoRecord} database as a single XML file.
 *
 * @author Richard "Shred" Körber
 */
public class MemoXmlExporter extends AbstractExporter<MemoRecord, CategoryAppInfo> {

    /**
     * Writes the {@link MemoRecord} database XML to the given {@link OutputStream}.
     *
     * @param database
     *            {@link MemoRecord} {@link PdbDatabase} to write
     * @param out
     *            {@link OutputStream} to write to
     */
    @Override
    public void export(PdbDatabase<MemoRecord, CategoryAppInfo> database, OutputStream out)
    throws IOException {
        XmlHelper xh = new XmlHelper();
        xh.openXmlWriter(out, "memodb");

        xh.writeDatabase(database);
        xh.writeCategories(database);

        xh.startElement("memos");
        List<MemoRecord> records = database.getRecords();
        for (int ix = 0; ix < records.size(); ix++) {
            MemoRecord record = records.get(ix);
            if (isAccepted(record)) {
                xh.startElement("memo",
                        "id", ix,
                        "category", record.getCategoryIndex(),
                        "secret", record.isSecret()
                );
                xh.writeContent(record.getMemo());
                xh.endElement();
            }
        }
        xh.endElement();

        xh.closeXmlWriter();
    }

}
