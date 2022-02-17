package application;

import application.ui.*;
import gaze.MouseInfo;
import gaze.devicemanager.GazeDeviceManagerFactory;
import gaze.devicemanager.TobiiGazeDeviceManager;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import utils.CalibrationConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

@Slf4j
public class Main extends Application {

    @Getter
    TobiiGazeDeviceManager gazeDeviceManager;
    @Getter
    CalibrationPane calibrationPane;
    @Getter
    MainPane home;
    @Getter
    MouseInfo mouseInfo;
    @Getter
    OptionsPane optionsPane;
    @Getter
    OptionsCalibrationPane optionsCalibrationPane;

    DecoratedPane decoratedPane;

    boolean startWithCalibration = false;

    public static void main(String[] args) {

        String os = System.getProperty("os.name").toLowerCase();
        String userName = System.getProperty("user.name");

        try {
            if (os.indexOf("nux") >= 0){
                File myFile = new File("args.txt");
                FileWriter myWritter = new FileWriter("args.txt");
                myWritter.write(args[0]);
                myWritter.close();
            }else{
                File myFolder = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze");
                myFolder.mkdirs();
                File myFile = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\args.txt");
                if (!myFile.exists()){
                    FileWriter myWritter = new FileWriter("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\args.txt");
                    myWritter.write("true");
                    myWritter.close();
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setWidth(600);
        primaryStage.setHeight(250);
        primaryStage.setTitle("InteraactionGaze");

        mouseInfo = new MouseInfo();
        CalibrationConfig calibrationConfig = new CalibrationConfig();
        gazeDeviceManager = GazeDeviceManagerFactory.getInstance().createNewGazeListener(this, calibrationConfig);

        optionsPane = new OptionsPane(primaryStage, this);
        optionsCalibrationPane = new OptionsCalibrationPane(primaryStage, this, calibrationConfig);
        calibrationPane = new CalibrationPane(primaryStage, gazeDeviceManager, calibrationConfig);
        home = new MainPane(this, primaryStage);

        decoratedPane = new DecoratedPane(primaryStage);
        decoratedPane.setCenter(home);

        Scene calibScene = new Scene(decoratedPane, primaryStage.getWidth(), primaryStage.getHeight());
        calibScene.getStylesheets().add("style.css");
        primaryStage.setScene(calibScene);
        calibScene.setFill(Color.TRANSPARENT);
        // calibrationPane.installEventHandler(primaryStage, this);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        String os = System.getProperty("os.name").toLowerCase();
        String userName = System.getProperty("user.name");

        try {
            File myFile = null;
            if (os.indexOf("win") >= 0){
                myFile = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\args.txt");
            }else {
                myFile = new File("args.txt");
            }

            Scanner myReader = new Scanner(myFile);
            String data = myReader.nextLine();

            if (Objects.equals(data, "true")){
                startCalibration(primaryStage);
                startWithCalibration = true;
                if(os.indexOf("win") >= 0){
                    try{
                        FileWriter myWritter = new FileWriter("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\args.txt");
                        myWritter.write("false");
                        myWritter.close();
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("");
        }

        if (!startWithCalibration){
            this.getGazeDeviceManager().setPause(true);
        }

        primaryStage.show();
    }

    public void startCalibration(Stage primaryStage) {
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.getScene().setRoot(this.getCalibrationPane());
        calibrationPane.startCalibration(this);
    }

    public void goToOptionsCalibration(Stage primaryStage){
        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(this.getOptionsCalibrationPane());
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }

    public void goToOptions(Stage primaryStage) {
        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(this.getOptionsPane());
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }

    public void goToMain(Stage primaryStage) {
        primaryStage.setFullScreen(false);
        primaryStage.getScene().setRoot(decoratedPane);
        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(this.getHome());
        primaryStage.getScene().setCursor(Cursor.DEFAULT);
    }
}
