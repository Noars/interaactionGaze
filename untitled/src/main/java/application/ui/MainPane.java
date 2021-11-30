package application.ui;

import application.Main;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainPane extends BorderPane {

    boolean running = true;
    boolean displayed = true;
    boolean iscancelled = false;

    public MainPane(Main main, Stage primaryStage){
        super();
        this.setWidth(500);
        this.setHeight(200);
        this.setTop(new Label("InteraactionGaze"));

        Button startstop = createStartStopButton(main);

        Button hide = createHideButton();

        Button clickActivation = createClickActivationButton(main);

        Button options = createOptionsButton(main, primaryStage);

        Button calibrate = createCalibrateButton(main, primaryStage);

        this.setBottom(new HBox(startstop,hide,clickActivation,calibrate,options));


        main.getMouseInfo().initTimer();
    }

    public Button createStartStopButton(Main main){
        Button startstop = new Button("Stop");
        startstop.setOnAction((e)->{
            if(running){
                running = false;
                main.getGazeDeviceManager().setPause(true);
                startstop.setText("Play");
            } else {
                running = true;
                main.getGazeDeviceManager().setPause(false);
                startstop.setText("Stop");
            }
        });
        return startstop;
    }

    public Button createHideButton(){
        Button hide = new Button("Cacher le curseur");
        hide.setOnAction((e)->{
            if(displayed) {
                displayed = false;
                this.setCursor(Cursor.NONE);
                hide.setText("Afficher le curseur");
            } else {
                displayed = true;
                this.setCursor(Cursor.CROSSHAIR);
                hide.setText("Cacher le curseur");
            }
        });
        return hide;
    }

    public Button createClickActivationButton(Main main){
        Button clickActivation = new Button("Activer le click");
        clickActivation.setOnAction((e)->{
            if(main.getMouseInfo().isClikcActivated()){
                main.getMouseInfo().setClikcActivated(false);
                iscancelled = true;
                clickActivation.setText("Activer le clic");
            }else{
                main.getMouseInfo().setClikcActivated(true);
                iscancelled = false;
                clickActivation.setText("Desactiver le clic");
            }
        });
        return clickActivation;
    }

    public Button createOptionsButton(Main main, Stage primaryStage){
        Button options = new Button("Options");
        options.setOnAction((e)->{main.goToOptions(primaryStage);});
        return options;
    }

    public Button createCalibrateButton(Main main, Stage primaryStage){
        Button calibrate = new Button("Calibrate");
        calibrate.setOnAction((e)->{main.startCalibration(primaryStage);});
        return calibrate;
    }
}
