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

import java.io.File;
import java.util.List;

import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.PdbFile;
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo;
import org.shredzone.pdbconverter.pdb.converter.ScheduleConverter;
import org.shredzone.pdbconverter.pdb.record.Schedule;

/**
 * An example for reading calendar PDBs.
 * 
 * @author Richard "Shred" Körber
 * @version $Revision: 367 $
 */
public class CalendarReaderExample {

    /**
     * Reads a Calendar PDB and outputs its content to stdout.
     * 
     * @param args
     *            First argument must be the name of the calendar PDB file
     */
    public static void main(String[] args) {
        try {
            File file = new File(args[0]);

            PdbFile pdb = new PdbFile(file);
            PdbDatabase<Schedule, CategoryAppInfo> database =
                pdb.readDatabase(new ScheduleConverter());
            pdb.close();

            System.out.printf("Name: %s\n", database.getName());

            List<String> cats = database.getAppInfo().getCategories();
            for (int ix = 0; ix < cats.size(); ix++) {
                System.out.printf("Category %d: %s\n", ix, cats.get(ix));
            }

            for (Schedule entry : database.getEntries()) {
                System.out.println(entry);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
