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
package org.shredzone.pdbconverter.pdb;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.Date;

import org.shredzone.pdbconverter.pdb.converter.EntryConverter;

/**
 * Opens a PDB file and gives access to its contents.
 * 
 * @author Richard "Shred" Körber
 * @version $Revision: 356 $
 * @see http://membres.lycos.fr/microfirst/palm/pdb.html
 */
public class PdbFile extends RandomAccessFile {

    private static final String CHARSET = "iso-8859-1";
    private static final int NUM_CATEGORIES = 16;
    private static final long EPOCH;    // Timestamp of PalmOS epoch (1904-01-01)
    
    static {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(1904, 0, 1, 0, 0, 0);
        EPOCH = cal.getTimeInMillis();
    }
    
    /**
     * Creates a new {@link PdbFile} for the given {@link File}.
     * 
     * @param file
     *            {@link File} to be opened
     * @throws FileNotFoundException
     */
    public PdbFile(File file) throws FileNotFoundException {
        super(file, "r");
    }

    /**
     * Reads the entire database file and returns a {@link PdbDatabase}. You usually want
     * to invoke this method, as the other methods are just helpers.
     * 
     * @param <T>
     *            {@link Entry} subclass the database shall consist of
     * @param converter
     *            {@link EntryConverter} that converts the raw database entries into
     *            {@link Entry} objects
     * @return {@link PdbDatabase} containing the file contents
     * @throws IOException
     *             The file could not be read. This can have various reasons, for example
     *             if the file was no valid PDB file or the converter was not able to
     *             convert the file's contents.
     */
    public <T extends Entry> PdbDatabase<T> readDatabase(EntryConverter<T> converter)
    throws IOException {
        PdbDatabase<T> result = new PdbDatabase<T>();
    
        // Read the database header
        seek(0);
        result.setName(readTerminatedFixedString(32));
        result.setAttributes(readShort());
        result.setVersion(readShort());
        result.setCreationTime(readDate());
        result.setModificationTime(readDate());
        result.setBackupTime(readDate());
        result.setModificationNumber(readInt());
        int appInfoPos = readInt();
        readInt();                              // Sort offset, usually skipped
        result.setType(readFixedString(4));
        result.setCreator(readFixedString(4));
        readInt();                              // Unique ID seed
        readInt();                              // Next index
        int records = readShort();
        
        // Read the entire record list
        int[] offsets = new int[records];
        byte[] attributes = new byte[records];
        for (int ix = 0; ix < records; ix++) {
            offsets[ix] = readInt();
            attributes[ix] = readByte();
            readByte();
            readShort();
        }
        
        // Read appInfo if available
        if (appInfoPos > 0) {
            seek(appInfoPos);
            
            // This is a bitmask about renamed categories. It is ignored for now.
            readShort();
            
            for (int ix = 0; ix < NUM_CATEGORIES; ix++) {
                String catName = readTerminatedFixedString(16);
                if (catName.length() > 0) {
                    result.getCategories().add(catName);
                }
            }

            for (int ix = 0; ix < NUM_CATEGORIES; ix++) {
                readByte(); // Key
            }
        }
        
        // Ask converter if it accepts the content
        if (!converter.isAcceptable(result)) {
            throw new IOException("Wrong database format");
        }
        
        // Read each record
        for (int ix = 0; ix < records; ix++) {
            if ((attributes[ix] & Entry.ATTR_DELETE) != 0 && offsets[ix] >= length()) {
                // Ignore deleted entries
                continue;
            }
            
            int size;
            if (ix < records - 1) {
                size = offsets[ix + 1] - offsets[ix];
            } else {
                size = ((int) length()) - offsets[ix];
            }

            seek(offsets[ix]);
            T entry = converter.convert(this, size, attributes[ix], result);
            result.getEntries().add(entry);
        }
        
        return result;
    }

    /**
     * Reads a string of a fixed length, not null terminated.
     * 
     * @param length
     *            The length of the string
     * @return String that was read
     */
    public String readFixedString(int length) throws IOException {
        byte[] data = new byte[length];
        readFully(data);
        return convertSpecialChars(new String(data, CHARSET));
    }

    /**
     * Reads a string of a fixed length that is null terminated. The given number of bytes
     * are always read, but the everything including and after the terminator character is
     * ignored.
     * 
     * @param length
     *            The length of the string
     * @return String that was read
     */
    public String readTerminatedFixedString(int length) throws IOException {
        byte[] data = new byte[length];
        readFully(data);
        int pos = 0;
        while (pos < length) {
            if (data[pos] == 0) {
                return new String(data, 0, pos, CHARSET);
            }
            pos++;
        }
        return convertSpecialChars(new String(data, CHARSET));
    }

    /**
     * Reads a string of a variable length that is null terminated.
     * 
     * @return String that was read
     */
    public String readTerminatedString() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        while(true) {
            int ch = readByte();
            if (ch == 0) break;
            baos.write(ch);
        }
        
        return convertSpecialChars(baos.toString(CHARSET));
    }
    
    /**
     * Reads an unsigned integer.
     * 
     * @return Unsigned integer that was read
     */
    public long readUnsignedInt() throws IOException {
        int msb = readUnsignedShort();
        int lsb = readUnsignedShort();
        return ((long) msb) << 16 | lsb;
    }
    
    /**
     * Reads a PalmOS date.
     * 
     * @return Date that was read
     */
    public Date readDate() throws IOException {
        long date = readUnsignedInt();
        return new Date(EPOCH + (date * 1000));
    }

    /**
     * Converts special PalmOS characters into their unicode equivalents. The string
     * methods of {@link PdbFile} will invoke this method by itself, so you usually do not
     * need to invoke it. Note that some very special PalmOS characters cannot be
     * converted and will be kept unchanged.
     * 
     * @param str
     *            String to be converted
     * @return Converted string
     */
    public static String convertSpecialChars(String str) {
        return str
                .replace('\u0018', '\u2026') // Ellipsis
                .replace('\u0019', '\u2007') // Numeric Space
                .replace('\u0080', '\u20AC') // Euro
                .replace('\u0082', '\u201A') // Single Low Quotation Mark
                .replace('\u0083', '\u0192') // Small F with Hook
                .replace('\u0084', '\u201E') // Double Low Quotation Mark
                .replace('\u0085', '\u2026') // Ellipsis
                .replace('\u0086', '\u2020') // Dagger
                .replace('\u0087', '\u2021') // Double Dagger
                .replace('\u0088', '\u0302') // Combining Circumflex Accent
                .replace('\u0089', '\u2030') // Per Mille
                .replace('\u008A', '\u0160') // Capital S with Caron
                .replace('\u008B', '\u2039') // Single Left-pointing Angle Quotation Mark
                .replace('\u008C', '\u0152') // Capital Ligature OE
                .replace('\u008D', '\u2662') // Diamond
                .replace('\u008E', '\u2663') // Club
                .replace('\u008F', '\u2661') // Heart
                .replace('\u0090', '\u2660') // Spade
                .replace('\u0091', '\u2018') // Left Single Quotation Mark
                .replace('\u0092', '\u2019') // Right Single Quotation Mark
                .replace('\u0093', '\u201C') // Left Double Quotation Mark
                .replace('\u0094', '\u201D') // Right Double Quotation Mark
                .replace('\u0095', '\u2219') // Bullet
                .replace('\u0096', '\u2011') // Non-breaking Hyphen
                .replace('\u0097', '\u2012') // Figure Dash
                .replace('\u0098', '\u0303') // Combining Tilde
                .replace('\u0099', '\u2122') // Trademark
                .replace('\u009A', '\u0161') // Small S with Caron
                .replace('\u009B', '\u203A') // Single Right-pointing Angle Quotation Mark
                .replace('\u009C', '\u0153') // Small Ligature OE
                .replace('\u009F', '\u0178') // Capital Y with Diaeresis
                ;
    }

}
