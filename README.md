# BFCTool
Modify SolaRola with this simple thing!

This program was designed for BFC files interaction.

BFC file - this is file used in SolaRola game files and this is package of many files (For example "gamelogo.pim" or "level.001.bin").

# Long guide to BFCTool
Start BFCTool pressing twice to jar executable file or start with CMD:
```
java -jar BFCTool.jar
```

Now, you're started BFCTool cmd, that will accepting these simple commands:
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
encodefn <filename> - encodes a string filename into a short int filename (looks like some type of CRC).
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
At first, replace **head.bfc** header file to main directory of program (**C:/Program Files/BFCTool**) and do same, but with BFCs with numeric filename (*****number***.bfc**).
