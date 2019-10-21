package application;


import application.controllers.MediaController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class MethodHelper {

    private double _duration;
    private int _correctAnswers = 0;
    private int _totalAnswers = 0;
    private int _scorePercentage = 0;
    private ObservableList<Creation> _answeredCreations = FXCollections.observableArrayList();
    private Scene previousScene;
    private boolean _next = false;
    private boolean _imagesDownloaded = false;

    public void changeScene(ActionEvent event, String scene) throws Exception {
        Parent newSceneParent = FXMLLoader.load(getClass().getResource(scene));
        Scene newScene = new Scene(newSceneParent);
        Stage window = (Stage) (((Node)event.getSource()).getScene().getWindow());
        window.setScene(newScene);
        window.show();
    }

    public boolean addConfirmationAlert(String title, String contentText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(contentText);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            return true;
        } else {
            return false;
        }
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

    public void createAlertBox(String message) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(message);
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

    public void changeCreationScene(ActionEvent event, String scene) throws Exception {
        System.out.println(previousScene);
        if (previousScene == null) {
            changeScene(event, scene);
        } else {
            Stage window = (Stage) (((Node) event.getSource()).getScene().getWindow());
            window.setScene(previousScene);
            window.show();
        }
    }

    public void setNext(boolean next) {
        _next = next;
    }

    public boolean getNext() {
        return _next;
    }

    public void setImagesDownloaded(boolean imagedDownloaded) {
        _imagesDownloaded = imagedDownloaded;
    }

    public boolean getBooleanImages() {
        return _imagesDownloaded;
    }
}