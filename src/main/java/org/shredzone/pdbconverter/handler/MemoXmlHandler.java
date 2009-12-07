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
import org.shredzone.pdbconverter.PdbConverter;
import org.shredzone.pdbconverter.export.MemoXmlExporter;
import org.shredzone.pdbconverter.export.filter.CategoryExportFilter;
import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.PdbFile;
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo;
import org.shredzone.pdbconverter.pdb.converter.MemoConverter;
import org.shredzone.pdbconverter.pdb.record.MemoRecord;

/**
 * {@link ExportHandler} that reads a Memo pdb file and writes an XML file.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 399 $
 */
public class MemoXmlHandler implements ExportHandler {
    
    public String getName() {
        return "memo";
    }

    public String getDescription() {
        return "MemoDB to XML";
    }

    public void export(File infile, File outfile, CommandLine cmd) throws IOException {
        PdbDatabase<MemoRecord, CategoryAppInfo> database;
        
        PdbFile pdb = null;
        try {
            pdb = new PdbFile(infile);
            database = pdb.readDatabase(new MemoConverter());
        } finally {
            if (pdb != null) pdb.close();
        }

        CategoryExportFilter<MemoRecord> filter = null;
        if (cmd.hasOption(PdbConverter.OPT_CATEGORY)) {
            filter = new CategoryExportFilter<MemoRecord>(
                            database.getAppInfo(), cmd.getOptionValue(PdbConverter.OPT_CATEGORY));
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outfile);
            MemoXmlExporter exporter = new MemoXmlExporter();
            exporter.setFilter(filter);
            exporter.export(database, fos);
        } finally {
            if (fos != null) fos.close();
        }
    }

}
