This software was originally coded, developed, and designed by HP Truong.

I, Langdon Staab, fixed some issues, trimmed it down a bit, moved it to Java 21 (from Java 8).

I removed many unnecessary dependencies and libraries, added proper licensing notices to source files I tweaked.

I fixed some bugs, improved startup tie considerably, and added a standardized HTTP response code system.

The project is licensed almost entirely under the Apache License, version 2.0.

Some libraries are MIT licensed, as are 6 source files I created myself (with inspiration from HP Truong's original code).

The 6 source files licensed under the MIT license are:
AbstractSimpleHandler.java
AbstractGETHandler.java
AbstractPOSTHandler.java
AbstractComplexGETHandler.java
GetEditedSourceHandler.java
ActionEditSourceCode.java

Check the git commit history on the GitHub repo for details of who coded what.
The source code is freely available, provided you comply with the terms of the license.
If you choose to fork this code and improve it, I ask that you make your changes public,
 so others can benefit as you have benefited from my small changes and additions.


===========================================================================================================================
        HOW TO RUN Repeat
===========================================================================================================================

Run the Jar file with a Java runtime.
1. Open a terminal (Called "Terminal" on macOS and Ubuntu Linux, "cmd" on Windows)
2. Type java --version
3. The output should look like this (this specific output is for Java 21.0.7 on Ubuntu 24.04 LTS):
openjdk 21.0.7 2025-04-15 LTS
OpenJDK Runtime Environment Temurin-21.0.7+6 (build 21.0.7+6-LTS)
OpenJDK 64-Bit Server VM Temurin-21.0.7+6 (build 21.0.7+6-LTS, mixed mode, sharing)
4. If the version number is less than 21, you need to install/download Java 21 or higher,
because this program only runs on Java 21 or higher. If you are on a non-Windows OS,
I recommend you install SDKMan! (follow the steps on sdkman.io),
then install Java by typing "sdk install java" in Terminal.
5. Open a terminal and type "java -jar ", then drag the JAr file you downloaded into the terminal, and press enter.
6. An icon labeled "Repeat" should appear in the system try of your computer desktop, click it to open Repeat;
 if no tray icon appears, open "localhost:8080" in a browser to open Repeat.

TROUBLESHOOTING:
If Repeat does not open, ensure your Java runtime is the correct version and is properly installed.
Repeat cannot move the mouse on Wayland; mouse buttons and keypresses can be controlled via Repeat on all systems.
Additional Notes:
You may add RAM flags to increase performance, though is not at all necessary, as this program consumes almost no resources.
java -XX:+AlwaysPreTouch -Xms64M -Xmx64M -jar Repeat.jar
Hover your mouse over a button to see what it does.

