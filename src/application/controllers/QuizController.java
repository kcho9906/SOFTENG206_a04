package application.controllers;

import application.Main;
import application.MethodHelper;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class QuizController implements Initializable {

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

    private static MethodHelper methodHelper = Main.getMethodHelper();
    private MediaPlayer _player;
    private Duration duration;
    private File _videoFile;
    private double[] volumeBeforeMute = {1};
    private FXMLLoader loader;

    public QuizController(File videoFile) {
        _videoFile = videoFile;
    }

    private void setUpProperties() {
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                if (oldValue != newValue) {
                    System.out.println("old volume was " + _player.getVolume());
                    _player.setVolume(volumeSlider.getValue()/100);
                    System.out.println("new volume is " + _player.getVolume());

                }
            }
        });

        timeSlider.valueProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {

                if (timeSlider.isPressed()) {

                    _player.seek(_player.getMedia().getDuration().multiply(timeSlider.getValue() / 100.00));
                }
            }
        });

        _player.currentTimeProperty().addListener(new ChangeListener<Duration>() {

            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue,
                                Duration newValue) {

                String time = "";
                time += String.format("%02d", (int) newValue.toMinutes());
                time += ":";
                time += String.format("%02d", (int) newValue.toSeconds()%60);
                String totalMins = String.format("%02d", (int) duration.toMinutes());
                String totalSecs = String.format("%02d", (int) (duration.toSeconds() - Integer.parseInt(totalMins)*60));
                timeLabel.setText(time + "/" + totalMins + ":" + totalSecs);
                timeSlider.setValue(_player.getCurrentTime().toMinutes()/_player.getTotalDuration().toMinutes()*100.00);
            }
        });

    }


    @FXML
    void answerText(ActionEvent event) {
        System.out.println("HI");
    }

    @FXML
    void checkAnswer(ActionEvent event) {

    }

    @FXML
    void fastForwardMedia(ActionEvent event) {
        _player.seek( _player.getCurrentTime().add( Duration.seconds(5)) );
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
        if (_player.getStatus() == MediaPlayer.Status.PLAYING) {

            _player.pause();
            playPauseButton.setText("Play");
        } else {

            playPauseButton.setText("Pause");
            _player.play();
        }
    }

    @FXML
    void returnToCreationList(ActionEvent event) throws Exception {
        _player.stop();
        _player.dispose();
        methodHelper.changeScene(event, "scenes/CreationList.fxml");
    }

    @FXML
    void rewindMedia(ActionEvent event) {
        _player.seek( _player.getCurrentTime().add( Duration.seconds(-3)) );
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Media video = new Media(_videoFile.toURI().toString());
        _player = new MediaPlayer(video);
        mediaView.setMediaPlayer(_player);
        String command = "ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 " + _videoFile;
        String getDuration = methodHelper.command(command);

        double milliseconds = Double.parseDouble(getDuration) * 1000;
        duration = new Duration(milliseconds);
        _player.setOnReady(new Runnable() {
            @Override
            public void run() {
                mediaView.setMediaPlayer(_player);
                setUpProperties();
                _player.play();
                volumeBeforeMute[0] = _player.getVolume()*100;
            }
        });

        _player.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {

                volumeSlider.setValue(1.0);
                muteButton.setText("Mute");
                _player.stop();
                _player.seek(Duration.minutes(0));
                playPauseButton.setText("Play");
            }
        });
    }

}
