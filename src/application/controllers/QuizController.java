package application.controllers;

import application.Creation;
import application.Main;
import application.MethodHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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

public class QuizController implements Initializable {

    @FXML
    private BorderPane mediaPane;

    @FXML
    private TextField answerTextField;

    @FXML
    private Button checkAnswerButton;

    @FXML
    private Button replayButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button endQuizButton;

    @FXML
    private MediaView mediaView;

    @FXML
    private ImageView resultImage;

    private static MethodHelper methodHelper = Main.getMethodHelper();
    private MediaPlayer _player;
    private Duration duration;
    private File _videoFile;
    private double[] volumeBeforeMute = {1};
    private FXMLLoader loader;
    private String _currentCreationName = "";
    private String answer = "";
    private int _correctAnswers = 0;
    private int _attemptedAnswers = 0;
    private Image tick = new Image(new File("componentImage/greenTick.png").toURI().toString());
    private Image cross = new Image(new File("componentImage/redCross.png").toURI().toString());
    private ObservableList<Creation> _answeredCreations = FXCollections.observableArrayList();

    private void setUpProperties() {

    }

    @FXML
    void checkAnswer(ActionEvent event) {

        boolean correct = false;
        // compares the input to the textField with the current creation
        String input = answerTextField.getText().trim().toLowerCase();
        if (input.equals(answer.toLowerCase())) {

            // set image view to tick and disable check answers button
            resultImage.setImage(tick);
            checkAnswerButton.setDisable(true);
            _correctAnswers++;
            correct = true;
        } else {

            // set image to a cross and disable check answer button
            resultImage.setImage(cross);
            checkAnswerButton.setDisable(true);
            correct = false;
        }

        _attemptedAnswers++;

        // save the creation in a list to be displayed later.
        Creation creation = new Creation(_currentCreationName, correct);
        _answeredCreations.add(creation);

    }


    @FXML
    void replayQuiz(ActionEvent event) {
        _player.play();
        replayButton.setDisable(true);
    }

    @FXML
    void toResultScene (ActionEvent event) throws Exception {

        // send the creation list to the methodHelper to be displayed
        methodHelper.setAnsweredCreations(_answeredCreations);

        // set the answers
        methodHelper.setBestScore(_correctAnswers,_attemptedAnswers);
        methodHelper.setAnswers(_correctAnswers, _attemptedAnswers);

        methodHelper.changeScene(event, "scenes/Results.fxml");
    }

    @FXML
    void toNextVideo (ActionEvent event) throws Exception {

        // enables the check answer button
        checkAnswerButton.setDisable(false);

        // reset the image to blank
        resultImage.setImage(null);

        // reset the text field
        answerTextField.setText("");

        // plays the next video
        playRandomVideo();
    }

    /**
     * method which will choose a random video and play it
     */
    public void playRandomVideo() {
        // get the random video file
        // get the number of creations
        File file = new File("src/creations");
        File[] files = file.listFiles();
        int noOfCreations = files.length;

        // get a random number between 1 and noOfCreations - cannot be zero
        int videoFileNumber = (int) (Math.random() * noOfCreations + 1);
        videoFileNumber -= 1;

        // select the random file
        File selectedFile = files[videoFileNumber];
        Creation creation = new Creation(selectedFile);
        _currentCreationName = selectedFile.getName();
        answer = creation.get_searchTerm().toLowerCase();
        String path = selectedFile.getPath();
        File videoFile = new File(path + "/imageOnly.mp4");

        // play the video straight away
        Media video = new Media(videoFile.toURI().toString());
        _player = new MediaPlayer(video);
        mediaView.setMediaPlayer(_player);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        playRandomVideo();

        // prepare the video
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
                _player.stop();
                _player.seek(Duration.minutes(0));
                replayButton.setDisable(false);
                replayButton.setText("Replay");
            }
        });
    }

}
