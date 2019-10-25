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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * This is a controller class for the scene "Audio.fxml" and is
 * responsible for everything relating to the Audio scene.
 */
public class MediaController implements Initializable {

    // all the FXML component fields
    @FXML private BorderPane mediaPane;
    @FXML private MediaView mediaView;
    @FXML private Slider volumeSlider;
    @FXML private Label timeLabel;
    @FXML private Slider timeSlider;
    @FXML private Button muteButton;
    @FXML private Button rewindButton;
    @FXML private Button playPauseButton;
    @FXML private Button fastForwardButton;
    @FXML private ImageView muteImage, playPauseImage;

    private static MethodHelper methodHelper = Main.getMethodHelper();
    private MediaPlayer _player;
    private Duration duration;
    private File _videoFile;
    private double[] volumeBeforeMute = {100};
    private FXMLLoader loader;
    File muteImageFile = new File("src/componentImages/volumeOn.png");
    File unmuteImageFile = new File("src/componentImages/volumeOff.png");
    File playImageFile = new File("src/componentImages/play.png");
    File pauseImageFile = new File("src/componentImages/pause.png");

    public MediaController(File videoFile) {

        _videoFile = videoFile;
    }

    /**
     * Method to set up the properties of all the components for the class
     */
    private void setUpProperties() {

        /**
         * Listener to check if the volume slider has been changed and set the volume accordingly.
         */
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                if (oldValue != newValue) {

                    _player.setVolume(volumeSlider.getValue()/100);
                }

                if (newValue.equals(0.0)) {

                    changeImage(muteImage, unmuteImageFile);
                } else {

                    changeImage(muteImage, muteImageFile);
                }

            }
        });

        /**
         * Listener to check if the time slider is adjusted and then change the time
         * in the video to the part of the video.
         */
        timeSlider.valueProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {

                if (timeSlider.isPressed()) {

                    _player.seek(_player.getMedia().getDuration().multiply(timeSlider.getValue() / 100.00));
                }
            }
        });

        /**
         * Listener to change the time property of the media player
         */
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

    /**
     * Method which will fast forward the time of the media by 5 seconds.
     * @param event
     */
    @FXML
    void fastForwardMedia(ActionEvent event) {

        _player.seek( _player.getCurrentTime().add( Duration.seconds(5)) );
    }

    /**
     * Method with will mute the media if the mute button is pressed
     * @param event
     */
    @FXML
    void muteMedia(ActionEvent event) {

        if (volumeSlider.getValue()!=0) {

            volumeBeforeMute[0] = volumeSlider.getValue();
            volumeSlider.setValue(0);
        } else {

            volumeSlider.setValue(volumeBeforeMute[0]);
        }
    }

    /**
     * Method to play and pause the media
     * @param event
     */
    @FXML
    void playPauseMedia(ActionEvent event) {

        if (_player.getStatus() == MediaPlayer.Status.PLAYING) {

            _player.pause();
            changeImage(playPauseImage, playImageFile);
        } else {

            _player.play();
            changeImage(playPauseImage, pauseImageFile);
        }
    }

    /**
     * Method to returnt to the creation list
     * @param event
     * @throws Exception
     */
    @FXML
    void returnToCreationList(ActionEvent event) throws Exception {

        _player.stop();
        _player.dispose();
        methodHelper.changeScene(event, "scenes/CreationList.fxml");
    }

    /**
     * Method to return to the main menu
     * @param event
     * @throws Exception
     */
    @FXML
    void returnToMenu(ActionEvent event) throws Exception {

        _player.stop();
        _player.dispose();
        methodHelper.changeScene(event, "scenes/MainMenu.fxml");
    }

    /**
     * Method to rewind the media by 3 seconds.
     * @param event
     */
    @FXML
    void rewindMedia(ActionEvent event) {

        _player.seek( _player.getCurrentTime().add( Duration.seconds(-3)) );
    }

    /**
     * Method to initialise the scene.
     * @param location
     * @param resources
     */

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
                volumeBeforeMute[0] = 100;
            }
        });

        _player.setOnEndOfMedia(new Runnable() {

            @Override
            public void run() {

                volumeSlider.setValue(100);
                changeImage(muteImage, unmuteImageFile);
                _player.stop();
                _player.seek(Duration.minutes(0));
                changeImage(playPauseImage, playImageFile);
            }
        });
    }

    public void changeImage(ImageView imageView, File imageFile) {
        Image image = new Image(imageFile.toURI().toString());
        imageView.setImage(image);
    }
}

