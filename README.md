# Kanto compiler
## A kanto code compiler

To compile a code you must first add the TXT with the desired code in the "/res/" folder (the examples are already loaded in this folder). 
Finally, in the "scanner" package, the "TokenScanner" class must be modified and the "init_filename" attribute must be modified. Once this is done, only the Main class must be executed. 
The generated ".ASM" will be located in the root folder of the project, i.e. "/main.asm".

If, on the other hand, you want to run the tests, you must execute the "Testing" class, inside the "testing" package. This will run a series of tests consecutively, 
These tests are located in the "src/testing/tests/" folder. The results obtained from the compilations are stored in "src/testing/results/". 
To add tests, just add the file to the "src/testing/tests/" folder.

Two libraries located in the "/libs/" folder must be added. These libraries allow to create jsons of the used data structures. 
These data structures are stored in the "/stats/" folder. The trees can be visualized using the online tool "https://vanya.jp.net/vtree/". 
Additionally, compile time statistics are generated, stored in the same folder as the jsons and in a file called "stats.txt".

If the online tool mentioned above is used, it is important to note that the nodes joined by a horizontal line are actually siblings.

## Versions
--= IntelliJ =--
SDK -> corretto- 15 Amazon Corretto version 15.0.2
Language level -> 17
Build #IU-213.6777.52, built on January 28, 2022
Runtime version: 11.0.13+7-b1751.25 amd64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.

--= Mars =--
Mars 4.5