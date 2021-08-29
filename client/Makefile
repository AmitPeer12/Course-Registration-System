# All Targets

LDFLAGS:=-lboost_system -lboost_thread -pthread

all: bin/Client

bin/Client: bin/BGRSClient.o bin/ConnectionHandler.o bin/Reader.o bin/Writer.o
	@echo 'Building target: Client'
	@echo 'Invoking: C++ Linker'
	g++ -Wall -Weffc++ -o bin/Client bin/BGRSClient.o bin/ConnectionHandler.o bin/Reader.o bin/Writer.o $(LDFLAGS)
	@echo 'Finished building target: Client'
	@echo ' '

bin/BGRSClient.o: src/BGRSClient.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/BGRSClient.o src/BGRSClient.cpp

bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/Reader.o: src/Reader.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Reader.o src/Reader.cpp

bin/Writer.o: src/Writer.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Writer.o src/Writer.cpp


clean:
	rm bin/BGRSClient.o bin/ConnectionHandler.o bin/Reader.o bin/Writer.o bin/Client $(objects)
