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

    String actualTarget = "snake";

    Button turtle;
    Button squirrel;
    Button snake;
    Button rabbit;
    Button owl;
    Button octopus;
    Button lion;
    Button hippo;
    Button hedgehog;
    Button giraffe;
    Button fox;
    Button elephant;
    Button dog;
    Button crab;
    Button bear;
    Button astonished;

    public OptionsCalibrationPane(Stage primaryStage, Main main, CalibrationConfig calibrationConfig){
        super();

        Button back = createBackButton(main, primaryStage);

        turtle = createTurtleButton(calibrationConfig);
        squirrel = createSquirrelButton(calibrationConfig);
        snake = createSnakeButton(calibrationConfig);
        rabbit = createRabbitButton(calibrationConfig);
        owl = createOwlButton(calibrationConfig);
        octopus = createOctopusButton(calibrationConfig);
        lion = createLionButton(calibrationConfig);
        hippo = createHippoButton(calibrationConfig);
        hedgehog = createHedgehogButton(calibrationConfig);
        giraffe = createGiraffeButton(calibrationConfig);
        fox = createFoxButton(calibrationConfig);
        elephant = createElephantButton(calibrationConfig);
        dog = createDogButton(calibrationConfig);
        crab = createCrabButton(calibrationConfig);
        bear = createBearButton(calibrationConfig);
        astonished = createAstonishedButton(calibrationConfig);

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
            primaryStage.setWidth(main.width);
            primaryStage.setHeight(main.height);
            main.goToOptions(primaryStage);
        });
        return back;
    }

    public Button createAstonishedButton(CalibrationConfig calibrationConfig){
        Button astonished = new MainButton("");
        astonished.setGraphic(createButtonImageView("images/target/astonished.png"));
        astonished.getStyleClass().add("white");
        astonished.setPrefHeight(200);
        astonished.setPrefWidth(495. / 5);
        astonished.setOnAction((e) -> {
            changeSelectedTarget();
            astonished.getStyleClass().add("selected");
            actualTarget = "astonished";
            calibrationConfig.setImgUse("images/target/astonished.png");
        });
        return astonished;
    }

    public Button createBearButton(CalibrationConfig calibrationConfig){
        Button bear = new MainButton("");
        bear.setGraphic(createButtonImageView("images/target/bear.jpg"));
        bear.getStyleClass().add("white");
        bear.setPrefHeight(200);
        bear.setPrefWidth(495. / 5);
        bear.setOnAction((e) -> {
            changeSelectedTarget();
            bear.getStyleClass().add("selected");
            actualTarget = "bear";
            calibrationConfig.setImgUse("images/target/bear.jpg");
        });
        return bear;
    }

    public Button createCrabButton(CalibrationConfig calibrationConfig){
        Button crab = new MainButton("");
        crab.setGraphic(createButtonImageView("images/target/crab.png"));
        crab.getStyleClass().add("white");
        crab.setPrefHeight(200);
        crab.setPrefWidth(495. / 5);
        crab.setOnAction((e) -> {
            changeSelectedTarget();
            crab.getStyleClass().add("selected");
            actualTarget = "crab";
            calibrationConfig.setImgUse("images/target/crab.png");
        });
        return crab;
    }

    public Button createDogButton(CalibrationConfig calibrationConfig){
        Button dog = new MainButton("");
        dog.setGraphic(createButtonImageView("images/target/dog.jpg"));
        dog.getStyleClass().add("white");
        dog.setPrefHeight(200);
        dog.setPrefWidth(495. / 5);
        dog.setOnAction((e) -> {
            changeSelectedTarget();
            dog.getStyleClass().add("selected");
            actualTarget = "dog";
            calibrationConfig.setImgUse("images/target/dog.jpg");
        });
        return dog;
    }

    public Button createElephantButton(CalibrationConfig calibrationConfig){
        Button elephant = new MainButton("");
        elephant.setGraphic(createButtonImageView("images/target/elephant.jpg"));
        elephant.getStyleClass().add("white");
        elephant.setPrefHeight(200);
        elephant.setPrefWidth(495. / 5);
        elephant.setOnAction((e) -> {
            changeSelectedTarget();
            elephant.getStyleClass().add("selected");
            actualTarget = "elephant";
            calibrationConfig.setImgUse("images/target/elephant.jpg");
        });
        return elephant;
    }

    public Button createFoxButton(CalibrationConfig calibrationConfig){
        Button fox = new MainButton("");
        fox.setGraphic(createButtonImageView("images/target/fox.jpg"));
        fox.getStyleClass().add("white");
        fox.setPrefHeight(200);
        fox.setPrefWidth(495. / 5);
        fox.setOnAction((e) -> {
            changeSelectedTarget();
            fox.getStyleClass().add("selected");
            actualTarget = "fox";
            calibrationConfig.setImgUse("images/target/fox.jpg");
        });
        return fox;
    }

    public Button createGiraffeButton(CalibrationConfig calibrationConfig){
        Button giraffe = new MainButton("");
        giraffe.setGraphic(createButtonImageView("images/target/giraffe.jpg"));
        giraffe.getStyleClass().add("white");
        giraffe.setPrefHeight(200);
        giraffe.setPrefWidth(495. / 5);
        giraffe.setOnAction((e) -> {
            changeSelectedTarget();
            giraffe.getStyleClass().add("selected");
            actualTarget = "giraffe";
            calibrationConfig.setImgUse("images/target/giraffe.jpg");
        });
        return giraffe;
    }

    public Button createHedgehogButton(CalibrationConfig calibrationConfig){
        Button hedgehog = new MainButton("");
        hedgehog.setGraphic(createButtonImageView("images/target/hedgehog.png"));
        hedgehog.getStyleClass().add("white");
        hedgehog.setPrefHeight(200);
        hedgehog.setPrefWidth(495. / 5);
        hedgehog.setOnAction((e) -> {
            changeSelectedTarget();
            hedgehog.getStyleClass().add("selected");
            actualTarget = "hedgehog";
            calibrationConfig.setImgUse("images/target/hedgehog.png");
        });
        return hedgehog;
    }

    public Button createHippoButton(CalibrationConfig calibrationConfig){
        Button hippo = new MainButton("");
        hippo.setGraphic(createButtonImageView("images/target/hippo.jpg"));
        hippo.getStyleClass().add("white");
        hippo.setPrefHeight(200);
        hippo.setPrefWidth(495. / 5);
        hippo.setOnAction((e) -> {
            changeSelectedTarget();
            hippo.getStyleClass().add("selected");
            actualTarget = "hippo";
            calibrationConfig.setImgUse("images/target/hippo.jpg");
        });
        return hippo;
    }

    public Button createLionButton(CalibrationConfig calibrationConfig){
        Button lion = new MainButton("");
        lion.setGraphic(createButtonImageView("images/target/lion.jpg"));
        lion.getStyleClass().add("white");
        lion.setPrefHeight(200);
        lion.setPrefWidth(495. / 5);
        lion.setOnAction((e) -> {
            changeSelectedTarget();
            lion.getStyleClass().add("selected");
            actualTarget = "lion";
            calibrationConfig.setImgUse("images/target/lion.jpg");
        });
        return lion;
    }

    public Button createOctopusButton(CalibrationConfig calibrationConfig){
        Button octopus = new MainButton("");
        octopus.setGraphic(createButtonImageView("images/target/octopus.png"));
        octopus.getStyleClass().add("white");
        octopus.setPrefHeight(200);
        octopus.setPrefWidth(495. / 5);
        octopus.setOnAction((e) -> {
            changeSelectedTarget();
            octopus.getStyleClass().add("selected");
            actualTarget = "octopus";
            calibrationConfig.setImgUse("images/target/octopus.png");
        });
        return octopus;
    }

    public Button createOwlButton(CalibrationConfig calibrationConfig){
        Button owl = new MainButton("");
        owl.setGraphic(createButtonImageView("images/target/owl.png"));
        owl.getStyleClass().add("white");
        owl.setPrefHeight(200);
        owl.setPrefWidth(495. / 5);
        owl.setOnAction((e) -> {
            changeSelectedTarget();
            owl.getStyleClass().add("selected");
            actualTarget = "owl";
            calibrationConfig.setImgUse("images/target/owl.png");
        });
        return owl;
    }

    public Button createRabbitButton(CalibrationConfig calibrationConfig){
        Button rabbit = new MainButton("");
        rabbit.setGraphic(createButtonImageView("images/target/rabbit.png"));
        rabbit.getStyleClass().add("white");
        rabbit.setPrefHeight(200);
        rabbit.setPrefWidth(495. / 5);
        rabbit.setOnAction((e) -> {
            changeSelectedTarget();
            rabbit.getStyleClass().add("selected");
            actualTarget = "rabbit";
            calibrationConfig.setImgUse("images/target/rabbit.png");
        });
        return rabbit;
    }

    public Button createSnakeButton(CalibrationConfig calibrationConfig){
        Button snake = new MainButton("");
        snake.setGraphic(createButtonImageView("images/target/snake.jpg"));
        snake.getStyleClass().add("white");
        snake.getStyleClass().add("selected");
        snake.setPrefHeight(200);
        snake.setPrefWidth(495. / 5);
        snake.setOnAction((e) -> {
            changeSelectedTarget();
            snake.getStyleClass().add("selected");
            actualTarget = "snake";
            calibrationConfig.setImgUse("images/target/snake.jpg");
        });
        return snake;
    }

    public Button createSquirrelButton(CalibrationConfig calibrationConfig){
        Button squirrel = new MainButton("");
        squirrel.setGraphic(createButtonImageView("images/target/squirrel.png"));
        squirrel.getStyleClass().add("white");
        squirrel.setPrefHeight(200);
        squirrel.setPrefWidth(495. / 5);
        squirrel.setOnAction((e) -> {
            changeSelectedTarget();
            squirrel.getStyleClass().add("selected");
            actualTarget = "squirrel";
            calibrationConfig.setImgUse("images/target/squirrel.png");
        });
        return squirrel;
    }

    public Button createTurtleButton(CalibrationConfig calibrationConfig){
        Button turtle = new MainButton("");
        turtle.setGraphic(createButtonImageView("images/target/turtle.jpg"));
        turtle.getStyleClass().add("white");
        turtle.setPrefHeight(200);
        turtle.setPrefWidth(495. / 5);
        turtle.setOnAction((e) -> {
            changeSelectedTarget();
            turtle.getStyleClass().add("selected");
            actualTarget = "turtle";
            calibrationConfig.setImgUse("images/target/turtle.jpg");
        });
        return turtle;
    }

    public void changeSelectedTarget(){

        switch (actualTarget){

            case "turtle":
                turtle.getStyleClass().remove("selected");
                break;

            case "squirrel":
                squirrel.getStyleClass().remove("selected");
                break;

            case "snake":
                snake.getStyleClass().remove("selected");
                break;

            case "rabbit":
                rabbit.getStyleClass().remove("selected");
                break;

            case "owl":
                owl.getStyleClass().remove("selected");
                break;

            case "octopus":
                octopus.getStyleClass().remove("selected");
                break;

            case "lion":
                lion.getStyleClass().remove("selected");
                break;

            case "hippo":
                hippo.getStyleClass().remove("selected");
                break;

            case "hedgehog":
                hedgehog.getStyleClass().remove("selected");
                break;

            case "giraffe":
                giraffe.getStyleClass().remove("selected");
                break;

            case "fox":
                fox.getStyleClass().remove("selected");
                break;

            case "elephant":
                elephant.getStyleClass().remove("selected");
                break;

            case "dog":
                dog.getStyleClass().remove("selected");
                break;

            case "crab":
                crab.getStyleClass().remove("selected");
                break;

            case "bear":
                bear.getStyleClass().remove("selected");
                break;

            case "astonished":
                astonished.getStyleClass().remove("selected");
                break;

            default:
                break;
        }
    }
}
