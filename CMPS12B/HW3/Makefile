JAVASRC		= Balanced.java Stack.java Node.java StackInterface.java StackException.java
SOURCES		= Makefile $(JAVASRC)
MAINCLASS	= Balanced
CLASSES		= Balanced.class Stack.class Node.class StackInterface.class StackException.class
JARFILE		= Balanced.jar

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