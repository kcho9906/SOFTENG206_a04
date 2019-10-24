package application.controllers;

import application.CreationWorker;
import application.FlickrImageExtractor;
import application.Main;
import application.MethodHelper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

/**
 * This is a controller class for the scene "Image.fxml" and is
 * responsible for everything relating to the Image scene.
 */
public class ImageController implements Initializable {

    // all FXML components
    @FXML private Button createButton;
    @FXML private TilePane imagePane;
    @FXML private ProgressIndicator loadingCircle;
    @FXML private TextField creationNameInput;

    private static MethodHelper methodHelper = Main.getMethodHelper();
    private ObservableList<File> imageList = FXCollections.observableArrayList();
    private ObservableList<File> selectedList = FXCollections.observableArrayList();
    private static String searchTerm = "";
    private String creationName = "";
    private final static int[] imagesRetrieved = {0};private Thread thread = new Thread();
    private CreationWorker creationWorker;


    /**
     * This method is responsible for creating the creation and uses
     * a javafx Task to run it in the background.
     * @param event
     * @throws Exception
     */
    @FXML void createCreation(ActionEvent event) throws Exception {

        // gets the creation name from the text field.
        creationName = creationNameInput.getText().trim();

        File creationDir = new File("src/creations/" + creationName);
        String action = getAction(creationDir);

        // set the loading circle to be visible.
        loadingCircle.setVisible(true);

        // create the creation using a worker to be done in a background thread.
        creationWorker = new CreationWorker(selectedList, searchTerm, action, creationDir);

        Thread th = new Thread(creationWorker);
        th.start();

        creationWorker.setOnSucceeded(complete  -> {

            loadingCircle.setVisible(false);

            // create an alert to see if the user wants to watch the creation made or go back to menu.
            boolean answer = methodHelper.addConfirmationAlert("Success!", creationName + " was created successfully!\nPlay creation?");
            if (answer) {

                // get the video file to be played
                File videoFile = new File (creationDir.getPath() + "/" + creationDir.getName() + ".mp4");

                try {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("../scenes/Media.fxml"));
                    MediaController controller = new MediaController(videoFile);
                    loader.setController(controller);
                    Parent newSceneParent = loader.load();
                    Scene newScene = new Scene(newSceneParent);
                    Stage window = (Stage) (((Node)event.getSource()).getScene().getWindow());
                    window.setScene(newScene);
                    window.show();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            } else {

                // try/catch to change back to main menu scene
                try {

                    methodHelper.changeScene(event, "scenes/MainMenu.fxml");
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Method to return back to the audio scene, maintaining the previous scene.
     * @param event
     * @throws Exception
     */
    @FXML
    void returnToAudio(ActionEvent event) throws Exception {

        if (creationWorker != null) {
            creationWorker.cancel();
            loadingCircle.setVisible(false);
        }
        methodHelper.command("rm src/audio/" + searchTerm + "/output.*"); //delete merged audio
        methodHelper.changeCreationScene(event, "scenes/Audio.fxml");
        methodHelper.setPreviousScene(createButton.getScene());
    }

    /**
     * Method to update the image grid.
     */
    public void updateGrid() {

        // initialise the grid with actions for each of the images.
        for (int i = 0; i < imagesRetrieved[0]; i++) {

            ToggleButton imageButton = (ToggleButton) imagePane.getChildren().get(i);
            ImageView imageView = (ImageView) imageButton.getGraphic();
            File imageFile = imageList.get(i);
            Image image = new Image(imageFile.toURI().toString());
            imageView.setImage(image);

            // set up the 'setOnAction' for all the images.
            imageButton.setOnAction(event -> {

                // if the list is less than size 10, then continue to allow adding images
                if (selectedList.size() <= 10) {

                    // if image is selected, deselect from GUI
                    if (selectedList.contains(imageFile)) {

                        imageButton.setStyle(null);
                        selectedList.remove(imageFile);

                    // if the size is less than 10, add an image.
                    } else if (selectedList.size() < 10) {

                        selectedList.add(imageFile);
                    }

                    // changes the status of the method helper is list is empty
                    if (selectedList.isEmpty()) {
                        methodHelper.setImagesSelected(false);
                    } else {
                        methodHelper.setImagesSelected(true);
                    }

                    // if both conditions are met, the button will appropriately enable and disable.
                    if (methodHelper.getImagesSelected() && methodHelper.getHasText()) {
                        createButton.setDisable(false);
                    } else {
                        createButton.setDisable(true);
                    }
                }
            });
        }
    }

    /**
     * Method which will get the images from the flickr API
     * @param searchTerm
     * @param nextButton
     */
    public static void getImages(String searchTerm, Button nextButton, ProgressIndicator loadingCircle, Label waitingFor) {

        ImageController.searchTerm = searchTerm;
        boolean exists = false;
        File searchTermImagesDir = new File("src/tempImages/" + searchTerm);

        // checks if the directory already exists
        if (searchTermImagesDir.isDirectory()) {

            imagesRetrieved[0] = 12;
            methodHelper.setHasDownloaded(true);
            exists = true;

        }

        // If the images don't already exist, then download the images from flickr.
        if (!exists) {

            // notifies user images are downloading
            nextButton.setDisable(true);
            waitingFor.setVisible(true);
            loadingCircle.setVisible(true);

            int number = 12;
            AudioWorker audioWorker = new AudioWorker(number, ImageController.searchTerm);

            audioWorker.setOnSucceeded(event -> {

                try {
                    loadingCircle.setVisible(false);
                    waitingFor.setVisible(false);
                    imagesRetrieved[0] = audioWorker.get();
                    if (imagesRetrieved[0] != number) {

                        // create an alert to tell the user that 12 images could not be found
                        methodHelper.createAlertBox("Did not get " + number + " images\nOnly retrieved " + imagesRetrieved[0] + " images");
                    }

                    // change status of the text if the audio list is not empty
                    methodHelper.setHasDownloaded(true);

                    // notify user images are ready to be downloaded
                    if (methodHelper.getContainsAudio()) {

                        nextButton.setDisable(false);
                    }

                } catch (InterruptedException e) {

                    e.printStackTrace();
                } catch (ExecutionException e) {

                    e.printStackTrace();
                }
            });

            Thread th = new Thread(audioWorker);
            th.start();
        }
    }

    /**
     * Method to upload the images to the grid
     */
    public void uploadImages() {

        String path = System.getProperty("user.dir") + "/src/tempImages/" + searchTerm;
        File[] imageFiles = new File(path).listFiles(File::isFile);

        for (File image: imageFiles) {

            imageList.add(image);
        }

        // update the grid with the new images
        updateGrid();
    }

    /**
     * Method to initialise the scene with necessary components.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // disable create button
        createButton.setDisable(true);

        // uploads the images
        uploadImages();

        // sets the binding properties of the components of the class
        bindProperties();
    }

    /**
     * Method to set up the bind properties of the components of the class.
     */
    public void bindProperties() {

        /**
         * Listener to make sure there are no spaces in the creation name
         */
        creationNameInput.textProperty().addListener(

                (observable, old_value, new_value) -> {

                    if(new_value.contains(" ")) {

                        //prevents from the new space char
                        creationNameInput.setText(old_value + "_");
                    }
                }
        );

        /**
         * Listener to check whether a value in the text field has been changed.
         */
        creationNameInput.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {

                // changes the status of the method helper is list is empty
                if (newValue.isEmpty()) {

                    methodHelper.setHasText(false);
                } else {

                    methodHelper.setHasText(true);
                }

                // if both conditions are met, the button will appropriately enable and disable.
                if (methodHelper.getImagesSelected() && methodHelper.getHasText()) {

                    createButton.setDisable(false);
                } else {

                    createButton.setDisable(true);
                }
            }
        });
    }

    /**
     * Method to get the action for creation creation
     * @param file
     * @return
     */
    private String getAction(File file) {

        if (file.exists()) {

            // create an alert to ask whether they would like to overwrite or rename the current creation
            Boolean overwrite = methodHelper.addConfirmationAlert("ERROR", "\"" + creationName + "\" exists. \nRename or overwrite?");
            if (overwrite) {

                return "overwrite";
            } else {

                creationNameInput.clear();
                return "rename";
            }
        }

        return "create";
    }

    /**
     * Worker which will download images in the background thread
     */
    @FXML
    void returnToMenu(ActionEvent event) throws Exception {
        boolean returnToMenu = methodHelper.addConfirmationAlert("Return to Menu", "All progress with be lost. Are you sure?");
        if ( returnToMenu ) {

            methodHelper.resetDirs();
            methodHelper.changeScene(event, "scenes/MainMenu.fxml");
        }
    }

    public static class AudioWorker extends Task<Integer> {

        private int _numImages;
        private String _query;

        public AudioWorker(int numImages, String searchTerm) {

            _numImages = numImages;
            _query = searchTerm;
        }

        @Override
        protected Integer call() throws Exception {

            return FlickrImageExtractor.downloadImages(_query, _numImages);
        }
    }
}
