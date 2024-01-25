package application.ui;

import application.Main;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class AddProfilsPane extends BorderPane {

    HBox hbox;

    public AddProfilsPane(Main main, Stage primaryStage){
        super();
        this.setWidth(main.width);
        this.setHeight(main.height);

        GridPane addProfilsGridPane = new GridPane();
        addProfilsGridPane.setHgap(10);
        addProfilsGridPane.setVgap(10);
        {

            HBox enterUserName = new HBox(10);
            enterUserName.setAlignment(Pos.CENTER);

            Label userName = new Label("Nom utilisateur:");
            userName.setStyle("-fx-font-size: 15px; -fx-text-fill: white");
            TextField userTextField = new TextField();

            enterUserName.getChildren().addAll(userName, userTextField);
            addProfilsGridPane.add(enterUserName, 0, 2);

            Button btnAdd = new Button("Créer");
            Button btnReturn = new Button("Retour");
            HBox hbBtnAdd = new HBox(10);
            hbBtnAdd.setAlignment(Pos.CENTER);
            hbBtnAdd.getChildren().addAll(btnReturn, btnAdd);
            addProfilsGridPane.add(hbBtnAdd, 0, 4);

            final Text error = new Text();
            error.setStyle("-fx-font-size: 15px; -fx-text-fill: white");
            addProfilsGridPane.add(error, 0, 6);

            btnAdd.setOnAction(event -> {
                boolean emptyName = Objects.equals(userTextField.getText(), "");
                boolean alreadyExist = this.checkIfNameAlreadyExist(userTextField.getText());

                if (emptyName){
                    error.setFill(Color.RED);
                    error.setText("Nom invalide ! Ne peut pas être vide !");
                } else if (alreadyExist) {
                    error.setFill(Color.RED);
                    error.setText("Nom invalide ! Existe déjà !");
                } else {
                    this.saveUser(userTextField.getText());
                    main.goToProfils(primaryStage);
                }

            });

            btnReturn.setOnAction(event -> {
                main.goToProfils(primaryStage);
            });
        }

        hbox = new HBox(addProfilsGridPane);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);
        BorderPane.setAlignment(hbox, Pos.CENTER);
        this.setCenter(hbox);

        this.setStyle("-fx-background-color: #535e65; -fx-background-radius: 0 0 15 15");
    }

    public boolean checkIfNameAlreadyExist(String name){

        String[] pathnames;
        String userName = System.getProperty("user.name");

        File f = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils");
        pathnames = f.list();

        if (pathnames != null){
            for (String pathname : pathnames) {
                if (Objects.equals(pathname, name)){
                    return true;
                }
            }
        }
        return false;
    }
    public void saveUser(String name){

        String userName = System.getProperty("user.name");
        File newUser = new File("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils\\" + name);
        boolean createUser = newUser.mkdirs();
        log.info("Create new user = " + createUser);

        JSONObject json = new JSONObject();
        try {
            json.put("Name", name);
            json.put("FixationLength", 2000);
            json.put("SizeTarget", 50);
            json.put("RedColorBackground", "1.0");
            json.put("BlueColorBackground", "1.0");
            json.put("GreenColorBackground", "1.0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try (PrintWriter out = new PrintWriter(new FileWriter("C:\\Users\\" + userName + "\\Documents\\interAACtionGaze\\profils\\" + name + "\\settings.json", StandardCharsets.UTF_8))) {
            out.write(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
