package application.controllers;

import application.MethodHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.util.List;

public class ImageController {

    MethodHelper methodHelper = new MethodHelper();
    private List<BufferedImage> imageList;

    @FXML
    private ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9, image10, image11, image12, image13, image14, image15, image16;

    @FXML
    private Button backButton, CreateButton;

    @FXML
    private TextField creationNameInput;

    @FXML
    void createCreation(ActionEvent event) {
        imageList = methodHelper.getImageList();
    }

    @FXML
    void returnToAudio(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/Audio.fxml");
    }

}
