package application.ui;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class DecoratedPane extends BorderPane {

    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

    private double xOffset = 0;
    private double yOffset = 0;

    public DecoratedPane(Stage primaryStage) {
        Button exit = new Button("fermer");
        exit.setOnAction((e) -> {
            System.exit(0);
        });

        Button minimize = new Button("minimiser");
        minimize.setOnAction((e) -> {
            primaryStage.setIconified(true);
        });

        HBox topBar = new HBox(minimize,exit);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        BorderPane.setAlignment(topBar, Pos.CENTER_RIGHT);
        this.setTop(topBar);
        this.setStyle("-fx-background-color: white; -fx-background-radius: 15");

        topBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        topBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double tempX, tempY;

                if (event.getScreenX() - xOffset < 0) {
                    tempX = 0;
                    xOffset = event.getSceneX();
                } else if (event.getScreenX() - xOffset < primaryScreenBounds.getWidth() - primaryStage.getWidth()) {
                    tempX = event.getScreenX() - xOffset;
                } else {
                    tempX = primaryScreenBounds.getWidth() - primaryStage.getWidth();
                    xOffset = event.getSceneX();
                }

                if (event.getScreenY() - yOffset < 0) {
                    tempY = 0;
                    yOffset = event.getSceneY();
                } else if (event.getScreenY() - yOffset < primaryScreenBounds.getHeight() - primaryStage.getHeight()) {
                    tempY = event.getScreenY() - yOffset;
                } else {
                    tempY = primaryScreenBounds.getHeight() - primaryStage.getHeight();
                    yOffset = event.getSceneY();
                }

                primaryStage.setX(tempX);
                primaryStage.setY(tempY);
            }
        });
    }
}
