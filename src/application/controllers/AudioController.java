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

/**
 * This is a controller class for the scene "Audio.fxml" and is
 * responsible for everything relating to the Audio scene.
 */
public class AudioController implements  Initializable {

    // all FXML related components
    @FXML private ChoiceBox<String> bgMusicChoiceBox;
    @FXML private TextField searchTextField;
    @FXML private Button searchButton;
    @FXML private Label statusLabel;
    @FXML private Label currentKeywordLabel;
    @FXML private ProgressIndicator loadingCircle;
    @FXML private TextArea wikiSearchTextArea;
    @FXML private Slider speedSlider;
    @FXML private Slider genderSlider;
    @FXML private Button previewTextButton;
    @FXML private Button saveTextButton;
    @FXML private ListView<String> audioListView;
    @FXML private Button playAudioButton;
    @FXML private Button deleteAudioButton;
    @FXML private Button nextButton;
    @FXML public Button moveUpButton;
    @FXML public Button moveDownButton;

    private String bgMusic = "";
    private String gender = "";
    private String speed = "";
    private int maleAudioFiles = 0;
    private int femaleAudioFiles = 0;
    private Thread thread = new Thread();
    private TerminalWorker previewAudioWorker = new TerminalWorker("");
    private TerminalWorker playAudioWorker = new TerminalWorker("");
    private ObservableList<String> listForCreation = FXCollections.observableArrayList();
    private static MethodHelper methodHelper = Main.getMethodHelper();
    private String searchTerm = "";

    /**
     * Method to search the wiki for the term in the input and
     * displays the output in the text area below.
     * @param event
     */
    @FXML
    void searchAction(ActionEvent event) {

        // show the loading circle
        loadingCircle.setVisible(true);

        // change the name of the button to downloading images...
        nextButton.setText("Downloading Images...");

        // searches if the search term is not empty
        searchTerm = (searchTextField.getText().trim());

        // use the terminal to wikit the term with a worker / task
        TerminalWorker wikitWorker = new TerminalWorker("wikit " + searchTerm);

        wikitWorker.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {

                searchTextField.clear();
                searchTextField.setDisable(true);

                // get the images by using the ImageController
                ImageController.getImages(searchTerm, nextButton);

                // set the current keyword for user to see
                currentKeywordLabel.setText(searchTerm);
                loadingCircle.setVisible(false);

                // updates the audio file list
                getAudioFileList();

                String result = "\"" + wikitWorker.getValue().trim() + "\"";
                if (result.contains("not found :^(")) {

                    // ask the user to try again because search term is not valid.
                    statusLabel.setText(searchTerm + " not found. Please try again.  ⃠");
                } else {

                    // Display the sentences in the display area
                    wikiSearchTextArea.setText(wikitWorker.getValue().trim());
                    wikiSearchTextArea.setWrapText(true);
                    wikiSearchTextArea.setDisable(false);
                }

                // check if the audio list is not empty, if it isn't
                // then enable the next button
                if (!audioListView.getItems().isEmpty()) {

                    nextButton.setDisable(false);
                }
            }
        });

        // start thread / worker
        Thread th = new Thread(wikitWorker);
        th.start();
    }

    /**
     * Method to preview how the selected text will sound when it has been
     * saved.
     * @param event
     */
    @FXML
    void previewTextAction(ActionEvent event) {

        // cancel current audio being played.
        cancelPlayingAudio();

        // gets the selected text
        String selectedText = wikiSearchTextArea.getSelectedText();
        boolean speak = countMaxWords(selectedText);

        // if the chunk has a valid number of words, then speak.
        if (speak) {

            // get the slider values for gender and speed.
            getSliderValues();

            String command = "espeak -v " + gender + " -s " + speed + " \"" + selectedText + "\"";
            previewAudioWorker = new TerminalWorker(command);

            thread = new Thread(previewAudioWorker);
            thread.start();
        }
    }

    /**
     * Method to save the selected text and add it to the audio file list
     * @param event
     */
    @FXML
    void saveTextAction(ActionEvent event) {

        // creates a file for the search term for all new audio files.
        methodHelper.createFileDirectory("src/audio/" + searchTerm);

        // get the selected text for text area
        String selectedText = wikiSearchTextArea.getSelectedText();

        // check selected text is in a valid range.
        boolean validRange = countMaxWords(selectedText);
        if (validRange) {

            // get slider values for gender and speed.
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

                // updates the audio list file
                getAudioFileList();

                // change status of the text if the audio list is not empty
                if (!audioListView.getItems().isEmpty()) {

                    methodHelper.setContainsAudio(true);
                }

                // checks if the images have downloaded
                if (methodHelper.getHasDownloaded()) {

                    nextButton.setDisable(false);
                }
            });
        }
    }

    /**
     * Method to play the saved audio.
     * @param event
     */
    @FXML
    void playAudioAction(ActionEvent event) {

        // cancels current audio being played
        cancelPlayingAudio();

        // using ffplay to play the saved audio file
        String command = "ffplay -nodisp -autoexit src/audio/" + searchTerm + "/" + audioListView.getSelectionModel().getSelectedItem();
        playAudioWorker = new TerminalWorker(command); //send to background thread
        thread = new Thread(playAudioWorker);
        thread.start();
    }

    /**
     * Method to delete selected audio file. This will disable the
     * next button if it is empty after deleting.
     * @param event
     */
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

    /**
     * Method which allows moving the audio up the list hierarchy
     * @param actionEvent
     */
    public void moveAudioUp(ActionEvent actionEvent) {

        String selectedAudio = audioListView.getSelectionModel().getSelectedItem();
        int originalPos = listForCreation.indexOf(selectedAudio);

        if (originalPos != 0) {

            // swaps the adjacent audio file
            Collections.swap(listForCreation, originalPos, (originalPos - 1));
        }

        // update the list
        audioListView.setItems(listForCreation);
    }

    /**
     * Method which allows moving the audio down the list hierarchy
     * @param actionEvent
     */
    public void moveAudioDown(ActionEvent actionEvent) {

        String selectedAudio = audioListView.getSelectionModel().getSelectedItem();
        int originalPos = listForCreation.indexOf(selectedAudio);

        if (originalPos != listForCreation.size() - 1) {

            // swaps the adjacent audio file
            Collections.swap(listForCreation, originalPos, originalPos + 1);
        }

        // update the list
        audioListView.setItems(listForCreation);
    }

    /**
     * Method to cancel the audio which is currently playing.
     */
    private void cancelPlayingAudio() {

        playAudioWorker.cancel();
        previewAudioWorker.cancel();
    }

    /**
     * Method to reset the scene so the user can start from scratch
     * @param event
     */
    @FXML
    void resetAction(ActionEvent event) {

        // add an alert to confirm whether the user is sure to reset the scene.
        boolean reset = methodHelper.addConfirmationAlert("Reset search", "All progress will be lost. Continue?");

        // if yes, then set all fields to default
        if (reset) {

            // delete all audio files which had just been created.
            deleteAudioFiles();

            // set default settings
            searchTextField.setDisable(false);
            searchTextField.clear();
            maleAudioFiles = 0;
            femaleAudioFiles = 0;
            currentKeywordLabel.setText("N/A");
            wikiSearchTextArea.clear();
            searchTerm = "";
            loadingCircle.setVisible(false);
            bgMusicChoiceBox.setValue("None");
            nextButton.setDisable(true);
        }
    }

    /**
     * Method to delete all the audio files
     */
    private void deleteAudioFiles() {

        methodHelper.command("rm -rf src/audio/*");

        // resets the audio files list
        getAudioFileList();
    }

    /**
     * Method to check that selected text in within 30 words
     * @param selectedText
     * @return
     */
    public boolean countMaxWords(String selectedText) {

        String[] words = selectedText.split("\\s+");
        if (words.length > 30) {

            // creates an alert if the chunk if more than 30 words
            methodHelper.createAlertBox("Chunk cannot be more than 30 words, try a smaller chunk");

            return false;
        }

        return true;
    }

    /**
     * Method to get the slider values from the gender and speed sliders.
     */
    private void getSliderValues() {

        // get the speed slider value
        speed = Double.toString(speedSlider.getValue()*160);

        // get the gender slider value
        if ( genderSlider.getValue() == 0 ) {

            gender = "male3";
            maleAudioFiles++;
        } else {

            gender = "female5";
            femaleAudioFiles++;
        }
    }

    /**
     * Method to return to the main menu.
     * @param event
     * @throws Exception
     */
    @FXML
    void returnToMenu(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/MainMenu.fxml");
    }

    /**
     * Method to switch to the next scene which is the image scene.
     * @param event
     * @throws Exception
     */
    @FXML
    void toNextStageButton(ActionEvent event) throws Exception {

        // worker to merge the audio in the background
        TerminalWorker mergeAudioWorker = new TerminalWorker(mergeAudio());
        thread = new Thread(mergeAudioWorker);
        thread.start();

        mergeAudioWorker.setOnSucceeded(finished -> {

            // get the length of the mp3 file.
            String getLengthCommand = "mp3info -p \"%S\" " + "src/audio/" + searchTerm + "/" + "output.mp3";
            double duration = Double.parseDouble(methodHelper.command(getLengthCommand));
            methodHelper.setDuration(duration);

            // try/catch to swap scenes to the image scene if everything is all good.
            try {
                methodHelper.changeCreationScene(event, "scenes/Image.fxml");
                methodHelper.setPreviousScene(nextButton.getScene());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Method to merge the audio files.
     * @return
     */
    private String mergeAudio() {

        String command = "";

        // if the creation list contains audio, then merge what is there
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

            // gets the background music value
            bgMusic = bgMusicChoiceBox.getValue();
            if (!bgMusic.equals("None")) {
                String bgMusicPath = System.getProperty("user.dir") + "/backgroundMusic/" + bgMusic + ".mp3";
                command += "ffmpeg -y -i " + mergedPath + " -i " + bgMusicPath + " -filter_complex amix=inputs=2:duration=shortest " + path + "output.mp3";
            }
        }
        return command;
    }

    /**
     * gets list of audio files related to the keyword
     * @return
     */
    public ListView<String> getAudioFileList() {

        audioListView.getItems().clear();
        String path = System.getProperty("user.dir") + "/src/audio/" + searchTerm;
        File folder = new File(path);

        // check if the folder exists
        if (folder.exists()) {

            File[] listOfFiles = folder.listFiles();
            for (File file : listOfFiles) {

                if (file.isFile()) {

                    listForCreation.add(file.getName());
                }
            }
        }

        // set the audio list view with the creation audio files.
        audioListView.setItems(listForCreation);
        return audioListView;
    }

    /**
     * Method which will initialise all the necessary attributes for the scene
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // set up disable bindings
        setUpBindings();

        // disable next button
        nextButton.setDisable(true);
    }

    /**
     * Method which sets up all the bindings for the scene.
     */
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
}





