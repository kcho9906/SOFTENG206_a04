package application.controllers;

import application.Creation;
import application.MethodHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

public class CreationListController {

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

    }

    @FXML
    void playCreation(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/Media.fxml");
    }

    @FXML
    void quizCreation(ActionEvent event) {

    }

    @FXML
    void returnToMenu(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/MainMenu.fxml");
    }

}

