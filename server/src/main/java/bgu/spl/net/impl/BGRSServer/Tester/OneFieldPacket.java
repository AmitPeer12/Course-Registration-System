package bgu.spl.net.impl.BGRSServer.Tester;

import java.nio.ByteBuffer;

public class OneFieldPacket extends Packet {
    private byte [] opCode;
    private byte [] field;

    public OneFieldPacket(byte [] opCode, byte [] field){
        this.opCode = opCode;
        this.field  = field;
    }

    ByteBuffer getPacketBytes(){
        ByteBuffer packetBytes = ByteBuffer.allocate(field.length+2);
        for (byte b : opCode) packetBytes.put(b);
        for (byte b : field ) packetBytes.put(b);
        return packetBytes;
    }
}