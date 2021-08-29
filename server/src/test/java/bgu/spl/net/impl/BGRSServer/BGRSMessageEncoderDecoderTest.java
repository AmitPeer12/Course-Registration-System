package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BGRSMessageEncoderDecoderTest {

    private byte[] bytes;
    Message msg;
    MessageEncoderDecoder<Message> encdec;

    @BeforeEach
    void setUp() {
        encdec = new BGRSMessageEncoderDecoder();
    }

    @AfterEach
    void tearDown() {
        msg = null;
    }

    @Test
    void decodeType1() {

        msg = new Message((short) 2);
        msg.setName("Morty");
        msg.setPass("a123");
        bytes = new byte[]{0x00, 0x02, 0x4d, 0x6f, 0x72, 0x74, 0x79, 0x00, 0x61, 0x31, 0x32, 0x33, 0x00};
        Message ans = null;
        int i = 0;
        while (ans == null) {
            ans = encdec.decodeNextByte(bytes[i]);
            i++;
        }
        assertEquals(ans.getOPCode(), msg.getOPCode());
        assertEquals(ans.getName(), msg.getName());
        assertEquals(ans.getPass(), msg.getPass());
    }

    @Test
    void decodeType2() {

        msg = new Message((short) 7);
        msg.setCourseNum((short) 32);
        bytes = new byte[]{0x00, 0x07, 0x00, 0x20};
        Message ans = null;
        int i = 0;
        while (ans == null) {
            ans = encdec.decodeNextByte(bytes[i]);
            i++;
        }
        assertEquals(ans.getOPCode(), msg.getOPCode());
        assertEquals(ans.getCourseNum(), msg.getCourseNum());
    }
    @Test
    void decodeType3() {

        msg = new Message((short) 8);
        msg.setName("Morty");
        bytes = new byte[]{0x00, 0x08, 0x4d, 0x6f, 0x72, 0x74, 0x79, 0x00};
        Message ans = null;
        int i = 0;
        while (ans == null) {
            ans = encdec.decodeNextByte(bytes[i]);
            i++;
        }
        assertEquals(ans.getOPCode(), msg.getOPCode());
        assertEquals(ans.getName(), msg.getName());
    }

    @Test
    void encode() {
        msg = new Message((short) 12);
        msg.setReturnOPCode((short) 2);
        msg.setReturnInfo("hello");
        bytes = encdec.encode(msg);
        assertEquals(new String(bytes, 4, bytes.length - 5), "hello");
    }
}