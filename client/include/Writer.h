
#ifndef ASSIGNMENT3_INPUTREADER_H
#define ASSIGNMENT3_INPUTREADER_H

#include <map>
#include <iostream>
#include <cassert>
#include <string>
#include <vector>
#include <cstdlib>
#include "../include/ConnectionHandler.h"

using std::string;

class Writer {
    std::map<std::string, int> opcodes;
    ConnectionHandler &_connectionHandler;
    std::vector<string> messageParts;

public:
    explicit Writer(ConnectionHandler &connectionHandler);

    void run(string &input);


private:
    void type1(short opcode);

    void type2(short opcode);

    void type3(short opcode);

    void type4(short opcode);

    static void shortToBytes(short num, char *bytesArr, int index = 0);
};

#endif //ASSIGNMENT3_INPUTREADER_H