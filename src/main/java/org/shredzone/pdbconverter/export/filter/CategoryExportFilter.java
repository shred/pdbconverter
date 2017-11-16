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
package org.shredzone.pdbconverter.export.filter;

import java.io.IOException;

import org.shredzone.commons.pdb.appinfo.CategoryAppInfo;
import org.shredzone.commons.pdb.record.Record;

/**
 * An {@link ExportFilter} that only accepts {@link Record} from the given category.
 *
 * @author Richard "Shred" Körber
 */
public class CategoryExportFilter<T extends Record> implements ExportFilter<T> {

    private int categoryIndex;

    /**
     * Creates a new {@link CategoryExportFilter} for the given category name.
     *
     * @param appinfo
     *            {@link CategoryAppInfo} with the categories
     * @param categoryName
     *            Category name that is filtered
     * @throws IOException
     *             if there is no such category
     */
    public CategoryExportFilter(CategoryAppInfo appinfo, String categoryName)
    throws IOException {
        int index = appinfo.findCategoryByName(categoryName);
        if (index < 0) {
            throw new IOException("Category '" + categoryName + "' is not defined");
        }
        this.categoryIndex = index;
    }

    /**
     * Creates a new {@link CategoryExportFilter} for the given category index.
     *
     * @param categoryIndex
     *            Category index that is filtered
     */
    public CategoryExportFilter(int categoryIndex) {
        this.categoryIndex = categoryIndex;
    }

    @Override
    public boolean accepts(T record) {
        return (record.getCategoryIndex() == categoryIndex);
    }

}
