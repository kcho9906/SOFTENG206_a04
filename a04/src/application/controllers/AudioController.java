package application.controllers;

import application.MethodHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AudioController {

    MethodHelper methodHelper = new MethodHelper();

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextArea wikiSearchTextArea;

    @FXML
    private Slider synthSlider;

    @FXML
    private Button previewTextButton;

    @FXML
    private Button saveTextButton;

    @FXML
    private Button resetButton;

    @FXML
    private ListView<?> existingAudioListView;

    @FXML
    private Button playAudioButton;

    @FXML
    private Button deleteAudioButton;

    @FXML
    private Button deleteAllButton;

    @FXML
    private Button addAudioButton;

    @FXML
    private Button removeAudioButton;

    @FXML
    private ListView<?> selectAudioListView;

    @FXML
    private Button moveAudioUpButton;

    @FXML
    private Button moveAudioDownButton;

    @FXML
    private Button nextStageButton;

    @FXML
    private Button cancelCreationButton;

    @FXML
    private Button enableMusicButton;

    @FXML
    void cancelCreationAction(ActionEvent event) {

    }

    @FXML
    void deleteAllAction(ActionEvent event) {

    }

    @FXML
    void deleteAudioAction(ActionEvent event) {

    }

    @FXML
    void enableMusicAction(ActionEvent event) {

    }

    @FXML
    void moveAudioDownAction(ActionEvent event) {

    }

    @FXML
    void moveAudioUpAction(ActionEvent event) {

    }

    @FXML
    void playAudioAction(ActionEvent event) {

    }

    @FXML
    void previewTextAction(ActionEvent event) {

    }

    @FXML
    void removeAudioAction(ActionEvent event) {

    }

    @FXML
    void resetAction(ActionEvent event) {

    }

    @FXML
    void saveTextAction(ActionEvent event) {

    }

    @FXML
    void searchAction(ActionEvent event) {

    }

    @FXML
    void toNextStageButton(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/Image.fxml");
    }

}
