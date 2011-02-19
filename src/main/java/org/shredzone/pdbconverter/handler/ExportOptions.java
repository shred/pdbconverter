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

import java.util.Calendar;

/**
 * Data transport object for export parameters.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 524 $
 */
public class ExportOptions {
    
    private boolean split;
    private String category;
    private Calendar from;
    private Calendar until;

    /**
     * Write categories into separate files?
     */
    public boolean isSplit()                { return split; }
    public void setSplit(boolean split)     { this.split = split; }

    /**
     * Category name to be exported only. {@code null} exports all categories.
     */
    public String getCategory()             { return category; }
    public void setCategory(String category) { this.category = category; }

    /**
     * Date range, starting from. {@code null} means there is no date set.
     */
    public Calendar getFrom()               { return from; }
    public void setFrom(Calendar from)      { this.from = from; }
    
    /**
     * Date range, ending at (exclusive). {@code null} means there is no date set.
     */
    public Calendar getUntil()              { return until; }
    public void setUntil(Calendar until)    { this.until = until; }
    
}
