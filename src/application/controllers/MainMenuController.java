package application.controllers;

import application.helpers.MethodHelper;
import application.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

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

        methodHelper.changeScene(event, "Audio.fxml");
    }

    /**
     * Method to change to the creation list scene.
     * @param event
     * @throws Exception
     */
    @FXML
    void toViewCreationScene(ActionEvent event) throws Exception {

        methodHelper.changeScene(event, "CreationList.fxml");
    }


}
