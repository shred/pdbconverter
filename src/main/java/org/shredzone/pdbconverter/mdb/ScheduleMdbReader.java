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
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.shredzone.pdbconverter.CalendarFactory;

import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo;
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo.Category;
import org.shredzone.pdbconverter.pdb.record.AbstractRecord;
import org.shredzone.pdbconverter.pdb.record.ScheduleRecord;
import org.shredzone.pdbconverter.pdb.record.ScheduleRecord.Alarm;
import org.shredzone.pdbconverter.pdb.record.ScheduleRecord.Alarm.Unit;
import org.shredzone.pdbconverter.pdb.record.ScheduleRecord.Repeat;
import org.shredzone.pdbconverter.pdb.record.ScheduleRecord.Repeat.Mode;
import org.shredzone.pdbconverter.pdb.record.ScheduleRecord.ShortDate;
import org.shredzone.pdbconverter.pdb.record.ScheduleRecord.ShortTime;

import com.healthmarketscience.jackcess.Table;

/**
 * This class reads a DateBook.mdb file.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 528 $
 */
public class ScheduleMdbReader extends AbstractMdbReader<ScheduleRecord, CategoryAppInfo> {

    private static final Pattern P_DAILY = Pattern.compile("D(\\d+)\\s(.*)");
    private static final Pattern P_WEEKLY = Pattern.compile("W(\\d+)\\s(\\S+)\\s(.*)");
    private static final Pattern P_MONTHLY = Pattern.compile("M(\\d+)\\s(.*)");
    private static final Pattern P_MD = Pattern.compile("MD(\\d+)\\s(\\d+)\\s(.*)");
    private static final Pattern P_MP = Pattern.compile("MP(\\d+)\\s(\\d)\\+\\s(\\S+)\\s(.*)");
    private static final Pattern P_YEARLY = Pattern.compile("YM(\\d+)\\s(\\d+)\\s(.*)");
    private static final Pattern P_TS = Pattern.compile("(\\d{4})(\\d{2})(\\d{2})T\\d{6}Z");
    private static final String[] WEEKDAYS = { "SU", "MO", "TU", "WE", "TH", "FR", "SA" };

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
        Integer id = getColumnRequired(row, "ID");
        String name = getColumnRequired(row, "Name");
        
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
        Boolean priv = getColumn(row, "Private", Boolean.FALSE);
        int catKey = Integer.parseInt((String) getColumnRequired(row, "Category"));
        int catIx = ai.findCategoryByKey(catKey);

        int attribute = priv ? AbstractRecord.ATTR_SECRET : 0;
        attribute |= (catIx & 0x0F);
        
        ScheduleRecord record = new ScheduleRecord((byte) attribute);
        
        Category cat = ai.getCategoryByKey(catKey);
        if (cat != null) {
            record.setCategory(cat.getName());
        }
        
        String note = getColumn(row, "Note", null);
        if (note != null && !note.isEmpty()) {
            record.setNote(note);
        }
        
        String summary = getColumn(row, "Summary", null);
        if (summary != null && !summary.isEmpty()) {
            record.setDescription(summary);
        }
        
        String location = getColumn(row, "Location", null);
        if (location != null && !location.isEmpty()) {
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
        TimeZone tz;
        String timeZone = getColumn(row, "Time Zone", null);
        if (timeZone != null) {
            tz = TimeZone.getTimeZone(timeZone);
        } else {
            tz = CalendarFactory.getInstance().getTimeZone();
        }

        Calendar startTime = getDateColumnRequired(row, "Start Time", tz);
        Calendar endTime = getDateColumnRequired(row, "End Time", tz);
        Boolean untimed = getColumnRequired(row, "Untimed");
        
        record.setSchedule(new ShortDate(startTime));
        
        if (!untimed) {
            record.setStartTime(new ShortTime(startTime));
            record.setEndTime(new ShortTime(endTime));
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
        Boolean alarm = getColumn(row, "Alarm", Boolean.FALSE);
        if (alarm) {
            Integer advance = getColumnRequired(row, "Alarm Advance");
            Integer unit = getColumnRequired(row, "Alarm Unit");

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
        String event = getColumn(row, "Repeated Event", null);
        if (event == null || event.isEmpty()) {
            return;
        }
        
        Mode mode = null;
        int frequency = 0;
        boolean[] weeklyDays = null;
        int monthlyWeek = 0;
        int monthlyDay = 0;
        String repeat = null;
        
        Matcher m;
        m = P_DAILY.matcher(event);
        if (m.matches()) {
            mode = Mode.DAILY;
            frequency = Integer.parseInt(m.group(1));
            repeat = m.group(2);
        }
        
        m = P_WEEKLY.matcher(event);
        if (m.matches()) {
            mode = Mode.WEEKLY;
            frequency = Integer.parseInt(m.group(1));
            weeklyDays = convertWeeklyDays(m.group(2));
            repeat = m.group(3);
        }
        
        m = P_MONTHLY.matcher(event);
        if (m.matches()) {
            mode = Mode.MONTHLY;
            frequency = Integer.parseInt(m.group(1));
            repeat = m.group(2);
        }
        
        m = P_MD.matcher(event);
        if (m.matches()) {
            mode = Mode.MONTHLY;
            frequency = Integer.parseInt(m.group(1));
            // m.group(2); // Day of the month, can be ignored
            repeat = m.group(3);
        }
        
        m = P_MP.matcher(event);
        if (m.matches()) {
            mode = Mode.MONTHLY_BY_DAY;
            frequency = Integer.parseInt(m.group(1));
            monthlyWeek = Integer.parseInt(m.group(2)) - 1;
            monthlyDay = convertMonthlyDay(m.group(3));
            repeat = m.group(4);
        }
        
        m = P_YEARLY.matcher(event);
        if (m.matches()) {
            mode = Mode.YEARLY;
            frequency = Integer.parseInt(m.group(1));
            // m.group(2); // Month of the year, can be ignored
            repeat = m.group(3);
        }

        ShortDate until = null;

        if (!repeat.isEmpty() && !"#0".equals(repeat)) {
            String[] parts = repeat.split("[;,]");
            until = convertDate(parts[0]);
            for (int ix = 1; ix < parts.length; ix++) {
                record.getExceptions().add(convertDate(parts[ix]));
            }
        }
        
        record.setRepeat(new Repeat(mode, frequency, until, weeklyDays, monthlyWeek, monthlyDay));
    }

    /**
     * Converts a date string to a ShortDate object. Only day, month and year is used.
     * 
     * @param date
     *            Date String
     * @return {@link ShortDate} with the given date
     */
    private ShortDate convertDate(String date) {
        Matcher m = P_TS.matcher(date);
        if (!m.matches()) {
            return null;
        }
        
        return new ShortDate(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
    }

    /**
     * Convert a string of weekly days to an appropriate array.
     * 
     * @param days
     *            String containing weekly days.
     * @return Array with the bits set accordingly
     */
    private boolean[] convertWeeklyDays(String days) {
        boolean[] result = new boolean[7];
        for (int ix = 0; ix < 7; ix++) {
            result[ix] = days.contains(WEEKDAYS[ix]);
        }
        return result;
    }

    /**
     * Converts a monthly weekday to its index.
     * 
     * @param day
     *            Monthly weekday
     * @return Weekday index
     */
    private int convertMonthlyDay(String day) {
        for (int ix = 0; ix < 7; ix++) {
            if (day.contains(WEEKDAYS[ix])) {
                return ix;
            }
        }
        return -1;
    }

}
