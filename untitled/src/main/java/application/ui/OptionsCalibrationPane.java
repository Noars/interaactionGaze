package application.ui;

import application.Main;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import utils.CalibrationConfig;

import static application.ui.MainPane.createButtonImageView;

public class OptionsCalibrationPane extends BorderPane {

    HBox hbox;

    public OptionsCalibrationPane(Stage primaryStage, Main main, CalibrationConfig calibrationConfig){
        super();

        Button back = createBackButton(main, primaryStage);

        Button turtle = createTurtleButton(main, primaryStage, calibrationConfig);
        Button squirrel = createSquirrelButton(main, primaryStage, calibrationConfig);
        Button snake = createSnakeButton(main, primaryStage, calibrationConfig);
        Button rabbit = createRabbitButton(main, primaryStage, calibrationConfig);
        Button owl = createOwlButton(main, primaryStage, calibrationConfig);
        Button octopus = createOctopusButton(main, primaryStage, calibrationConfig);
        Button lion = createLionButton(main, primaryStage, calibrationConfig);
        Button hippo = createHippoButton(main, primaryStage, calibrationConfig);
        Button hedgehog = createHedgehogButton(main, primaryStage, calibrationConfig);
        Button giraffe = createGiraffeButton(main, primaryStage, calibrationConfig);
        Button fox = createFoxButton(main, primaryStage, calibrationConfig);
        Button elephant = createElephantButton(main, primaryStage, calibrationConfig);
        Button dog = createDogButton(main, primaryStage, calibrationConfig);
        Button crab = createCrabButton(main, primaryStage, calibrationConfig);
        Button bear = createBearButton(main, primaryStage, calibrationConfig);
        Button astonished = createAstonishedButton(main, primaryStage, calibrationConfig);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        gridPane.add(back, 0, 0);
        gridPane.add(turtle, 1, 0);
        gridPane.add(squirrel, 2, 0);
        gridPane.add(snake, 3, 0);
        gridPane.add(rabbit, 4, 0);
        gridPane.add(owl, 0, 1);
        gridPane.add(octopus, 1, 1);
        gridPane.add(lion, 2, 1);
        gridPane.add(hippo, 3, 1);
        gridPane.add(hedgehog, 4, 1);
        gridPane.add(giraffe, 0, 2);
        gridPane.add(fox, 1, 2);
        gridPane.add(elephant, 2, 2);
        gridPane.add(dog, 3, 2);
        gridPane.add(crab, 4, 2);
        gridPane.add(bear, 0, 3);
        gridPane.add(astonished, 1, 3);

        hbox = new HBox(gridPane);
        hbox.setSpacing(5);
        hbox.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(hbox, Pos.CENTER);
        gridPane.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(gridPane, Pos.CENTER);
        this.setCenter(hbox);

        this.setStyle("-fx-background-color: #535e65; -fx-background-radius: 0 0 15 15");
    }

    public Button createBackButton(Main main, Stage primaryStage) {
        Button back = new MainButton("Retour");
        back.setGraphic(createButtonImageView("images/white/back.png"));
        back.getStyleClass().add("grey");
        back.setContentDisplay(ContentDisplay.TOP);
        back.setPrefHeight(200);
        back.setPrefWidth(495. / 5);
        back.setOnAction((e) -> {
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToOptions(primaryStage);
        });
        return back;
    }

    public Button createAstonishedButton(Main main, Stage primaryStage, CalibrationConfig calibrationConfig){
        Button astonished = new MainButton("");
        astonished.setGraphic(createButtonImageView("images/target/astonished.png"));
        astonished.getStyleClass().add("white");
        astonished.setPrefHeight(200);
        astonished.setPrefWidth(495. / 5);
        astonished.setOnAction((e) -> {
            calibrationConfig.setImgUse("images/target/astonished.png");
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToOptions(primaryStage);
        });
        return astonished;
    }

    public Button createBearButton(Main main, Stage primaryStage, CalibrationConfig calibrationConfig){
        Button bear = new MainButton("");
        bear.setGraphic(createButtonImageView("images/target/bear.jpg"));
        bear.getStyleClass().add("white");
        bear.setPrefHeight(200);
        bear.setPrefWidth(495. / 5);
        bear.setOnAction((e) -> {
            calibrationConfig.setImgUse("images/target/bear.jpg");
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToOptions(primaryStage);
        });
        return bear;
    }

    public Button createCrabButton(Main main, Stage primaryStage, CalibrationConfig calibrationConfig){
        Button crab = new MainButton("");
        crab.setGraphic(createButtonImageView("images/target/crab.png"));
        crab.getStyleClass().add("white");
        crab.setPrefHeight(200);
        crab.setPrefWidth(495. / 5);
        crab.setOnAction((e) -> {
            calibrationConfig.setImgUse("images/target/crab.png");
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToOptions(primaryStage);
        });
        return crab;
    }

    public Button createDogButton(Main main, Stage primaryStage, CalibrationConfig calibrationConfig){
        Button dog = new MainButton("");
        dog.setGraphic(createButtonImageView("images/target/dog.jpg"));
        dog.getStyleClass().add("white");
        dog.setPrefHeight(200);
        dog.setPrefWidth(495. / 5);
        dog.setOnAction((e) -> {
            calibrationConfig.setImgUse("images/target/dog.jpg");
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToOptions(primaryStage);
        });
        return dog;
    }

    public Button createElephantButton(Main main, Stage primaryStage, CalibrationConfig calibrationConfig){
        Button elephant = new MainButton("");
        elephant.setGraphic(createButtonImageView("images/target/elephant.jpg"));
        elephant.getStyleClass().add("white");
        elephant.setPrefHeight(200);
        elephant.setPrefWidth(495. / 5);
        elephant.setOnAction((e) -> {
            calibrationConfig.setImgUse("images/target/elephant.jpg");
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToOptions(primaryStage);
        });
        return elephant;
    }

    public Button createFoxButton(Main main, Stage primaryStage, CalibrationConfig calibrationConfig){
        Button fox = new MainButton("");
        fox.setGraphic(createButtonImageView("images/target/fox.jpg"));
        fox.getStyleClass().add("white");
        fox.setPrefHeight(200);
        fox.setPrefWidth(495. / 5);
        fox.setOnAction((e) -> {
            calibrationConfig.setImgUse("images/target/fox.jpg");
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToOptions(primaryStage);
        });
        return fox;
    }

    public Button createGiraffeButton(Main main, Stage primaryStage, CalibrationConfig calibrationConfig){
        Button giraffe = new MainButton("");
        giraffe.setGraphic(createButtonImageView("images/target/giraffe.jpg"));
        giraffe.getStyleClass().add("white");
        giraffe.setPrefHeight(200);
        giraffe.setPrefWidth(495. / 5);
        giraffe.setOnAction((e) -> {
            calibrationConfig.setImgUse("images/target/giraffe.jpg");
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToOptions(primaryStage);
        });
        return giraffe;
    }

    public Button createHedgehogButton(Main main, Stage primaryStage, CalibrationConfig calibrationConfig){
        Button hedgehog = new MainButton("");
        hedgehog.setGraphic(createButtonImageView("images/target/hedgehog.png"));
        hedgehog.getStyleClass().add("white");
        hedgehog.setPrefHeight(200);
        hedgehog.setPrefWidth(495. / 5);
        hedgehog.setOnAction((e) -> {
            calibrationConfig.setImgUse("images/target/hedgehog.png");
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToOptions(primaryStage);
        });
        return hedgehog;
    }

    public Button createHippoButton(Main main, Stage primaryStage, CalibrationConfig calibrationConfig){
        Button hippo = new MainButton("");
        hippo.setGraphic(createButtonImageView("images/target/hippo.jpg"));
        hippo.getStyleClass().add("white");
        hippo.setPrefHeight(200);
        hippo.setPrefWidth(495. / 5);
        hippo.setOnAction((e) -> {
            calibrationConfig.setImgUse("images/target/hippo.jpg");
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToOptions(primaryStage);
        });
        return hippo;
    }

    public Button createLionButton(Main main, Stage primaryStage, CalibrationConfig calibrationConfig){
        Button lion = new MainButton("");
        lion.setGraphic(createButtonImageView("images/target/lion.jpg"));
        lion.getStyleClass().add("white");
        lion.setPrefHeight(200);
        lion.setPrefWidth(495. / 5);
        lion.setOnAction((e) -> {
            calibrationConfig.setImgUse("images/target/lion.jpg");
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToOptions(primaryStage);
        });
        return lion;
    }

    public Button createOctopusButton(Main main, Stage primaryStage, CalibrationConfig calibrationConfig){
        Button octopus = new MainButton("");
        octopus.setGraphic(createButtonImageView("images/target/octopus.png"));
        octopus.getStyleClass().add("white");
        octopus.setPrefHeight(200);
        octopus.setPrefWidth(495. / 5);
        octopus.setOnAction((e) -> {
            calibrationConfig.setImgUse("images/target/octopus.png");
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToOptions(primaryStage);
        });
        return octopus;
    }

    public Button createOwlButton(Main main, Stage primaryStage, CalibrationConfig calibrationConfig){
        Button owl = new MainButton("");
        owl.setGraphic(createButtonImageView("images/target/owl.png"));
        owl.getStyleClass().add("white");
        owl.setPrefHeight(200);
        owl.setPrefWidth(495. / 5);
        owl.setOnAction((e) -> {
            calibrationConfig.setImgUse("images/target/owl.png");
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToOptions(primaryStage);
        });
        return owl;
    }

    public Button createRabbitButton(Main main, Stage primaryStage, CalibrationConfig calibrationConfig){
        Button rabbit = new MainButton("");
        rabbit.setGraphic(createButtonImageView("images/target/rabbit.png"));
        rabbit.getStyleClass().add("white");
        rabbit.setPrefHeight(200);
        rabbit.setPrefWidth(495. / 5);
        rabbit.setOnAction((e) -> {
            calibrationConfig.setImgUse("images/target/rabbit.png");
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToOptions(primaryStage);
        });
        return rabbit;
    }

    public Button createSnakeButton(Main main, Stage primaryStage, CalibrationConfig calibrationConfig){
        Button snake = new MainButton("");
        snake.setGraphic(createButtonImageView("images/target/snake.jpg"));
        snake.getStyleClass().add("white");
        snake.setPrefHeight(200);
        snake.setPrefWidth(495. / 5);
        snake.setOnAction((e) -> {
            calibrationConfig.setImgUse("images/target/snake.jpg");
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToOptions(primaryStage);
        });
        return snake;
    }

    public Button createSquirrelButton(Main main, Stage primaryStage, CalibrationConfig calibrationConfig){
        Button squirrel = new MainButton("");
        squirrel.setGraphic(createButtonImageView("images/target/squirrel.png"));
        squirrel.getStyleClass().add("white");
        squirrel.setPrefHeight(200);
        squirrel.setPrefWidth(495. / 5);
        squirrel.setOnAction((e) -> {
            calibrationConfig.setImgUse("images/target/squirrel.png");
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToOptions(primaryStage);
        });
        return squirrel;
    }

    public Button createTurtleButton(Main main, Stage primaryStage, CalibrationConfig calibrationConfig){
        Button turtle = new MainButton("");
        turtle.setGraphic(createButtonImageView("images/target/turtle.jpg"));
        turtle.getStyleClass().add("white");
        turtle.setPrefHeight(200);
        turtle.setPrefWidth(495. / 5);
        turtle.setOnAction((e) -> {
            calibrationConfig.setImgUse("images/target/turtle.jpg");
            primaryStage.setWidth(600);
            primaryStage.setHeight(250);
            main.goToOptions(primaryStage);
        });
        return turtle;
    }


}
