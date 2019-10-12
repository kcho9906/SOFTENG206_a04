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

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class AudioController implements  Initializable {

    @FXML
    public Button moveUpButton;

    @FXML
    public Button moveDownButton;

    private ObservableList<String> selectedAudio = FXCollections.observableArrayList();
    private ObservableList<String> listForCreation = FXCollections.observableArrayList();
    private MethodHelper methodHelper = new MethodHelper();
    private String searchTerm = "";

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Label currentKeywordLabel;

    @FXML
    private ProgressIndicator loadingCircle;

    @FXML
    private TextArea wikiSearchTextArea;

    @FXML
    private Slider synthSlider;

    @FXML
    private Button previewTextButton;

    @FXML
    private Button saveTextButton;

    @FXML
    private ListView<String> audioListView;

    @FXML
    private Button playAudioButton;

    @FXML
    private Button deleteAudioButton;

    @FXML
    private Button deleteAllButton;

    @FXML
    private Button resetButton;

    @FXML
    private Button MenuButton;

    @FXML
    private Button nextButton;

    @FXML
    void deleteAudioAction(ActionEvent event) {


       // audioListView.getItems().remove(audioListView.getSelectionModel().getSelectedItem());
    }

    @FXML
    void playAudioAction(ActionEvent event) {

    }

    @FXML
    void previewTextAction(ActionEvent event) {

    }

    @FXML
    void resetAction(ActionEvent event) {

        audioListView.getItems().clear();
        searchTextField.clear();
        currentKeywordLabel.setText("N/A");
        wikiSearchTextArea.clear();
        searchTerm = "";
        loadingCircle.setVisible(false);
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

                currentKeywordLabel.setText(searchTerm);
                loadingCircle.setVisible(false);
                ImageController.getImages(searchTerm);
                getAudioFileList();
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
    void toNextStageButton(ActionEvent event) throws Exception {

        methodHelper.changeScene(event, "scenes/Image.fxml");
    }

    //gets list of audio files related to the keyword
    public ListView<String> getAudioFileList() {

        audioListView.getItems().clear();
        String path = System.getProperty("user.dir") + "/src/audio/" + searchTerm;
        File folder = new File(path);
        if (folder.exists()) {

            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles) {

                if (file.isFile()) {

                    audioListView.getItems().add(file.getName());
                }
            }
        }
        return audioListView;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //----------------------------SET UP DISABLE BINDINGS------------------------------//
        setUpBindings();

//        BooleanBinding booleanBinding = new BooleanBinding() {
//
//            {super.bind(currentKeywordLabel.textProperty(), audioListView.selectionModelProperty());}
//
//            @Override
//            protected boolean computeValue() {
//                return (searchTerm.equals("N/A") || audioListView.getSelectionModel().getSelectedItems().size()==0);
//            }
//        };
//
//        nextButton.disableProperty().bind(booleanBinding);


    }

    private void setUpBindings() {

        searchButton.disableProperty().bind(searchTextField.textProperty().isEmpty());
        playAudioButton.disableProperty().bind(audioListView.getSelectionModel().selectedItemProperty().isNull());
        deleteAudioButton.disableProperty().bind(audioListView.getSelectionModel().selectedItemProperty().isNull());
        moveDownButton.disableProperty().bind(audioListView.getSelectionModel().selectedItemProperty().isNull());
        moveUpButton.disableProperty().bind(audioListView.getSelectionModel().selectedItemProperty().isNull());

    }

    public void moveAudioUp(ActionEvent actionEvent) {
    }

    public void moveAudioDown(ActionEvent actionEvent) {
    }

    //checks that selected text in within 30 words
    public boolean countMaxWords(String selectedText) {

        String[] words = selectedText.split("\\s+");
        if (words.length > 30) {

            methodHelper.createAlertBox("Chunk cannot be more than 30 words, try a smaller chunk");
            return false;
        }
        return true;
    }

}





