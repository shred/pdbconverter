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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.shredzone.pdbconverter.export.ZipExporter;
import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.PdbFile;
import org.shredzone.pdbconverter.pdb.appinfo.RawAppInfo;
import org.shredzone.pdbconverter.pdb.converter.RawConverter;
import org.shredzone.pdbconverter.pdb.record.RawRecord;

/**
 * {@link ExportHandler} that reads any pdb file and writes a ZIP file containing a
 * database index xml file, an appinfo dump and a binary file for each record.
 *
 * @author Richard "Shred" Körber
 */
public class ZipHandler implements ExportHandler {

    @Override
    public String getName() {
        return "zip";
    }

    @Override
    public String getDescription() {
        return "Any PDB to ZIP";
    }

    @Override
    public void export(File infile, File outfile, ExportOptions options) throws IOException {
        PdbDatabase<RawRecord, RawAppInfo> database;

        PdbFile pdb = null;
        try {
            pdb = new PdbFile(infile);
            database = pdb.readDatabase(new RawConverter());
        } finally {
            if (pdb != null) pdb.close();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outfile);
            ZipExporter exporter = new ZipExporter();
            exporter.export(database, fos);
        } finally {
            if (fos != null) fos.close();
        }
    }

}
