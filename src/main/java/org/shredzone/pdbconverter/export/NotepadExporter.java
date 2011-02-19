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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo;
import org.shredzone.pdbconverter.pdb.record.NotepadRecord;

/**
 * Writes a {@link NotepadRecord} database as ZIP file.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 524 $
 */
public class NotepadExporter extends AbstractExporter<NotepadRecord, CategoryAppInfo> {
    
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    static {
        DATE_FMT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
    
    /**
     * Writes a database of {@link NotepadRecord} to a ZIP file. The zip file contains a
     * file "db-info.xml" with generic database information, and a .png file for each
     * database record.
     */
    @Override
    public void export(PdbDatabase<NotepadRecord, CategoryAppInfo> database, OutputStream out)
    throws IOException {
        ZipOutputStream zos = new ZipOutputStream(out);

        writeDatabaseInfo(database, zos);
        
        List<NotepadRecord> records = database.getRecords();
        for (int ix = 0; ix < records.size(); ix++) {
            NotepadRecord record = records.get(ix);
            
            if (isAccepted(record)) {
                String name = String.format("images/%04d.png", ix);
                ZipEntry ze = new ZipEntry(name);
                if (record.getModified() != null) {
                    ze.setTime(record.getModified().getTimeInMillis());
                }
                zos.putNextEntry(ze);
                zos.write(record.getImagePng());
                zos.flush();
                zos.closeEntry();
            }
        }
        
        zos.close();
    }
    
    /**
     * Creates the "db-info.xml" file with generic database information.
     * 
     * @param database
     *            {@link PdbDatabase} to be written
     * @param zos
     *            {@link ZipOutputStream} to write to
     */
    private void writeDatabaseInfo(PdbDatabase<NotepadRecord, CategoryAppInfo> database, ZipOutputStream zos)
    throws IOException {
        zos.putNextEntry(new ZipEntry("db-info.xml"));
        
        XmlHelper xh = new XmlHelper();
        xh.openXmlWriter(zos, "dbinfo");
        xh.writeDatabase(database);
        xh.writeCategories(database);

        xh.startElement("records");

        List<NotepadRecord> records = database.getRecords();
        for (int ix = 0; ix < records.size(); ix++) {
            NotepadRecord record = records.get(ix);

            if (isAccepted(record)) {
                xh.startElement(
                        "record",
                        "id", ix,
                        "category", record.getCategoryIndex(),
                        "secret", record.isSecret()
                );
    
                xh.writeDate("created", record.getCreated());
                if (record.getModified() != null) {
                    xh.writeDate("modified", record.getModified());
                }
                if (record.getAlarm() != null) {
                    xh.writeDate("alarm", record.getAlarm());
                }
                if (record.getTitle() != null) {
                    xh.writeValue("title", record.getTitle());
                }
    
                xh.writeFormatted("file", "images/%04d.png", ix);
                
                xh.endElement();
            }
        }
        
        xh.endElement();
        
        xh.closeXmlWriter();
        zos.closeEntry();
    }
    
}
