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
package org.shredzone.pdbconverter.pdb.record;

import java.util.Date;

import org.shredzone.pdbconverter.pdb.Record;


/**
 * An {@link Record} implementation that contains a Notepad entry.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 367 $
 */
public class NotepadEntry extends Record {

    private String title;
    private Date created;
    private Date modified;
    private Date alarm;
    private byte[] imagePng;
    
    /**
     * Creates a new {@link NotepadEntry}.
     * 
     * @param attribute
     *            Record attribute
     */
    public NotepadEntry(byte attribute) {
        super(attribute);
    }

    /**
     * Get the title of the note. May be {@code null}.
     */
    public String getTitle()                    { return title; }
    public void setTitle(String title)          { this.title = title; }

    /**
     * Get the date and time when the note was created.
     */
    public Date getCreated()                    { return created; }
    public void setCreated(Date created)        { this.created = created; }

    /**
     * Get the date and time when the note was modified. May be {@code null}.
     */
    public Date getModified()                   { return modified; }
    public void setModified(Date modified)      { this.modified = modified; }

    /**
     * Get the date and time of the notepad alarm. {@code null} when no alarm is set.
     */
    public Date getAlarm()                      { return alarm; }
    public void setAlarm(Date alarm)            { this.alarm = alarm; }
    
    /**
     * Gets the image data. This is a PNG file.
     */
    public byte[] getImagePng()                 { return imagePng; }
    public void setImagePng(byte[] imagePng)    { this.imagePng = imagePng; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Notepad:[");
        sb.append("created=").append(created);
        if (modified != null) {
            sb.append(" modified=").append(modified);
        }
        if (title != null) {
            sb.append(" title='").append(title).append('\'');
        }
        if (alarm != null) {
            sb.append(" alarm=").append(alarm);
        }
        sb.append(" image-png=").append(imagePng.length).append(" bytes]");
        return sb.toString();
    }
    
}