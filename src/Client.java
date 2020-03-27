import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    static Socket socket = null;
    static ObjectOutputStream oos = null;
    static ObjectInputStream ois = null;
    static String message = "";
    static boolean running = true;

    public static void main(String[] args) {
        try {
//            socket = new Socket(args[0], Integer.parseInt(args[1]));
            socket = new Socket(, 55124);
            System.out.println("LOGGED TO SERVER");
        } catch (Exception e) {
            e.printStackTrace();
        }
        t1.start();
        t2.start();
    }

    static Thread t1 = new Thread(() -> {
        try {
            while (!message.equals("LOGOUT")) {

                Scanner scanner = new Scanner(System.in);
                oos = new ObjectOutputStream(socket.getOutputStream());
                message = scanner.nextLine();
                oos.writeObject(message);
            }
            running = false;
            socket.close();
        } catch (Exception e) {
        }
    });

    static Thread t2 = new Thread(() -> {
        try {
            while (!message.equals("BYE")) {
                ois = new ObjectInputStream(socket.getInputStream());
                message = ois.readObject().toString();
                System.out.println("\n" + message);
            }
        } catch (Exception e) {
        }
    });
}
