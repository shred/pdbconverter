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

import org.shredzone.commons.pdb.appinfo.CategoryAppInfo;
import org.shredzone.commons.pdb.converter.Converter;
import org.shredzone.commons.pdb.converter.TodoConverter;
import org.shredzone.commons.pdb.record.TodoRecord;
import org.shredzone.pdbconverter.export.Exporter;
import org.shredzone.pdbconverter.export.TodoXmlExporter;

/**
 * {@link ExportHandler} that reads a Todo pdb file and writes an XML file.
 *
 * @author Richard "Shred" Körber
 */
public class TodoXmlHandler extends AbstractCategoryExportHandler<TodoRecord, CategoryAppInfo> {

    @Override
    public String getName() {
        return "todo";
    }

    @Override
    public String getDescription() {
        return "ToDoDB to XML";
    }

    @Override
    protected Converter<TodoRecord, CategoryAppInfo> createConverter() {
        return new TodoConverter();
    }

    @Override
    protected Exporter<TodoRecord, CategoryAppInfo> createExporter() {
        return new TodoXmlExporter();
    }

}
