package CheckersGame;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class DisplayBoard extends VBox {

    private Label depthLabel;

    private List<Board> history;
    private int[] boardDisplay = new int[64];
    private int[] boardDisplayKings = new int[64];
    private double tileSize;

    int borderSize = 15;

    int depth;

    private Canvas canvas;

    private final Color[] boardColors = new Color[]{Color.WHITE, Color.ROSYBROWN};
    private final Color[] playerColors = new Color[]{Color.LIGHTSTEELBLUE, Color.MINTCREAM};

    public DisplayBoard(double size, List<Board> history){
        size -= 2*borderSize + 25 + 2;
        this.canvas = new Canvas(size, size);
        this.history = history;
        this.tileSize = ((size)/8);

        this.getChildren().add(canvas);
        this.setPadding(new Insets(borderSize));

        constructPanel();
        redraw();
    }

    private void constructPanel(){
        HBox panel = new HBox();

        Button fullLeft = new Button("<<");
        fullLeft.setMinSize(35, 25);
        fullLeft.setMaxSize(35, 25);
        fullLeft.setOnAction(this::bottomDepth);
        fullLeft.setStyle("-fx-border-style: none");

        Button left = new Button("<");
        left.setMinSize(25, 25);
        left.setMaxSize(25, 25);
        left.setOnAction(this::reduceDepth);

        depthLabel = new Label("0");
        depthLabel.setMinSize(20, 25);
        depthLabel.setMaxSize(20, 25);
        depthLabel.setAlignment(Pos.CENTER);

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
        panel.getChildren().addAll(fullLeft, left, depthLabel, right, fullRight);
        this.getChildren().add(panel);

        this.setStyle("-fx-background-color: powderblue");
    }

    private void topDepth(ActionEvent actionEvent) {
        depth = history.size()-1;
        depthLabel.setText(Integer.toString(depth));
        redraw();
    }

    private void bottomDepth(ActionEvent actionEvent) {
        depth = 0;
        depthLabel.setText(Integer.toString(depth));
        redraw();
    }

    private void increaseDepth(ActionEvent actionEvent) {
        if(depth < history.size()-1) depth++;
        depthLabel.setText(Integer.toString(depth));
        redraw();
    }

    private void reduceDepth(ActionEvent actionEvent) {
        if(depth > 0) depth--;
        depthLabel.setText(Integer.toString(depth));
        redraw();
    }

    public void redraw(){

        // Copy integer bits to board array.
        updateBoardDisplay();

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

    private void updateBoardDisplay(){
        Board board = history.get(depth);

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

    public void update(List<Board> history){
        this.history = history;
        this.depth = history.size()-1;
        depthLabel.setText(Integer.toString(depth));
        redraw();
    }

    public static void r(){

    }

}

