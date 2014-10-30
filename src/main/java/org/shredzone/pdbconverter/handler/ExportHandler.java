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

import java.io.File;
import java.io.IOException;

/**
 * Interface for a handler that takes care for reading and exporting.
 *
 * @author Richard "Shred" Körber
 */
public interface ExportHandler {

    /**
     * Gets the name of the handler.
     */
    String getName();

    /**
     * Gets a description about the input and output file format.
     */
    String getDescription();

    /**
     * Exports the given infile to the outfile.
     *
     * @param infile
     *            input pdb file
     * @param outfile
     *            output file name
     * @param options
     *            {@link ExportOptions} with further parameters
     */
    void export(File infile, File outfile, ExportOptions options) throws IOException;

}
