package UCT;

import CheckersGame.Board;

import java.util.ArrayList;
import java.util.List;

public class Tree {

    public static SearchData runUCT(Board board, double timeAllowed, boolean player, boolean eval){
        Node root = new Node(board);

        double time = System.currentTimeMillis();
        double duration = 0;
        //int it = 0;

        while(duration <= timeAllowed){
            duration = (System.currentTimeMillis() - time)/1000;
            //it++;

            Node node = root;

            if(root.isTerminal()){
                return null;
            }

            //int depth = 0;
            while(!node.isExpandable()){
                node = node.selectChild();
                //depth++;

                if(node.isTerminal()){
                    break;
                }
            }
            //System.out.println(depth);

            if(node.isTerminal()){
                node.update(node.getTerminalValue());
                continue;
            }

            Node expanded = node.makeMove(node.getRandomMove());

            int result;
            if(expanded.board.isGameOver()){
                result = expanded.board.getWinner();
                expanded.setTerminal(true);
                expanded.setTerminalValue(result);
            } else {
                Board copy = expanded.board.replicate();
                result = copy.simulateGame(eval);
            }

            expanded.update(result);
        }
        //System.out.println("It: "+it);

        int bestMove = 0;
        double bestValue = root.children.get(0).getValue();

        for(Node child: root.children){
            double value = child.getValue();
            //System.out.println(child.lastMove+": "+value + ", visits: "+child.visits+", wins: "+child.wins);
            if(player){
                if(value >= bestValue){
                    bestValue = value;
                    bestMove = child.lastMove;
                }
            } else {
                if(value <= bestValue){
                    bestValue = value;
                    bestMove = child.lastMove;
                }
            }

        }

        //System.out.println("Top Eval: "+bestValue);

        Node node = root;

        List<Board> history = new ArrayList<>();
        while(!node.isExpandable()){
            node = node.selectChild();
            history.add(node.board.replicate());
            if(node.isTerminal()){
                break;
            }
        }

        return new SearchData(bestMove, bestValue, history);
    }

}
