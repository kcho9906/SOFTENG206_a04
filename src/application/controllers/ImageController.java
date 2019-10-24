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

public class ImageController implements Initializable {



    @FXML
    private Button createButton;
    @FXML
    private TilePane imagePane;

    @FXML
    private ProgressIndicator loadingCircle;
    private static MethodHelper methodHelper = Main.getMethodHelper();
    private ObservableList<File> imageList = FXCollections.observableArrayList();
    private ObservableList<File> selectedList = FXCollections.observableArrayList();
    private static String query = "";
    private String creationName = "";
    private final static int[] imagesRetrieved = {0};

    @FXML
    private TextField creationNameInput;

    @FXML
    void createCreation(ActionEvent event) throws Exception {

        creationName = creationNameInput.getText().trim();


        File creationDir = new File("src/creations/" + creationName);
        String action = getAction(creationDir);


        System.out.println(selectedList.size() + " selected images");
        loadingCircle.setVisible(true);
        CreationWorker creationWorker = new CreationWorker(selectedList, query, action, creationDir);
        Thread th = new Thread(creationWorker);
        th.start();

        creationWorker.setOnSucceeded(complete  -> {

            loadingCircle.setVisible(false);
            boolean answer = methodHelper.addConfirmationAlert("Success!", creationName + " was created successfully!\nPlay creation?");
            if (answer) {
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
                try {
                    methodHelper.changeScene(event, "scenes/MainMenu.fxml");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    void returnToAudio(ActionEvent event) throws Exception {

        methodHelper.command("rm src/audio/" + query + "/output.mp3"); //delete merged audio
        methodHelper.changeCreationScene(event, "scenes/Audio.fxml");
        methodHelper.setPreviousScene(createButton.getScene());
    }

    public void updateGrid() {

        System.out.println(); // just for debug spacing

        // initialise the grid with actions for each of the imags.
        for (int i = 0; i < imagesRetrieved[0]; i++) {

            ToggleButton imageButton = (ToggleButton) imagePane.getChildren().get(i);
            ImageView imageView = (ImageView) imageButton.getGraphic();
            File imageFile = imageList.get(i);
            Image image = new Image(imageFile.toURI().toString());
            imageView.setImage(image);
            imageButton.setOnAction(event -> {

                if (selectedList.size() <= 10) {

                    if (selectedList.contains(imageFile)) {
                        imageButton.setStyle(null);
                        selectedList.remove(imageFile);
                        System.out.println(imageFile + " was removed from the list of selected images");

                    } else if (selectedList.size() < 10) {

                        selectedList.add(imageFile);
                        imageButton.setStyle("-fx-background-color: Yellow");
                        System.out.println(imageFile + " was added to the list of selected images");

                    }

                    if (selectedList.isEmpty()) {
                        methodHelper.setImagesSelected(false);
                    } else {
                        methodHelper.setImagesSelected(true);
                    }

                    if (methodHelper.getImagesSelected() && methodHelper.getHasText()) {
                        createButton.setDisable(false);
                    } else {
                        createButton.setDisable(true);
                    }

                }

            });
        }
    }

    public static void getImages(String searchTerm, Button nextButton) {

        query = searchTerm;
        boolean exists = false;
        File searchTermImagesDir = new File("src/tempImages/" + searchTerm);

        // checks if the directory already exists
        if (searchTermImagesDir.isDirectory()) {
            System.out.println("exists");
            imagesRetrieved[0] = 12;
            exists = true;

        }

        if (!exists) {
            nextButton.setDisable(true);
            nextButton.setText("downloading images");
            int number = 12;
            AudioWorker audioWorker = new AudioWorker(number, query);
            audioWorker.setOnSucceeded(event -> {
                try {
                    imagesRetrieved[0] = audioWorker.get();
                    if (imagesRetrieved[0] != number) {
                        methodHelper.createAlertBox("Did not get " + number + " images\nOnly retrieved " + imagesRetrieved[0] + " images");
                    }

                    // change status of the text if the audio list is not empty
                    methodHelper.setHasDownloaded(true);

                    // checks if the images have downloaded
                    if (methodHelper.getContainsAudio()) {
                        nextButton.setDisable(false);
                        nextButton.setText("Next");
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

    public void uploadImages() {

        String path = System.getProperty("user.dir") + "/src/tempImages/" + query;
        File[] imageFiles = new File(path).listFiles(File::isFile);

        for (File image: imageFiles) {

            System.out.println(image.getName());
            imageList.add(image);
        }
        System.out.println("image list has " + imageList.size() + " pictures");
        updateGrid();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // disable create button
        createButton.setDisable(true);

        System.out.println(methodHelper.getDuration());
        uploadImages();
        bindProperties();
    }

    public void bindProperties() {

        creationNameInput.textProperty().addListener(
                (observable, old_value, new_value) -> {
                    if(new_value.contains(" "))
                    {
                        //prevents from the new space char
                        creationNameInput.setText(old_value);
                    }
                }
        );

        creationNameInput.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {

                System.out.println(" Text Changed to  " + newValue + "\n");
                if (newValue.isEmpty()) {
                    methodHelper.setHasText(false);
                } else {
                    methodHelper.setHasText(true);
                }

                if (methodHelper.getImagesSelected() && methodHelper.getHasText()) {
                    createButton.setDisable(false);
                } else {
                    createButton.setDisable(true);
                }

            }
        });

    }

    private String getAction(File file) {

        if (file.exists()) {
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

    public static class AudioWorker extends Task<Integer> {

        private int _numImages;
        private String _query;

        public AudioWorker(int numImages, String query) {

            _numImages = numImages;
            _query = query;
        }

        @Override
        protected Integer call() throws Exception {
            return FlickrImageExtractor.downloadImages(_query, _numImages);
        }
    }
}
