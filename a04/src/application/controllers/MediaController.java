package application.controllers;

import application.MethodHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MediaController implements Initializable {

    private MethodHelper methodHelper = new MethodHelper();
    private Media video;
    private MediaPlayer player;
    private Duration duration;




    @FXML
    private MediaView mediaView;

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

    /**
     * This creates the media player with name of the creation being played
     */
    public void createMediaPlayer(File fileUrl) {

        video = new Media(fileUrl.toURI().toString());
        player = new MediaPlayer(video);
        player.setAutoPlay(true);

        String command = "ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 " + fileUrl;
        String getDuration = methodHelper.command(command);

        double milliseconds = Double.parseDouble(getDuration) * 1000;
        duration = new Duration(milliseconds);
        timeSlider.setValue(0);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        createMediaPlayer();
    }
}

