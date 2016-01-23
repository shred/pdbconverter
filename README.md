# PdbConverter

_PdbConverter_ is a too for converting PalmOS PDB files into more common file formats.

![Screenshot](./src/site/resources/img/screenshot.png)

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

Just run the compiled jar file:

```
java -jar target/pdbconverter.jar --gui
```

See the [online documentation](http://www.shredzone.org/maven/pdbconverter/) for further details.

I have been providing a WebStart version and also a converter web site for some years. These sites have been closed because even though there were hundreds of visitors every month, not a single one felt obliged to give a small donation for it.

## License

_PdbConverter_ is distributed under [Gnu Public License v3](http://www.gnu.org/licenses/gpl-3.0.html).
