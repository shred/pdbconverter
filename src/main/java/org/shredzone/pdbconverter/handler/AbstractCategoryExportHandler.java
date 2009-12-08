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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.shredzone.pdbconverter.PdbConverter;
import org.shredzone.pdbconverter.export.Exporter;
import org.shredzone.pdbconverter.export.filter.CategoryExportFilter;
import org.shredzone.pdbconverter.export.filter.ExportFilter;
import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.PdbFile;
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo;
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo.Category;
import org.shredzone.pdbconverter.pdb.converter.Converter;
import org.shredzone.pdbconverter.pdb.record.Record;

/**
 * Abstract superclass for {@link Category} exporters.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 400 $
 */
public abstract class AbstractCategoryExportHandler<T extends Record, U extends CategoryAppInfo>
implements ExportHandler {

    @Override
    public void export(File infile, File outfile, CommandLine cmd) throws IOException {
        PdbDatabase<T, U> database;
        
        PdbFile pdb = null;
        try {
            pdb = new PdbFile(infile);
            database = pdb.readDatabase(createConverter());
        } finally {
            if (pdb != null) pdb.close();
        }
        
        CategoryExportFilter<T> filter = null;
        if (cmd.hasOption(PdbConverter.OPT_CATEGORY)) {
            filter = new CategoryExportFilter<T>(
                            database.getAppInfo(), cmd.getOptionValue(PdbConverter.OPT_CATEGORY));
        }
        
        if (cmd.hasOption(PdbConverter.OPT_SPLIT)) {
            if (filter != null) {
                throw new IllegalArgumentException("split and category options are mutually exclusive");
            }
        
            Set<String> catnameSet = new HashSet<String>();
            
            List<Category> categories = database.getAppInfo().getCategories();
            for (int ix = 0; ix < categories.size(); ix++) {
                Category cat = categories.get(ix);
                if (cat == null) continue;

                filter = new CategoryExportFilter<T>(ix);
                
                File catfile = computeFilename(outfile, cat, catnameSet);
                writeOutputFile(catfile, database, filter);
            }

        } else {
            writeOutputFile(outfile, database, filter);
        
        }
    }

    /**
     * Computes a single file name for the split option. It appends the category name to
     * the file name, after escaping characters that should not occur in file names. If
     * the file name was already used (e.g. for two categories with the same name), the
     * category key is also appended.
     * 
     * @param outfile
     *            Base output file name
     * @param category
     *            {@link Category} to be written
     * @param catnameSet
     *            Set of category names that were already generated.
     * @return File name for this category
     */
    private File computeFilename(File outfile, Category category, Set<String> catnameSet) {
        File path = outfile.getParentFile();
        String filename = outfile.getName();
        
        String catname = category.getName();
        catname = catname.replaceAll("(\\\\|\\/|\\.|\\:|\\$|\\s)+", "_");
        if (!catnameSet.add(catname)) {
            catname = catname + '-' + category.getKey();
        }
        
        int pos = filename.lastIndexOf('.');
        if (pos >= 0) {
            String suffix = (pos + 1 < filename.length() ? filename.substring(pos + 1) : "");
            filename = filename.substring(0, pos) + '-' + catname + '.' + suffix;
        } else {
            filename = filename + '-' + catname;
        }
        
        return new File(path, filename);
    }
    
    /**
     * Writes the database to the output file.
     * 
     * @param outfile
     *            output file to write to
     * @param database
     *            {@link PdbDatabase} to be written
     * @param filter
     *            {@link ExportFilter} to be used
     * @throws IOException
     *             if the file could not be written
     */
    private void writeOutputFile(File outfile, PdbDatabase<T, U> database, ExportFilter<T> filter)
    throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outfile);
            Exporter<T, U> exporter = createExporter();
            exporter.setFilter(filter);
            exporter.export(database, fos);
        } finally {
            if (fos != null) fos.close();
        }
    }

    /**
     * Creates the {@link Converter} that converts the database file.
     * 
     * @return {@link Converter}
     */
    protected abstract Converter<T, U> createConverter();
    
    /**
     * Creates the {@link Exporter} that writes the output file.
     * 
     * @return {@link Exporter}
     */
    protected abstract Exporter<T, U> createExporter();

}
