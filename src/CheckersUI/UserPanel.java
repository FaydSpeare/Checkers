package CheckersUI;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class UserPanel extends VBox {

    private Button playButton;
    private Button stopButton;
    private Button resetButton;

    private Label whiteEvaluation;
    private Label blackEvaluation;

    private RadioButton whiteRB;
    private RadioButton blackRB;

    private BoardUI boardUI;

    private ComboBox whiteTime;
    private ComboBox blackTime;

    private DisplayBoard whitePVBoard;
    private DisplayBoard blackPVBoard;

    public UserPanel(BoardUI boardUI, DisplayBoard whitePVBoard, DisplayBoard blackPVBoard){
        super();
        this.boardUI = boardUI;
        this.whitePVBoard = whitePVBoard;
        this.blackPVBoard = blackPVBoard;
        initComponents();
    }

    private void initComponents(){



        HBox whiteAI = new HBox();

        whiteRB = new RadioButton();
        whiteRB.setOnAction(this::whiteRadio);

        whiteTime = new ComboBox();
        whiteTime.setDisable(true);
        whiteTime.setPromptText("White Time");
        whiteTime.setOnAction(this::whiteTimeUpdate);
        whiteTime.getItems().addAll(
                0.1,
                0.5,
                1.0,
                2.0,
                3.0,
                4.0,
                5.0,
                10.0,
                20.0,
                30.0
        );
        whiteAI.getChildren().addAll(whiteRB, whiteTime);
        this.getChildren().add(whiteAI);

        HBox blackAI = new HBox();

        blackRB = new RadioButton();
        blackRB.setOnAction(this::blackRadio);

        blackTime = new ComboBox();
        blackTime.setDisable(true);
        blackTime.setPromptText("Black Time");
        blackTime.setOnAction(this::blackTimeUpdate);
        blackTime.getItems().addAll(
                0.1,
                0.5,
                1.0,
                2.0,
                3.0,
                4.0,
                5.0,
                10.0,
                20.0,
                30.0
        );
        blackAI.getChildren().addAll(blackRB, blackTime);
        this.getChildren().add(blackAI);

        playButton = new Button("Play");
        playButton.setStyle("-fx-text-fill: #3cc439;");
        playButton.setOnAction(this::play);
        getChildren().add(playButton);

        stopButton = new Button("Stop");
        stopButton.setStyle("-fx-text-fill: #ff0000;");
        stopButton.setOnAction(this::stop);
        getChildren().add(stopButton);

        resetButton = new Button("Reset");
        resetButton.setStyle("-fx-text-fill: #394ec4;");
        resetButton.setOnAction(this::resetBoard);
        getChildren().add(resetButton);

        HBox evals = new HBox();
        whiteEvaluation = new Label("whiteEval");
        whiteEvaluation.setPadding(new Insets(5,5,5,5));
        whiteEvaluation.setStyle("-fx-background-color: #efcadc;");
        whiteEvaluation.setMinSize(60,30);
        whiteEvaluation.setMaxSize(60,30);
        blackEvaluation = new Label("blackEval");
        blackEvaluation.setPadding(new Insets(5,5,5,5));
        blackEvaluation.setStyle("-fx-background-color: #ebe7e9;");
        blackEvaluation.setMinSize(60,30);
        blackEvaluation.setMaxSize(60,30);
        evals.setSpacing(10);
        evals.getChildren().addAll(whiteEvaluation, blackEvaluation);
        getChildren().add(evals);

        this.setPadding(new Insets(10,10,10,10));
        this.setSpacing(15);
        this.setAlignment(Pos.CENTER);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(500), e -> {

                    if(boardUI.needsUpdating){
                        whiteEvaluation.setText(String.format("%.2g%n", boardUI.whiteEval));
                        blackEvaluation.setText(String.format("%.2g%n", boardUI.blackEval));
                        whitePVBoard.update(boardUI.whitePV);
                        blackPVBoard.update(boardUI.blackPV);
                    }
                    boardUI.needsUpdating = false;
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        this.setStyle("-fx-background-color: powderblue");
    }

    private void whiteTimeUpdate(Event event) {
        boardUI.whiteAITime = (double)whiteTime.getValue();
    }

    private void blackTimeUpdate(Event event) {
        boardUI.blackAITime = (double)blackTime.getValue();
    }

    private void stop(ActionEvent actionEvent) {
        boardUI.stop();
    }

    private void play(ActionEvent actionEvent) {
        boardUI.play();
    }

    private void blackRadio(ActionEvent actionEvent) {
        if(blackRB.isSelected()){
            blackTime.setDisable(false);
            boardUI.blackAI = true;
        } else {
            blackTime.setDisable(true);
            boardUI.blackAI = false;
        }
    }

    private void whiteRadio(ActionEvent actionEvent) {
        if(whiteRB.isSelected()){
            whiteTime.setDisable(false);
            boardUI.whiteAI = true;
        } else {
            whiteTime.setDisable(true);
            boardUI.whiteAI = false;
        }
    }

    private void resetBoard(ActionEvent actionEvent) {
        boardUI.reset();
    }

}
