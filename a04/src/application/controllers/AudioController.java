package application.controllers;

import application.MethodHelper;
import application.TerminalWorker;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class AudioController implements Initializable {

    MethodHelper methodHelper = new MethodHelper();
    String searchTerm = "";

    private ObservableList<String> selectedAudio = FXCollections.observableArrayList();
    private ObservableList<String> listForCreation = FXCollections.observableArrayList();

    @FXML
    private ProgressIndicator loadingCircle;

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
        loadingCircle.setVisible(true);
        methodHelper.resetSearchTerm();
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
                loadingCircle.setVisible(false);
                ImageController.getImages(searchTerm);
                String result = "\"" + wikitWorker.getValue().trim() + "\"";
                if (result.contains("not found :^(")) {
                    statusLabel.setText(searchTerm + " not found. Please try again.  ⃠");
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    //----------------------------SET UP DISABLE BINDINGS------------------------------//
        searchButton.disableProperty().bind(searchTextField.textProperty().isEmpty());
        nextButton.disableProperty().bind(new BooleanBinding() {

            @Override
            protected boolean computeValue() {
                return (searchTerm.equals(null) || selectedAudio.isEmpty());
            }
        });


    }
}





