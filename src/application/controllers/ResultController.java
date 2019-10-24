package application.controllers;

import application.Creation;
import application.Main;
import application.MethodHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This is a controller class for the scene "Result.fxml" and is
 * responsible for everything relating to the Result scene.
 */
public class ResultController implements Initializable {

    private MethodHelper methodHelper = Main.getMethodHelper();

    // all FXML component fields
    @FXML private Label correctAnswers;
    @FXML private Label totalAnswers;
    @FXML private Label bestPercentage;
    @FXML private TableView<Creation> correctTableView;
    @FXML private TableColumn<Creation, String> creationNameColumn;
    @FXML private TableColumn<Creation, String> searchTermColumn;
    @FXML private TableColumn<Creation, String> attemptColumn;
    @FXML private TableColumn<Creation, String> correctColumn;
    @FXML private Button mainMenuButton;

    private ObservableList<Creation> _answeredCreations = FXCollections.observableArrayList();

    /**
     * Method to return back to the main menu.
     * @param event
     * @throws Exception
     */
    @FXML
    void returnToMainMenu(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/MainMenu.fxml");
    }

    /**
     * Method to initialise the scene
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // initialise the table view
        _answeredCreations = methodHelper.getAnsweredCreations();

        creationNameColumn.setCellValueFactory(new PropertyValueFactory<>("_creationName"));
        searchTermColumn.setCellValueFactory(new PropertyValueFactory<>("_searchTerm"));
        attemptColumn.setCellValueFactory(new PropertyValueFactory<>("_attempt"));
        correctColumn.setCellValueFactory(new PropertyValueFactory<>("_correct"));

        // set the items to the table view
        correctTableView.setItems(_answeredCreations);

        int[] answers = methodHelper.getAnswers();
        correctAnswers.setText("" + answers[0]);
        totalAnswers.setText("" + answers[1]);
        bestPercentage.setText("Best Score Percentage: " + answers[2] + "%");

    }
}
