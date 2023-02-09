JAVASRC		= NQueens.java
SOURCES		= Makefile $(JAVASRC)
MAINCLASS	= NQueens
CLASSES		= NQueens.class
JARFILE		= NQueens.jar

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

