JAVASRC		= SlugFest.java Customer.java Queue.java QueueException.java QueueInterface.java Node.java
SOURCES		= Makefile $(JAVASRC)
MAINCLASS	= SlugFest
CLASSES		= SlugFest.class Customer.class Queue.class QueueException.class QueueInterface.class Node.class
JARFILE		= SlugFest.jar

all: $(JARFILE)

$(JARFILE): $(CLASSES)
	echo Main-class: $(MAINCLASS) > Manifest
	jar cvfm $(JARFILE) Manifest $(CLASSES)
	rm Manifest
	chmod +x $(JARFILE)

$(CLASSES): $(JAVASRC)
	javac -Xlint $(JAVASRC)

clean:
	rm $(CLASSES) $(JARFILE)