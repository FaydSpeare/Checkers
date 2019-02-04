package CheckersGame;

import UCT.SearchData;
import UCT.Tree;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoardUI extends VBox {

    public boolean whiteAI = false;
    public boolean blackAI = false;

    public double whiteAITime = 1;
    public double blackAITime = 1;

    public double whiteEval;
    public double blackEval;

    public List<Board> whitePV = new ArrayList<>(Collections.singletonList(MoveGen.startingPos()));
    public List<Board> blackPV = new ArrayList<>(Collections.singletonList(MoveGen.startingPos()));

    private volatile boolean playing = false;

    private Board board;
    private int[] boardDisplay = new int[64];
    private int[] boardDisplayKings = new int[64];
    private int tileSize;

    private final Color[] boardColors = new Color[]{Color.WHITE, Color.ROSYBROWN};
    private final Color[] playerColors = new Color[]{Color.STEELBLUE, Color.MINTCREAM};

    private int selectedTile = -1;
    private int moveSelection = 0;
    private List<Integer> highlight = new ArrayList<>();

    private int borderSize = 10;
    private Canvas canvas;

    public boolean needsUpdating = false;

    private int historyIndex;

    private List<Board> history;

    public BoardUI(double size){
        canvas = new Canvas(size, size);

        createListeners();

        this.board = MoveGen.startingPos();
        this.history = new ArrayList<>();
        history.add(board.replicate());

        this.tileSize = (int) (size/8);
        this.getChildren().add(canvas);

        this.setMaxSize(size, size);
        this.setMinSize(size, size);

        constructPanel();

        Thread gameThread = new Thread(() -> {
            while (true) {

                while (!board.isGameOver()) {
                    if (playing) {
                        double time;
                        if (board.toMove && whiteAI) {
                            time = whiteAITime;
                            SearchData result = Tree.runUCT(board, time, board.toMove, true);
                            board.makeMove(result.move);
                            history.add(board.replicate());
                            historyIndex++;
                            whiteEval = result.evaluation;
                            whitePV = result.pvs;
                            needsUpdating = true;
                            redraw(board);
                        } else if (!board.toMove && blackAI) {
                            time = blackAITime;
                            SearchData result = Tree.runUCT(board, time, board.toMove, false);
                            board.makeMove(result.move);
                            history.add(board.replicate());
                            historyIndex++;
                            blackEval = result.evaluation;
                            blackPV = result.pvs;
                            needsUpdating = true;
                            redraw(board);
                        }

                    }

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
        gameThread.start();

        redraw(board);
    }

    private void constructPanel(){
        HBox panel = new HBox();

        Button fullLeft = new Button("<<");
        fullLeft.setMinSize(35, 25);
        fullLeft.setMaxSize(35, 25);
        fullLeft.setOnAction(this::bottomDepth);

        Button left = new Button("<");
        left.setMinSize(25, 25);
        left.setMaxSize(25, 25);
        left.setOnAction(this::reduceDepth);

        Button right = new Button(">");
        right.setMinSize(25, 25);
        right.setMaxSize(25, 25);
        right.setOnAction(this::increaseDepth);

        Button fullRight = new Button(">>");
        fullRight.setMinSize(35, 25);
        fullRight.setMaxSize(35, 25);
        fullRight.setOnAction(this::topDepth);

        panel.setPadding(new Insets(1));
        panel.setAlignment(Pos.CENTER);
        panel.getChildren().addAll(fullLeft, left, right, fullRight);
        panel.setSpacing(10);
        this.getChildren().add(panel);
    }

    private void createListeners(){
        canvas.setOnMousePressed((this::handleClick));
    }

    private void handleClick(MouseEvent e){
        int row = 7 - (int)Math.floor(e.getX() / tileSize);
        int col = 7 - (int)Math.floor(e.getY() / tileSize);
        int pos = row + 8*col;
        int boardPosition = MoveGen.convertedBitIndex[(int)Math.floor(pos/2)];

        if(historyIndex != history.size()-1){
            return;
        }

        if(board.toMove){
            if(selectedTile == -1){
                if(boardDisplay[pos] == 1){
                    selectedTile = pos;
                    moveSelection = MoveGen.getSingleWhiteMove(board, 1<<boardPosition);
                    highlightMoves(moveSelection & ~board.black);
                }
            } else {
                if(boardDisplay[pos] == 0 && highlight.contains(pos)){
                    int selectedPosition = MoveGen.convertedBitIndex[(int)Math.floor(selectedTile/2)];
                    int move = (1 << selectedPosition | 1 << boardPosition | board.black) & moveSelection;
                    board.makeMove(move);
                    history.add(board.replicate());
                    historyIndex++;
                    selectedTile = -1;
                    highlight.clear();
                    redraw(board);
                }
                else if(boardDisplay[pos] == 1){
                    selectedTile = pos;
                    highlight.clear();
                    moveSelection = MoveGen.getSingleWhiteMove(board, 1<<boardPosition);
                    highlightMoves(moveSelection & ~board.black);
                }
            }
        }
        else {
            if(selectedTile == -1){
                if(boardDisplay[pos] == 2){
                    selectedTile = pos;
                    moveSelection = MoveGen.getSingleBlackMove(board, 1<<boardPosition);
                    highlightMoves(moveSelection & ~board.white);
                }
            } else {
                if(boardDisplay[pos] == 0 && highlight.contains(pos)){
                    int selectedPosition = MoveGen.convertedBitIndex[(int)Math.floor(selectedTile/2)];
                    int move = (1 << selectedPosition | 1 << boardPosition | board.white) & moveSelection;
                    board.makeMove(move);
                    history.add(board.replicate());
                    historyIndex++;
                    selectedTile = -1;
                    highlight.clear();
                    redraw(board);
                }
                else if(boardDisplay[pos] == 2){
                    selectedTile = pos;
                    highlight.clear();
                    moveSelection = MoveGen.getSingleBlackMove(board, 1<<boardPosition);
                    highlightMoves(moveSelection & ~board.white);
                }
            }
        }

        redraw(history.get(historyIndex));

    }

    public void redraw(Board board){

        // Copy integer bits to board array.
        updateBoardDisplay(board);

        // Padding
        final double padding = .25*tileSize;
        final double piecePadding = 0.04*tileSize;
        final double tilePadding = 0.025*tileSize;
        final double kingSquarePadding = 0.4*tileSize;

        GraphicsContext g = canvas.getGraphicsContext2D();

        int num = 0;
        for(int tile: boardDisplay) {

            int row = 7 - num / 8;
            int col = 7 - num % 8;

            // background
            g.setFill(Color.BLACK);
            // highlighted
            if (num == selectedTile) g.setFill(Color.CYAN);
            else if (highlight.contains(num)) g.setFill(Color.HONEYDEW);

            g.fillRect(col * tileSize, row * tileSize, tileSize, tileSize);

            // background
            g.setFill(boardColors[(row + col) % 2]);
            g.fillRect(col * tileSize + tilePadding, row * tileSize + tilePadding, tileSize - 2 * tilePadding, tileSize -
                    2 * tilePadding);


            if (tile != 0) {

                // outer
                g.setFill(Color.BLACK);
                g.fillOval(col * tileSize + padding - piecePadding, row * tileSize + padding - piecePadding,
                        tileSize - 2 * padding + 2 * piecePadding, tileSize - 2 * padding + 2 * piecePadding);

                // inner
                g.setFill(playerColors[tile - 1]);
                g.fillOval(col * tileSize + padding, row * tileSize + padding, tileSize - 2 * padding, tileSize - 2 * padding);

                // black king circle
                if(boardDisplayKings[num] > 0){
                    g.setFill(Color.BLACK);
                    g.fillOval(col * tileSize + kingSquarePadding, row * tileSize + kingSquarePadding, tileSize - 2 *
                            kingSquarePadding, tileSize - 2 * kingSquarePadding);
                }
            }


            num++;
        }
    }

    private void updateBoardDisplay(Board board){
        for(int i = 0; i < 32; i++){
            int index = MoveGen.convertedBitIndex[i];
            int m = 2*i + 1;

            if((i / 4) % 2 == 1) m--;

            if(MoveGen.testBit(board.white, index)){
                boardDisplay[m] = 1;
            }
            else if(MoveGen.testBit(board.black, index)){
                boardDisplay[m] = 2;
            }
            else {
                boardDisplay[m] = 0;
            }

            if(MoveGen.testBit(board.whiteKings, index)){
                boardDisplayKings[m] = 1;
            }
            else if(MoveGen.testBit(board.blackKings, index)){
                boardDisplayKings[m] = 2;
            }
            else {
                boardDisplayKings[m] = 0;
            }
        }
    }

    private void highlightMoves(int integer){
        for(int i = 0; i < 32; i++){
            int index = MoveGen.convertedBitIndex[i];
            int m = 2*i + 1;

            if((i / 4) % 2 == 1) m--;

            if(MoveGen.testBit(integer, index)){
                highlight.add(m);
            }
        }
    }

    public void reset(){
        board = MoveGen.startingPos();
        whitePV = new ArrayList<>(Collections.singletonList(MoveGen.startingPos()));
        blackPV = new ArrayList<>(Collections.singletonList(MoveGen.startingPos()));
        history.clear();
        history.add(board.replicate());
        needsUpdating = true;
        whiteEval = 0;
        blackEval = 0;
        historyIndex = 0;
        redraw(board);
    }

    public void play(){
        playing = true;
    }

    public void stop(){
        playing = false;
    }

    private void topDepth(ActionEvent actionEvent) {
        historyIndex = history.size()-1;
        redraw(history.get(historyIndex));
    }

    private void bottomDepth(ActionEvent actionEvent) {
        historyIndex = 0;
        redraw(history.get(historyIndex));
    }

    private void increaseDepth(ActionEvent actionEvent) {
        if(historyIndex < history.size()-1) historyIndex++;
        redraw(history.get(historyIndex));
    }

    private void reduceDepth(ActionEvent actionEvent) {
        if(historyIndex > 0) historyIndex--;
        redraw(history.get(historyIndex));
    }
}
