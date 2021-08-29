#include <thread>
#include <mutex>
#include "../include/Reader.h"

Reader::Reader(const std::string &host, int port, ConnectionHandler &connectionHandler, std::mutex &mutex,
               std::condition_variable &condition) :
        _host(host),
        _port(port),
        _connectionHandler(connectionHandler),
        _mutex(mutex),
        _condition(condition) {}

bool Reader::shouldTerminate() const {
    return terminate;
}

void Reader::run() {
    while (!terminate) {
        //read the first 4 bytes that represent ACK / ERR and the responded opcode
        char bytes[4];
        if (!_connectionHandler.getBytes(bytes, 4)) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }
        bool answer = (bytesToShort(bytes) == 12);
        short answerOpcode = bytesToShort(bytes, 2);

        //if we received conformation for 'logout' terminate and notify the other thread
        if (answerOpcode == 4) {
            std::unique_lock<std::mutex> lock(_mutex);
            terminate = answer;
            _condition.notify_all();
        }
        if (answer) {
            //if received ACK it might be accompanied by info to print, read it
            std::string info;
            if (!_connectionHandler.getFrameAscii(info, '\0')) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            std::cout << "ACK " << std::to_string(answerOpcode) << info << std::endl;

        } else
            std::cout << "ERR " << std::to_string(answerOpcode) << std::endl;
    }
}

short Reader::bytesToShort(const char *bytesArr, int index) {
    auto result = (short) ((bytesArr[index] & 0xff) << 8);
    result += (short) (bytesArr[index + 1] & 0xff);
    return result;
}