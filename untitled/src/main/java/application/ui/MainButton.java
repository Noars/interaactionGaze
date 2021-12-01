package application.ui;

import javafx.scene.control.Button;

public class MainButton extends Button {

    public MainButton() {
        super();
        getStyleClass().add("customizedButton");
        this.applyCss();

    }

    public MainButton(String name) {
        super(name);
        getStyleClass().add("customizedButton");
        this.applyCss();
    }
}
