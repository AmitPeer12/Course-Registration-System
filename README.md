# Course-Registration-System
Course Registration system implemented as client server communication. Server implemented as TPC and Reactor.

Project instructions: https://docs.google.com/document/d/e/2PACX-1vRn6esmt4SNU4tU-iStPERIEJQPV4SSmBtIvmGEPWCvT0GzaTSpkjkp4u1hOtaGHg/pub

In the instructions you can find as well the opcodes to operate the system (such as Admin registration/login, student registration/login/ course registration, course info and more..)

When running the program you can choose if you want the server to work in Reactor way and choose the number of threads, or TPC( Thread Per Client).

The communication between the server and the client is performed using a binary communication protocol.

Server written in Java and Client in C++.

How to Operate:

1) (in Client directory) Make sure bin is empty and : make

2) (in Server directory) mvn clean

3) (in Server directory) mvn compile

4) (in Server directory) for running Reactor Server: mvn exec:java -Dexec.mainClass=”bgu.spl.net.impl.BGRSServer.ReactorMain” -Dexec.args=”-port- -No of threads-” (we used port 7777)

For running TPC Server: mvn exec:java -Dexec.mainClass=”bgu.spl.net.impl.BGRSServer.TPCMain” -Dexec.args=”-port-”

Should get a message "Server started"

5) (in Client directory) bin/BGRSclient -ip- -port- (we used 127.0.0.1 ip and 7777 port).

Should get a message "Starting connect to -ip-:-port- "

6) (in Client directory) Now start run commands from the opcodes as described in the project description, after every request should get ACK -opcode- for successful operation and ERROR -opcode- for failed operation.
