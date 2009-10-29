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
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo;
import org.shredzone.pdbconverter.pdb.record.NotepadEntry;

/**
 * Writes a {@link NotepadEntry} database as ZIP file.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 367 $
 */
public class NotepadExporter implements Exporter<NotepadEntry, CategoryAppInfo> {
    
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    static {
        DATE_FMT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
    
    /**
     * Writes a database of {@link NotepadEntry} to a ZIP file. The zip file contains a
     * file "db-info.xml" with generic database information, and a .png file for each
     * database record.
     */
    public void export(PdbDatabase<NotepadEntry, CategoryAppInfo> database, OutputStream out)
    throws IOException {
        ZipOutputStream zos = new ZipOutputStream(out);

        writeDatabaseInfo(database, zos);
        
        int counter = 0;
        for (NotepadEntry entry : database.getEntries()) {
            String name = String.format("images/%04d.png", counter++);
            ZipEntry ze = new ZipEntry(name);
            if (entry.getModified() != null) {
                ze.setTime(entry.getModified().getTime());
            }
            zos.putNextEntry(ze);
            zos.write(entry.getImagePng());
            zos.flush();
            zos.closeEntry();
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
    private void writeDatabaseInfo(PdbDatabase<NotepadEntry, CategoryAppInfo> database, ZipOutputStream zos)
    throws IOException {
        zos.putNextEntry(new ZipEntry("db-info.xml"));
        PrintStream out = new PrintStream(zos, false, "utf-8");

        // TODO: This is just a quick hack. Use a real XML writer.
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<dbinfo>");
        out.printf("  <name>%s</name>", database.getName()).println();
        out.printf("  <type>%s</type>", database.getType()).println();
        out.printf("  <creator>%s</creator>", database.getCreator()).println();
        out.printf("  <created>%s</created>", DATE_FMT.format(database.getCreationTime())).println();
        out.printf("  <modified>%s</modified>", DATE_FMT.format(database.getModificationTime())).println();
        if (database.getBackupTime() != null) {
            out.printf("  <backup>%s</backup>", DATE_FMT.format(database.getBackupTime())).println();
        }

        out.println("  <categories>");
        List<String> categories = database.getAppInfo().getCategories(); 
        for (int ix = 0; ix < categories.size(); ix++) {
            out.printf("    <category id=\"%d\">%s</category>", ix, categories.get(ix)).println();
        }
        out.println("  </categories>");
        
        out.println("  <records>");
        List<NotepadEntry> records = database.getEntries();
        for (int ix = 0; ix < records.size(); ix++) {
            NotepadEntry record = records.get(ix);
            out.printf(
                    "      <record id=\"%d\" category=\"%d\"%s>",
                    ix,
                    record.getCategoryIndex(),
                    record.isSecret() ? " secret=\"true\"" : ""
            ).println();
            out.printf("      <created>%s</created>", DATE_FMT.format(record.getCreated())).println();
            if (record.getModified() != null) {
                out.printf("      <modified>%s</modified>", DATE_FMT.format(record.getModified())).println();
            }
            if (record.getAlarm() != null) {
                out.printf("      <alarm>%s</alarm>", DATE_FMT.format(record.getAlarm())).println();
            }
            if (record.getTitle() != null) {
                out.printf("      <title>%s</title>", record.getTitle()).println();
            }
            out.printf("      <file>images/%04d.png</file>", ix).println();
            out.println("    </record>");
        }
        out.println("  </records>");
        
        out.println("</dbinfo>");
        out.flush();
        
        zos.closeEntry();
    }
    
}
