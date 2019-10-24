package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

        // create all necessary temporary files
        createDirs();

        Parent root = FXMLLoader.load(getClass().getResource("scenes/MainMenu.fxml"));
        primaryStage.setTitle("VARpedia");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();

        // on closing the application, deletes all direectories and safely exits.
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            boolean exit = methodHelper.addConfirmationAlert("Quit Application", "Are you sure?");
            if (exit) {
                deleteDirs();
                Platform.exit();
                System.exit(0);
            }
        });
    }

    /**
     * Method which deletes all directories which are not necessary.
     */
    private void deleteDirs() {
        methodHelper.command("rm -rf src/audio; rm -rf src/tempImages;");
    }

    public static void main(String[] args) {

        launch(args);
    }

    /**
     * Method which creates all necessary files to be used for creations
     */
    public void createDirs() {

        methodHelper.createFileDirectory("src/creations");
        methodHelper.createFileDirectory("src/tempImages");
        methodHelper.createFileDirectory("src/audio");
    }
}
