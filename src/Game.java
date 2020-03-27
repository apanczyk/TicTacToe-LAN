import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Game {

    private User player1;
    private User player2;
    private String[][] board;
    private int turn;
    private static boolean solved;
    private static boolean started = false;
    private static boolean ended1 = false;
    private static boolean ended2 = false;

    public Game(User player1) {
        this.player1 = player1;
        turn = 0;
        solved = false;
        this.board = new String[][]{{" "," "," "},
                                    {" "," "," "},
                                    {" "," "," "}};

    }

    public void addPlayer(User player2) {
        this.player2 = player2;
        started = true;
        player1Thread.start();
        player2Thread.start();
    }

    Thread player1Thread = new Thread(() -> {
        try {
            ObjectOutputStream outP1 = new ObjectOutputStream(player1.getSocket().getOutputStream());;
            ObjectInputStream inP1;
            outP1.writeObject("GAME STARTING...");

            outP1 = new ObjectOutputStream(player1.getSocket().getOutputStream());
            outP1.writeObject("PLAYING: " + player1.getNickname() + " vs " + player2.getNickname() + "\n");

            outP1 = new ObjectOutputStream(player1.getSocket().getOutputStream());
            outP1.writeObject(menu());

            while (!solved) {
                inP1 = new ObjectInputStream(player1.getSocket().getInputStream());
                String field = (String) inP1.readObject();
                if (!solved) {
                    if (turn % 2 == 0) {
                        set(field);
                        sendBoard();
                        checkBoard("O");
                    } else {
                        outP1 = new ObjectOutputStream(player1.getSocket().getOutputStream());
                        outP1.writeObject("NOT YOUR TURN");
                    }
                }
            }
            ended1 = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
    Thread player2Thread = new Thread(() -> {
        try {
            ObjectOutputStream outP2 = new ObjectOutputStream(player2.getSocket().getOutputStream());
            outP2.writeObject("GAME STARTING...");
            ObjectInputStream inP2;

            outP2 = new ObjectOutputStream(player2.getSocket().getOutputStream());
            outP2.writeObject("PLAYING: " + player1.getNickname() + " vs " + player2.getNickname() + "\n");

            outP2 = new ObjectOutputStream(player2.getSocket().getOutputStream());
            outP2.writeObject(menu());

            while (!solved) {
                inP2 = new ObjectInputStream(player2.getSocket().getInputStream());
                String field = (String)inP2.readObject();

                if(!solved) {
                    if (turn % 2 == 1) {
                        set(field);
                        sendBoard();
                        checkBoard("X");
                    } else {
                        outP2 = new ObjectOutputStream(player2.getSocket().getOutputStream());
                        outP2.writeObject("NOT YOUR TURN");
                    }
                }
            }
            ended2 = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    });

    public void sendBoard() {
        try {
            ObjectOutputStream outP1 = new ObjectOutputStream(player1.getSocket().getOutputStream());
            ObjectOutputStream outP2 = new ObjectOutputStream(player2.getSocket().getOutputStream());
            outP1.writeObject(getBoard());
            outP2.writeObject(getBoard());
        } catch(Exception e) {e.printStackTrace();}
    }

    public void sendGameEnding() {
        try {
            ObjectOutputStream outP1 = new ObjectOutputStream(player1.getSocket().getOutputStream());
            ObjectOutputStream outP2 = new ObjectOutputStream(player2.getSocket().getOutputStream());
            outP1.writeObject("GAME ENDED");
            outP2.writeObject("GAME ENDED");
        } catch(Exception e) {e.printStackTrace();}
    }

    public String getBoard() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                sb.append("[ " + board[i][j] + " ]");
            }
            sb.append("\n");
        }
        if(turn%2==0)
            sb.append(player1.getNickname()+ "'S TURN");
        else
            sb.append(player2.getNickname() + "'S TURN");
        return sb.toString();
    }

    public boolean isEnded() {
        return !(ended1||ended2);
    }
    public boolean isStarted() {
        return !started;
    }

    public String menu() {
        StringBuilder sb = new StringBuilder();
        sb.append("To play enter numbers that reflects fields on the board\n");
        sb.append("============\n");
        int field = 1;
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                sb.append("[ " + field + " ]");
                field++;
            }
            sb.append("\n");
        }
        sb.append("============\n");
        return sb.toString();
    }


    public void set(String p) {
        int point = Integer.parseInt(p);
        try {
            if (board[(point - 1) / 3][(point - 1) % 3].equals(" ")) {
                if (turn % 2 == 0)
                    board[(point - 1) / 3][(point - 1) % 3] = "O";
                else
                    board[(point - 1) / 3][(point - 1) % 3] = "X";
                turn++;
            }
        } catch(Exception e) {
            System.out.println("BAD INDEX");
        }
    }

    public void checkBoard(String s) {
        for (int i = 0; i < 3; i++) {
            int countX = 0;
            int countY = 0;
            for (int j = 0; j < 3; j++) {
                if(board[i][j].equals(s)) countX++;
                if(board[j][i].equals(s)) countY++;
            }
            if(countX == 3 || countY == 3) solved = true;
        }
        if(board[0][0].equals(s) && board[1][1].equals(s) && board[2][2].equals(s)) solved = true;
        if(board[0][2].equals(s) && board[1][1].equals(s) && board[2][0].equals(s)) solved = true;
        if(turn>8) solved = true;
        if(solved) sendGameEnding();
    }

}

