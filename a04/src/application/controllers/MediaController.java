package application.controllers;

import application.MethodHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

public class MediaController {

    MethodHelper methodHelper = new MethodHelper();

    @FXML
    private Label timeLabel;

    @FXML
    private Slider timeSlider;

    @FXML
    private Button muteButton;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Button rewindButton;

    @FXML
    private Button playPauseButton;

    @FXML
    private Button fastForwardButton;

    @FXML
    private Button menuButton;

    @FXML
    private Button creationListButton;

    @FXML
    void fastForwardMedia(ActionEvent event) {

    }

    @FXML
    void muteMedia(ActionEvent event) {

    }

    @FXML
    void playPauseMedia(ActionEvent event) {

    }

    @FXML
    void returnToCreationList(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/CreationList.fxml");
    }

    @FXML
    void returnToMenu(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/MainMenu.fxml");
    }

    @FXML
    void rewindMedia(ActionEvent event) {

    }

}

