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
import java.util.Date;

import org.shredzone.pdbconverter.pdb.record.DatedRecord;

/**
 * An {@link ExportFilter} that only accepts {@link DatedRecord} within the
 * given time range.
 * 
 * @author Richard "Shred" Körber
 * @version $Revision: 405 $
 */
public class DatedExportFilter<T extends DatedRecord> implements ExportFilter<T> {

    private final Date from;
    private final Date until;

    /**
     * Creates a new {@link DatedExportFilter} for the given time range. Undated
     * records are only accepted if until is set to {@code null}.
     * 
     * @param from
     *            Start of date range. {@code null} means that the beginning of
     *            all times.
     * @param until
     *            End of date range, exclusive. {@code null} means the ending of
     *            all times, and includes all undated records.
     * @throws IOException
     *             if the date range was not acceptable
     */
    public DatedExportFilter(Date from, Date until)
    throws IOException {
        if (from == null && until == null) {
            throw new IOException("No date range set");
        }
        
        if (until != null && from != null && until.before(from)) {
            throw new IOException("Date range ends before start");
        }
        
        this.from = from;
        this.until = until;
    }

    @Override
    public boolean accepts(T record) {
        Date date = record.getRecordDate();
        
        if (date == null) {
            return (until == null);
        }
        
        if (from != null && date.before(from)) {
            return false;
        }
        
        if (until != null && !date.before(until)) {
            return false;
        }
        
        return true;
    }

}
