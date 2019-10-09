package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MethodHelper {

    public void changeScene(ActionEvent event, String scene) throws Exception {
        Parent newSceneParent = FXMLLoader.load(getClass().getResource(scene));
        Scene newScene = new Scene(newSceneParent);
        Stage window = (Stage) (((Node)event.getSource()).getScene().getWindow());
        window.setScene(newScene);
        window.show();
    }

    /**
     * This method acts as the bash command line where the input command
     * runs in the bash terminal
     * @param command
     * @return
     */
    public static String command(String command) {

        try {

            String output = null;

            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);

            Process process = pb.start();

            BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            int exitStatus = process.waitFor();

            if (exitStatus == 0) {

                String line;
                String text = "";
                while ((line = stdout.readLine()) != null) {

                    text = text + "\n" + line;
                }
                output = text;
            } else {

                String line;
                while ((line = stderr.readLine()) != null) {

                    output = line;
                }
            }

            if (output == null) {

                return "No output";
            }

            return output;
        } catch (Exception e) {

            e.printStackTrace();
        }
        return "Error";
    }
}