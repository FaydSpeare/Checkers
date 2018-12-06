package UCT;

import CheckersGame.Board;
import CheckersGame.MoveGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Node {

    private static Random rand = new Random();

    private double wins;
    private double visits;

    private Node parent;

    private List<Integer> toExplore;
    public List<Node> children;

    public int lastMove;
    public Board board;

    private boolean terminal = false;
    private int terminalValue;

    public Node(Board board){
        parent = null;
        this.board = board.replicate();

        if(board.toMove){
            toExplore = MoveGen.getWhiteMoves(board);
        } else {
            toExplore = MoveGen.getBlackMoves(board);
        }
        children = new ArrayList<>();
    }

    public Node(Node parent, int move){
        this.parent = parent;
        this.lastMove = move;
        this.board = parent.board.replicate();
        this.board.makeMove(move);

        if(board.toMove){
            toExplore = MoveGen.getWhiteMoves(board);
        } else {
            toExplore = MoveGen.getBlackMoves(board);
        }

        this.children = new ArrayList<>();
    }

    public Node makeMove(int move){
        Node creation = new Node(this, move);
        this.children.add(creation);
        this.toExplore.remove(Integer.valueOf(move));
        return creation;
    }

    public Node selectChild(){
        Node best = null;
        double bestUCT = children.get(0).uct();

        if(!board.toMove){
            for(Node child: children){
                double uct = child.uct();
                if(uct <= bestUCT){
                    bestUCT = uct;
                    best = child;
                }
            }
        } else {
            for(Node child: children){
                double uct = child.uct();
                if(uct >= bestUCT){
                    bestUCT = uct;
                    best = child;
                }
            }
        }

        return best;
    }

    public double uct(){
        double expand = Math.sqrt(2*Math.log(parent.visits)/this.visits);
        if(board.toMove){
            expand *= -1;
        }
        return this.wins/this.visits + expand;
    }

    public boolean isExpandable(){
        return !toExplore.isEmpty();
    }

    public void update(int value){
        this.wins += value;
        this.visits++;

        if(parent != null){
            parent.update(value);
        }
    }

    public int getRandomMove(){
        int index = rand.nextInt(toExplore.size());
        return toExplore.get(index);
    }

    public double getValue(){
        return wins/visits;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public int getTerminalValue() {
        return terminalValue;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminalValue(int terminalValue) {
        this.terminalValue = terminalValue;
    }
}
