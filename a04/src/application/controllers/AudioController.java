package application.controllers;

import application.MethodHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class AudioController {

    MethodHelper methodHelper = new MethodHelper();

    @FXML
    private TextField searchTextField;

    @FXML
    private Button searchButton;

    @FXML
    private Label statusLabel;

    @FXML
    private Slider synthSlider;

    @FXML
    private Button previewTextButton;

    @FXML
    private Button saveTextButton;

    @FXML
    private ListView<?> existingAudioListView;

    @FXML
    private Button resetButton;

    @FXML
    private Button MenuButton;

    @FXML
    private Button nextButton;

    @FXML
    void previewTextAction(ActionEvent event) {

    }

    @FXML
    void resetAction(ActionEvent event) {

    }

    @FXML
    void returnToMenu(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/MainMenu.fxml");
    }

    @FXML
    void saveTextAction(ActionEvent event) {

    }

    @FXML
    void searchAction(ActionEvent event) {

    }

    @FXML
    void toNextStageButton(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/Image.fxml");
    }

}
