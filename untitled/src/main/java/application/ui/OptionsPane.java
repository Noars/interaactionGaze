package application.ui;

import application.Main;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import static application.ui.MainPane.createButtonImageView;

public class OptionsPane extends BorderPane {

    HBox hbox;

    public OptionsPane(Stage primaryStage, Main main) {
        super();

        Button back = createBackButton(main, primaryStage);

        Button calibrate = createCalibrateButton(main, primaryStage);


        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        {
            Label fixationLabel = new Label("Temps de fixation:");
            Label milliSecondesLabel = new Label("millisecondes");
            TextField dwellTime = new TextField("" + main.getMouseInfo().DWELL_TIME);
            gridPane.add(fixationLabel, 0, 0);
            gridPane.add(dwellTime, 1, 0);
            gridPane.add(milliSecondesLabel, 2, 0);

            fixationLabel.getStyleClass().add("text");
            milliSecondesLabel.getStyleClass().add("text");

            dwellTime.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    dwellTime.setText(newValue.replaceAll("[^\\d]", ""));
                }
                if (newValue.equals("")) {
                    dwellTime.setText("0");
                }
                main.getMouseInfo().DWELL_TIME = Integer.parseInt(dwellTime.getText());

            });
        }
        hbox = new HBox(back, calibrate, gridPane);
        hbox.setSpacing(5);
        hbox.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(hbox, Pos.CENTER);
        gridPane.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(gridPane, Pos.CENTER);
        this.setCenter(hbox);

        this.setStyle("-fx-background-color: #535e65");
    }

    public Button createCalibrateButton(Main main, Stage primaryStage) {
        Button calibrate = new MainButton("Calibrer");
        calibrate.setGraphic(createButtonImageView("images/white/calibrate.png"));
        calibrate.getStyleClass().add("purple");
        calibrate.setContentDisplay(ContentDisplay.TOP);
        calibrate.setPrefHeight(200);
        calibrate.setPrefWidth(495. / 5);
        calibrate.setOnAction((e) -> {
            main.startCalibration(primaryStage);
        });
        return calibrate;
    }

    public Button createBackButton(Main main, Stage primaryStage) {
        Button back = new MainButton("Retour");
        back.setGraphic(createButtonImageView("images/white/back.png"));
        back.getStyleClass().add("grey");
        back.setContentDisplay(ContentDisplay.TOP);
        back.setPrefHeight(200);
        back.setPrefWidth(495. / 5);
        back.setOnAction((e) -> {
            main.goToMain(primaryStage);
        });
        return back;
    }
}
