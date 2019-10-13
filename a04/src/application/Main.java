package application;

import javafx.application.Application;
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
