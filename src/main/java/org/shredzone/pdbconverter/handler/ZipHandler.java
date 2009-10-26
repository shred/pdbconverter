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

import org.apache.commons.cli.CommandLine;
import org.shredzone.pdbconverter.export.ZipExporter;
import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.PdbFile;
import org.shredzone.pdbconverter.pdb.RawEntry;
import org.shredzone.pdbconverter.pdb.converter.RawConverter;

/**
 * {@link ExportHandler} that reads any pdb file and writes a ZIP file containing a
 * database index xml file, and a binary file for each record.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 362 $
 */
public class ZipHandler implements ExportHandler {
    
    public String getName() {
        return "zip";
    }

    public String getDescription() {
        return "any pdb to a zip file";
    }

    public void export(File infile, File outfile, CommandLine cmd) throws IOException {
        PdbDatabase<RawEntry> database;
        
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