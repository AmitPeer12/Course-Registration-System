#include "../include/Writer.h"
#include <string>

//Constructor
Writer::Writer(ConnectionHandler &connectionHandler) :
        opcodes(std::map<string, int>()),
        _connectionHandler(connectionHandler),
        messageParts(std::vector<string>()) {
    opcodes.insert({"ADMINREG", 1});
    opcodes.insert({"STUDENTREG", 2});
    opcodes.insert({"LOGIN", 3});
    opcodes.insert({"LOGOUT", 4});
    opcodes.insert({"COURSEREG", 5});
    opcodes.insert({"KDAMCHECK", 6});
    opcodes.insert({"COURSESTAT", 7});
    opcodes.insert({"STUDENTSTAT", 8});
    opcodes.insert({"ISREGISTERED", 9});
    opcodes.insert({"UNREGISTER", 10});
    opcodes.insert({"MYCOURSES", 11});
}

void Writer::run(string &input) {
    //split the message into words and determine type using the opcodes map, then act accordingly
    boost::split(messageParts, input, boost::is_any_of(" "));
    short opcode = opcodes.at(messageParts.at(0));
    switch (opcode) {
        case 1:
        case 2:
        case 3:
            type1(opcode);
            break;
        case 4:
        case 11:
            type4(opcode);
            break;
        case 5:
        case 6:
        case 7:
        case 9:
        case 10:
            type2(opcode);
            break;
        case 8:
            type3(opcode);
            break;
        default:
            std::cout << "Illegale Argument" << std::endl;
    }
}

//messages with 2 strings, send the opcode and then the 2 strings one by one
void Writer::type1(short opcode) {
    char code[2];
    shortToBytes(opcode, code);
    _connectionHandler.sendBytes(code, 2);
    _connectionHandler.sendFrameAscii(messageParts.at(1), '\0');
    _connectionHandler.sendFrameAscii(messageParts.at(2), '\0');
}

//messages with opcode and another short, send 4 bytes, 2 for each short
void Writer::type2(short opcode) {
    char toSend[4];
    shortToBytes(opcode,toSend);
    shortToBytes(stoi(messageParts.at(1)),toSend,2);
    _connectionHandler.sendBytes(toSend, 4);
}

//messages with 1 string, send the opcode and then the string one by one
void Writer::type3(short opcode) {
    char code[2];
    shortToBytes(opcode, code);
    _connectionHandler.sendBytes(code, 2);
    _connectionHandler.sendFrameAscii(messageParts.at(1), '\0');
}

//messages with only an opcode, send 2 bytes for that short
void Writer::type4(short opcode) {
    char toSend[2];
    shortToBytes(opcode,toSend);
    _connectionHandler.sendBytes(toSend, 2);
}

void Writer::shortToBytes(short num, char *bytesArr, int index) {
    bytesArr[index] = ((num >> 8) & 0xFF);
    bytesArr[index + 1] = (num & 0xFF);
}