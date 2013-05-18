Java library for downloading a file over http protocol (the one that web uses).

You just need to import

net.Downloader;

Then you need to make the downloader

Downlowder d = new Downloader("url","path to save the file");

(Remember to replace the url and path to save the file with correct strings of url and file)

Then yoy can get stull like mime and the progress as percentage;

It can download onje file so you need multiple downloaders to download multiple files.

NOTICE:
The library has a depedency on Commons-io. Wi the library is shipped too and commons-io-2.4 (the latest stable that author found).
For newer versions you just need to download the new version and compile having the classpath to show the .jar file that corresponds into newer version of commons-io jar file.

You can find commons-io at:
http://commons.apache.org/proper/commons-io/

----------------------------------------------Compoling------------------------------------------------------

For unix like Systems there is a Makefile just type make jar to compile and make your library as jar just type:

make jar

If you have a newer version of commons io download it and do any neessasery extracts if it is into copresed for (except .jar files) and then chacne CLASSPATH variable of makefile in order to point into the latest version of commons-io.

For windows like joy need to excecute over cmd

javac -cp <path that is commons-io-<version>.jar> net/*.java

For any problems contact with me at: ddesyllas@yahoo.gr
