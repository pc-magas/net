CLASSPATH=./commons-io-2.4/commons-io-2.4.jar
JARNAME=download
VERSION=1.0

net: Downloader

CountingStream: ./net/CountingStream.java
	javac -cp ${CLASSPATH}:. ./net/CountingStream.java

Downloader: CountingStream ./net/Downloader.java
	javac -cp ${CLASSPATH}:. ./net/Downloader.java

clean:
	rm -rf ./net/*.class
	
jar: net
	jar cvf ${JARNAME}-${VERSION}.jar ./net/*.class ./net/
