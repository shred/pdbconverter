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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.shredzone.pdbconverter.export.Exporter;
import org.shredzone.pdbconverter.export.filter.CategoryExportFilter;
import org.shredzone.pdbconverter.export.filter.ChainedExportFilter;
import org.shredzone.pdbconverter.export.filter.DatedExportFilter;
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
 * @version $Revision: 490 $
 */
public abstract class AbstractCategoryExportHandler<T extends Record, U extends CategoryAppInfo>
implements ExportHandler {

    @SuppressWarnings("unchecked")
    @Override
    public void export(File infile, File outfile, ExportOptions options) throws IOException {
        PdbDatabase<T, U> database;
        
        PdbFile pdb = null;
        try {
            pdb = new PdbFile(infile);
            database = pdb.readDatabase(createConverter());
        } finally {
            if (pdb != null) pdb.close();
        }
        
        ExportFilter<T> filter = createExportFilter(database, options);
        
        if (options.isSplit()) {
            Set<String> catnameSet = new HashSet<String>();
            
            List<Category> categories = database.getAppInfo().getCategories();
            for (int ix = 0; ix < categories.size(); ix++) {
                Category cat = categories.get(ix);
                if (cat == null) continue;
                
                ExportFilter<T> catFilter;
                if (filter != null) {
                    ExportFilter<T>[] filterChain = new ExportFilter[2];
                    filterChain[0] = new CategoryExportFilter<T>(ix);
                    filterChain[1] = filter;
                    catFilter = new ChainedExportFilter<T>(filterChain);
                } else {
                    catFilter = new CategoryExportFilter<T>(ix);
                }
                
                File catfile = computeFilename(outfile, cat, catnameSet);
                writeOutputFile(catfile, database, catFilter);
            }

        } else {
            writeOutputFile(outfile, database, filter);
        
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private ExportFilter<T> createExportFilter(PdbDatabase<T, U> database, ExportOptions options)
    throws IOException {
        List<ExportFilter<T>> filterList = new ArrayList<ExportFilter<T>>();

        if (options.getCategory() != null) {
            filterList.add(new CategoryExportFilter<T>(database.getAppInfo(), options.getCategory()));
        }
        
        if (options.getFrom() != null || options.getUntil() != null) {
            // This is a little ugly since DatedExportFilter only accepts DatedRecords.
            // Anyhow this cannot be checked at compile time in this generic class.
            // We will rely on a ClassCastException at runtime.
            filterList.add(new DatedExportFilter(options.getFrom(), options.getUntil()));
        }
        
        if (filterList.isEmpty()) {
            return null;
        } else if (filterList.size() == 1) {
            return filterList.get(0);
        } else {
            return new ChainedExportFilter<T>(filterList.toArray(new ExportFilter[filterList.size()]));
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
