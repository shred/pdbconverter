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

import org.shredzone.pdbconverter.export.Exporter;
import org.shredzone.pdbconverter.export.ScheduleExporter;
import org.shredzone.pdbconverter.mdb.ScheduleMdbReader;
import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo;
import org.shredzone.pdbconverter.pdb.converter.Converter;
import org.shredzone.pdbconverter.pdb.record.ScheduleRecord;

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
        ScheduleMdbReader reader = new ScheduleMdbReader();
        try {
            reader.open(infile);
            return reader.read();
        } finally {
            reader.close();
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
