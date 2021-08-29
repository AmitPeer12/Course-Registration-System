package bgu.spl.net.impl.BGRSServer.Tester;

import java.util.Scanner;

public class TestsMain {

    public static void main(String [] args)
    {
        int numOfClients = 25;
        ClientHandler cHandler = new ClientHandler("localhost",7777,numOfClients);
        cHandler.initiateClients();

        Scanner in = new Scanner(System.in);
        System.out.println("MultiThreaded Tester....Have Fun\r\n");
        new Thread(new GeneralTests(cHandler,numOfClients)).start();

    }
}