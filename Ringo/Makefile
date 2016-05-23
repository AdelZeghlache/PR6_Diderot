SRC = src/Ringo/
BIN = bin/Ringo
JC = javac
JC_FLAGS = -g
JVM = java
JVM_FLAGS = -classpath $(BIN)
BINARY = entite1
RM_FLAGS = rm -rf

default: $(SRC)*.java
	$(JC) $(JC_FLAGS) $(SRC)*.java
	mv $(SRC)*.class $(BIN)

new: clean default

run: $(BIN)$(BINARY).class
	$(JVM) $(JVM_FLAGS) $(BINARY)

$(BIN)$(BINARY).class: $(SRC)*.java
	make new

clean:
	$(RM_FLAGS) $(BIN)*.class
