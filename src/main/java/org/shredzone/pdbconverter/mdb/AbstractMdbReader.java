/**
 * pdbconverter - Convert Palm PDB files into more common formats
 *
 * Copyright (C) 2011 Richard "Shred" Körber
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
package org.shredzone.pdbconverter.mdb;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.shredzone.pdbconverter.pdb.appinfo.AppInfo;
import org.shredzone.pdbconverter.pdb.record.Record;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

/**
 * An abstract implementation for reading MDB databases files.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 523 $
 */
public abstract class AbstractMdbReader<T extends Record, U extends AppInfo> implements MdbReader<T, U> {

    private static final int EPOCH_YEAR = 1970;
    private static final long EPOCH;    // Timestamp of MDB epoch (1904-01-01)
    
    static {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(EPOCH_YEAR, 0, 1, 0, 0, 0);
        EPOCH = cal.getTimeInMillis();
    }

    private Database db;
    
    @Override
    public void open(File mdbFile) throws IOException {
        db = Database.open(mdbFile);
    }
    
    @Override
    public void close() throws IOException {
        db.close();
    }
    
    /**
     * Reads a {@link Table} from the database.
     * 
     * @param table
     *            Table name
     * @return {@link Table}
     */
    protected Table getTable(String table) throws IOException {
        return db.getTable(table);
    }

    /**
     * Gets the value of a row's column.
     * 
     * @param <C>
     *            expected column type
     * @param row
     *            Row that was read
     * @param column
     *            Column name
     * @return Column value
     */
    @SuppressWarnings("unchecked")
    protected <C> C getColumn(Map<String, Object> row, String column) throws IOException {
        if (!row.containsKey(column)) {
            throw new IOException("Column " + column + ": undefined");
        }

        C value;
        try {
            value = (C) row.get(column);
        } catch (ClassCastException ex) {
            throw new IOException("Column " + column + ": unexpected type");
        }        
        
        return value;
    }

    /**
     * Reads a column as {@link Date}.
     * 
     * @param row
     *            Row that was read
     * @param column
     *            Column name
     * @return {@link Date} that was read
     */
    protected Date getDateColumn(Map<String, Object> row, String column) throws IOException {
        String value = getColumn(row, column);
        
        if (value != null) {
            return new Date(EPOCH + (Long.parseLong(value) * 1000));
        } else {
            return null;
        }
    }

}
