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

import org.junit.Assert;
import org.junit.Test;
import org.shredzone.commons.pdb.record.ScheduleRecord;
import org.shredzone.commons.pdb.record.ScheduleRecord.ShortDate;
import org.shredzone.pdbconverter.mdb.ScheduleMdbReader.RepeatConverter;

/**
 * Unit tests for the ScheduleMdbReader.
 *
 * @author Richard "Shred" Körber
 */
public class ScheduleMdbReaderTest {

    @Test
    public void convertTest() {
        ScheduleRecord record;

        record = new ScheduleRecord(0);
        RepeatConverter.convert("D1 19990524T000000Z", record);
        Assert.assertEquals("Schedule:[null repeat=DAILY-1-until:1999-05-24]", record.toString());

        record = new ScheduleRecord(0);
        RepeatConverter.convert("D1 19980911T000000Z;19980910T000000Z", record);
        Assert.assertEquals("Schedule:[null repeat=DAILY-1-until:1998-09-11 exceptions={1998-09-10,}]", record.toString());

        record = new ScheduleRecord(0);
        RepeatConverter.convert("D1 19980911T000000Z,19980910T000000Z,19980830T000000Z", record);
        Assert.assertEquals("Schedule:[null repeat=DAILY-1-until:1998-09-11 exceptions={1998-09-10,1998-08-30,}]", record.toString());

        record = new ScheduleRecord(0);
        RepeatConverter.convert("W1 TH 20051208T000000Z", record);
        Assert.assertEquals("Schedule:[null repeat=WEEKLY-1-until:2005-12-08-on:Th]", record.toString());

        record = new ScheduleRecord(0);
        RepeatConverter.convert("W1 TU WE TH FR 20090807T070000Z", record);
        Assert.assertEquals("Schedule:[null repeat=WEEKLY-1-until:2009-08-07-on:Tu:We:Th:Fr]", record.toString());

        record = new ScheduleRecord(0);
        RepeatConverter.convert("W1 TU TH 20090730T070000Z", record);
        Assert.assertEquals("Schedule:[null repeat=WEEKLY-1-until:2009-07-30-on:Tu:Th]", record.toString());

        record = new ScheduleRecord(0);
        RepeatConverter.convert("M1 20060211T000000Z", record);
        Assert.assertEquals("Schedule:[null repeat=MONTHLY-1-until:2006-02-11]", record.toString());

        record = new ScheduleRecord(0);
        RepeatConverter.convert("MD3 24 20040723T000000Z", record);
        Assert.assertEquals("Schedule:[null repeat=MONTHLY-3-until:2004-07-23]", record.toString());

        record = new ScheduleRecord(0);
        RepeatConverter.convert("MP12 2+ SU #0", record);
        Assert.assertEquals("Schedule:[null repeat=MONTHLY_BY_DAY-12-week:1-day:Su]", record.toString());

        record = new ScheduleRecord(0);
        RepeatConverter.convert("MP1 5+ FR #0", record);
        Assert.assertEquals("Schedule:[null repeat=MONTHLY_BY_DAY-1-week:4-day:Fr]", record.toString());

        record = new ScheduleRecord(0);
        RepeatConverter.convert("YM1 10 19911020T000000Z", record);
        Assert.assertEquals("Schedule:[null repeat=YEARLY-1-until:1991-10-20]", record.toString());

        record = new ScheduleRecord(0);
        RepeatConverter.convert("YM1 12 #0", record);
        Assert.assertEquals("Schedule:[null repeat=YEARLY-1]", record.toString());
    }

    @Test
    public void convertWeeklyDaysTest() {
        boolean[] result;

        result = RepeatConverter.convertWeeklyDays("SU");
        assertBooleanArrayEquals(new boolean[] { true, false, false, false, false, false, false }, result);

        result = RepeatConverter.convertWeeklyDays("WE");
        assertBooleanArrayEquals(new boolean[] { false, false, false, true, false, false, false }, result);

        result = RepeatConverter.convertWeeklyDays("th");
        assertBooleanArrayEquals(new boolean[] { false, false, false, false, true, false, false }, result);

        result = RepeatConverter.convertWeeklyDays("SA");
        assertBooleanArrayEquals(new boolean[] { false, false, false, false, false, false, true }, result);

        result = RepeatConverter.convertWeeklyDays("SU TU FR");
        assertBooleanArrayEquals(new boolean[] { true, false, true, false, false, true, false }, result);

        result = RepeatConverter.convertWeeklyDays("SU MO TU WE TH FR SA");
        assertBooleanArrayEquals(new boolean[] { true, true, true, true, true, true, true }, result);

        result = RepeatConverter.convertWeeklyDays("     TU    SU, FR   ");
        assertBooleanArrayEquals(new boolean[] { true, false, true, false, false, true, false }, result);

        result = RepeatConverter.convertWeeklyDays("SU - TU - PI - FR");
        assertBooleanArrayEquals(new boolean[] { true, false, true, false, false, true, false }, result);

        result = RepeatConverter.convertWeeklyDays("");
        assertBooleanArrayEquals(new boolean[] { false, false, false, false, false, false, false }, result);

        result = RepeatConverter.convertWeeklyDays("PI");
        assertBooleanArrayEquals(new boolean[] { false, false, false, false, false, false, false }, result);
    }

    @Test
    public void convertMonthlyDayTest() {
        int result;

        result = RepeatConverter.convertMonthlyDay("SU");
        Assert.assertEquals(0, result);

        result = RepeatConverter.convertMonthlyDay("WE");
        Assert.assertEquals(3, result);

        result = RepeatConverter.convertMonthlyDay("th");
        Assert.assertEquals(4, result);

        result = RepeatConverter.convertMonthlyDay("SA");
        Assert.assertEquals(6, result);

        result = RepeatConverter.convertMonthlyDay("    SA  ");
        Assert.assertEquals(6, result);

        result = RepeatConverter.convertMonthlyDay("PI");
        Assert.assertEquals(-1, result);

        result = RepeatConverter.convertMonthlyDay("");
        Assert.assertEquals(-1, result);

        result = RepeatConverter.convertMonthlyDay("WE FR");
        Assert.assertEquals(-1, result);
    }

    @Test
    public void convertDateTest() {
        ShortDate result;

        result = RepeatConverter.convertDate("20090730T070000Z");
        assertShortDateEquals(2009, 07, 30, result);

        result = RepeatConverter.convertDate("19991121T000000Z");
        assertShortDateEquals(1999, 11, 21, result);

        result = RepeatConverter.convertDate("19991121T");
        Assert.assertNull(result);

        result = RepeatConverter.convertDate("foobar");
        Assert.assertNull(result);

        result = RepeatConverter.convertDate("");
        Assert.assertNull(result);
    }

    private void assertBooleanArrayEquals(boolean[] expected, boolean[] actual) {
        Assert.assertEquals(expected.length, actual.length);
        for (int ix = 0; ix < expected.length; ix++) {
            Assert.assertEquals("index " + ix, expected[ix], actual[ix]);
        }
    }

    private void assertShortDateEquals(int year, int month, int day, ShortDate date) {
        Assert.assertNotNull(date);
        Assert.assertEquals("year", year, date.getYear());
        Assert.assertEquals("month", month, date.getMonth());
        Assert.assertEquals("day", day, date.getDay());
    }

}
