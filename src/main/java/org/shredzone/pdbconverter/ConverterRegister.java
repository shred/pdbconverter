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
package org.shredzone.pdbconverter;

import org.shredzone.pdbconverter.handler.AddressXmlHandler;
import org.shredzone.pdbconverter.handler.ExportHandler;
import org.shredzone.pdbconverter.handler.ICalendarHandler;
import org.shredzone.pdbconverter.handler.MdbICalendarHandler;
import org.shredzone.pdbconverter.handler.MemoXmlHandler;
import org.shredzone.pdbconverter.handler.NotepadHandler;
import org.shredzone.pdbconverter.handler.TodoXmlHandler;
import org.shredzone.pdbconverter.handler.VCardHandler;
import org.shredzone.pdbconverter.handler.ZipHandler;

/**
 * A register of all available {@link ExportHandler}.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 523 $
 */
public final class ConverterRegister {
    
    private static final ExportHandler[] HANDLERS = {
        new AddressXmlHandler(),
        new ICalendarHandler(),
        new MdbICalendarHandler(),
        new MemoXmlHandler(),
        new NotepadHandler(),
        new TodoXmlHandler(),
        new VCardHandler(),
        new ZipHandler(),
    };
    
    /**
     * Utility class cannot be constructed.
     */
    private ConverterRegister() {}

    /**
     * Gets all registered {@link ExportHandler}.
     * 
     * @return Array of {@link ExportHandler}
     */
    public static ExportHandler[] getHandlers() {
        return HANDLERS;
    }
    
    /**
     * Finds the {@link ExportHandler} for the given converter name.
     * 
     * @param converter
     *            Converter name
     * @return {@link ExportHandler} or {@code null} if there is none.
     */
    public static ExportHandler findHandler(String name) {
        for (ExportHandler handler : HANDLERS) {
            if (handler.getName().equalsIgnoreCase(name)) {
                return handler;
            }
        }
        return null;
    }

}
