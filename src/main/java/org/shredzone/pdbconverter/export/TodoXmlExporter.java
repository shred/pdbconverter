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
import org.shredzone.pdbconverter.pdb.record.TodoRecord;

/**
 * Writes a {@link TodoRecord} database as a single XML file.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 523 $
 */
public class TodoXmlExporter extends AbstractExporter<TodoRecord, CategoryAppInfo> {

    /**
     * Writes the {@link TodoRecord} database XML to the given {@link OutputStream}.
     * 
     * @param database
     *            {@link TodoRecord} {@link PdbDatabase} to write
     * @param out
     *            {@link OutputStream} to write to
     */
    @Override
    public void export(PdbDatabase<TodoRecord, CategoryAppInfo> database, OutputStream out)
    throws IOException {
        XmlHelper xh = new XmlHelper();
        xh.openXmlWriter(out, "tododb");
        
        xh.writeDatabase(database);
        xh.writeCategories(database);

        xh.startElement("todos");
        List<TodoRecord> records = database.getRecords();
        for (int ix = 0; ix < records.size(); ix++) {
            TodoRecord record = records.get(ix);
            if (isAccepted(record)) {
                xh.startElement("todo",
                        "id", ix,
                        "category", record.getCategoryIndex(),
                        "secret", record.isSecret()
                );
    
                if (record.isCompleted()) {
                    xh.startElement("completed");
                    xh.endElement();
                }
                
                xh.writeValue("priority", record.getPriority());
    
                if (record.getDate() != null) {
                    xh.writeDate("date", record.getDate());
                }
                
                xh.writeValue("description", record.getDescription());
    
                if (record.getNote() != null) {
                    xh.writeValue("note", record.getNote());
                }
                
                xh.endElement();
            }
        }
        xh.endElement();
        
        xh.closeXmlWriter();
    }
    
}
