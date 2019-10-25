package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Optional;

/**
 * This class acts as a 'Hub' for all information that is transferred
 * between controllers and also reduces code duplication by having
 * methods which are used many times throughout the application.
 * There is ever only one instance of this class as we declare a static
 * MethodHelper in the main which is reused.
 */
public class MethodHelper {

    private double _duration;
    private ObservableList<Creation> _answeredCreations = FXCollections.observableArrayList();
    private Scene previousScene;

    // fields which contain the information to the past correct answers to the quiz
    private int _correctAnswers = 0;
    private int _totalAnswers = 0;
    private int _scorePercentage = 0;

    // fields which tell whether the images have been downloaded and audio list contains something
    private boolean _hasDownloaded = false;
    private boolean _containsAudio = false;

    // fields for whether the images are selecte and there is input in creation name
    private boolean _hasText = false;
    private boolean _imagesSelected = false;

    // contains the current search term
    private String _searchTerm = "";

    /**
     * Method changes the current scene to the input string scene.
     * use of this will make a new instance of the FXML scene.
     * @param event
     * @param scene
     * @throws Exception
     */
    public void changeScene(ActionEvent event, String scene) throws Exception {
        Parent newSceneParent = FXMLLoader.load(getClass().getResource(scene));
        Scene newScene = new Scene(newSceneParent);
        Stage window = (Stage) (((Node)event.getSource()).getScene().getWindow());
        window.setScene(newScene);
        window.show();
    }

    /**
     * A separate method for the change creation scene as we can use this to
     * maintain the information from the previous scene.
     * @param event
     * @param scene
     * @throws Exception
     */
    public void changeCreationScene(ActionEvent event, String scene) throws Exception {
        if (previousScene == null) {
            changeScene(event, scene);
        } else {
            Stage window = (Stage) (((Node) event.getSource()).getScene().getWindow());
            window.setScene(previousScene);
            window.show();
        }
    }

    /**
     * Method creates a confirmation alert where action is determined by
     * the user.
     * @param title
     * @param contentText
     * @return
     */
    public boolean addConfirmationAlert(String title, String contentText, String confirm, String cancel) {

        ButtonType confirmButton = new ButtonType(confirm);
        ButtonType cancelButton = new ButtonType(cancel);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", cancelButton, confirmButton);
        alert.setTitle(title);
        alert.setHeaderText(contentText);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("../cyanBlueGreyPalette.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        Window window = alert.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(e -> {
            alert.hide();
        });
        Optional<ButtonType> result = alert.showAndWait();
        boolean[] answer = {false};
        result.ifPresent(res->{
            if (res.equals(confirmButton)) {
                answer[0] = true;
            } else if (res.equals(cancelButton)) {
                answer[0] = false;
            }
        });

        return answer[0];
    }

    /**
     * This method acts as the bash command line where the input command
     * runs in the bash terminal
     * @param command
     * @return
     */
    public static String command(String command) {
        Process process = null;
        try {

            String output = null;

            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);

            process = pb.start();

            BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            int exitStatus = process.waitFor();

            if (exitStatus == 0) {

                String line;
                String text = "";
                while ((line = stdout.readLine()) != null) {

                    text = text + "\n" + line;
                }
                output = text;
            } else {

                String line;
                while ((line = stderr.readLine()) != null) {

                    output = line;
                }
            }

            if (output == null) {

                return "No output";
            }

            return output;
        } catch (InterruptedException ie) {
            process.destroy();
        } catch (Exception e) {

            e.printStackTrace();
        }
        return "Error";
    }

    /**
     * Method creates an alert box with a given message.
     * @param message
     */
    public void createAlertBox(String message) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(message);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
                getClass().getResource("../cyanBlueGreyPalette.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        alert.show();
    }

    /**
     * Creates a file directory of a given name
     * @param directory
     */
    public void createFileDirectory(String directory) {

        try {

            new File(directory).mkdir();

        } catch (Exception e) {
            createAlertBox("Error: " + e.getMessage());
        }
    }

    /**
     * Method which clears and create all necessary files to be used for creations
     */
    public void resetDirs() {

        command("rm -rf src/audio/*; rm -rf src/tempImages/*;");
        createFileDirectory("src/creations");
        createFileDirectory("src/tempImages");
        createFileDirectory("src/audio");
    }

    //-------------------------------Getters and Setters--------------------------------//
    public void setDuration(double duration) {

        _duration = duration;
    }

    public double getDuration() {

        return _duration;
    }

    public void setAnswers(int correct, int total) {
        _correctAnswers = correct;
        _totalAnswers = total;
    }

    public int[] getAnswers() {
        int[] output = {_correctAnswers,_totalAnswers,_scorePercentage};
        return output;
    }

    public void setBestScore(int correct, int total) {

        // calculate percentage
        double decimal = ((double) correct) / total;
        int percentage = (int) (decimal * 100);
        if (percentage > _scorePercentage) {
            _scorePercentage = percentage;
        }
    }

    public void setAnsweredCreations(ObservableList<Creation> creations) {
        _answeredCreations = creations;
    }

    public ObservableList<Creation> getAnsweredCreations() {
        return _answeredCreations;
    }

    public void setPreviousScene(Scene scene) {
        previousScene = scene;
    }

    public void setHasDownloaded(boolean hasDownloaded) {
        _hasDownloaded = hasDownloaded;
    }

    public void setContainsAudio(boolean containsAudio) {
        _containsAudio = containsAudio;
    }

    public boolean getHasDownloaded() {
        return _hasDownloaded;
    }

    public boolean getContainsAudio() { return _containsAudio; }

    public void setImagesSelected(boolean imagesSelected) {
        _imagesSelected = imagesSelected;
    }

    public void setHasText(boolean hasText) {
        _hasText = hasText;
    }

    public boolean getImagesSelected() {
        return _imagesSelected;
    }

    public boolean getHasText() {
        return _hasText;
    }

    public void setSearchTerm(String searchTerm) { _searchTerm = searchTerm; }

    public String getSearchTerm() { return _searchTerm; }

}