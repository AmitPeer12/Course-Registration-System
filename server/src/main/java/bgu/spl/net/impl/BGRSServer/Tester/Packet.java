package bgu.spl.net.impl.BGRSServer.Tester;

import java.nio.ByteBuffer;

public abstract class Packet {
    abstract ByteBuffer getPacketBytes();
}