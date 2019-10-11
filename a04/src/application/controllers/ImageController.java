package application.controllers;

import application.FlickrImageExtractor;
import application.MethodHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ImageController implements Initializable {

    @FXML
    public GridPane imagePane;
    MethodHelper methodHelper = new MethodHelper();
    private ObservableList<File> imageList;

    //@FXML
    //private ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9, image10, image11, image12, image13, image14, image15, image16;

    @FXML
    private Button backButton, CreateButton;

    @FXML
    private TextField creationNameInput;

    @FXML
    void createCreation(ActionEvent event) {
        //imageList = methodHelper.getImageList();
    }

    @FXML
    void returnToAudio(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/Audio.fxml");
    }

    public void updateGrid() {

        final int rows = 2;
        final int cols = 5  ;
        for (int i = 0; i < rows; i++ ) {
            for (int j = 0; j < cols; j++ ) {
                File file = imageList.get(i+j);
                Image image = new Image(file.toURI().toString());
                ImageView imageView = new ImageView();
                imageView.setImage(image);
                final HBox picRegion = new HBox();
                picRegion.getChildren().add(imageView);
                System.out.println(imagePane);
                imagePane.add(picRegion, j, i);
                System.out.println("imagePane.add(" + picRegion + "," + j + "," + i);
            }

        }

    }

    public void getImages(String searchTerm) {
        int number = 10;
        int numImage = FlickrImageExtractor.downloadImages(searchTerm, number);
    }

    public void uploadImages() {

        imageList = FXCollections.observableArrayList();
        String path = System.getProperty("user.dir") + "/src/tempImages";
        File[] imageFiles = new File(path).listFiles(File::isFile);
        for (File image: imageFiles) {

            imageList.add(image);
        }
        updateGrid();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        uploadImages();
    }
}
