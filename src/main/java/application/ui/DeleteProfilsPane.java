package application.ui;

import application.Main;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Objects;

@Slf4j
public class DeleteProfilsPane extends BorderPane {

    HBox hbox;
    String userSelected = "";

    public DeleteProfilsPane(Main main, Stage primaryStage, ProfilsPane profilsPane){
        super();
        this.setWidth(main.width);
        this.setHeight(main.height);

        GridPane deleteProfilsGridPane = new GridPane();
        deleteProfilsGridPane.setHgap(10);
        deleteProfilsGridPane.setVgap(10);
        {
            String[] listName = profilsPane.getAllUser();
            primaryStage.setHeight(profilsPane.getHeightNeeded(main, listName.length));

            Label allUser = new Label("Liste de tous les utilisateur :");
            allUser.setStyle("-fx-text-fill: white; -fx-font-size: 20px");
            deleteProfilsGridPane.add(allUser, 0, 1);

            ToggleGroup groupNames = new ToggleGroup();

            VBox vbBtnUser = new VBox(10);
            vbBtnUser.setAlignment(Pos.CENTER_LEFT);
            for (String s : listName) {
                RadioButton btnName = new RadioButton(s);
                btnName.setStyle("-fx-text-fill: white; -fx-font-size: 15px");
                btnName.setToggleGroup(groupNames);
                vbBtnUser.getChildren().add(btnName);
            }

            deleteProfilsGridPane.add(vbBtnUser, 0, 2);

            groupNames.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
                if (groupNames.getSelectedToggle() != null) {
                    String getBtnSelected = groupNames.getSelectedToggle().toString();
                    String[] getName = getBtnSelected.split("'");
                    userSelected = getName[1];
                }
            });

            Button btnDelete = new Button("Supprimer");
            Button btnReturn = new Button("Retour");
            HBox hbBtnDelete = new HBox(10);
            hbBtnDelete.setAlignment(Pos.CENTER);
            hbBtnDelete.getChildren().addAll(btnReturn, btnDelete);
            deleteProfilsGridPane.add(hbBtnDelete, 0, 3);

            final Text error = new Text();
            error.setStyle("-fx-font-size: 15px;");
            deleteProfilsGridPane.add(error, 0, 4);

            btnDelete.setOnAction( event -> {
                if (!Objects.equals(this.userSelected, "") && !Objects.equals(this.userSelected, "default")){
                    this.deleteUser();
                    primaryStage.setHeight(main.height);
                    main.goToProfils(primaryStage);
                } else if (Objects.equals(this.userSelected, "")){
                    error.setFill(Color.RED);
                    error.setText("Aucun utilisateur sélectionner !");
                }else {
                    error.setFill(Color.RED);
                    error.setText("Default ne peut être supprimer !");
                }
            });

            btnReturn.setOnAction(event -> {
                main.goToProfils(primaryStage);
            });
        }

        hbox = new HBox(deleteProfilsGridPane);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(hbox, Pos.CENTER);
        this.setCenter(hbox);

        this.setStyle("-fx-background-color: #535e65; -fx-background-radius: 0 0 15 15");
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
