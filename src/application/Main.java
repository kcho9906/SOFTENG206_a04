package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *                              VARpedia
 *
 * JavaFX GUI application which lets the user search for a term on Wikipedia.
 * They will be given text from the search, the user can select chunks of text
 * and merge them into one audio file. Alongside this, the user can select images
 * to be made into a video with the superimposed word. The video can then be played
 * back to the user.
 *
 * @Authors     Charles Paterson
 *              Steven Ho
 */
public class Main extends Application {

    private static MethodHelper methodHelper = new MethodHelper();

    public static MethodHelper getMethodHelper() {

        return methodHelper;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // clear and create all necessary files to be used for creations
        methodHelper.resetDirs();

        Parent root = FXMLLoader.load(getClass().getResource("scenes/MainMenu.fxml"));
        primaryStage.setTitle("VARpedia");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

        // on closing the application, deletes all direectories and safely exits.
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            boolean exit = methodHelper.addConfirmationAlert("Quit Application", "Are you sure?", "Quit", "Cancel");
            if (exit) {
                methodHelper.resetDirs();
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {

        launch(args);
    }
}
