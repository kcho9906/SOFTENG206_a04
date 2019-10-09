package application.controllers;

import application.MethodHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;

public class ImageController {

    MethodHelper methodHelper = new MethodHelper();

    @FXML
    private ListView<?> flickrImagesList;

    @FXML
    private Button addButton;

    @FXML
    private Button removeButton;

    @FXML
    private ListView<?> creationImageList;

    @FXML
    private Button shiftUpButton;

    @FXML
    private Button shiftDownButton;

    @FXML
    private HBox imageDisplay;

    @FXML
    private Button getImageButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button backButton;

    @FXML
    private Button nextButton;

    @FXML
    void addImageToCreation(ActionEvent event) {

    }

    @FXML
    void getImages(ActionEvent event) {

    }

    @FXML
    void nextScene(ActionEvent event) {

    }

    @FXML
    void toPreviousScene(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/Audio.fxml");

    }

    @FXML
    void removeImageFromCreation(ActionEvent event) {

    }

    @FXML
    void shiftOrderDown(ActionEvent event) {

    }

    @FXML
    void shiftOrderUp(ActionEvent event) {

    }

}
