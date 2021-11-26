package application.ui;

import gaze.devicemanager.TobiiGazeDeviceManager;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class Home extends BorderPane {

    public Home(TobiiGazeDeviceManager gazeDeviceManager){
        super();
        this.setWidth(500);
        this.setHeight(200);
        this.setTop(new Label("InteraactionGaze"));
        Button startstop = new Button("Stop");
        startstop.setOnAction((e)->{
            if(startstop.getText().contains("Stop")){
                gazeDeviceManager.setPause(true);
                startstop.setText("Play");
            } else {
                gazeDeviceManager.setPause(false);
                startstop.setText("Stop");
            }
        });
        this.setBottom(new HBox(startstop,new Button("Cacher le curseur"),new Button("Activer le click"), new Button("Options")));
    }
}
