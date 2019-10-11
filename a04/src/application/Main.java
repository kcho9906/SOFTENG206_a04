package application;

import application.controllers.MediaController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.print.attribute.standard.Media;
import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        createDirs();
        Parent root = FXMLLoader.load(getClass().getResource("scenes/MainMenu.fxml"));
        primaryStage.setTitle("VARpedia");
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();
        MethodHelper methodHelper = new MethodHelper();
        methodHelper.playVideo(new File("src/creations/test/test.mp4"));
    }


    public static void main(String[] args) {

        launch(args);
    }

    public void createDirs() {

        File creationDir = new File("src/creations");
        File tempImagesDir = new File("src/tempImages");
        File audioDir = new File("src/audio");

        System.out.println(creationDir.mkdir());
        System.out.println(tempImagesDir.mkdir());
        System.out.println(audioDir.mkdir());
    }
}
