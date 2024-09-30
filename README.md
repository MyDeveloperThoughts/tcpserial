# TCPSerial Modem Emulator
TCPSerial is a program that provides an Emulated Hayes modem on a TCP Port.   
This software should run on any computer that has a Java runtime environment v21 or greater available.  

I created this project because I wanted an easy way to call Commodore bulletin board systems from the VICE emulator on my Windows 11 computer.  
## License
This code is under the [GNU General Public License, version 2](https://www.gnu.org/licenses/old-licenses/gpl-2.0.html) license.

## Documentation
[Quick Start Guide for VICE and Windows](docs/QuickStartWithVICEAndWindows.md)  
[ Emulated Hayes commands and registers ](docs/HayesCommands.md)

## tcpser4J origins and project goals
This project's code was created from the excellent source code in the project https://github.com/go4retro/tcpser4j (by Jim Brain and Brain Innovations, 2004,2005).

The goals for this project:
* Runs on Java 21 or greater (TCPSer4J was written in Java 1.2)
* No platforms specific dependencies (No need for cygwin1.dll, rxtxSerial.ddl or librxtxSerial.so)
* Provide a simple 'unzip and run' experience for users.

The following are the changes I made to the original code base:
* Recreated a new project and converted the code in tcpser4J one class and file at a time. 
* Introduced Apache Maven as the build tool
* The project is IDE agnostic.  There are no IDE specific configuration files in this project.
* Changed the Logging API from log4j-1.2.5.jar to Java.Util.Logging that is included in the JDK
* Changed the XML Parser from dom4j-full.jar to the Streaming API for XML (StAX) that is included in the JDK
* Removed dependency on RXTXComm.jar.  This is native code to work with Serial Ports.  https://mvnrepository.com/artifact/org.rxtx/rxtx
  * The result is removed support for modems of type "RS232" (Uses a real serial port on the PC).
* Simplified the config.xml for more casual usage for users.
* Updated the source code to use Generics, Try with Resources, MultiCatch and many other features introduced into Java since version 1.2.


