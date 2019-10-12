package application.controllers;

import application.MethodHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MediaController {

    @FXML
    private Slider volumeSlider;

    @FXML
    private MediaView mediaView;

    @FXML
    private Label timeLabel;

    @FXML
    private Slider timeSlider;

    @FXML
    private Button muteButton;

    @FXML
    private Button rewindButton;

    @FXML
    private Button playPauseButton;

    @FXML
    private Button fastForwardButton;

    private MethodHelper methodHelper = new MethodHelper();
    private Media video;
    private MediaPlayer player;
    private Duration duration;
    private File videoFile;
    private double[] volumeBeforeMute = {1};
    private FXMLLoader loader;


    public MediaController() {
        loader = new FXMLLoader(getClass().getResource("scenes/Media.fxml"));
        videoFile = new File("src/creations/test/test.mp4");
        createMediaPlayer();
    }

    private void setUpProperties() {
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                if (oldValue != newValue) {

                    player.setVolume(volumeSlider.getValue());
                }
            }
        });

        timeSlider.valueProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {

                if (timeSlider.isPressed()) {

                    player.seek(player.getMedia().getDuration().multiply(timeSlider.getValue() / 100.00));
                }
            }
        });

        player.currentTimeProperty().addListener(new ChangeListener<Duration>() {

            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
                                Duration newValue) {

                String time = "";
                time += String.format("%02d", (int) newValue.toMinutes());
                time += ":";
                time += String.format("%02d", (int) newValue.toSeconds());
                String totalMins = String.format("%02d", (int) duration.toMinutes());
                String totalSecs = String.format("%02d", (int) (duration.toSeconds() - Integer.parseInt(totalMins)*60));
                timeLabel.setText(time + "/" + totalMins + ":" + totalSecs);
                timeSlider.setValue(player.getCurrentTime().toMinutes()/player.getTotalDuration().toMinutes()*100.00);
            }
        });

    }

    @FXML
    void fastForwardMedia(ActionEvent event) {

        player.seek( player.getCurrentTime().add( Duration.seconds(5)) );
    }

    @FXML
    void muteMedia(ActionEvent event) {

        if (volumeSlider.getValue()!=0) {

            volumeBeforeMute[0] = volumeSlider.getValue();
            volumeSlider.setValue(0);
            muteButton.setText("Unmute");
        } else {

            muteButton.setText("Mute");
            volumeSlider.setValue(volumeBeforeMute[0]);
        }
    }

    @FXML
    void playPauseMedia(ActionEvent event) {
        if (player.getStatus() == MediaPlayer.Status.PLAYING) {

            player.pause();
            playPauseButton.setText("Play");
        } else {

            playPauseButton.setText("Pause");
            player.play();
        }    }

    @FXML
    void returnToCreationList(ActionEvent event) throws Exception {
        player.stop();
        player.dispose();
        methodHelper.changeScene(event, "scenes/CreationList.fxml");
    }

    @FXML
    void returnToMenu(ActionEvent event) throws Exception {
        player.stop();
        player.dispose();
        methodHelper.changeScene(event, "scenes/MainMenu.fxml");
    }

    @FXML
    void rewindMedia(ActionEvent event) {

        player.seek( player.getCurrentTime().add( Duration.seconds(-3)) );
    }

    /**
     * This creates the media player with name of the creation being played
     */
    public void createMediaPlayer() {

        video = new Media(videoFile.toURI().toString());
        player = new MediaPlayer(video);

        String command = "ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 " + videoFile;
        String getDuration = methodHelper.command(command);

        double milliseconds = Double.parseDouble(getDuration) * 1000;
        duration = new Duration(milliseconds);
        System.out.println(duration);
        player.setOnReady(new Runnable() {
            @Override
            public void run() {
                mediaView.setMediaPlayer(player);
                setUpProperties();
                player.play();
                System.out.println(player.getVolume());
                volumeBeforeMute[0] = player.getVolume()*100;
            }
        });

        player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {

                player.stop();
                playPauseButton.setText("Play");
            }
        });
    }

}

