# PdbConverter

_PdbConverter_ is a tool for converting PalmOS PDB files into more common file formats.

![Screenshot](./src/site/resources/img/screenshot.png)

## DISCONTINUED

I will not make updates or bugfixes to this project anymore. It is still functional, so if you need to convert old PalmOS PDB files, you can download the latest release and run it (see [Usage](#usage) section below).

For you Retro Developers out there, there is a separate Java library called [commons-pdb](https://codeberg.org/shred/commons-pdb) that enables you to read different kind of PDB files. This library is still maintained by me, and even supports Android.

## Features

* Calender can be converted to iCalendar and imported into most calendar applications.
* Address can be converted to vCard and imported into many address books.
* Also supports Memo, ToDo and Notepad.
* Also supports `DateBook.mdb` files.
* All other PDBs can be converted into a ZIP file containing the raw record data, for further processing.
* Optional category filter and separate export of each category.
* Developers can use PdbConverter to parse PDB files in their own application, just by adding a Maven dependency.
* Completely written in Java, and runs on Linux, MacOS X and Windows. Also usable as Android library (untested).

## Usage

Please install [Java](http://java.com), then download the latest precompiled jar from [Codeberg](https://codeberg.org/shred/pdbconverter/releases/) and run it:

```
java -jar pdbconverter.jar --gui
```

See the [online documentation](http://www.shredzone.org/maven/pdbconverter/) for further details.

## License

_PdbConverter_ is distributed under [Gnu Public License v3](http://www.gnu.org/licenses/gpl-3.0.html).
