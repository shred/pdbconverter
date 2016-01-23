# How to convert Calendar to ICalendar

So, you finally replaced your good old Palm with a new cool smartphone? And now you wonder how you could easily move all your schedules to the new device? You have found the solution!

Basically you'll need to do three steps in order to move your schedule database to any iCalendar compliant calendar.

1. Get a copy of the Calendar database of your Palm
2. Convert the database file to an iCalendar file
3. Import the iCalendar file in your new calendar

Let me give an example of how I moved my Palm calendar to an Android phone.

## Extract the database

There are two ways to get the Calendar database.

If you use the HotSync Manager, it's pretty easy. In your PalmOS Desktop directory, you will find a file called `DateBook.mdb`. This is the file you need.

If you don't use HotSync Manager, it's a little more tricky. What you need is a file called `CalendarDB-PDat.pdb`.

What I did was inserting a SD card into my Palm. Then I installed a tool called [_FileZ_](https://www.google.com?q=FileZ+PalmOS). With _FileZ_ it was rather easy to locate the _Internal_ file `CalendarDB-PDat`, mark it and copy it to the SD card.

After that, I put the SD card into a card reader, and _voilà_, there is the file I need for the next step.

## Convert the database

The MDB or PDB file has a proprietary PalmOS file format. Other tools are unable to read those files, so we have to convert them into something more common. For calendars, the [iCalendar](http://en.wikipedia.org/wiki/ICalendar) file format is a standard that can be read by nearly any calendar application.

Using the PdbConverter, we convert the PDB into an iCalendar file in a few seconds:

```
java -jar PdbConverter.jar -c icalendar-mdb -i DateBook.mdb -o CalendarDB.ics
```

Of course you can also use the GUI to convert the file.

`CalendarDB.ics` is a standard iCalendar file that contains all of your Palm's schedules.

PdbConverter does its job pretty well. Virtually all of your calendar data is converted into the iCalendar format. Your secret entries will even be marked as `PRIVATE`. If you miss some details in your calendar application after importing the iCalendar file, it is rather likely that your application is unable to fully handle the generated iCalendar file.

## Importing the schedules

In a final step, the iCalendar file needs to be imported. I opened the Google Calendar, and created a new calendar called "Palm". This is because I want to keep the Palm schedules separate from the schedules of my standard Google Calendar. If the import turns out to be faulty, I just need to delete that calendar and start again. It is also possible to import the iCalendar file to the standard calendar immediately. But be warned that you would have to manually delete every single Palm schedule from your calendar if something went wrong.

In the settings area of Google Calendar, there is an option to import a calendar file. In the subsequent dialog, I select the `CalendarDB.ics` file, and chose the "Palm" calendar as target.

The upload process can take quite some time, so don't be alarmed if the new entries are not shown immediately. Anyhow, after a couple of minutes your Palm schedules should show up in the Google Calendar. Congratulations! You just migrated your calendar.

On my Android phone, I triggered a calendar synchronization. It takes a few minutes to complete. After that, I chose the "Palm" calendar, and my Palm schedules finally appeared on my Android phone.

## No categories?

Google Calendar ignores the categories of your schedules, which means that all your categories are effectively lost during migration. There is a way to simulate the categories, though. If you enable the "Split Categories" option (or pass `--split` at the command line), every category is exported into a separate file.

Now you can create a new calendar for every category, pick the color you prefer, and import the respective iCalendar file.

## Need a CSV file instead?

PdbConverter only offers to generate an iCalendar file, because it wants to convert as much of your data as ever possible. A CSV file is rather limited, and data loss will occur. Anyhow if you really need a CSV file, you will find converters in the interwebs that convert iCalendar files to CSV files. The Google Calendar is also able to export a calendar as CSV file.
