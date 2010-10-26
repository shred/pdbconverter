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

/**
 * A {@link Record} implementation that contains a Todo record.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 491 $
 */
public class TodoRecord extends AbstractRecord implements DatedRecord {

    private Date date;
    private int priority;
    private boolean completed;
    private String description;
    private String note;
    
    /**
     * Creates a new {@link MemoRecord}.
     * 
     * @param attribute
     *            Record attribute
     */
    public TodoRecord(byte attribute) {
        super(attribute);
    }
    
    /**
     * Gets the due date. The time part is always set to midnight local time.
     */
    public Date getDate()                       { return date; }
    public void setDate(Date date)              { this.date = date; }

    /**
     * Gets the priority.
     */
    public int getPriority()                    { return priority; }
    public void setPriority(int priority)       { this.priority = priority; }

    /**
     * Checks if this todo is completed.
     */
    public boolean isCompleted()                { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    /**
     * Gets the description.
     */
    public String getDescription()              { return description; }
    public void setDescription(String description) { this.description = description; }

    /**
     * Gets the note.
     */
    public String getNote()                     { return note; }
    public void setNote(String note)            { this.note = note; }

    @Override
    public Date getRecordDate() {
        return getDate();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Todo:[");

        sb.append(completed ? "complete" : "open");
        
        if (date != null) {
            sb.append(" date=").append(date);
        }
        
        sb.append(" priority=").append(priority);
        
        if (description != null) {
            sb.append(" description='").append(description).append('\'');
        }
        
        if (note != null) {
            sb.append(" note='").append(note).append('\'');
        }

        sb.append(']');
        
        return sb.toString();
    }
    
}
