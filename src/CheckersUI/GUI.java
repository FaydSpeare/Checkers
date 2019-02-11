package CheckersUI;

import CheckersGame.MoveGen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;

public class GUI extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        BorderPane root = new BorderPane();

        BoardUI interactiveBoard = new BoardUI(400);

        VBox pvs = new VBox();
        DisplayBoard whitePV = new DisplayBoard(200, new ArrayList<>(Collections.singletonList(MoveGen.startingPos())));
        DisplayBoard blackPV = new DisplayBoard(200, new ArrayList<>(Collections.singletonList(MoveGen.startingPos())));
        pvs.getChildren().addAll(blackPV, whitePV);

        UserPanel uPanel = new UserPanel(interactiveBoard, whitePV, blackPV);

        root.setLeft(pvs);
        root.setCenter(interactiveBoard);
        root.setRight(uPanel);

        primaryStage.setTitle(" Checkers ");
        primaryStage.setScene(new Scene(root, 760, 500));
        primaryStage.show();

    }
}
