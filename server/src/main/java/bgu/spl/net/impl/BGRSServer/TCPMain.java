package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.Server;

public class TCPMain {
    public static void main(String[] args) {

        Database database = Database.getInstance();
        database.initialize("./Courses.txt");
        Server.threadPerClient(
                7777, //port
                BGRSProtocol::new, //protocol factory
                BGRSMessageEncoderDecoder::new //message encoder decoder factory
        ).serve();

    }
}