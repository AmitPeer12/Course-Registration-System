package bgu.spl.net.impl.BGRSServer;

/**
 * Passive object representing a message sent from the client to the server
 * a message can contain up to 6 fields, depending on its OPCode
 */

public class Message {
    private final short OPCode;
    private short courseNum = 0;
    private String name = null;
    private String pass = null;
    private short returnOPCode = 0;
    private String returnInfo = "";

    public Message(short OPCode) {
        this.OPCode = OPCode;
    }

    public short getOPCode() {
        return OPCode;
    }

    public int getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(short courseNum) {
        this.courseNum = courseNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public short getReturnOPCode() {
        return returnOPCode;
    }

    public void setReturnOPCode(short returnOPCode) {
        this.returnOPCode = returnOPCode;
    }

    public String getReturnInfo() {
        return returnInfo;
    }

    public void setReturnInfo(String returnInfo) {
        this.returnInfo = returnInfo;
    }

    public String toString() {
        return (OPCode + returnOPCode + returnInfo);
    }
}