package bgu.spl.net.impl.BGRSServer.Tester;

import java.util.Scanner;

public class MyTests {
    public static void main(String [] args)
    {
        int numOfClients = 1;
        ClientHandler cHandler = new ClientHandler("localhost",7777,numOfClients);
        cHandler.initiateClients();

        Scanner in = new Scanner(System.in);
        cHandler.processSpecificCommandOnClient(cHandler.getClients().get(0),"STUDENTREG", "student1" ,"123");
        cHandler.processSpecificCommandOnClient(cHandler.getClients().get(0),"LOGIN", "student1" ,"123");
        cHandler.processSpecificCommandOnClient(cHandler.getClients().get(0),"COURSEREG", "1","");
        cHandler.processSpecificCommandOnClient(cHandler.getClients().get(0),"MYCOURSES", "student1","");
    }
}
