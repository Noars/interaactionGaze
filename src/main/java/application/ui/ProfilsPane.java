package application.ui;

import application.Main;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static application.ui.MainPane.createButtonImageView;
@Slf4j
public class ProfilsPane extends BorderPane {

    HBox hbox;
    boolean isWindowsOpen = false;
    String userSelected = "";

    SelectProfilsPane selectProfilsPane;
    AddProfilsPane addProfilsPane ;

    public ProfilsPane(Main main, Stage primaryStage) {
        super();

        Button selectProfil = createSelectProfilButton(main, primaryStage);
        Button addProfil = createAddProfilButton(main, primaryStage);
        Button deleteProfil = createDeleteProfilButton(main, primaryStage);
        Button back = createBackButton(main, primaryStage);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        hbox = new HBox(back, selectProfil, addProfil, deleteProfil);
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
        back.setOnAction((e) -> main.goToMain(primaryStage));
        return back;
    }

    public Button createSelectProfilButton(Main main, Stage primaryStage){
        Button selectProfil = new MainButton("Choisir Profil");
        selectProfil.setGraphic(createButtonImageView("images/white/selectUser.png"));
        selectProfil.getStyleClass().add("red");
        selectProfil.setContentDisplay(ContentDisplay.TOP);
        selectProfil.setPrefHeight(200);
        selectProfil.setPrefWidth(495. / 5);
        selectProfil.setOnAction((e) -> {
            selectProfilsPane = new SelectProfilsPane(main,primaryStage, this);
            ((BorderPane) primaryStage.getScene().getRoot()).setCenter(this.selectProfilsPane);
            primaryStage.getScene().setCursor(Cursor.DEFAULT);
        });
        return selectProfil;
    }

    public Button createAddProfilButton(Main main, Stage primaryStage){
        Button addProfil = new MainButton("ajouter Profil");
        addProfil.setGraphic(createButtonImageView("images/white/addUser.png"));
        addProfil.getStyleClass().add("blue");
        addProfil.setContentDisplay(ContentDisplay.TOP);
        addProfil.setPrefHeight(200);
        addProfil.setPrefWidth(495. / 5);
        addProfil.setOnAction((e) -> {
            addProfilsPane = new AddProfilsPane(main,primaryStage);
            ((BorderPane) primaryStage.getScene().getRoot()).setCenter(this.addProfilsPane);
            primaryStage.getScene().setCursor(Cursor.DEFAULT);
        });
        return addProfil;
    }

    public Button createDeleteProfilButton(Main main, Stage primaryStage){
        Button deleteProfil = new MainButton("Supprimer Profil");
        deleteProfil.setGraphic(createButtonImageView("images/white/deleteUser.png"));
        deleteProfil.getStyleClass().add("purple");
        deleteProfil.setContentDisplay(ContentDisplay.TOP);
        deleteProfil.setPrefHeight(200);
        deleteProfil.setPrefWidth(495. / 5);
        deleteProfil.setOnAction((e) -> {

            if (!this.isWindowsOpen){

                this.isWindowsOpen = true;
                String[] listName = this.getAllUser();

                GridPane deleteProfilGridPane = new GridPane();
                deleteProfilGridPane.setAlignment(Pos.CENTER);
                deleteProfilGridPane.setHgap(10);
                deleteProfilGridPane.setVgap(10);
                deleteProfilGridPane.setPadding(new Insets(25, 25, 25, 25));

                Stage deleteProfilStage = new Stage();

                if (listName.length == 0){
                    Text noUser = new Text("Il n'y a pas d'utilisateur !");
                    noUser.setFill(Color.RED);
                    deleteProfilGridPane.add(noUser, 0, 1);
                }else {
                    Label allUser = new Label("Liste de tous les utilisateur :");
                    deleteProfilGridPane.add(allUser, 0, 1);

                    ToggleGroup groupNames = new ToggleGroup();
                    int maxIndex = 0;

                    for (int i=1; i<listName.length; i++) {
                        RadioButton btnName = new RadioButton(listName[i]);
                        btnName.setToggleGroup(groupNames);
                        deleteProfilGridPane.add(btnName, 0, i+2);
                        maxIndex = i+2;
                    }

                    groupNames.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
                        if (groupNames.getSelectedToggle() != null) {
                            String getBtnSelected = groupNames.getSelectedToggle().toString();
                            String[] getName = getBtnSelected.split("'");
                            userSelected = getName[1];
                        }
                    });

                    Button btnDelete = new Button("Supprimer");
                    HBox hbBtnDelete = new HBox(10);
                    hbBtnDelete.setAlignment(Pos.BOTTOM_LEFT);
                    hbBtnDelete.getChildren().add(btnDelete);
                    deleteProfilGridPane.add(hbBtnDelete, 0, maxIndex+1);

                    final Text error = new Text();
                    deleteProfilGridPane.add(error, 0, maxIndex+2);

                    btnDelete.setOnAction( event -> {
                        if (!Objects.equals(this.userSelected, "") && !Objects.equals(this.userSelected, "default")){
                            this.isWindowsOpen = false;
                            this.deleteUser();
                            deleteProfilStage.close();
                        } else if (Objects.equals(this.userSelected, "")){
                            error.setFill(Color.RED);
                            error.setText("Aucun utilisateur sélectionner !");
                        }else {
                            error.setFill(Color.RED);
                            error.setText("Default ne peut être supprimer !");
                        }
                    });
                }

                Scene deleteProfilScene = new Scene(deleteProfilGridPane, 400, 50 + (50 * (listName.length - 1)));
                deleteProfilScene.getStylesheets().add("style.css");

                deleteProfilStage.setTitle("Supprimer un profil");
                deleteProfilStage.setScene(deleteProfilScene);
                deleteProfilStage.setX(primaryStage.getX() + 100);
                deleteProfilStage.setY(primaryStage.getY() + 10);

                deleteProfilStage.show();

                deleteProfilStage.setOnCloseRequest(event -> this.isWindowsOpen = false);
            }
        });
        return deleteProfil;
    }

    public String[] getAllUser(){

        String[] pathnames;
        String userName = System.getProperty("user.name");

        File f = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils");
        pathnames = f.list();

        return pathnames;
    }

    public void deleteUser(){
        String userName = System.getProperty("user.name");
        File folder = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils\\" + this.userSelected);
        File[] contents = folder.listFiles();

        if (contents != null) {
            for (File f : contents) {
                boolean deleteFile = f.delete();
                log.info("File deleted -> " + deleteFile);
            }
        }
        boolean deleteUser = folder.delete();
        log.info("User deleted -> " + deleteUser);
    }
}
