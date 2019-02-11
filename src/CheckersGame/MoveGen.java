package CheckersGame;

import java.util.*;

public class MoveGen {

    private static final int lMaskdown = 0b1111_1011_1111_1011_1110_1011_1011_1010;
    private static final int rMaskup = 0b0111_1101_1111_1101_1111_0101_1101_1101;

    private static final int lMaskup = 0b0111_1001_1111_1011_1111_0011_1101_1011;
    private static final int rMaskdown = 0b1111_1101_1111_1001_1110_1101_1011_1100;

    private static final int lMask2white = 0b0011_0000_1111_0011_1111_0011_1100_0011;
    private static final int rMask2white = 0b0011_1100_1111_1100_1111_0000_1100_1100;

    private static final int lMask2black = 0b1111_0011_1111_0011_1100_0011_0011_0000;
    private static final int rMask2black = 0b1111_1100_1111_0000_1100_1100_0011_1100;

    public static final int whiteWinMask = 0b1000_0010_0000_0000_0000_1000_0010_0000;
    public static final int blackWinMask = 0b0000_0000_0000_0100_0001_0000_0100_0001;


    // WHITE MOVES

    private static int getLeftWhiteSliders(Board board){

        // slide left
        int leftSliders = board.white & lMaskup;
        leftSliders &= ~board.whiteKings;
        leftSliders &= ~(Integer.rotateRight(board.white|board.black, 7));

        //System.out.println(Integer.toUnsignedString(leftSliders, 2));

        return leftSliders;
    }

    private static int getRightWhiteSliders(Board board){

        // slide right
        int rightSliders = board.white & rMaskup;
        rightSliders &= ~board.whiteKings;
        rightSliders &= ~(Integer.rotateRight(board.white|board.black, 1));

        //System.out.println(Integer.toUnsignedString(rightSliders, 2));

        return rightSliders;
    }

    private static int getLeftWhiteJumpers(Board board){

        // jump left
        int leftJumpers = board.white & lMask2white;
        leftJumpers &= ~board.whiteKings;
        leftJumpers &= Integer.rotateRight(board.black, 7);
        leftJumpers &= ~(Integer.rotateRight(board.white|board.black, 14));

        //System.out.println(Integer.toUnsignedString(leftJumpers, 2));

        return leftJumpers;
    }

    private static int getRightWhiteJumpers(Board board){

        // jump right
        int rightJumpers = board.white & rMask2white;
        rightJumpers &= ~board.whiteKings;
        rightJumpers &= Integer.rotateRight(board.black, 1);
        rightJumpers &= ~(Integer.rotateRight(board.white|board.black, 2));

        //System.out.println(Integer.toUnsignedString(rightJumpers, 2));

        return rightJumpers;
    }

    private static boolean canWhiteJumpLeft(Board board, int jumper){
        jumper &= lMask2white;
        jumper &= Integer.rotateRight(board.black, 7);
        jumper &= ~(Integer.rotateRight(board.black|board.white, 14));
        return jumper != 0;
    }

    private static boolean canWhiteJumpRight(Board board, int jumper){
        jumper &= rMask2white;
        jumper &= Integer.rotateRight(board.black, 1);
        jumper &= ~(Integer.rotateRight(board.black|board.white, 2));
        return jumper != 0;
    }

    private static Set<Integer> whiteJumpEnds(Board board, int jumper){
        Set<Integer> ends = new HashSet<>();
        boolean left;
        boolean right;

        if(left = canWhiteJumpLeft(board, jumper)){
            if(canWhiteJumpLeft(board, Integer.rotateLeft(jumper, 14))){
                int captures = Integer.rotateLeft(jumper, 7) | Integer.rotateLeft(jumper, 21);
                ends.add(Integer.rotateLeft(jumper, 28) | captures);

            }
            if(canWhiteJumpRight(board, Integer.rotateLeft(jumper, 14))){
                int captures = Integer.rotateLeft(jumper, 7) | Integer.rotateLeft(jumper, 15);
                ends.add(Integer.rotateLeft(jumper, 16) | captures);
            }

        }

        if(right = canWhiteJumpRight(board, jumper)){
            if(canWhiteJumpLeft(board, Integer.rotateLeft(jumper, 2))){
                int captures = Integer.rotateLeft(jumper, 1) | Integer.rotateLeft(jumper, 9);
                ends.add(Integer.rotateLeft(jumper, 16) | captures);
            }
            if(canWhiteJumpRight(board, Integer.rotateLeft(jumper, 2))){
                int captures = Integer.rotateLeft(jumper, 1) | Integer.rotateLeft(jumper, 3);
                ends.add(Integer.rotateLeft(jumper, 4) | captures);
            }
        }

        if(ends.isEmpty()){

            if(left){
                int captures = Integer.rotateLeft(jumper, 7);
                ends.add(Integer.rotateLeft(jumper, 14) | captures);
            }
            if(right){
                int captures = Integer.rotateLeft(jumper, 1);
                ends.add(Integer.rotateLeft(jumper, 2) | captures);

            }
        }

        if(ends.isEmpty()){
            ends.add(jumper);
        }

        return ends;
    }

    public static List<Integer> getWhiteMoves(Board board){
        List<Integer> moves;
        int leftJumpers = getLeftWhiteJumpers(board);
        int rightJumpers = getRightWhiteJumpers(board);

        moves = getWhiteKingJumpMoves(board, board.whiteKings);

        if((leftJumpers | rightJumpers) == 0 && moves.size() == 0){
            int leftSliders = getLeftWhiteSliders(board);
            int rightSliders = getRightWhiteSliders(board);

            while(leftSliders != 0){
                int move = Integer.lowestOneBit(leftSliders);
                leftSliders ^= move;
                move |= Integer.rotateLeft(move, 7);
                moves.add(move);
            }

            while(rightSliders != 0){
                int move = Integer.lowestOneBit(rightSliders);
                rightSliders ^= move;
                move |= Integer.rotateLeft(move, 1);
                moves.add(move);
            }

            if(board.whiteKings != 0){
                moves.addAll(getWhiteKingSliderMoves(board));
            }
            return moves;
        }


        while(leftJumpers != 0){
            int move = Integer.lowestOneBit(leftJumpers);
            leftJumpers ^= move;

            int end = Integer.rotateLeft(move, 14);
            int capture = Integer.rotateLeft(move, 7);

            for(int e: whiteJumpEnds(board, end)){
                moves.add(move | e | capture);
            }
        }

        while(rightJumpers != 0){
            int move = Integer.lowestOneBit(rightJumpers);
            rightJumpers ^= move;

            int end = Integer.rotateLeft(move, 2);
            int capture = Integer.rotateLeft(move, 1);

            for(int e: whiteJumpEnds(board, end)){
                moves.add(move | e | capture);
            }
        }

        return moves;
    }


    // BLACK MOVES

    private static int getLeftBlackSliders(Board board){

        // slide left
        int leftSliders = board.black & lMaskdown;
        leftSliders &= ~board.blackKings;
        leftSliders &= ~(Integer.rotateLeft(board.white|board.black, 1));

        //System.out.println(Integer.toUnsignedString(leftSliders, 2));

        return leftSliders;
    }

    private static int getRightBlackSliders(Board board){

        // slide right
        int rightSliders = board.black & rMaskdown;
        rightSliders &= ~board.blackKings;
        rightSliders &= ~(Integer.rotateLeft(board.white|board.black, 7));

        //System.out.println(Integer.toUnsignedString(rightSliders, 2));

        return rightSliders;
    }

    private static int getLeftBlackJumpers(Board board){

        // jump left
        int leftJumpers = board.black & lMask2black;
        leftJumpers &= ~board.blackKings;
        leftJumpers &= Integer.rotateLeft(board.white, 1);
        leftJumpers &= ~(Integer.rotateLeft(board.white|board.black, 2));

        //System.out.println(Integer.toUnsignedString(leftJumpers, 2));

        return leftJumpers;
    }

    private static int getRightBlackJumpers(Board board){

        // jump right
        int rightJumpers = board.black & rMask2black;
        rightJumpers &= ~board.blackKings;
        rightJumpers &= Integer.rotateLeft(board.white, 7);
        rightJumpers &= ~(Integer.rotateLeft(board.white|board.black, 14));

        //System.out.println(Integer.toUnsignedString(rightJumpers, 2));

        return rightJumpers;
    }

    private static boolean canBlackJumpLeft(Board board, int jumper){
        jumper &= lMask2black;
        jumper &= Integer.rotateLeft(board.white, 1);
        jumper &= ~(Integer.rotateLeft(board.black|board.white, 2));
        return jumper != 0;
    }

    private static boolean canBlackJumpRight(Board board, int jumper){
        jumper &= rMask2black;
        jumper &= Integer.rotateLeft(board.white, 7);
        jumper &= ~(Integer.rotateLeft(board.black|board.white, 14));
        return jumper != 0;
    }

    private static Set<Integer> blackJumpEnds(Board board, int jumper){
        Set<Integer> ends = new HashSet<>();
        boolean left;
        boolean right;



        if(left = canBlackJumpLeft(board, jumper)){
            if(canBlackJumpLeft(board, Integer.rotateRight(jumper, 2))){
                int captures = Integer.rotateRight(jumper, 1) | Integer.rotateRight(jumper, 3);
                ends.add(Integer.rotateRight(jumper, 4) | captures);

            }
            if(canBlackJumpRight(board, Integer.rotateRight(jumper, 2))){
                int captures = Integer.rotateRight(jumper, 1) | Integer.rotateRight(jumper, 9);
                ends.add(Integer.rotateRight(jumper, 16) | captures);

            }
        }

        if(right = canBlackJumpRight(board, jumper)){
            if(canBlackJumpLeft(board, Integer.rotateRight(jumper, 14))){
                int captures = Integer.rotateRight(jumper, 7) | Integer.rotateRight(jumper, 15);
                ends.add(Integer.rotateRight(jumper, 16) | captures);
            }
            if(canBlackJumpRight(board, Integer.rotateRight(jumper, 14))){
                int captures = Integer.rotateRight(jumper, 7) | Integer.rotateRight(jumper, 21);
                ends.add(Integer.rotateRight(jumper, 28) | captures);
            }
        }

        if(ends.isEmpty()){

            if(left){
                int captures = Integer.rotateRight(jumper, 1);
                ends.add(Integer.rotateRight(jumper, 2) | captures);
            }
            if(right){
                int captures = Integer.rotateRight(jumper, 7);
                ends.add(Integer.rotateRight(jumper, 14) | captures);

            }
        }

        if(ends.isEmpty()){
            ends.add(jumper);
        }

        return ends;
    }

    public static List<Integer> getBlackMoves(Board board){
        List<Integer> moves;
        int leftJumpers = getLeftBlackJumpers(board);
        int rightJumpers = getRightBlackJumpers(board);

        moves = getBlackKingJumpMoves(board, board.blackKings);


        if((leftJumpers | rightJumpers) == 0 && moves.size() == 0){

            int leftSliders = getLeftBlackSliders(board);
            int rightSliders = getRightBlackSliders(board);

            while(leftSliders != 0){
                int move = Integer.lowestOneBit(leftSliders);
                leftSliders ^= move;
                move |= Integer.rotateRight(move, 1);
                moves.add(move);
            }

            while(rightSliders != 0){
                int move = Integer.lowestOneBit(rightSliders);
                rightSliders ^= move;
                move |= Integer.rotateRight(move, 7);
                moves.add(move);
            }

            if(board.blackKings != 0){
                moves.addAll(getBlackKingSliderMoves(board));
            }

            return moves;
        }


        while(leftJumpers != 0){
            int move = Integer.lowestOneBit(leftJumpers);
            leftJumpers ^= move;

            int end = Integer.rotateRight(move, 2);
            int captures = Integer.rotateRight(move, 1);

            for(int e: blackJumpEnds(board, end)){
                moves.add(move | e | captures);
            }
        }

        while(rightJumpers != 0){
            int move = Integer.lowestOneBit(rightJumpers);
            rightJumpers ^= move;

            int end = Integer.rotateRight(move, 14);
            int captures = Integer.rotateRight(move, 7);

            for(int e: blackJumpEnds(board, end)){
                moves.add(move | e | captures);
            }
        }

        return moves;
    }


    // SINGLE WHITE MOVE

    private static int getSingleLeftWhiteSliders(Board board, int single){

        // slide left
        int leftSliders = single & lMaskup;
        leftSliders &= ~(Integer.rotateRight(board.white|board.black, 7));

        //System.out.println(Integer.toUnsignedString(leftSliders, 2));

        return leftSliders;
    }

    private static int getSingleRightWhiteSliders(Board board, int single){

        // slide right
        int rightSliders = single & rMaskup;
        rightSliders &= ~(Integer.rotateRight(board.white|board.black, 1));

        //System.out.println(Integer.toUnsignedString(rightSliders, 2));

        return rightSliders;
    }

    private static int getSingleLeftWhiteJumpers(Board board, int single){

        // jump left
        int leftJumpers = single & lMask2white;
        leftJumpers &= Integer.rotateRight(board.black, 7);
        leftJumpers &= ~(Integer.rotateRight(board.white|board.black, 14));

        //System.out.println(Integer.toUnsignedString(leftJumpers, 2));

        return leftJumpers;
    }

    private static int getSingleRightWhiteJumpers(Board board, int single){

        // jump right
        int rightJumpers = single & rMask2white;
        rightJumpers &= Integer.rotateRight(board.black, 1);
        rightJumpers &= ~(Integer.rotateRight(board.white|board.black, 2));

        //System.out.println(Integer.toUnsignedString(rightJumpers, 2));

        return rightJumpers;
    }

    public static int getSingleWhiteMove(Board board, int single){
        int moves = 0;

        int leftJumpers = getSingleLeftWhiteJumpers(board, single);
        int rightJumpers = getSingleRightWhiteJumpers(board, single);

        //this
        if((single & board.whiteKings) != 0) {
            for(int m: getWhiteKingJumpMoves(board, single)){
                moves |= m;
            }
            if(moves != 0) return moves;
        }

        if((leftJumpers | rightJumpers) == 0){

            int leftJumpersAll = getLeftWhiteJumpers(board);
            int rightJumpersAll = getRightWhiteJumpers(board);
            //this
            List<Integer> kingJumps = getWhiteKingJumpMoves(board, board.whiteKings);
            if((leftJumpersAll | rightJumpersAll) != 0 && kingJumps.size() == 0){
                return 0;
            }


            if((single & board.whiteKings) != 0){

                for(int i: getWhiteKingSliderMovesSingle(board, single)){
                    moves |= i;
                }

            } else {

                int leftSliders = getSingleLeftWhiteSliders(board, single);
                int rightSliders = getSingleRightWhiteSliders(board, single);

                while(leftSliders != 0){
                    int move = Integer.lowestOneBit(leftSliders);
                    leftSliders ^= move;
                    move |= Integer.rotateLeft(move, 7);
                    moves |= move;
                }

                while(rightSliders != 0){
                    int move = Integer.lowestOneBit(rightSliders);
                    rightSliders ^= move;
                    move |= Integer.rotateLeft(move, 1);
                    moves |= move;
                }

            }
            return moves;
        }

        while(leftJumpers != 0){
            int move = Integer.lowestOneBit(leftJumpers);
            leftJumpers ^= move;

            int end = Integer.rotateLeft(move, 14);
            int capture = Integer.rotateLeft(move, 7);

            for(int e: whiteJumpEnds(board, end)){
                moves |= move | e | capture;
            }
        }

        while(rightJumpers != 0){
            int move = Integer.lowestOneBit(rightJumpers);
            rightJumpers ^= move;

            int end = Integer.rotateLeft(move, 2);
            int capture = Integer.rotateLeft(move, 1);

            for(int e: whiteJumpEnds(board, end)){
                moves |= move | e | capture;
            }
        }

        return moves;
    }

    private static List<Integer> getWhiteKingSliderMovesSingle(Board board, int single){
        List<Integer> moves = new LinkedList<>();

        int leftUpSliders = getLeftUpWhiteKingSliders(board) & single;
        int rightUpSliders = getRightUpWhiteKingSliders(board) & single;
        int leftDownSliders = getLeftDownWhiteKingSliders(board) & single;
        int rightDownSliders = getRightDownWhiteKingSliders(board) & single;

        while(leftUpSliders != 0){
            int move = Integer.lowestOneBit(leftUpSliders);
            leftUpSliders ^= move;
            move |= Integer.rotateLeft(move, 7);
            moves.add(move);

        }

        while(rightUpSliders != 0){
            int move = Integer.lowestOneBit(rightUpSliders);
            rightUpSliders ^= move;
            move |= Integer.rotateLeft(move, 1);
            moves.add(move);
        }

        while(leftDownSliders != 0){
            int move = Integer.lowestOneBit(leftDownSliders);
            leftDownSliders ^= move;
            move |= Integer.rotateRight(move, 1);
            moves.add(move);
        }

        while(rightDownSliders != 0){
            int move = Integer.lowestOneBit(rightDownSliders);
            rightDownSliders ^= move;
            move |= Integer.rotateRight(move, 7);
            moves.add(move);
        }

        return moves;
    }


    // SINGLE BLACK MOVES

    private static int getSingleLeftBlackSliders(Board board, int single){

        // slide left
        int leftSliders = single & lMaskdown;
        leftSliders &= ~(Integer.rotateLeft(board.white|board.black, 1));

        //System.out.println(Integer.toUnsignedString(leftSliders, 2));

        return leftSliders;
    }

    private static int getSingleRightBlackSliders(Board board, int single){

        // slide right
        int rightSliders = single & rMaskdown;
        rightSliders &= ~(Integer.rotateLeft(board.white|board.black, 7));

        //System.out.println(Integer.toUnsignedString(rightSliders, 2));

        return rightSliders;
    }

    private static int getSingleLeftBlackJumpers(Board board, int single){

        // jump left
        int leftJumpers = single & lMask2black;
        leftJumpers &= Integer.rotateLeft(board.white, 1);
        leftJumpers &= ~(Integer.rotateLeft(board.white|board.black, 2));

        //System.out.println(Integer.toUnsignedString(leftJumpers, 2));

        return leftJumpers;
    }

    private static int getSingleRightBlackJumpers(Board board, int single){

        // jump right
        int rightJumpers = single & rMask2black;
        rightJumpers &= Integer.rotateLeft(board.white, 7);
        rightJumpers &= ~(Integer.rotateLeft(board.white|board.black, 14));

        //System.out.println(Integer.toUnsignedString(rightJumpers, 2));

        return rightJumpers;
    }

    public static int getSingleBlackMove(Board board, int single){
        int moves = 0;

        int leftJumpers = getSingleLeftBlackJumpers(board, single);
        int rightJumpers = getSingleRightBlackJumpers(board, single);

        if((single & board.blackKings) != 0) {
            for(int m: getBlackKingJumpMoves(board, single)){
                moves |= m;
            }
            if(moves != 0) return moves;
        }

        if((leftJumpers | rightJumpers) == 0){

            int leftJumpersAll = getLeftBlackJumpers(board);
            int rightJumpersAll = getRightBlackJumpers(board);
            List<Integer> kingJumps = getBlackKingJumpMoves(board, board.blackKings);
            if((leftJumpersAll | rightJumpersAll) != 0 && kingJumps.size() == 0){
                return 0;
            }

            if((single & board.blackKings) != 0){
                for(int i: getBlackKingSliderMovesSingle(board, single)){
                    moves |= i;
                }

            } else {
                int leftSliders = getSingleLeftBlackSliders(board, single);
                int rightSliders = getSingleRightBlackSliders(board, single);

                while(leftSliders != 0){
                    int move = Integer.lowestOneBit(leftSliders);
                    leftSliders ^= move;
                    move |= Integer.rotateRight(move, 1);
                    moves |= move;
                }

                while(rightSliders != 0){
                    int move = Integer.lowestOneBit(rightSliders);
                    rightSliders ^= move;
                    move |= Integer.rotateRight(move, 7);
                    moves |= move;
                }
            }



            return moves;
        }

        while(leftJumpers != 0){
            int move = Integer.lowestOneBit(leftJumpers);
            leftJumpers ^= move;

            int end = Integer.rotateRight(move, 2);
            int captures = Integer.rotateRight(move, 1);

            for(int e: blackJumpEnds(board, end)){
                moves |= move | e | captures;
            }
        }

        while(rightJumpers != 0){
            int move = Integer.lowestOneBit(rightJumpers);
            rightJumpers ^= move;

            int end = Integer.rotateRight(move, 14);
            int captures = Integer.rotateRight(move, 7);

            for(int e: blackJumpEnds(board, end)){
                moves |= move | e | captures;
            }
        }

        return moves;
    }

    private static List<Integer> getBlackKingSliderMovesSingle(Board board, int single){
        List<Integer> moves = new LinkedList<>();

        int leftUpSliders = getLeftUpBlackKingSliders(board) & single;
        int rightUpSliders = getRightUpBlackKingSliders(board) & single;
        int leftDownSliders = getLeftDownBlackKingSliders(board) & single;
        int rightDownSliders = getRightDownBlackKingSliders(board) & single;

        while(leftUpSliders != 0){
            int move = Integer.lowestOneBit(leftUpSliders);
            leftUpSliders ^= move;
            move |= Integer.rotateLeft(move, 7);
            moves.add(move);
        }

        while(rightUpSliders != 0){
            int move = Integer.lowestOneBit(rightUpSliders);
            rightUpSliders ^= move;
            move |= Integer.rotateLeft(move, 1);
            moves.add(move);
        }

        while(leftDownSliders != 0){
            int move = Integer.lowestOneBit(leftDownSliders);
            leftDownSliders ^= move;
            move |= Integer.rotateRight(move, 1);
            moves.add(move);
        }

        while(rightDownSliders != 0){
            int move = Integer.lowestOneBit(rightDownSliders);
            rightDownSliders ^= move;
            move |= Integer.rotateRight(move, 7);
            moves.add(move);
        }

        return moves;
    }


    // WHITE KINGS

    private static boolean canWhiteKingJumpLeftUp(Board board, int king){
        int leftUp = king & lMask2white;
        leftUp &= Integer.rotateRight(board.black, 7);
        leftUp &= ~Integer.rotateRight(board.black|board.white, 14);
        return leftUp != 0;
    }

    private static boolean canWhiteKingJumpRightUp(Board board, int king){
        int rightUp = king & rMask2white;
        rightUp &= Integer.rotateRight(board.black, 1);
        rightUp &= ~Integer.rotateRight(board.black|board.white, 2);
        return rightUp != 0;
    }

    private static boolean canWhiteKingJumpLeftDown(Board board, int king){
        int leftDown = king & lMask2black;
        leftDown &= Integer.rotateLeft(board.black, 1);
        leftDown &= ~Integer.rotateLeft(board.black|board.white, 2);
        return leftDown != 0;
    }

    private static boolean canWhiteKingJumpRightDown(Board board, int king){
        int rightDown = king & rMask2black;
        rightDown &= Integer.rotateLeft(board.black, 7);
        rightDown &= ~Integer.rotateLeft(board.black|board.white, 14);
        return rightDown != 0;
    }

    private static List<Integer> getWhiteKingJumps(Board board, int jumper){

        List<Integer> list = new LinkedList<>();

        int kingWithoutCaptures = jumper & board.white;

        if(canWhiteKingJumpLeftUp(board, kingWithoutCaptures)){
            int king = jumper ^ kingWithoutCaptures;
            //System.out.println("1");
            Board copy = board.replicate();
            copy.white ^= (kingWithoutCaptures | Integer.rotateLeft(kingWithoutCaptures, 14));
            copy.black ^= Integer.rotateLeft(kingWithoutCaptures, 7);
            king |= Integer.rotateLeft(kingWithoutCaptures, 7);
            List<Integer> ret = getWhiteKingJumps(copy, king | Integer.rotateLeft(kingWithoutCaptures, 14));
            list.addAll(ret);

        }
        if(canWhiteKingJumpRightUp(board, kingWithoutCaptures)){
            int king = jumper ^ kingWithoutCaptures;
            //System.out.println("2");
            Board copy = board.replicate();
            copy.white ^= (kingWithoutCaptures | Integer.rotateLeft(kingWithoutCaptures, 2));
            copy.black ^= Integer.rotateLeft(kingWithoutCaptures, 1);
            king |= Integer.rotateLeft(kingWithoutCaptures, 1);
            List<Integer> ret = getWhiteKingJumps(copy, king | Integer.rotateLeft(kingWithoutCaptures, 2));
            list.addAll(ret);

        }
        if(canWhiteKingJumpLeftDown(board, kingWithoutCaptures)){
            int king = jumper ^ kingWithoutCaptures;
            //System.out.println("3");
            Board copy = board.replicate();
            copy.white ^= (kingWithoutCaptures | Integer.rotateRight(kingWithoutCaptures, 2));
            copy.black ^= Integer.rotateRight(kingWithoutCaptures, 1);
            king |= Integer.rotateRight(kingWithoutCaptures, 1);
            List<Integer> ret = getWhiteKingJumps(copy, king | Integer.rotateRight(kingWithoutCaptures, 2));
            list.addAll(ret);

        }
        if(canWhiteKingJumpRightDown(board, kingWithoutCaptures)){
            int king = jumper ^ kingWithoutCaptures;
            //System.out.println("4");
            Board copy = board.replicate();
            copy.white ^= (kingWithoutCaptures | Integer.rotateRight(kingWithoutCaptures, 14));
            copy.black ^= Integer.rotateRight(kingWithoutCaptures, 7);
            king |= Integer.rotateRight(kingWithoutCaptures, 7);
            List<Integer> ret = getWhiteKingJumps(copy, king | Integer.rotateRight(kingWithoutCaptures, 14));
            list.addAll(ret);

        }

        if(list.isEmpty()){
            list.add(jumper);
        }

        return list;
    }

    private static List<Integer> getWhiteKingJumpMoves(Board board, int whiteKings){
        List<Integer> moves = new LinkedList<>();
        int copy = whiteKings;

        while(copy != 0){
            int mover = Integer.lowestOneBit(copy);
            copy ^= mover;
            for(int i: getWhiteKingJumps(board, mover)){
                if((i^mover) != 0)
                    moves.add(i^mover);
            }
        }

        return moves;
    }

    private static int getLeftUpWhiteKingSliders(Board board){

        // slide left
        int leftSliders = board.whiteKings & lMaskup;
        leftSliders &= ~(Integer.rotateRight(board.white|board.black, 7));

        //System.out.println(Integer.toUnsignedString(leftSliders, 2));

        return leftSliders;
    }

    private static int getRightUpWhiteKingSliders(Board board){

        // slide right
        int rightSliders = board.whiteKings & rMaskup;
        rightSliders &= ~(Integer.rotateRight(board.white|board.black, 1));
        //System.out.println(Integer.toUnsignedString(rightSliders, 2));
        return rightSliders;
    }

    private static int getLeftDownWhiteKingSliders(Board board){

        // slide left
        int leftSliders = board.whiteKings & lMaskdown;
        leftSliders &= ~(Integer.rotateLeft(board.white|board.black, 1));

        //System.out.println(Integer.toUnsignedString(leftSliders, 2));

        return leftSliders;
    }

    private static int getRightDownWhiteKingSliders(Board board){

        // slide right
        int rightSliders = board.whiteKings & rMaskdown;
        rightSliders &= ~(Integer.rotateLeft(board.white|board.black, 7));

        //System.out.println(Integer.toUnsignedString(rightSliders, 2));

        return rightSliders;
    }

    private static List<Integer> getWhiteKingSliderMoves(Board board){
        List<Integer> moves = new LinkedList<>();

        int leftUpSliders = getLeftUpWhiteKingSliders(board);
        int rightUpSliders = getRightUpWhiteKingSliders(board);
        int leftDownSliders = getLeftDownWhiteKingSliders(board);
        int rightDownSliders = getRightDownWhiteKingSliders(board);

        while(leftUpSliders != 0){
            int move = Integer.lowestOneBit(leftUpSliders);
            leftUpSliders ^= move;
            move |= Integer.rotateLeft(move, 7);
            moves.add(move);
        }

        while(rightUpSliders != 0){
            int move = Integer.lowestOneBit(rightUpSliders);
            rightUpSliders ^= move;
            move |= Integer.rotateLeft(move, 1);
            moves.add(move);
        }

        while(leftDownSliders != 0){
            int move = Integer.lowestOneBit(leftDownSliders);
            leftDownSliders ^= move;
            move |= Integer.rotateRight(move, 1);
            moves.add(move);
        }

        while(rightDownSliders != 0){
            int move = Integer.lowestOneBit(rightDownSliders);
            rightDownSliders ^= move;
            move |= Integer.rotateRight(move, 7);
            moves.add(move);
        }

        return moves;
    }


    // BLACK KINGS

    private static boolean canBlackKingJumpLeftUp(Board board, int king){
        int leftUp = king & lMask2white;
        leftUp &= Integer.rotateRight(board.white, 7);
        leftUp &= ~Integer.rotateRight(board.black|board.white, 14);
        return leftUp != 0;
    }

    private static boolean canBlackKingJumpRightUp(Board board, int king){
        int rightUp = king & rMask2white;
        rightUp &= Integer.rotateRight(board.white, 1);
        rightUp &= ~Integer.rotateRight(board.black|board.white, 2);
        return rightUp != 0;
    }

    private static boolean canBlackKingJumpLeftDown(Board board, int king){
        int leftDown = king & lMask2black;
        leftDown &= Integer.rotateLeft(board.white, 1);
        leftDown &= ~Integer.rotateLeft(board.black|board.white, 2);
        return leftDown != 0;
    }

    private static boolean canBlackKingJumpRightDown(Board board, int king){
        int rightDown = king & rMask2black;
        rightDown &= Integer.rotateLeft(board.white, 7);
        rightDown &= ~Integer.rotateLeft(board.black|board.white, 14);
        return rightDown != 0;
    }

    private static List<Integer> getBlackKingJumps(Board board, int jumper){

        List<Integer> list = new LinkedList<>();

        int kingWithoutCaptures = jumper & board.black;

        if(canBlackKingJumpLeftUp(board, kingWithoutCaptures)){
            //System.out.println("1");
            int king = jumper ^ kingWithoutCaptures;
            Board copy = board.replicate();
            copy.black ^= (kingWithoutCaptures | Integer.rotateLeft(kingWithoutCaptures, 14));
            copy.white ^= Integer.rotateLeft(kingWithoutCaptures, 7);
            king |= Integer.rotateLeft(kingWithoutCaptures, 7);
            List<Integer> ret = getBlackKingJumps(copy, king | Integer.rotateLeft(kingWithoutCaptures, 14));
            list.addAll(ret);
        }
        if(canBlackKingJumpRightUp(board, kingWithoutCaptures)){
            //System.out.println("2");
            int king = jumper ^ kingWithoutCaptures;
            Board copy = board.replicate();
            copy.black ^= (kingWithoutCaptures | Integer.rotateLeft(kingWithoutCaptures, 2));
            copy.white ^= Integer.rotateLeft(kingWithoutCaptures, 1);
            king |= Integer.rotateLeft(kingWithoutCaptures, 1);
            List<Integer> ret = getBlackKingJumps(copy, king | Integer.rotateLeft(kingWithoutCaptures, 2));
            list.addAll(ret);
        }
        if(canBlackKingJumpLeftDown(board, kingWithoutCaptures)){
            //System.out.println("3");
            int king = jumper ^ kingWithoutCaptures;
            Board copy = board.replicate();
            copy.black ^= (kingWithoutCaptures | Integer.rotateRight(kingWithoutCaptures, 2));
            copy.white ^= Integer.rotateRight(kingWithoutCaptures, 1);
            king |= Integer.rotateRight(kingWithoutCaptures, 1);
            List<Integer> ret = getBlackKingJumps(copy, king | Integer.rotateRight(kingWithoutCaptures, 2));
            list.addAll(ret);
        }
        if(canBlackKingJumpRightDown(board, kingWithoutCaptures)){
            //System.out.println("4");
            int king = jumper ^ kingWithoutCaptures;
            Board copy = board.replicate();
            copy.black ^= (kingWithoutCaptures | Integer.rotateRight(kingWithoutCaptures, 14));
            copy.white ^= Integer.rotateRight(kingWithoutCaptures, 7);
            king |= Integer.rotateRight(kingWithoutCaptures, 7);
            List<Integer> ret = getBlackKingJumps(copy, king | Integer.rotateRight(kingWithoutCaptures, 14));
            list.addAll(ret);
        }

        if(list.isEmpty()){
            list.add(jumper);
        }

        return list;
    }

    private static List<Integer> getBlackKingJumpMoves(Board board, int blackKings){
        List<Integer> moves = new LinkedList<>();
        int copy = blackKings;

        while(copy != 0){
            int mover = Integer.lowestOneBit(copy);
            copy ^= mover;
            for(int i: getBlackKingJumps(board, mover)){
                if((i^mover) != 0)
                    moves.add(i^mover);
            }
        }

        return moves;
    }

    private static int getLeftUpBlackKingSliders(Board board){

        // slide left
        int leftSliders = board.blackKings & lMaskup;
        leftSliders &= ~(Integer.rotateRight(board.white|board.black, 7));

        //System.out.println(Integer.toUnsignedString(leftSliders, 2));

        return leftSliders;
    }

    private static int getRightUpBlackKingSliders(Board board){

        // slide right
        int rightSliders = board.blackKings & rMaskup;
        rightSliders &= ~(Integer.rotateRight(board.white|board.black, 1));

        //System.out.println(Integer.toUnsignedString(rightSliders, 2));

        return rightSliders;
    }

    private static int getLeftDownBlackKingSliders(Board board){

        // slide left
        int leftSliders = board.blackKings & lMaskdown;
        leftSliders &= ~(Integer.rotateLeft(board.white|board.black, 1));

        //System.out.println(Integer.toUnsignedString(leftSliders, 2));

        return leftSliders;
    }

    private static int getRightDownBlackKingSliders(Board board){

        // slide right
        int rightSliders = board.blackKings & rMaskdown;
        rightSliders &= ~(Integer.rotateLeft(board.white|board.black, 7));

        //System.out.println(Integer.toUnsignedString(rightSliders, 2));

        return rightSliders;
    }

    private static List<Integer> getBlackKingSliderMoves(Board board){
        List<Integer> moves = new LinkedList<>();

        int leftUpSliders = getLeftUpBlackKingSliders(board);
        int rightUpSliders = getRightUpBlackKingSliders(board);
        int leftDownSliders = getLeftDownBlackKingSliders(board);
        int rightDownSliders = getRightDownBlackKingSliders(board);

        while(leftUpSliders != 0){
            int move = Integer.lowestOneBit(leftUpSliders);
            leftUpSliders ^= move;
            move |= Integer.rotateLeft(move, 7);
            moves.add(move);
        }

        while(rightUpSliders != 0){
            int move = Integer.lowestOneBit(rightUpSliders);
            rightUpSliders ^= move;
            move |= Integer.rotateLeft(move, 1);
            moves.add(move);
        }

        while(leftDownSliders != 0){
            int move = Integer.lowestOneBit(leftDownSliders);
            leftDownSliders ^= move;
            move |= Integer.rotateRight(move, 1);
            moves.add(move);
        }

        while(rightDownSliders != 0){
            int move = Integer.lowestOneBit(rightDownSliders);
            rightDownSliders ^= move;
            move |= Integer.rotateRight(move, 7);
            moves.add(move);
        }

        return moves;
    }


    // Board Position ---> Bit Index
    public static final int[] convertedBitIndex = new int[]{0,6,12,18,1,7,13,19,8,14,20,26,9,15,21,27,16,22,28,2,17,
            23,29,3,24, 30,4,10,25,31,5,11};

    public static boolean testBit(int i, int n){
        return ((i >>> n) & 1) != 0;
    }

    public static Board startingPos(){
        int white = 0b0000_0100_0001_1100_0111_0001_1100_0011;
        int black = 0b1110_0011_1000_0010_0000_1100_0011_1000;
        int whiteKings = 0b0000_0000_0000_0000_0000_0000_0000_0000;
        int blackKings = 0b0000_0000_0000_0000_0000_0000_0000_0000;
        return new Board(true, white, black, whiteKings, blackKings);
    }



}
