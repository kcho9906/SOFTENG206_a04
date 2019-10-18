package application.controllers;

import application.Main;
import application.MethodHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaView;

import java.net.URL;
import java.util.ResourceBundle;

public class quizController implements Initializable {

    private static MethodHelper methodHelper = Main.getMethodHelper();

    @FXML
    private BorderPane mediaPane;

    @FXML
    private TextField answerTextField;

    @FXML
    private Button checkAnswerButton;

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
    private Button creationListButton;

    @FXML
    private MediaView mediaView;

    @FXML
    void checkAnswer(ActionEvent event) {

    }

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
    void returnToCreationList(ActionEvent event) {

    }

    @FXML
    void rewindMedia(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(methodHelper.getDuration());
        // play a random video

    }


}
