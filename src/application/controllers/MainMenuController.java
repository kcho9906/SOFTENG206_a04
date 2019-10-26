package application.controllers;

import application.helpers.MethodHelper;
import application.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import java.awt.*;
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

    /**
     * Method to open user manual for user
     * @param actionEvent
     */
    public void openUserManual(ActionEvent actionEvent) {

        File file = new File("UserManual.docx");
        if (Desktop.isDesktopSupported()) {

            new Thread(() -> {

                try {
                    Desktop.getDesktop().open(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }).start();
        }

    }
}
