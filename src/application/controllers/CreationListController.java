package application.controllers;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import application.Main;
import application.helpers.Creation;
import application.helpers.MethodHelper;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * This is a controller class for the scene "CreationList.fxml" and is
 * responsible for everything relating to the CreationList scene.
 */
public class CreationListController implements Initializable {

	private static MethodHelper methodHelper = Main.getMethodHelper();

	// all FXML component fields
	@FXML
	private TableColumn<Creation, String> nameColumn;
	@FXML
	private TableColumn<Creation, String> searchTermColumn;
	@FXML
	private TableColumn<Creation, String> durationColumn;
	@FXML
	private TableColumn<Creation, String> timeColumn;
	@FXML
	private TableView<Creation> creationTableView;
	@FXML
	private Button playButton;
	@FXML
	private Button deleteButton;
	@FXML
	private Button quizButton;

	/**
	 * Method to delete an already created creation
	 *
	 * @param event
	 */
	@FXML
	void deleteCreation(ActionEvent event) {

		ObservableList<Creation> allCreations = creationTableView.getItems();
		Object creationSelected = creationTableView.getSelectionModel().getSelectedItem();
		String creationName = ((Creation) creationSelected).get_creationName();

		// add confirmation alert to confirm whether they want to delete the creation.
		Boolean answer = methodHelper.addConfirmationAlert("Deleting Creation",
				"Are you sure you want to delete \"" + creationName + "\"?", "Delete Creation", "Cancel");
		if (answer) {

			allCreations.remove(creationSelected);
			String command = "rm -r -f src/creations/" + creationName;
			methodHelper.command(command);
		}
	}

	/**
	 * Method to play the selected creation in the list. Changes to the media scene
	 * with the chosen video.
	 *
	 * @param event
	 * @throws Exception
	 */
	@FXML
	void playCreation(ActionEvent event) throws Exception {

		String creationName = creationTableView.getSelectionModel().getSelectedItem().toString();
		File videoFile = new File("src/creations/" + creationName + "/" + creationName + ".mp4");
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/scenes/Media.fxml"));
		MediaController controller = new MediaController(videoFile);
		loader.setController(controller);
		Parent newSceneParent = loader.load();
		Scene newScene = new Scene(newSceneParent);
		Stage window = (Stage) (((Node) event.getSource()).getScene().getWindow());
		window.setScene(newScene);
		window.show();
	}

	/**
	 * Method to change to the quiz scene
	 *
	 * @param event
	 * @throws Exception
	 */
	@FXML
	void quizCreation(ActionEvent event) throws Exception {
		methodHelper.changeScene(event, "Quiz.fxml");
	}

	/**
	 * Method to change back to main menu
	 *
	 * @param event
	 * @throws Exception
	 */
	@FXML
	void returnToMenu(ActionEvent event) throws Exception {
		methodHelper.changeScene(event, "MainMenu.fxml");
	}

	/**
	 * This method is for getting the creations from the list.
	 *
	 * @return
	 */
	private ObservableList<Creation> getCreations() {

		ObservableList<Creation> creations = FXCollections.observableArrayList();
		String path = System.getProperty("user.dir") + "/src/creations";
		File[] directories = new File(path).listFiles(File::isDirectory);
		if (directories != null) {
			for (File directory : directories) {
	
				Creation creation = new Creation(directory);
				creations.add(creation);
			}
		}
		return creations;
	}

	/**
	 * updates the table
	 */
	public void updateTable() {

		creationTableView.setItems(getCreations());
	}

	/**
	 * Method to initialise the scene with necessary values and appropriate
	 * bindings.
	 *
	 * @param location
	 * @param resources
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// updates the table
		updateTable();

		nameColumn.setCellValueFactory(new PropertyValueFactory<>("_creationName"));
		searchTermColumn.setCellValueFactory(new PropertyValueFactory<>("_searchTerm"));
		timeColumn.setCellValueFactory(new PropertyValueFactory<>("_timeCreated"));
		durationColumn.setCellValueFactory(new PropertyValueFactory<>("_duration"));

		// disable buttons until a creation is selected (or exists for the quiz button)
		playButton.disableProperty().bind(Bindings.isEmpty(creationTableView.getSelectionModel().getSelectedItems()));
		deleteButton.disableProperty().bind(Bindings.isEmpty(creationTableView.getSelectionModel().getSelectedItems()));
		quizButton.disableProperty().bind(Bindings.isEmpty(creationTableView.itemsProperty().getValue()));
	}
}
