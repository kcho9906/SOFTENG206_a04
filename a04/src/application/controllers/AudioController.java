package application.controllers;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import application.*;

public class AudioController {

    @FXML
    private TextField searchTextField;

    @FXML
    private TextArea wikiSearchTextArea;

    @FXML
    private Button searchButton;

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
    private ListView<?> selectedAudioListView;

    @FXML
    private Button moveAudioUpButton;

    @FXML
    private Button moveAudioDownButton;

    @FXML
    private Button toNextStageButton;

    @FXML
    private Button cancelCreationButton;

    @FXML
    private Button enableMusicButton;

    @FXML
    void addAudioAction(ActionEvent event) {

    }

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
        // searches if the search term is not empty
        String searchTerm = (searchTextField.getText().trim());
        // use the terminal to wikit the term with a worker / task
        //currentKeyWord.setText("Current Keyword: " + keyword);
        TerminalWorker wikitWorker = new TerminalWorker("wikit " + searchTerm);

        // start the progress bar
        // startProgressBar("Searching for ...", wikitWorker);

        wikitWorker.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                String result = "\"" + wikitWorker.getValue().trim() + "\"";
                if (result.contains("not found :^(")) {
//                    // alert to say not found, resets the text field and area
                } else {
                    // Display the sentences in the display area
                    wikiSearchTextArea.setText(wikitWorker.getValue().trim());
                    wikiSearchTextArea.setWrapText(true);
                    wikiSearchTextArea.setDisable(false);
                    // need to reset stuff
                }
            }
        });

        Thread th = new Thread(wikitWorker);
        th.start();
    }

    @FXML
    void toNextStageAction(ActionEvent event) {

    }

}




