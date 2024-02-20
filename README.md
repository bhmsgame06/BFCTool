# BFCTool
Modify SolaRola with this simple thing!

# v0.4 Early access update
Fixes:
* Fixed head.cfg file to normal view.



Updates:
* Added "addbfc" command with parameters;
* Updated file extracting/saving processing. Now console shows percent of progress;
* Filenames self-updates first time using find/findfn commands;
* Updated key names:
  
  SHORT_INT_FILENAME -> SHORT_FN;
  
  TYPE -> RAM_STATE.



# Welcome *walmart bag* and whole world!

BFCTool is the program was designed for BFC files interaction.

BFC file - this is file used in SolaRola game files and this is package of many files (For example "gamelogo.pim" or "level.001.bin").

# Long guide to BFCTool
Start BFCTool pressing twice to jar executable file or start with CMD:
```
java -jar BFCTool.jar
```

Now, you're started BFCTool CMD, that will accepting these simple commands:
```
extract - extracts all files from BFCs;
save - saves files to BFCs;
updatefn - updates a filenames from head.bfc file;
exit - just return to Windows cmd.
```
... and these advanced commands:
```
findfn <filename> - finds and returns an index of specified file;
find <short_int_filename> - same as findfn, but requires short int operand, not string;
encodefn <filename> - encodes a string filename into a short int filename (looks like some type of CRC);
addbfc <new_number> - adds specified BFC file between others.
```

From the execution of the program, if there are no BFCTool data files in **C:/Program Files** directory, program will create BFCTool folder and bfc&ext_bfc subdirectories.

Here's the tree:
```
C:/Program Files/BFCTool
C:/Program Files/BFCTool/bfc
C:/Program Files/BFCTool/ext_bfc
```
In many cases, the BFCTool directory will not be created, due Windows protection or another OS protection is in effect.
Recommended to run CMD as Administrator and run BFCTool.jar with java.exe.

Okay, let's go extract files from BFC files!!!

#### Extracting a files from BFCs
At first, move **head.bfc** header file from SolaRola JAR file to main directory of program **BFCTool** and do same, but move BFCs with numeric filename (*****number***.bfc**) to **bfc** directory. And now, type "extract" to BFCTool CMD! Wait some time...

And see main directory.
Program created **head.cfg** file and this file contains very useful information to modify BFCs.

See **ext_bfc** directory and you will see many directories. Each of those directories contains separate files that ends with *.bin. It's them! Files! You've full ability to edit these files.

Okay, **ext_bfc** has been solved.

Now, go solve **head.cfg** and edit it!

#### head.cfg guide
CFG has default Key&Value type.

CFG file has 2 chunks:
```
HEAD_CHUNK
DATA_CHUNK
```
... and each chunk has each piece (file) and each piece has Key&Value and piece is separated by a blank line between each piece.

'#' symbol before item means annotation item or invisible item.
This means that this item should be ignored by the computer.
The annotation items were invented specifically for people to understand.

HEAD_CHUNK should look something like this:
```
NUM_FILES=*NUMBER*
#OTHER_ITEMS=AT LEAST ONLY ANNOTATIONS
```
Where *NUMBER* is only short int value.

DATA_CHUNK should look something like this:
```
SHORT_FN=*FILENAME*
RAM_STATE=*STATE_OF_FILE_IN_MEMORY*
#OFFSET=*OFFSET_TO_FILE_IN_BFC*
LOCATION=*LOCATION*
#SIZE=*SIZE_OF_FILE*
PATH=*PATH_TO_BIN_FILE*
```

Where *FILENAME* is only short int encoded filename;

*STATE_OF_FILE_IN_MEMORY* is only byte type and means write file to RAM or not (if >= 0 { write_to_RAM_for_long_use; } else { delete_from_RAM; });

*OFFSET_TO_FILE_IN_BFC* is int offset to specified file in BFC file;

*LOCATION* is only byte int and contains a number of BFC file (***LOCATION*.bfc**);

*SIZE_OF_FILE* is int size in bytes;

*PATH_TO_BIN_FILE* is string type and contains path from **ext_bfc** directory (for example **5.bfc/10.bin**).



#### Adding a own file to BFC file
For example we want to add a tiny picture without PPL palette: **PITR.pim**.
Just move this PIM file to any BFC folder, for example the file path: **10.bfc/PITR.pim**
Type to BFCTool CMD that command:
```
encodefn PITR.pim
```
... and you'll get ***27014*** short int value.

#### Editing head.cfg:
Add to NUM_FILES 1 (NUM_FILES++).

Add piece of file:
```
SHORT_FN=27014
RAM_STATE=1
LOCATION=10
PATH=10.bfc/PITR.pim
```
... and make sure you created a blank lines at borders of piece.

Adding **#OFFSET** and **#SIZE** annotation items is optional. Do you want to add or not - your choice.

Type "save" command to BFCTool CMD.

And... PITR.pim file has been saved to 10.bfc.
Congrats!

# Conclusion
I hope you will be enjoying my program and you will be thankful to me!

If you find a **bug**, tell me on Discord: BHms game#9794.

Have luck and enjoy! ^⁠_⁠^
