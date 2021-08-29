
#ifndef ASSIGNMENT3_OUTPUTREADER_H
#define ASSIGNMENT3_OUTPUTREADER_H

#include <map>
#include <iostream>
#include <cassert>
#include <condition_variable>
#include "ConnectionHandler.h"

class Reader {
private:
    bool terminate = false;
    const std::string &_host;
    int _port;
    ConnectionHandler &_connectionHandler;
    std::mutex &_mutex;
    std::condition_variable &_condition;

public:
    Reader(const std::string &host, int port, ConnectionHandler &connectionHandler, std::mutex &_mutex,
           std::condition_variable &_condition);

    bool shouldTerminate() const;

    void run();

private:
    static short bytesToShort(const char *bytesArr, int index = 0);

};


#endif //ASSIGNMENT3_OUTPUTREADER_H