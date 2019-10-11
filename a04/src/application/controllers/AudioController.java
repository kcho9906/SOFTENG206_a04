package application.controllers;

import application.MethodHelper;
import application.TerminalWorker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AudioController {


    MethodHelper methodHelper = new MethodHelper();
    String searchTerm = "";

    @FXML
    public TextArea wikiSearchTextArea;

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Slider synthSlider;

    @FXML
    private Button previewTextButton;

    @FXML
    private Button saveTextButton;

    @FXML
    private ListView<?> existingAudioListView;

    @FXML
    private Button resetButton;

    @FXML
    private Button MenuButton;

    @FXML
    private Button nextButton;

    @FXML
    void previewTextAction(ActionEvent event) {

    }

    @FXML
    void resetAction(ActionEvent event) {

    }

    @FXML
    void returnToMenu(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/MainMenu.fxml");
    }

    @FXML
    void saveTextAction(ActionEvent event) {

    }

    @FXML
    void searchAction(ActionEvent event) {
        // searches if the search term is not empty
        searchTerm = (searchTextField.getText().trim());
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

        wikitWorker.setOnSucceeded(event1 -> {
            ImageController controller = new ImageController();
            controller.getImages(searchTerm);

        });
        Thread th = new Thread(wikitWorker);
        th.start();
    }

    @FXML
    void toSpeechSettingsAction(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/eSpeakSettings.fxml");
    }

    public void toNextStageButton(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/Image.fxml");
    }


    @FXML
    void deleteAllAction(ActionEvent event) {

    }

    @FXML
    void deleteAudioAction(ActionEvent event) {

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
    void removeAudioAction(ActionEvent event) {

    }

    @FXML
    void addAudioAction(ActionEvent actionEvent) {
    }
}





