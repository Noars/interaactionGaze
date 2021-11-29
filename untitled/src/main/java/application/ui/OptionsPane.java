package application.ui;

import application.Main;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class OptionsPane extends Pane {

    public OptionsPane(Stage primaryStage, Main main){
        super();
        BorderPane borderPane = new BorderPane();

        Button back = new Button("retour");
        back.setOnAction((e)->{
            main.goToMain(primaryStage);
        });

        borderPane.setTop(back);

        GridPane gridPane = new GridPane();
        {
            Label dwellTimeLabel = new Label("Temps de fixation:");
            TextField dwellTime = new TextField();
            gridPane.add(dwellTimeLabel,0,0);
            gridPane.add(dwellTime,1,0);
        }
        borderPane.setCenter(gridPane);
        this.getChildren().add(borderPane);
    }
}
