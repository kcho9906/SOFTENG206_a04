package application.controllers;

import application.CreationWorker;
import application.FlickrImageExtractor;
import application.MethodHelper;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ImageController implements Initializable {

    @FXML
    private Button createButton;
    @FXML
    private TilePane imagePane;

    @FXML
    private ProgressIndicator loadingCircle;
    MethodHelper methodHelper = new MethodHelper();
    private ObservableList<File> imageList = FXCollections.observableArrayList();
    private ObservableList<File> selectedList = FXCollections.observableArrayList();
    private String query = "";
    private String creationName = "";

    @FXML
    private TextField creationNameInput;


    @FXML
    void createCreation(ActionEvent event) throws Exception {

        creationName = creationNameInput.getText().trim();


        File creationDir = new File("src/creations/" + creationName);
        String action = getAction(creationDir);


        System.out.println(selectedList.size() + " selected images");
        loadingCircle.setVisible(true);
        CreationWorker creationWorker = new CreationWorker(selectedList.size(), query, action, creationDir);
        Thread th = new Thread(creationWorker);
        th.start();

        creationWorker.setOnSucceeded(complete  -> {

            loadingCircle.setVisible(false);
            methodHelper.addConfirmationAlert("Success!", creationName + " was created successfully!\nPlay creation?");
        });
    }

    @FXML
    void returnToAudio(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/Audio.fxml");
    }

    public void updateGrid() {

        System.out.println(imagePane.getChildren().size());
        for (int i = 0; i < imagePane.getChildren().size(); i++) {

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

                } else {

                }
            });
        }

    }

    public void getImages(String searchTerm) {
        query = searchTerm;
        int number = 12;
        int numImage = FlickrImageExtractor.downloadImages(query, number);
    }

    public void uploadImages() {

        String path = System.getProperty("user.dir") + "/src/tempImages";
        File[] imageFiles = new File(path).listFiles(File::isFile);
        for (File image: imageFiles) {

            imageList.add(image);
        }
        System.out.println(imageList.size());
        updateGrid();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        uploadImages();
        bindProperties();
    }

    public void bindProperties() {

        createButton.disableProperty().bind(new BooleanBinding() {
            @Override
            protected boolean computeValue() {
                return (creationNameInput.getText().isEmpty() || !creationName.matches("[a-zA-Z0-9_-]+"));
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
}
