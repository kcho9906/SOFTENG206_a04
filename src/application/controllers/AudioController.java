package application.controllers;

import application.Main;
import application.MethodHelper;
import application.TerminalWorker;
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
import java.util.Collections;
import java.util.ResourceBundle;

public class AudioController implements  Initializable {


    @FXML
    private ChoiceBox<String> bgMusicChoiceBox;
    private String bgMusic = "";


    @FXML
    private Button nextButton;
    private String gender = "";
    private String speed = "";
    private int maleAudioFiles = 0;
    private int femaleAudioFiles = 0;
    private Thread thread = new Thread();
    private TerminalWorker previewAudioWorker = new TerminalWorker("");
    private TerminalWorker playAudioWorker = new TerminalWorker("");
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

        // check if audio list is empty then set next button
        nextButton.setDisable(audioListView.getItems().isEmpty());

    }

    @FXML
    void playAudioAction(ActionEvent event) {

        cancelPlayingAudio();
        String command = "ffplay -nodisp -autoexit src/audio/" + searchTerm + "/" + audioListView.getSelectionModel().getSelectedItem();
        playAudioWorker = new TerminalWorker(command); //send to background thread
        thread = new Thread(playAudioWorker);
        thread.start();

    }

    @FXML
    void previewTextAction(ActionEvent event) {

        cancelPlayingAudio();
        String selectedText = wikiSearchTextArea.getSelectedText();
        boolean speak = countMaxWords(selectedText);
        if (speak) {
            getSliderValues();

            String command = "espeak -v " + gender + " -s " + speed + " \"" + selectedText + "\"";
            previewAudioWorker = new TerminalWorker(command);

            thread = new Thread(previewAudioWorker);
            thread.start();
        }
    }

    private void cancelPlayingAudio() {

        playAudioWorker.cancel();
        previewAudioWorker.cancel();
    }


    @FXML
    void resetAction(ActionEvent event) {
        boolean reset = methodHelper.addConfirmationAlert("Reset search", "All progress will be lost. Continue?");
        if (reset) {

            searchTextField.setDisable(false);
            deleteAudioFiles();
            searchTextField.clear();
            maleAudioFiles = 0;
            femaleAudioFiles = 0;
            currentKeywordLabel.setText("N/A");
            wikiSearchTextArea.clear();
            searchTerm = "";
            loadingCircle.setVisible(false);
            bgMusicChoiceBox.setValue("None");
        }
    }

    private void deleteAudioFiles() {

        methodHelper.command("rm -rf src/audio/*");
        getAudioFileList();

        // do this
        // disable next button
        nextButton.setDisable(true);
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
            String fileName = searchTerm + "_male_" + maleAudioFiles + ".wav";
            if (gender.contains("female")) {

                fileName = searchTerm + "_female_" + femaleAudioFiles + ".wav";
            }

            String command = "espeak -v " + gender + " -s " + speed + " \"" + selectedText + "\" -w " + path + fileName + "; lame -b 320 -h " + path + fileName + "; rm " + path + "*.wav";
            TerminalWorker audioWorker = new TerminalWorker(command);
            Thread th = new Thread(audioWorker);
            th.start();

            audioWorker.setOnSucceeded(event1 -> {

                getAudioFileList();

                // change status of the text if the audio list is not empty
                if (!audioListView.getItems().isEmpty()) {
                    methodHelper.setContainsAudio(true);
                }

                // checks if the images have downloaded
                if (methodHelper.getHasDownloaded()) {
                    System.out.println("has downloaded");
                    nextButton.setDisable(false);
                }
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

                searchTextField.clear();
                searchTextField.setDisable(true);
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

                if (!audioListView.getItems().isEmpty()) {
                    nextButton.setDisable(false);
                }
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
            String getLengthCommand = "mp3info -p \"%S\" " + "src/audio/" + searchTerm + "/" + "output.mp3";
            System.out.println(getLengthCommand);
            double duration = Double.parseDouble(methodHelper.command(getLengthCommand));
            methodHelper.setDuration(duration);
            try {
                methodHelper.changeCreationScene(event, "scenes/Image.fxml");
                methodHelper.setPreviousScene(nextButton.getScene());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private String mergeAudio() {

        String command = "";
        if (listForCreation.size()>0) {

            String path = "src/audio/" + searchTerm + "/";
            command = "ffmpeg -y ";
            int count = 0;
            for (String fileName : listForCreation) {

                command += "-i " + path + fileName + " ";
                count++;
            }

            command += "-filter_complex '";
            for (int i = 0; i < count; i++) {

                command += "[" + i + ":0]";
            }
            String mergedPath = path + "output.wav";
            command += "concat=n=" + count + ":v=0:a=1[out]' -map '[out]' " + mergedPath;
            command += "; lame -b 192 -h " + mergedPath + " " + path + "output.mp3; ";

            bgMusic = bgMusicChoiceBox.getValue();
            if (!bgMusic.equals("None")) {
                String bgMusicPath = System.getProperty("user.dir") + "/backgroundMusic/" + bgMusic + ".mp3";
                command += "ffmpeg -y -i " + mergedPath + " -i " + bgMusicPath + " -filter_complex amix=inputs=2:duration=shortest " + path + "output.mp3";
            }

        }
        System.out.println(command);
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

        // disable next button
        nextButton.setDisable(true);

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
            maleAudioFiles++;
        } else {
            gender = "female5";
            femaleAudioFiles++;
        }
    }

}





