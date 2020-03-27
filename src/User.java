
import java.net.Socket;

public class User {

    private static int whichUser = 0;
    private String nickname;
    private String ip;
    private int port;
    private Socket socket;

    public User(Socket user) {
        whichUser++;
        this.nickname = "Player"+whichUser;
        this.ip = user.getInetAddress().toString();
        this.port = user.getPort();
        this.socket = user;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public String toString() {
        return nickname + " " + port + " " + ip;
    }
}
