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
package org.shredzone.pdbconverter.handler;

import java.io.File;
import java.io.IOException;

import org.shredzone.commons.pdb.PdbDatabase;
import org.shredzone.commons.pdb.appinfo.CategoryAppInfo;
import org.shredzone.commons.pdb.converter.Converter;
import org.shredzone.commons.pdb.record.ScheduleRecord;
import org.shredzone.pdbconverter.export.Exporter;
import org.shredzone.pdbconverter.export.ScheduleExporter;
import org.shredzone.pdbconverter.mdb.ScheduleMdbReader;

/**
 * {@link ExportHandler} that reads a DateBook.mdb and writes an iCalendar file.
 *
 * @author Richard "Shred" Körber
 */
public class MdbICalendarHandler extends AbstractCategoryExportHandler<ScheduleRecord, CategoryAppInfo> {

    @Override
    public String getName() {
        return "iCalendar-mdb";
    }

    @Override
    public String getDescription() {
        return "DateBook.mdb to iCalendar";
    }

    @Override
    protected PdbDatabase<ScheduleRecord, CategoryAppInfo> readDatabase(File infile) throws IOException {
        try (ScheduleMdbReader reader = new ScheduleMdbReader()) {
            reader.open(infile);
            return reader.read();
        }
    }

    @Override
    protected Converter<ScheduleRecord, CategoryAppInfo> createConverter() {
        // Does not use a converter
        return null;
    }

    @Override
    protected Exporter<ScheduleRecord, CategoryAppInfo> createExporter() {
        return new ScheduleExporter();
    }

}
