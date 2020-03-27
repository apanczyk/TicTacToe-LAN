
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private static List<User> players = new ArrayList<>();
    private static List<User> readyToPlay = new ArrayList<>();
    private static List<Game> games = new ArrayList<>();
    private static int gameId = 0;
    private User user;
    private boolean connected;

    public static void main(String[] args) {
        try {
//            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
            ServerSocket serverSocket = new ServerSocket(55555);
//            System.out.println("SERVER WORKING ON PORT " + args[0]);
            while (true) {
                new Server(serverSocket.accept());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server(Socket player) {
        connected = true;
        this.user = new User(player);
        players.add(user);
        this.start();
    }


    @Override
    public void run() {
        try {
            ObjectInputStream ois;
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(user.getSocket().getOutputStream());
            oos.writeObject("Hello, your nickname is: " + user.getNickname());

            oos = new ObjectOutputStream(user.getSocket().getOutputStream());
            oos.writeObject(menu());
            while (connected) {
                ois = new ObjectInputStream(user.getSocket().getInputStream());
                String message = (String) ois.readObject();

                System.out.println("Message Received from " + user.getSocket().getPort() + " " + user.getSocket().getInetAddress() + " " + message);
                switch (message) {
                    case "LIST":
                        oos = new ObjectOutputStream(user.getSocket().getOutputStream());
                        oos.writeObject(sendPlayerList());
                        break;
                    case "LOGOUT":
                        connected = false;
                        players.remove(user);
                        oos = new ObjectOutputStream(user.getSocket().getOutputStream());
                        oos.writeObject("BYE");
                        break;
                    case "PLAY":
                        readyToPlay.add(user);

                        if (readyToPlay.size() == 1) {
                            System.out.println("STARTING NEW GAME");
                            games.add(new Game(user));
                        } else {
                            readyToPlay.remove(0);
                            readyToPlay.remove(0);
                            games.get(gameId++).addPlayer(user);
                        }
                        oos = new ObjectOutputStream(user.getSocket().getOutputStream());
                        oos.writeObject("WAITING FOR PLAYER");

                        while(games.get(0).isStarted()) {
                            Thread.sleep(1000);
                        }
                        while(games.get(0).isEnded()) {
                            Thread.sleep(1000);
                        }
                        break;
                    default:
                        oos = new ObjectOutputStream(user.getSocket().getOutputStream());
                        oos.writeObject("Invalid command");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String sendPlayerList() {
        StringBuilder sb = new StringBuilder();
        for (User s : players) {
            sb.append(s + "\n");
        }
        return sb.toString();
    }

    public String menu() {
        StringBuilder sb = new StringBuilder();
        String[] commands = {"PLAY", "LIST", "LOGOUT"};
        sb.append("============\n");
        for (String s : commands) {
            sb.append("||" + s + "\n");
        }
        sb.append("============\n");
        return sb.toString();
    }
}
