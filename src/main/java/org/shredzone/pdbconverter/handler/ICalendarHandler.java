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

import org.shredzone.pdbconverter.export.Exporter;
import org.shredzone.pdbconverter.export.ScheduleExporter;
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo;
import org.shredzone.pdbconverter.pdb.converter.Converter;
import org.shredzone.pdbconverter.pdb.converter.ScheduleConverter;
import org.shredzone.pdbconverter.pdb.record.ScheduleRecord;

/**
 * {@link ExportHandler} that reads Calendar pdb and writes an iCalendar file.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 490 $
 */
public class ICalendarHandler extends AbstractCategoryExportHandler<ScheduleRecord, CategoryAppInfo> {

    @Override
    public String getName() {
        return "iCalendar";
    }

    @Override
    public String getDescription() {
        return "CalendarDB to iCalendar";
    }

    @Override
    protected Converter<ScheduleRecord, CategoryAppInfo> createConverter() {
        return new ScheduleConverter();
    }
    
    @Override
    protected Exporter<ScheduleRecord, CategoryAppInfo> createExporter() {
        return new ScheduleExporter();
    }

}
