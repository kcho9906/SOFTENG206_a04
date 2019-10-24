package application.controllers;

import application.MethodHelper;
import application.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javax.print.attribute.standard.Media;
import java.io.File;

/**
 * This is a controller class for the scene "MainMenu.fxml" and is
 * responsible for everything relating to the Main Menu scene.
 */
public class MainMenuController {

    private static MethodHelper methodHelper = Main.getMethodHelper();

    /**
     * Method to change to the audio scene.
     * @param event
     * @throws Exception
     */
    @FXML
    void toCreationScene(ActionEvent event) throws Exception {

        methodHelper.changeScene(event, "scenes/Audio.fxml");
    }

    /**
     * Method to change to the creation list scene.
     * @param event
     * @throws Exception
     */
    @FXML
    void toViewCreationScene(ActionEvent event) throws Exception {

        methodHelper.changeScene(event, "scenes/CreationList.fxml");
    }


}
