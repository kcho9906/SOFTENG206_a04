package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {


        createDirs();
        Parent root = FXMLLoader.load(getClass().getResource("scenes/MainMenu.fxml"));
        primaryStage.setTitle("VARpedia");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
    }


    public static void main(String[] args) {

        launch(args);
    }

    public void createDirs() {

        File creationDir = new File("src/creations");
        File tempImagesDir = new File("src/tempImages");
        File audioDir = new File("src/audio");
        creationDir.mkdir();
        tempImagesDir.mkdir();
        audioDir.mkdir();
    }
}
