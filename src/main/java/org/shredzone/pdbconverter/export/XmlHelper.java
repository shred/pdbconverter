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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import net.shredzone.jshred.io.XMLWriter;

import org.shredzone.pdbconverter.pdb.PdbDatabase;
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo;
import org.shredzone.pdbconverter.pdb.appinfo.CategoryAppInfo.Category;

/**
 * A helper class for writing XML content to the given output stream.
 * <p>
 * This class uses XMLWriter from jshred-util, but can easily be changed to use
 * any other XML writer.
 *
 * @author Richard "Shred" Körber
 */
public class XmlHelper {

    private final SimpleDateFormat dateFmt;
    private XMLWriter xw;

    public XmlHelper() {
        dateFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Opens the XML writer for the given output stream. UTF-8 is used.
     *
     * @param out
     *            {@link OutputStream} to write to
     * @param tag
     *            Name of the topmost container
     * @return {@code this}
     */
    public XmlHelper openXmlWriter(OutputStream out, String tag) throws IOException {
        if (xw != null) {
            throw new IllegalStateException("Writer is already opened");
        }
        xw = new XMLWriter(out);
        xw.startDocument();
        xw.startElement(tag);
        return this;
    }

    /**
     * Starts an element.
     *
     * @param tag
     *            Element name
     * @return {@code this}
     */
    public XmlHelper startElement(String tag) throws IOException {
        xw.startElement(tag);
        return this;
    }

    /**
     * Starts an element with the given attributes.
     *
     * @param tag
     *            Element name
     * @param attr
     *            Attributes, as key/value pairs
     * @return {@code this}
     */
    public XmlHelper startElement(String tag, Object... attr) throws IOException {
        String[] param = new String[attr.length];
        for (int ix = 0; ix < attr.length; ix++) {
            param[ix] = String.valueOf(attr[ix]);
        }
        xw.startElement(tag, param);
        return this;
    }

    /**
     * Writes the content of an element.
     *
     * @param content
     *            Content to be written
     * @return {@code this}
     */
    public XmlHelper writeContent(Object content) throws IOException {
        xw.writeContent(content.toString());
        return this;
    }

    /**
     * Ends the last element on the element stack.
     *
     * @return {@code this}
     */
    public XmlHelper endElement() throws IOException {
        xw.endElement();
        return this;
    }

    /**
     * Writes a value as content of a container named key.
     *
     * @param key
     *            Container key
     * @param value
     *            Value to be written
     * @return {@code this}
     */
    public XmlHelper writeValue(String key, Object value) throws IOException {
        startElement(key);
        writeContent(value.toString());
        endElement();
        return this;
    }

    /**
     * Writes a date as content of a container named key. The date and time is
     * properly RFC 3339 formatted.
     *
     * @param key
     *            Container key
     * @param date
     *            Calendar date to be written
     * @return {@code this}
     * @see <a href="http://www.ietf.org/rfc/rfc3339.txt">RFC 3339</a>
     */
    public XmlHelper writeDate(String key, Calendar date) throws IOException {
        startElement(key);
        writeContent(dateFmt.format(date.getTime()));
        endElement();
        return this;
    }

    /**
     * Writes a formatted string as content of a container named key.
     *
     * @param key
     *            Container key
     * @param format
     *            Format string (see {@link String#format(String, Object...)})
     * @param args
     *            Arguments
     * @return {@code this}
     */
    public XmlHelper writeFormatted(String key, String format, Object... args) throws IOException {
        startElement(key);
        writeContent(String.format(format, args));
        endElement();
        return this;
    }

    /**
     * Writes the informal part of the given database.
     *
     * @param database
     *            Database to be written
     * @return {@code this}
     */
    public XmlHelper writeDatabase(PdbDatabase<?, ?> database) throws IOException {
        writeValue("name", database.getName());
        writeValue("type", database.getType());
        writeValue("creator", database.getCreator());
        writeDate("created", database.getCreationTime());
        writeDate("modified", database.getModificationTime());
        if (database.getBackupTime() != null) {
            writeDate("backup", database.getBackupTime());
        }
        return this;
    }

    /**
     * Writes all categories in a {@link CategoryAppInfo}.
     *
     * @param database
     *            Database to be written
     * @return {@code this}
     */
    public XmlHelper writeCategories(PdbDatabase<?, ? extends CategoryAppInfo> database)
    throws IOException {
        startElement("categories");

        CategoryAppInfo cai = database.getAppInfo();

        List<Category> categories = cai.getCategories();
        for (int ix = 0; ix < categories.size(); ix++) {
            Category cat = categories.get(ix);
            if (cat != null) {
                startElement("category", "id", ix, "key", cat.getKey());
                writeContent(cat.getName());
                endElement();
            }
        }

        endElement();
        return this;
    }

    /**
     * Closes the writer. Note that the {@link OutputStream} will not be closed!
     */
    public void closeXmlWriter() throws IOException {
        xw.endElement();
        xw.endDocument();
        xw.flush();
        xw = null;
    }

}
