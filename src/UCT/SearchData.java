package UCT;

import CheckersGame.Board;

import java.util.List;

public class SearchData {

    public double evaluation;
    public int move;
    public List<Board> pvs;

    public SearchData(int move, double evaluation, List<Board> pvs){
        this.evaluation = evaluation;
        this.move = move;
        this.pvs = pvs;
    }
}
