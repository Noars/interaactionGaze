package application.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class Home extends BorderPane {

    public Home(){
        super();
        this.setWidth(500);
        this.setHeight(200);
        this.setTop(new Label("InteraactionGaze"));
    }
}
