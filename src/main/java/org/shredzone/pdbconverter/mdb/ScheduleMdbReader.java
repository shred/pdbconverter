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

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo;
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo.Category;
import org.shredzone.pdbconverter.pdb.record.AbstractRecord;
import org.shredzone.pdbconverter.pdb.record.ScheduleRecord;
import org.shredzone.pdbconverter.pdb.record.ScheduleRecord.Alarm;
import org.shredzone.pdbconverter.pdb.record.ScheduleRecord.Alarm.Unit;
import org.shredzone.pdbconverter.pdb.record.ScheduleRecord.ShortDate;
import org.shredzone.pdbconverter.pdb.record.ScheduleRecord.ShortTime;

import com.healthmarketscience.jackcess.Table;

/**
 * This class reads a DateBook.mdb file.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 523 $
 */
public class ScheduleMdbReader extends AbstractMdbReader<ScheduleRecord, CategoryAppInfo> {
    
    @Override
    public PdbDatabase<ScheduleRecord, CategoryAppInfo> read() throws IOException {
        PdbDatabase<ScheduleRecord, CategoryAppInfo> result = new PdbDatabase<ScheduleRecord, CategoryAppInfo>();
        
        CategoryAppInfo ai = createAppInfo();
        result.setAppInfo(ai);
        
        Table table = getTable("Main");
        for(Map<String, Object> row : table) {
            result.getRecords().add(createScheduleRecord(row, ai));
        }
        
        return result;
    }
    
    /**
     * Creates an {@link CategoryAppInfo}.
     * 
     * @return {@link CategoryAppInfo} that was created.
     */
    private CategoryAppInfo createAppInfo() throws IOException {
        CategoryAppInfo ai = new CategoryAppInfo();
        
        Table table = getTable("Category");
        for(Map<String, Object> row : table) {
            ai.getCategories().add(createCategory(row));
        }
        
        return ai;
    }

    /**
     * Creates a {@link Category} from the given database row.
     * 
     * @param row
     *            Database row
     * @return {@link Category} that was created
     */
    private Category createCategory(Map<String, Object> row) throws IOException {
        Integer id = getColumn(row, "ID");
        String name = getColumn(row, "Name");

        return new Category(name, id.intValue(), false);
    }
    
    /**
     * Creates a {@link ScheduleRecord} from the given database row.
     * 
     * @param row
     *            Database row
     * @param ai
     *            {@link CategoryAppInfo}
     * @return {@link ScheduleRecord} that was created
     */
    private ScheduleRecord createScheduleRecord(Map<String, Object> row, CategoryAppInfo ai)
    throws IOException {
        Boolean priv = getColumn(row, "Private");
        int catKey = Integer.parseInt((String) getColumn(row, "Category"));
        int catIx = ai.findCategoryByKey(catKey);

        int attribute = priv ? AbstractRecord.ATTR_SECRET : 0;
        attribute |= (catIx & 0x0F);
        
        ScheduleRecord record = new ScheduleRecord((byte) attribute);
        
        Category cat = ai.getCategoryByKey(catKey);
        if (cat != null) {
            record.setCategory(cat.getName());
        }
        
        String note = getColumn(row, "Note");
        if (!note.isEmpty()) {
            record.setNote(note);
        }
        String summary = getColumn(row, "Summary");
        if (!summary.isEmpty()) {
            record.setDescription(summary);
        }
        String location = getColumn(row, "Location");
        if (!location.isEmpty()) {
            record.setLocation(location);
        }

        convertSchedule(row, record);
        convertAlarm(row, record);
        convertRepeat(row, record);
            
        return record;
    }

    /**
     * Converts a schedule and sets the ScheduleRecord accordingly.
     * 
     * @param row
     *            Database row
     * @param record
     *            {@link ScheduleRecord} to be filled
     */
    private void convertSchedule(Map<String, Object> row, ScheduleRecord record) throws IOException {
        Date startTime = getDateColumn(row, "Start Time");
        Date endTime = getDateColumn(row, "End Time");
        Boolean untimed = getColumn(row, "Untimed");
        String timeZone = getColumn(row, "Time Zone");
        
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        
        Calendar start = Calendar.getInstance(tz);
        start.setTime(startTime);
        
        Calendar end = Calendar.getInstance(tz);
        end.setTime(endTime);
        
        record.setSchedule(new ShortDate(start));
        
        if (!untimed) {
            record.setStartTime(new ShortTime(start));
            record.setEndTime(new ShortTime(end));
        }
    }
    
    /**
     * Converts an alarm and sets the ScheduleRecord accordingly.
     * 
     * @param row
     *            Database row
     * @param record
     *            {@link ScheduleRecord} to be filled
     */
    private void convertAlarm(Map<String, Object> row, ScheduleRecord record) throws IOException {
        Boolean alarm = getColumn(row, "Alarm");
        if (alarm) {
            Integer advance = getColumn(row, "Alarm Advance");
            Integer unit = getColumn(row, "Alarm Unit");

            ScheduleRecord.Alarm.Unit alarmUnit;
            switch (unit) {
                case 0: alarmUnit = Unit.MINUTES; break;
                case 1: alarmUnit = Unit.HOURS; break;
                case 2: alarmUnit = Unit.DAYS; break;
                default: throw new IOException("Unknown alarm unit: " + unit);
            }
            
            record.setAlarm(new Alarm(advance, alarmUnit));
        }
    }

    /**
     * Converts a repetition and sets the ScheduleRecord accordingly.
     * 
     * @param row
     *            Database row
     * @param record
     *            {@link ScheduleRecord} to be filled
     */
    private void convertRepeat(Map<String, Object> row, ScheduleRecord record) throws IOException {
        String event = getColumn(row, "Repeated Event");
        
        if (!event.isEmpty()) {
            System.out.println("EVENT = " + event); // DEBUG
        }

        // TODO: Convert
        //    private Repeat repeat;
        //    private List<ShortDate> exceptions = new ArrayList<ShortDate>();
        
    }

}
