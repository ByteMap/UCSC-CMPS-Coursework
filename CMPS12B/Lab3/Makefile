JAVASRC		= FileReverse.java
SOURCES		= Makefile $(JAVASRC)
MAINCLASS	= FileReverse
CLASSES		= FileReverse.class
JARFILE		= FileReverse.jar

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
