package bgu.spl.net.impl.BGRSServer;

public class Admin {
    private final String name;
    private final String psw;
    private boolean loggedIn = false;
    public Admin(String name,String psw){
        this.name = name;
        this.psw = psw;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void logInOrOut() {
        loggedIn = !loggedIn;
    }

    public String getPsw() {
        return psw;
    }
}
