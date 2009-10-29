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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.shredzone.pdbconverter.pdb.appinfo.AppInfo;
import org.shredzone.pdbconverter.pdb.record.Record;

/**
 * Represents the contents of a PDB database file.
 *
 * @author Richard "Shred" Körber
 * @version $Revision: 369 $
 */
public class PdbDatabase<T extends Record, U extends AppInfo> {
    public static final int ATTR_RESDB = 0x0001;
    public static final int ATTR_READONLY = 0x0002;
    public static final int ATTR_APPINFODIRTY = 0x0004;
    public static final int ATTR_BACKUP = 0x0008;
    public static final int ATTR_OKINSTALLNEWER = 0x0010;
    public static final int ATTR_RESET = 0x0020;
    public static final int ATTR_COPYPREVENTION = 0x0040;
    public static final int ATTR_STREAM = 0x0080;
    public static final int ATTR_HIDDEN = 0x0100;
    public static final int ATTR_LAUNCHABLE_DATA = 0x0200;
    public static final int ATTR_RECYCLABLE = 0x0400;
    public static final int ATTR_BUNDLE = 0x0800;
    public static final int ATTR_OPEN = 0x8000;
    
    private String name;
    private int attributes;
    private int version;
    private Date creationTime;
    private Date modificationTime;
    private Date backupTime;
    private int modificationNumber;
    private String type;
    private String creator;
    private U appInfo;
    private List<T> records = new ArrayList<T>();

    /**
     * Gets the database name (for example "CalendarDB-PDat").
     */
    public String getName()             { return name; }
    public void setName(String name)    { this.name = name; }
    
    /**
     * Gets the attributes of the database. See ATTR constants, which are or'ed.
     */
    public int getAttributes()          { return attributes; }
    public void setAttributes(int attributes) { this.attributes = attributes; }

    /**
     * Gets the database version.
     */
    public int getVersion()             { return version; }
    public void setVersion(int version) { this.version = version; }

    /**
     * Gets the creation time of the database. Should not be {@code null}.
     */
    public Date getCreationTime()       { return creationTime; }
    public void setCreationTime(Date creationTime) { this.creationTime = creationTime; }

    /**
     * Gets the modification time of the database. Should not be {@code null}.
     */
    public Date getModificationTime()   { return modificationTime; }
    public void setModificationTime(Date modificationTime) { this.modificationTime = modificationTime; }

    /**
     * Gets the backup time of the database. Is {@code null} if the database has not
     * been backed up yet.
     */
    public Date getBackupTime()         { return backupTime; }
    public void setBackupTime(Date backupTime) { this.backupTime = backupTime; }
    
    /**
     * Gets the modification number.
     */
    public int getModificationNumber()  { return modificationNumber; }
    public void setModificationNumber(int modificationNumber) { this.modificationNumber = modificationNumber; }

    /**
     * Gets the database type.
     */
    public String getType()             { return type; }
    public void setType(String type)    { this.type = type; }

    /**
     * Gets the database creator. For example "PDAT".
     */
    public String getCreator()          { return creator; }
    public void setCreator(String creator) { this.creator = creator; }

    /**
     * Gets the {@link AppInfo} of this database. {@code null} if no appinfo area was
     * available.
     */
    public U getAppInfo()               { return appInfo; }
    public void setAppInfo(U appInfo)   { this.appInfo = appInfo; }

    /**
     * Gets all records of this database.
     */
    public List<T> getRecords()         { return records; }
        
}
