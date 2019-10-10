package application.controllers;

import application.Creation;
import application.MethodHelper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class CreationListController implements Initializable {

    MethodHelper methodHelper = new MethodHelper();

    @FXML
    private TableView<Creation> creationTableView;

    @FXML
    private Button playButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button quizButton;

    @FXML
    private Button menuButton;

    @FXML
    void deleteCreation(ActionEvent event) {
        ObservableList<Creation> allCreations = creationTableView.getItems();
        Object creationSelected = creationTableView.getSelectionModel().getSelectedItem();
        String creationName = ((Creation) creationSelected).get_creationName();
        Boolean answer = methodHelper.addConfirmationAlert("Deleting Creation", "Are you sure you want to delete \"" + creationName + "\"?");
        if (answer) {

            allCreations.remove(creationSelected);
            String command = "rm -r -f src/creations/" + creationName;
            methodHelper.command(command);
        }
    }

    @FXML
    void playCreation(ActionEvent event) throws Exception {
        String creationName = creationTableView.getSelectionModel().getSelectedItem().toString();
        File creationFile = new File("src/creations/" + creationName + "/" + creationName + ".mp4");
        MediaController.setMedia(creationFile);
    }

    @FXML
    void quizCreation(ActionEvent event) {

    }

    @FXML
    void returnToMenu(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/MainMenu.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}

