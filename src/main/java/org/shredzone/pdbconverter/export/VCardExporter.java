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
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.EnumMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.appinfo.AddressAppInfo;
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo.Category;
import org.shredzone.pdbconverter.pdb.record.AddressRecord;
import org.shredzone.pdbconverter.pdb.record.AddressRecord.Field;
import org.shredzone.pdbconverter.pdb.record.AddressRecord.Label;

/**
 * Writes an {@link AddressRecord} database as vCard file.
 * <p>
 * Note that the exporter tries its best to generate a reasonable vCard file. Anyhow
 * the database allows a lot of free text, so there is some guesswork to do and the
 * result may be invalid and data is lost.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 490 $
 * @see http://tools.ietf.org/html/rfc2426
 */
public class VCardExporter extends AbstractExporter<AddressRecord, AddressAppInfo> {

    private static final int MAX_LINE_LENGTH = 73;
    
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[^@]+\\@[0-9a-z.-]+", Pattern.CASE_INSENSITIVE);
    
    private static final Pattern URL_PATTERN =
            Pattern.compile("(ftp|https?)\\:\\/\\/.*", Pattern.CASE_INSENSITIVE);

    private Pattern bdayPattern = null;
    private EnumMap<Label, PhoneType> phoneMap = new EnumMap<Label, PhoneType>(Label.class);

    /**
     * Creates a new {@link VCardExporter} with common settings.
     */
    public VCardExporter() {
        phoneMap.put(Label.PHONE1, PhoneType.WORK);
        phoneMap.put(Label.PHONE2, PhoneType.HOME);
        phoneMap.put(Label.PHONE3, PhoneType.FAX);
        phoneMap.put(Label.PHONE7, PhoneType.PAGER);
        phoneMap.put(Label.PHONE8, PhoneType.CELL);
    }
    
    /**
     * Sets a regular expression for birthday detection. If a custom field matches this
     * pattern, it is exported as birthday. Group index 1 must be the year, group index
     * 2 the month and group index 3 the day, all of them numerical.
     */
    public void setBirthdayPattern(Pattern bdayPattern) {
        this.bdayPattern = bdayPattern;
    }
    
    /**
     * Sets a custom {@link PhoneType} for the given {@link Label}. If a phone
     * number with this label is exported, the given {@link PhoneType} is set.
     * 
     * @param label
     *            {@link Label}, must be one of PHONE, others are ignored
     * @param type
     *            {@link PhoneType} containing the new type of phone, or {@code
     *            null} to export a generic TEL line.
     */
    public void setLabelType(Label label, PhoneType type) {
        if (type != null) {
            phoneMap.put(label, type);
        } else {
            phoneMap.remove(label);
        }
    }
    
    /**
     * Writes the {@link AddressRecord} database as vCard to the given
     * {@link OutputStream}.
     * 
     * @param database
     *            {@link AddressRecord} {@link PdbDatabase} to write
     * @param out
     *            {@link OutputStream} to write to
     */
    @Override
    public void export(PdbDatabase<AddressRecord, AddressAppInfo> database, OutputStream out)
    throws IOException {
        AddressAppInfo appInfo = database.getAppInfo();

        PrintStream ps = new PrintStream(out, false, "UTF-8");
        
        for (AddressRecord address : database.getRecords()) {
            if (isAccepted(address)) {
                writeVCard(address, appInfo, ps);
            }
        }
        
        ps.flush();
    }
    
    /**
     * Writes a single VCard block.
     * 
     * @param address
     *            {@link AddressRecord} to be written
     * @param appInfo
     *            {@link AddressAppInfo} containing further data
     * @param ps
     *            {@link PrintStream} to write to
     */
    private void writeVCard(AddressRecord address, AddressAppInfo appInfo, PrintStream ps) {
        ps.print("BEGIN:VCARD\r\n");
        ps.print("VERSION:3.0\r\n");
//        ps.print("PRODID:-//Shredzone.org/pdbconverter 1.0//EN\r\n");
        
        if (address.isSecret()) {
            ps.print("CLASS:CONFIDENTIAL\r\n");
        }

        writeName(address, ps);
        writeOrg(address, ps);
        writeAdr(address, ps);

        int pref = address.getDisplayPhone();
        
        writePhone(address.getField(Field.PHONE1), address.getLabel(Field.PHONE1), pref == 0, ps);
        writePhone(address.getField(Field.PHONE2), address.getLabel(Field.PHONE2), pref == 1, ps);
        writePhone(address.getField(Field.PHONE3), address.getLabel(Field.PHONE3), pref == 2, ps);
        writePhone(address.getField(Field.PHONE4), address.getLabel(Field.PHONE4), pref == 3, ps);
        writePhone(address.getField(Field.PHONE5), address.getLabel(Field.PHONE5), pref == 4, ps);
        
        writeCustom(address.getField(Field.CUSTOM1), ps);
        writeCustom(address.getField(Field.CUSTOM2), ps);
        writeCustom(address.getField(Field.CUSTOM3), ps);
        writeCustom(address.getField(Field.CUSTOM4), ps);
        
        writeCategory(address, appInfo, ps);
        writeNote(address, ps);
        
        ps.print("END:VCARD\r\n");
    }

    /**
     * Writes the VCard name lines. This are 'N' and 'FN'. These lines must be written,
     * so there is some guesswork in case the respective fields were not set.
     * 
     * @param address
     *            {@link AddressRecord} to be written
     * @param ps
     *            {@link PrintStream} to write to
     */
    private void writeName(AddressRecord address, PrintStream ps) {
        String first = address.getField(Field.FIRST_NAME);
        String name = address.getField(Field.NAME);
        if (name == null) {
            name = address.getField(Field.COMPANY);
            if (name != null) {
                first = null;
            }
        }
        if (name == null) {
            if (first != null) {
                name = first;
                first = null;
            } else {
                name = "?";
            }
        }
        
        if (first != null) {
            writeLine(ps, "N", null, name, first);
            writeLine(ps, "FN", null, first + " " + name);
        } else {
            writeLine(ps, "N", null, name);
            writeLine(ps, "FN", null, name);
        }
    }

    /**
     * Writes the VCard organisation.
     * 
     * @param address
     *            {@link AddressRecord} to be written
     * @param ps
     *            {@link PrintStream} to write to
     */
    private void writeOrg(AddressRecord address, PrintStream ps) {
        String org = address.getField(Field.COMPANY);
        if (org != null) {
            writeLine(ps, "ORG", null, org);
        }
        
        String title = address.getField(Field.TITLE);
        if (title != null) {
            writeLine(ps, "TITLE", null, title);
        }
    }

    /**
     * Writes the VCard address.
     * 
     * @param address
     *            {@link AddressRecord} to be written
     * @param ps
     *            {@link PrintStream} to write to
     */
    private void writeAdr(AddressRecord address, PrintStream ps) {
        String addr = address.getField(Field.ADDRESS);
        String city = address.getField(Field.CITY);
        String state = address.getField(Field.STATE);
        String zip = address.getField(Field.ZIP);
        String country = address.getField(Field.COUNTRY);
        
        if (addr != null || city != null || state != null || zip != null || country != null) {
            writeLine(ps, "ADR", null, null, null, addr, city, state, zip, country);
        }
    }

    /**
     * Writes a phone number.
     * 
     * @param value
     *            Phone number to be written. Nothing is written if this is
     *            {@code null}.
     * @param label
     *            Respective label of this phone number
     * @param pref
     *            Preferred phone
     * @param ps
     *            {@link PrintStream} to write to
     */
    private void writePhone(String value, Label label, boolean pref, PrintStream ps) {
        if (value == null) return;
        
        if (EMAIL_PATTERN.matcher(value).matches()) {
            writeLine(
                ps,
                "EMAIL",
                "TYPE=INTERNET" + (pref ? ",PREF" : ""),
                value
            );
            
        } else if (URL_PATTERN.matcher(value).matches()) {
            writeLine(ps, "URL", null, value);
            
        } else {
            String type = "";
            PhoneType pt = phoneMap.get(label);
            if (pt != null) {
                type = pt.name();
            }
            if (pref) {
                if (type.length() > 0) type += ',';
                type += "PREF";
                
            }
            if (type.length() > 0) {
                writeLine(ps, "TEL", "TYPE=" + type, value);
            } else {
                writeLine(ps, "TEL", null, value);
            }
        }
    }

    /**
     * Writes a custom field. EMAIL, URL or BDAY is written if the respective patterns
     * were recognized. Otherwise a NOTE is written, which could lead to a vCard having
     * more than one NOTE.
     * 
     * @param value
     *            Custom valur to be written. Nothing is written if this is
     *            {@code null}.
     * @param ps
     *            {@link PrintStream} to write to
     */
    private void writeCustom(String value, PrintStream ps) {
        if (value == null) return;
        
        Matcher m;
        
        if (EMAIL_PATTERN.matcher(value).matches()) {
            writeLine(ps, "EMAIL", "TYPE=INTERNET", value);
            
        } else if (URL_PATTERN.matcher(value).matches()) {
            writeLine(ps, "URL", null, value);
            
        } else if (bdayPattern != null && (m = bdayPattern.matcher(value)).matches()) {
            int year  = Integer.parseInt(m.group(1));
            int month = Integer.parseInt(m.group(2));
            int day   = Integer.parseInt(m.group(3));
            writeLine(ps, "BDAY", null, String.format("%04d-%02d-%02d", year, month, day));
            
        } else {
            writeLine(ps, "NOTE", null, value);
        }
    }
    
    /**
     * Writes the Category.
     * 
     * @param address
     *            {@link AddressRecord} to be written
     * @param appInfo
     *            {@link AddressAppInfo} carrying the category information
     * @param ps
     *            {@link PrintStream} to write to
     */
    private void writeCategory(AddressRecord address, AddressAppInfo appInfo, PrintStream ps) {
        int catKey = address.getCategoryIndex();
        Category category = appInfo.getCategoryByIndex(catKey);
        if (category != null) {
            writeLine(ps, "CATEGORIES", null, category.getName());
        }
    }

    /**
     * Writes the Note.
     * 
     * @param address
     *            {@link AddressRecord} to be written
     * @param ps
     *            {@link PrintStream} to write to
     */
    private void writeNote(AddressRecord address, PrintStream ps) {
        String note = address.getField(Field.NOTE);
        if (note != null) {
            writeLine(ps, "NOTE", null, note);
        }
    }

    /**
     * Writes a single vCard line. Cares for proper line breaks and escaping.
     * 
     * @param ps
     *            {@link PrintStream} to write to
     * @param property
     *            line property
     * @param parameter
     *            optional property parameters, not escaped (can be {@code null})
     * @param values
     *            values of this property
     */
    private void writeLine(PrintStream ps, String property, String parameter, String... values) {
        StringBuilder sb = new StringBuilder();
        sb.append(property);
        if (parameter != null) {
            sb.append(';').append(parameter);
        }
        sb.append(':');
        for (int ix = 0; ix < values.length; ix++) {
            if (ix > 0) sb.append(';');
            if (values[ix] != null) {
                sb.append(escape(values[ix]));
            }
        }
        
        int pos = MAX_LINE_LENGTH;
        while (pos < sb.length()) {
            sb.insert(pos-1, "\r\n ");
            pos += MAX_LINE_LENGTH + 1;
        }

        sb.append("\r\n");
        ps.print(sb.toString());
    }

    /**
     * Escapes all characters that need to be escaped.
     * 
     * @param str
     *            String to escape
     * @return Escaped string
     */
    private String escape(String str) {
        return str.replace("\\", "\\\\")    // Must be first!
                  .replace(",", "\\,")
                  .replace(";", "\\;")
                  .replace("\n", "\\n");
    }
    
    /**
     * Types of vCard phones.
     */
    public static enum PhoneType {
        HOME, WORK, VOICE, FAX, CELL, VIDEO, MSG, PAGER, BBS, MODEM, CAR, ISDN, PCS;
    }

}
