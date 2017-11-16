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

import org.shredzone.commons.pdb.PdbDatabase;
import org.shredzone.commons.pdb.appinfo.RawAppInfo;
import org.shredzone.commons.pdb.record.RawRecord;

/**
 * Writes a {@link RawRecord} database as ZIP file.
 *
 * @author Richard "Shred" Körber
 */
public class ZipExporter extends AbstractExporter<RawRecord, RawAppInfo> {

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    static {
        DATE_FMT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Writes a database of {@link RawRecord} to a ZIP file. The zip file contains a file
     * "db-info.xml" with generic database information, and a .bin file for each database
     * record.
     */
    @Override
    public void export(PdbDatabase<RawRecord, RawAppInfo> database, OutputStream out)
    throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(out)) {
            writeDatabaseInfo(database, zos);

            writeAppInfo(database, zos);

            List<RawRecord> records = database.getRecords();
            for (int ix = 0; ix < records.size(); ix++) {
                RawRecord record = records.get(ix);
                if (isAccepted(record)) {
                    String name = String.format("records/%04d.bin", ix);
                    zos.putNextEntry(new ZipEntry(name));
                    zos.write(record.getRaw());
                    zos.flush();
                    zos.closeEntry();
                }
            }
        }
    }

    /**
     * Creates the "db-info.xml" file with generic database information.
     *
     * @param database
     *            {@link PdbDatabase} to be written
     * @param zos
     *            {@link ZipOutputStream} to write to
     */
    private void writeDatabaseInfo(PdbDatabase<RawRecord, RawAppInfo> database, ZipOutputStream zos)
    throws IOException {
        zos.putNextEntry(new ZipEntry("db-info.xml"));

        XmlHelper xh = new XmlHelper();
        xh.openXmlWriter(zos, "dbinfo");
        xh.writeDatabase(database);

        xh.startElement("records");

        List<RawRecord> records = database.getRecords();
        for (int ix = 0; ix < records.size(); ix++) {
            RawRecord record = records.get(ix);

            if (isAccepted(record)) {
                xh.startElement(
                        "record",
                        "id", ix,
                        "category", record.getCategoryIndex(),
                        "secret", record.isSecret()
                );
                xh.writeFormatted("file", "records/%04d.bin", ix);
                xh.endElement();
            }
        }

        xh.endElement();

        xh.closeXmlWriter();
        zos.closeEntry();
    }

    /**
     * Creates the "appinfo.bin" file with a dump of the appinfo area.
     *
     * @param database
     *            {@link PdbDatabase} to be written
     * @param zos
     *            {@link ZipOutputStream} to write to
     */
    private void writeAppInfo(PdbDatabase<RawRecord, RawAppInfo> database, ZipOutputStream zos)
    throws IOException {
        if (database.getAppInfo() != null) {
            zos.putNextEntry(new ZipEntry("appinfo.bin"));
            zos.write(database.getAppInfo().getRawAppInfo());
            zos.flush();
            zos.closeEntry();
        }
    }

}
