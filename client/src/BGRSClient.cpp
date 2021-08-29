#include <cstdlib>
#include <thread>
#include <mutex>
#include <condition_variable>
#include "../include/ConnectionHandler.h"
#include "../include/Writer.h"
#include "../include/Reader.h"

int main(int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " _host _port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port;
    port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect())
        return 1;

    //this lock will make sure that a upon sending 'logout' we wait for a response before terminating or continuing
    std::mutex mutex;
    std::condition_variable condition;

    //the second thread that is responsible for reading
    Reader reader(host, port, connectionHandler, mutex, condition);
    std::thread readerThread(&Reader::run, &reader);

    Writer writer(connectionHandler);
    while (!reader.shouldTerminate()) {
        string line;
        std::getline(std::cin, line);
        writer.run(line);
        //if sent 'logout' wait until it was acknowledged / rejected before continuing
        if (line == "LOGOUT") {
            std::unique_lock<std::mutex> lock(mutex);
            condition.wait(lock);
        }
    }
    readerThread.join();
    return 0;
}