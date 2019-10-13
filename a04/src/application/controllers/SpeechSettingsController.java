package application.controllers;

import application.Main;
import application.MethodHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class SpeechSettingsController implements Initializable {

    private static MethodHelper methodHelper = Main.getMethodHelper();
    String _currentLanguage;
    String _currentGender;

    @FXML
    private Slider pitchBaseSlider;

    @FXML
    private Slider pitchRangeSlider;

    @FXML
    private Slider speedSlider;

    @FXML
    private Slider ageSlider;

    @FXML
    private ChoiceBox<String> languageChoiceBox;

    @FXML
    private ChoiceBox<String> genderChoiceBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    void cancelAction(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/Audio.fxml");
    }

    @FXML
    void saveAction(ActionEvent event) throws Exception {
        // saves the current changes made in the settings scene

        // return to audio scene
        methodHelper.changeScene(event, "scenes/Audio.fxml");
    }

    public void setLanguage(String language) {
        this._currentLanguage = language;
    }

    public void setGender(String gender) {
        this._currentGender = gender;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        languageChoiceBox.setValue("en English"); // change this to be the current language.
        languageChoiceBox.setItems(languageChoiceBoxList);

        genderChoiceBox.setValue("Male");
        genderChoiceBox.setItems(genderChoiceBoxList);
    }

    private ObservableList<String> languageChoiceBoxList = FXCollections.observableArrayList(
            "af  Afrikaans",
            "bs  Bosnian",
            "ca  Catalan",
            "cs  Czech",
            "da  Danish",
            "de  German",
            "el  Greek",
            "en English",
            "eo  Esperanto",
            "es  Spanish",
            "es-la  Spanish - Latin America",
            "fi  Finnish",
            "fr  French",
            "hr  Croatian",
            "hu  Hungarian",
            "it  Italian",
            "kn  Kannada",
            "ku  Kurdish",
            "lv  Latvian",
            "nl  Dutch",
            "pl  Polish",
            "pt  Portuguese (Brazil)",
            "pt-pt  Portuguese (European)",
            "ro  Romanian",
            "sk  Slovak",
            "sr  Serbian",
            "sv  Swedish",
            "sw  Swahihi",
            "ta  Tamil",
            "tr  Turkish",
            "zh  Mandarin Chinese"
    );
    private ObservableList<String> genderChoiceBoxList = FXCollections.observableArrayList(
            "Male",
            "Female",
            "Unknown"
    );
}
