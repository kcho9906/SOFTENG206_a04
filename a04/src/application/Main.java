package application;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static MethodHelper methodHelper = new MethodHelper();

    public static MethodHelper getMethodHelper() {

        return methodHelper;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        createDirs();
        Parent root = FXMLLoader.load(getClass().getResource("scenes/MainMenu.fxml"));
        primaryStage.setTitle("VARpedia");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            boolean exit = methodHelper.addConfirmationAlert("Quit Application", "Are you sure?");
            if (exit) {
                deleteDirs();
                Platform.exit();
                System.exit(0);
            }
        });
    }

    private void deleteDirs() {
        methodHelper.command("rm -rf src/audio/*; rm -rf src/tempImages/*;");
    }


    public static void main(String[] args) {

        launch(args);
    }

    public void createDirs() {

        methodHelper.createFileDirectory("src/creations");
        methodHelper.createFileDirectory("src/tempImages");
        methodHelper.createFileDirectory("src/audio");
    }
}
