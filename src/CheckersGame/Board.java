package CheckersGame;

import java.util.List;
import java.util.Random;

public class Board {

     private static Random rand = new Random();

     public boolean toMove;

     public int white;
     public int black;

     public int whiteKings;
     public int blackKings;

     public Board(boolean toMove, int white, int black, int whiteKings, int blackKings){
          this.toMove = toMove;
          this.white = white;
          this.black = black;
          this.whiteKings = whiteKings;
          this.blackKings = blackKings;
     }

     public void makeMove(int move){
          if(toMove){
               int captures = move & black;
               black ^= captures;
               move ^= captures;
               white ^= move;

               blackKings &= ~captures;
               if((whiteKings & move) != 0){
                    whiteKings ^= move;
               }

               whiteKings |= ((white&~whiteKings) & MoveGen.whiteWinMask);

          } else {
               int captures = move & white;
               white ^= captures;
               move ^= captures;
               black ^= move;

               whiteKings &= ~captures;
               if((blackKings & move) != 0){
                    blackKings ^= move;
               }
               blackKings |= ((black&~blackKings) & MoveGen.blackWinMask);
          }
          toMove = !toMove;

     }

     public boolean isGameOver(){

          if(Integer.bitCount(white|whiteKings) == 0){
               return true;
          }
          if(Integer.bitCount(black|blackKings) == 0){
               return true;
          }


          if(toMove){
               if(MoveGen.getWhiteMoves(this).size() == 0){
                    return true;
               }
          }
          else {
               if(MoveGen.getBlackMoves(this).size() == 0){
                    return true;
               }
          }


          return false;
     }

     public int getWinner(){

          if(Integer.bitCount(white|whiteKings) == 0){
               return -1;
          }
          if(Integer.bitCount(black|blackKings) == 0){
               return 1;
          }


          if(toMove && MoveGen.getWhiteMoves(this).size() == 0){
               return -1;
          }
          else if(!toMove && MoveGen.getBlackMoves(this).size() == 0){
               return 1;
          }

          return 0;
     }

     private void playRandomMove(){

          if(toMove){
               List<Integer> moves = MoveGen.getWhiteMoves(this);
               int rInt = rand.nextInt(moves.size());
               this.makeMove(moves.get(rInt));
          } else {
               List<Integer> moves = MoveGen.getBlackMoves(this);
               int rInt = rand.nextInt(moves.size());
               this.makeMove(moves.get(rInt));
          }
     }

     public int simulateGame(boolean eval){
          if(eval || !eval){
               int i = 0;
               int depth = 0;
               while(!isGameOver()){

                    playRandomMove();


                    if((white ^ whiteKings) == 0 && (black ^ blackKings) == 0){
                         i++;
                    }
                    depth++;


                    if(i > 25){
                         return 0;
                    }
               }
               //System.out.println(depth);
               return getWinner();

          }
          int topMask = 0b1111_0011_1100_0011_0000_1100_0011_1100;
          int bottomMask = 0b0000_1100_0011_1100_1111_0011_1100_0011;

          int numBlack = 0;
          int numWhite = 0;

          numBlack += 5*Integer.bitCount(black);
          numWhite += 5*Integer.bitCount(white);

          numBlack += 2*Integer.bitCount(blackKings);
          numWhite += 2*Integer.bitCount(whiteKings);

          //numBlack += Integer.bitCount(bottomMask&black);
          //numWhite += Integer.bitCount(topMask&white);

          return (numWhite - numBlack);
     }

     private void print(int b){
          String s = Integer.toUnsignedString(b, 2);
          while(s.length() < 32){
               s = "0" + s;
          }
          StringBuilder sb = new StringBuilder();
          sb.append(s);
          sb = sb.reverse();
          s = sb.toString();

          String board = "";
          board += " " + s.substring(11, 12);
          board += " " + s.substring(5, 6);
          board += " " + s.substring(31, 32);
          board += " " + s.substring(25, 26);
          board += "\n";

          board += s.substring(10, 11) + " ";
          board += s.substring(4, 5) + " ";
          board += s.substring(30, 31) + " ";
          board += s.substring(24, 25) + " ";
          board += "\n";

          board += " " + s.substring(3, 4);
          board += " " + s.substring(29, 30);
          board += " " + s.substring(23, 24);
          board += " " + s.substring(17, 18);
          board += "\n";

          board += s.substring(2, 3) + " ";
          board += s.substring(28, 29) + " ";
          board += s.substring(22, 23) + " ";
          board += s.substring(16, 17) + " ";
          board += "\n";

          board += " " + s.substring(27, 28);
          board += " " + s.substring(21, 22);
          board += " " + s.substring(15, 16);
          board += " " + s.substring(9, 10);
          board += "\n";

          board += s.substring(26, 27) + " ";
          board += s.substring(20, 21) + " ";
          board += s.substring(14, 15) + " ";
          board += s.substring(8, 9) + " ";
          board += "\n";

          board += " " + s.substring(19, 20);
          board += " " + s.substring(13, 14);
          board += " " + s.substring(7, 8);
          board += " " + s.substring(1, 2);
          board += "\n";

          board += s.substring(18, 19) + " ";
          board += s.substring(12, 13) + " ";
          board += s.substring(6, 7) + " ";
          board += s.substring(0, 1) + " ";
          board += "\n";

          System.out.println(board);
     }

     public Board replicate(){
          return new Board(toMove, white, black, whiteKings, blackKings);
     }

}
