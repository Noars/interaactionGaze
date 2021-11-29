package application.ui;

import application.Main;
import gaze.devicemanager.TobiiGazeDeviceManager;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Home extends BorderPane {

    boolean running = true;
    boolean displayed = true;
    boolean clikcActivated = false;

    public Home(Main main, Stage primaryStage){
        super();
        this.setWidth(500);
        this.setHeight(200);
        this.setTop(new Label("InteraactionGaze"));

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

        Button clickActivation = new Button("Activer le click");
        Runnable helloRunnable = () -> {click(main.getMouseInfo());};
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(helloRunnable, 0, 5, TimeUnit.SECONDS);
        clickActivation.setOnAction((e)->{
            if(clikcActivated){
                clikcActivated = false;
                clickActivation.setText("Activer le clic");
            }else{
                clikcActivated = true;
                clickActivation.setText("Desactiver le clic");
            }
        });

        Button options = new Button("Options");

        Button calibrate = new Button("Calibrate");
        calibrate.setOnAction((e)->{main.startCalibration(primaryStage);});

        this.setBottom(new HBox(startstop,hide,clickActivation, options,calibrate));
    }

    public void click(gaze.MouseInfo mouseInfo){
        if(clikcActivated && mouseInfo.isBeingDwelled()) {
            System.out.println("clicking");
            Robot bot;
            try {
                int x = (int) MouseInfo.getPointerInfo().getLocation().getX();
                int y = (int) MouseInfo.getPointerInfo().getLocation().getY();
                bot = new Robot();
                bot.mouseMove(x, y);
                bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }
}
