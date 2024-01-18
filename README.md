# BFCTool
Modify SolaRola with this thing!

This program was designed for BFC files interaction.

BFC file - this is file used in SolaRola game files and this is package of many files (For example "gamelogo.pim" or "level.001.bin").

# Long guide to BFCTool
Start BFCTool pressing twice to jar executable file or start with cmd:
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
