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
package org.shredzone.pdbconverter.export;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Dur;
import net.fortuna.ical4j.model.Recur;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Categories;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.UidGenerator;

import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.Schedule;
import org.shredzone.pdbconverter.pdb.Schedule.Alarm;
import org.shredzone.pdbconverter.pdb.Schedule.Repeat;
import org.shredzone.pdbconverter.pdb.Schedule.ShortDate;
import org.shredzone.pdbconverter.pdb.Schedule.ShortTime;

/*
 * NOTE TO THE READER:
 *   This class uses ical4j for writing iCalendar output. ical4j uses classes that
 *   are named like standard JDK classes, so take care when reading the source code.
 *   For example, "Date" is not what you might expect it to be.
 */

/**
 * Writes a {@link Schedule} database as iCalender file.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 360 $
 * @see http://wiki.modularity.net.au/ical4j/
 */
public class ScheduleExporter implements Exporter<Schedule> {
    
    private static final WeekDay[] WEEKDAYS = {
        WeekDay.SU, WeekDay.MO, WeekDay.TU, WeekDay.WE, WeekDay.TH, WeekDay.FR, WeekDay.SA,
    };

    /**
     * Writes the {@link Schedule} database as iCalendar to the given {@link PrintStream}.
     * iCalendar support is pretty good! It copes with the entire schedule database.
     * 
     * @param database
     *            {@link Schedule} {@link PdbDatabase} to write
     * @param out
     *            {@link PrintStream} to write to
     */
    public void export(PdbDatabase<Schedule> database, PrintStream out) throws IOException {
        UidGenerator uidGenerator = new UidGenerator("uidGen");

        net.fortuna.ical4j.model.Calendar calendar = new net.fortuna.ical4j.model.Calendar();
        calendar.getProperties().add(new ProdId("-//Shredzone.org/pdbconverter 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        for (Schedule schedule : database.getEntries()) {
            VEvent event = createVEvent(schedule);
            event.getProperties().add(uidGenerator.generateUid());
            calendar.getComponents().add(event);
        }

        out.println(calendar);
    }

    /**
     * Creates a new {@link VEvent} for a single {@link Schedule}.
     * 
     * @param schedule
     *            {@link Schedule} to be exported
     * @return {@link VEvent} containing the {@link Schedule}
     */
    private VEvent createVEvent(Schedule schedule) {
        VEvent result = new VEvent();

        if (schedule.getStartTime() == null && schedule.getEndTime() == null) {
            // all-day event
            setAllDaySchedule(result, schedule);
            
        } else {
            // event with starting and ending time
            setSchedule(result, schedule);
        }

        setAlarm(result, schedule);
        setRepeat(result, schedule);
        setDescription(result, schedule);
        setLocation(result, schedule);
        setNote(result, schedule);
        setCategory(result, schedule);
        
        return result;
    }

    /**
     * Sets the schedule data for a standard calendar entry with definite starting and
     * ending time.
     * 
     * @param event
     *            {@link VEvent} to write to
     * @param schedule
     *            {@link Schedule} to read from
     */
    private void setSchedule(VEvent event, Schedule schedule) {
        Calendar startDate = convertDateTime(schedule.getSchedule(), schedule.getStartTime());
        Calendar endDate   = convertDateTime(schedule.getSchedule(), schedule.getEndTime());
        
        // If ending time is before starting time, add one day to make it end tomorrow
        if (endDate.before(startDate)) {
            endDate.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        event.getProperties().add(new DtStart(new DateTime(startDate.getTime())));
        event.getProperties().add(new DtEnd(new DateTime(endDate.getTime())));
    }
    
    /**
     * Sets the schedule data for an all-day event.
     * 
     * @param event
     *            {@link VEvent} to write to
     * @param schedule
     *            {@link Schedule} to read from
     */
    private void setAllDaySchedule(VEvent event, Schedule schedule) {
        Calendar startDate = convertDate(schedule.getSchedule());
        event.getProperties().add(new DtStart(new Date(startDate.getTime())));
    }

    /**
     * Sets the alarm data, if given.
     * 
     * @param event
     *            {@link VEvent} to write to
     * @param schedule
     *            {@link Schedule} to read from
     */
    private void setAlarm(VEvent event, Schedule schedule) {
        Alarm alarm = schedule.getAlarm();
        if (alarm != null) {
            int before = -alarm.getValue();

            Dur dur = null;
            switch (alarm.getUnit()) {
                case MINUTES: dur = new Dur(0, 0, before, 0); break;
                case HOURS:   dur = new Dur(0, before, 0, 0); break;
                case DAYS:    dur = new Dur(before, 0, 0, 0); break;
                default: throw new IllegalStateException("unknown alarm unit " + alarm.getUnit());
            }
            
            VAlarm valarm = new VAlarm(dur);
            valarm.getProperties().add(Action.DISPLAY);
            event.getAlarms().add(valarm);
        }
    }

    /**
     * Sets the repetition data, if given.
     * 
     * @param event
     *            {@link VEvent} to write to
     * @param schedule
     *            {@link Schedule} to read from
     */
    private void setRepeat(VEvent event, Schedule schedule) {
        Repeat repeat = schedule.getRepeat();
        if (repeat != null) {
            Date until = null;
            if (repeat.getUntil() != null) {
                until = new Date(convertDate(repeat.getUntil()).getTime());
            }

            Recur recur = null;
            switch (repeat.getMode()) {
                case DAILY:
                    recur = new Recur(Recur.DAILY, until);
                    break;
                    
                case WEEKLY:
                    recur = new Recur(Recur.WEEKLY, until);
                    boolean[] repeatWeekDays = repeat.getWeeklyDays();
                    for (int ix = 0; ix < repeatWeekDays.length; ix++) {
                        if (repeatWeekDays[ix]) {
                            recur.getDayList().add(WEEKDAYS[ix]);
                        }
                    }
                    break;

                case MONTHLY:
                    recur = new Recur(Recur.MONTHLY, until);
                    break;
                
                case MONTHLY_BY_DAY:
                    recur = new Recur(Recur.MONTHLY, until);
                    WeekDay wd;
                    int week = repeat.getMonthlyWeek();
                    if (week == 4) {
                        // Last week in month
                        wd = new WeekDay(WEEKDAYS[repeat.getMonthlyDay()], -1);
                    } else {
                        // Any other week, starting from 1
                        wd = new WeekDay(WEEKDAYS[repeat.getMonthlyDay()], week + 1);
                    }
                    recur.getDayList().add(wd);
                    break;
                    
                case YEARLY:
                    recur = new Recur(Recur.YEARLY, until);
                    break;
                    
                default:
                    throw new IllegalStateException("unknown repeat mode " + repeat.getMode());
            }

            if (repeat.getFrequency() > 1) {
                recur.setInterval(repeat.getFrequency());
            }
            
            event.getProperties().add(new RRule(recur));
            setExceptions(event, schedule);
        }
    }
    
    /**
     * Sets exceptions to a repeating event.
     * 
     * @param event
     *            {@link VEvent} to write to
     * @param schedule
     *            {@link Schedule} to read from
     */
    private void setExceptions(VEvent event, Schedule schedule) {
        if (!schedule.getExceptions().isEmpty()) {
            DateList datelist = new DateList(Value.DATE);
            for (ShortDate exception : schedule.getExceptions()) {
                datelist.add(new Date(convertDate(exception).getTime()));
            }
            event.getProperties().add(new ExDate(datelist));
        }
    }
    
    /**
     * Sets the description, if given.
     * 
     * @param event
     *            {@link VEvent} to write to
     * @param schedule
     *            {@link Schedule} to read from
     */
    private void setDescription(VEvent event, Schedule schedule) {
        String summary = schedule.getDescription();
        if (summary != null) {
            event.getProperties().add(new Summary(summary));
        }
    }

    /**
     * Sets the location, if given.
     * 
     * @param event
     *            {@link VEvent} to write to
     * @param schedule
     *            {@link Schedule} to read from
     */
    private void setLocation(VEvent event, Schedule schedule) {
        String location = schedule.getLocation();
        if (location != null) {
            event.getProperties().add(new Location(location));
        }
    }

    /**
     * Sets the note, if given.
     * 
     * @param event
     *            {@link VEvent} to write to
     * @param schedule
     *            {@link Schedule} to read from
     */
    private void setNote(VEvent event, Schedule schedule) {
        String note = schedule.getNote();
        if (note != null) {
            event.getProperties().add(new Description(note));
        }
    }

    /**
     * Sets the category, if given.
     * 
     * @param event
     *            {@link VEvent} to write to
     * @param schedule
     *            {@link Schedule} to read from
     */
    private void setCategory(VEvent event, Schedule schedule) {
        String cat = schedule.getCategory();
        if (cat != null) {
            event.getProperties().add(new Categories(cat));
        }
    }

    /**
     * Converts a {@link ShortDate} to a {@link Calendar} object.
     * 
     * @param date
     *            {@link ShortDate} to be converted
     * @return {@link Calendar} containing the date only
     */
    private Calendar convertDate(ShortDate date) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(date.getYear(), date.getMonth() - 1, date.getDay());
        return cal;
    }

    /**
     * Converts a {@link ShortDate} and {@link ShortTime} to a {@link Calendar} object.
     * 
     * @param date
     *            {@link ShortDate} to be converted
     * @param time
     *            {@link ShortTime} to be converted
     * @return {@link Calendar} containing the date and time
     */
    private Calendar convertDateTime(ShortDate date, ShortTime time) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(date.getYear(), date.getMonth() - 1, date.getDay(), time.getHour(), time.getMinute());
        return cal;
    }
    
}
