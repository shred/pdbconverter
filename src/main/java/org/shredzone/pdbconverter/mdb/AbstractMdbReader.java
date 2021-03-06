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
import java.util.TimeZone;

import org.shredzone.commons.pdb.CalendarFactory;
import org.shredzone.commons.pdb.appinfo.AppInfo;
import org.shredzone.commons.pdb.record.Record;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

/**
 * An abstract implementation for reading MDB databases files.
 *
 * @author Richard "Shred" Körber
 */
public abstract class AbstractMdbReader<T extends Record, U extends AppInfo> implements MdbReader<T, U> {

    private CalendarFactory cf = CalendarFactory.getInstance();
    private Database db;

    @Override
    public void open(File mdbFile) throws IOException {
        db = new DatabaseBuilder(mdbFile).setReadOnly(true).open();
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
     * @param def
     *            A default value, may be {@code null}
     * @return Column value
     */
    @SuppressWarnings("unchecked")
    protected <C> C getColumn(Row row, String column, C def)
    throws IOException {
        if (!row.containsKey(column)) {
            return def;
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
     * Gets the value of a row's column. Throws an exception if the column did not exist.
     *
     * @param <C>
     *            expected column type
     * @param row
     *            Row that was read
     * @param column
     *            Column name
     * @return Column value
     */
    protected <C> C getColumnRequired(Row row, String column)
    throws IOException {
        if (!row.containsKey(column)) {
            throw new IOException("Column " + column + ": undefined");
        }

        return getColumn(row, column, null);
    }

    /**
     * Reads a column as {@link Calendar}. Throws an exception if the column did not
     * exist.
     *
     * @param row
     *            Row that was read
     * @param column
     *            Column name
     * @param tz
     *            {@link TimeZone} to be used
     * @return {@link Calendar} that was read
     */
    protected Calendar getDateColumnRequired(Row row, String column, TimeZone tz)
    throws IOException {
        String value = getColumnRequired(row, column);

        Calendar cal = cf.createWithTimeZone(tz);
        cal.setTimeInMillis(Long.parseLong(value) * 1000L);
        return cal;
    }

}
