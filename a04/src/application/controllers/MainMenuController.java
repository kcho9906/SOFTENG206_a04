package application.controllers;

import application.MethodHelper;
import application.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javax.print.attribute.standard.Media;
import java.io.File;


public class MainMenuController {

    MethodHelper methodHelper = new MethodHelper();

    @FXML
    void toCreationScene(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/Audio.fxml");
    }

    @FXML
    void toViewCreationScene(ActionEvent event) throws Exception {

        methodHelper.changeScene(event, "scenes/CreationList.fxml");
    }


}
