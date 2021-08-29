package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessageEncoderDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BGRSMessageEncoderDecoder implements MessageEncoderDecoder<Message> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    private Message decoded = null; //the eventual message we will return

    @Override
    public Message decodeNextByte(byte nextByte) {
        //if we are still decoding the OPCode of the message
        if (decoded == null) {
            if (len == 1) {
                //we successfully decoded the OPCode of the message, insert it into 'decoded'
                pushByte(nextByte);
                decoded = new Message(bytesToShort());
                if (decoded.getOPCode() == 4 || decoded.getOPCode() == 8)
                    return returnMessage();
                else return null;
            } else {
                pushByte(nextByte);
                return null;
            }
        }
        //we successfully decoded the OPCode and will move onto decoding the rest according to the OPCode
        else {
            switch (decoded.getOPCode()) {
                case 1:
                case 2:
                case 3:
                    return decodeType1(nextByte);
                case 5:
                case 6:
                case 7:
                case 9:
                case 10:
                    return decodeType2(nextByte);
                case 8:
                    return decodeType3(nextByte);
                default:
                    clearBytes();
                    //for message of type 4 (LOGOUT) we have only an OPCode
                    return returnMessage();
            }
        }
    }

    /**
     * used to encode messages of types: ACK, ERR
     *
     * @return a byte array representing the encoded message
     */
    @Override
    public byte[] encode(Message message) {
        shortToBytes(message.getOPCode());
        shortToBytes(message.getReturnOPCode());
        byte[] temp = message.getReturnInfo().getBytes(StandardCharsets.UTF_8);
        for (byte e : temp)
            pushByte(e);
        //if it's an ACK message we need to add a 00 byte at the end
        if (message.getOPCode() == 12)
            len++;
        temp = Arrays.copyOf(bytes, len);
        clearBytes();
        return temp;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    private String popString() {
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        clearBytes();
        return result;
    }

    /**
     * used to decode messages of types: ADMINREG, STUDENTREG, LOGIN
     *
     * @return decoded if finished decoding and reached a '0' byte, null otherwise
     */
    private Message decodeType1(byte nextByte) {
        if (nextByte == '\0') {
            if (decoded.getName() != null) {
                decoded.setPass(popString());
                return returnMessage();
            } else {
                decoded.setName(popString());
                return null;
            }
        } else {
            pushByte(nextByte);
            return null; //not a line yet
        }
    }

    /**
     * used to decode messages of types: COURSEREG, KDAMCHECK, COURSESTAT, ISREGISTERED
     *
     * @return decoded if finished decoding after 2 bytes, null otherwise
     */
    private Message decodeType2(byte nextByte) {
        if (len == 1) {
            pushByte(nextByte);
            decoded.setCourseNum(bytesToShort());
            return returnMessage();
        } else {
            pushByte(nextByte);
            return null; //not a line yet
        }
    }

    /**
     * used to decode messages of type: STUDENTSTAT
     *
     * @return decoded if finished decoding and reached a '0' byte, null otherwise
     */
    private Message decodeType3(byte nextByte) {
        if (nextByte == '\0') {
            decoded.setName(popString());
            return returnMessage();
        } else {
            pushByte(nextByte);
            return null; //not a line yet
        }
    }

    private short bytesToShort() {
        short result = (short) ((bytes[0] & 0xff) << 8);
        result += (short) (bytes[1] & 0xff);
        clearBytes();
        return result;
    }

    private void shortToBytes(short num) {
        pushByte((byte) ((num >> 8) & 0xFF));
        pushByte((byte) (num & 0xFF));
    }

    private void clearBytes() {
        bytes = new byte[1 << 10]; //start with 1k
        len = 0;
    }

    private Message returnMessage() {
        Message temp = decoded;
        decoded = null;
        return temp;
    }
}