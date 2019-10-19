package application.controllers;

import application.Main;
import application.MethodHelper;
import application.TerminalWorker;
import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

public class AudioController implements  Initializable {

    @FXML
    private Button nextButton;
    private String gender = "";
    private String speed = "";
    private Thread thread = new Thread();
    @FXML
    public Button moveUpButton;

    @FXML
    public Button moveDownButton;

    private ObservableList<String> listForCreation = FXCollections.observableArrayList();
    private static MethodHelper methodHelper = Main.getMethodHelper();
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
    private Slider speedSlider;

    @FXML
    private Slider genderSlider;

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
    void deleteAudioAction(ActionEvent event) {

        String selectedAudio = audioListView.getSelectionModel().getSelectedItem();
        listForCreation.remove(selectedAudio);
        String command = "rm -f src/audio/" + searchTerm + "/" + selectedAudio;
        methodHelper.command(command);
        getAudioFileList();

    }

    @FXML
    void playAudioAction(ActionEvent event) {
        String command = "ffplay -nodisp -autoexit src/audio/" + searchTerm + "/" + audioListView.getSelectionModel().getSelectedItem() + " >/dev/null 2>&1";
        TerminalWorker playSelectedWorker = new TerminalWorker(command); //send to background thread
        thread = new Thread(playSelectedWorker);
        thread.start();

    }

    @FXML
    void previewTextAction(ActionEvent event) {

        String selectedText = wikiSearchTextArea.getSelectedText();
        boolean speak = countMaxWords(selectedText);
        if (speak) {
            TerminalWorker previewSpeechWorker; //create worker to do task
            getSliderValues();

            String command = "espeak -v " + gender + " -s " + speed + " \"" + selectedText + "\"";
            previewSpeechWorker = new TerminalWorker(command);

            thread = new Thread(previewSpeechWorker);
            thread.start();

            previewSpeechWorker.setOnSucceeded(event1 -> {
            });

        }
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

        methodHelper.createFileDirectory("src/audio/" + searchTerm);
        String selectedText = wikiSearchTextArea.getSelectedText();
        boolean validRange = countMaxWords(selectedText);
        if (validRange) {

            getSliderValues();
            String path = "src/audio/" + searchTerm + "/";
            String firstWord = selectedText.substring(0, selectedText.indexOf(' '));
            String lastWord = selectedText.substring(selectedText.lastIndexOf(' ') + 1);
            String fileName = searchTerm + "_" + firstWord + "_" + lastWord + ".wav";
            String command = "espeak -v " + gender + " -s " + speed + " \"" + selectedText + "\" -w " + path + fileName + "; lame -b 320 -h " + path + fileName + "; rm " + path + "*.wav";
            TerminalWorker audioWorker = new TerminalWorker(command);
            Thread th = new Thread(audioWorker);
            th.start();

            audioWorker.setOnSucceeded(event1 -> {

                getAudioFileList();
                nextButton.setDisable(isEmptyAudioList());
            });
        }
    }

    @FXML
    void searchAction(ActionEvent event) {
        loadingCircle.setVisible(true);
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

                ImageController.getImages(searchTerm, nextButton);
                currentKeywordLabel.setText(searchTerm);
                loadingCircle.setVisible(false);
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

                nextButton.setDisable(isEmptyAudioList());
            }
        });

        Thread th = new Thread(wikitWorker);
        th.start();
    }

    @FXML
    void toNextStageButton(ActionEvent event) throws Exception {

        TerminalWorker mergeAudioWorker = new TerminalWorker(mergeAudio());
        thread = new Thread(mergeAudioWorker);
        thread.start();

        mergeAudioWorker.setOnSucceeded(finished -> {
            String getLengthCommand = "soxi -D src/audio/" + searchTerm + "/" + searchTerm + "MERGED.wav";
            double duration = Double.parseDouble(methodHelper.command(getLengthCommand));
            methodHelper.setDuration(duration);
            try {
                methodHelper.changeScene(event, "scenes/Image.fxml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private String mergeAudio() {

        String command = "";
        if (listForCreation.size()>0) {

            String path = "src/audio/" + searchTerm + "/";
            command = "ffmpeg ";
            int count = 0;
            for (String fileName : listForCreation) {

                command += "-i " + path + fileName + " ";
                count++;
            }

            command += "-filter_complex '";
            for (int i = 0; i < count; i++) {

                command += "[" + i + ":0]";
            }
            String outputPath = path + "" + searchTerm + "MERGED.wav";
            command += "concat=n=" + count + ":v=0:a=1[out]' -map '[out]' " + outputPath;
        }
        return command;
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

                    listForCreation.add(file.getName());
                }
            }
        }
        audioListView.setItems(listForCreation);
        return audioListView;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //----------------------------SET UP DISABLE BINDINGS------------------------------//
        setUpBindings();

        // disable the next button initially
        nextButton.setDisable(true);

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
        playAudioButton.disableProperty().bind(audioListView.getSelectionModel().selectedItemProperty().isNull());
        deleteAudioButton.disableProperty().bind(audioListView.getSelectionModel().selectedItemProperty().isNull());
        previewTextButton.disableProperty().bind(wikiSearchTextArea.selectedTextProperty().isEmpty());
        saveTextButton.disableProperty().bind(wikiSearchTextArea.selectedTextProperty().isEmpty());

//        BooleanBinding threadBinding = new BooleanBinding() {
//            @Override
//            protected boolean computeValue() {
//                return (thread.isAlive());
//            }
//        };
//
//        previewTextButton.disableProperty().bind(threadBinding);
//        playAudioButton.disableProperty().bind(threadBinding);

    }

    public void moveAudioUp(ActionEvent actionEvent) {

        String selectedAudio = audioListView.getSelectionModel().getSelectedItem();
        int originalPos = listForCreation.indexOf(selectedAudio);
        if (originalPos != 0) {

            Collections.swap(listForCreation, originalPos, (originalPos - 1));
        }
        audioListView.setItems(listForCreation); //update list
    }

    public void moveAudioDown(ActionEvent actionEvent) {
        String selectedAudio = audioListView.getSelectionModel().getSelectedItem();
        int originalPos = listForCreation.indexOf(selectedAudio);
        if (originalPos != listForCreation.size() - 1) {

            Collections.swap(listForCreation, originalPos, originalPos + 1);
        }

        audioListView.setItems(listForCreation); //update list
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

    private void getSliderValues() {
        speed = Double.toString(speedSlider.getValue()*160);
        if ( genderSlider.getValue() == 0 ) {

            gender = "male3";
        } else { gender = "female5";}
    }

    public boolean isEmptyAudioList() {
        if (audioListView.getItems().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

}





