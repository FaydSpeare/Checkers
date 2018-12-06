package CheckersGame;

public class Main {

    // WHITE START 0b0000_0100_0001_1100_0111_0001_1100_0011;
    // BLACK START 0b1110_0011_1000_0010_0000_1100_0011_1000;

    public static void main(String[] args) {

        int white = 0b0000_0100_0001_1100_0111_0001_1100_0011;
        int black = 0b1110_0011_1000_0010_0000_1100_0011_1000;
        white = 0b0000_0000_0001_0000_0000_0000_0000_0000;
        black = 0b0000_0000_0000_0000_0000_0001_0000_0000;

        Board board = new Board(true, white, black, 0, 0);
        int total = 0;

        for(int i = 0; i < 1000; i++){
            //total += board.simulateGame();
            board = new Board(true, white, black, 0, 0);
        }
        System.out.println(total);


    }
}
