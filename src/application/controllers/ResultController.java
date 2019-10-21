package application.controllers;

import application.Main;
import application.MethodHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class ResultController implements Initializable {

    private MethodHelper methodHelper = Main.getMethodHelper();

    @FXML
    private Label correctAnswers;

    @FXML
    private Label totalAnswers;

    @FXML
    private Label bestPercentage;

    @FXML
    private ListView<?> answeredQuestionList;

    @FXML
    private Button mainMenuButton;

    @FXML
    void returnToMainMenu(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/MainMenu.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int[] answers = methodHelper.getAnswers();
        correctAnswers.setText("" + answers[0]);
        totalAnswers.setText("" + answers[1]);
        bestPercentage.setText("Best Score Percentage: " + answers[2] + "%");

    }
}
