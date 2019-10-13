package application.controllers;

import application.Main;
import application.MethodHelper;
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

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;

public class CreationListController implements Initializable{

    private static MethodHelper methodHelper = Main.getMethodHelper();

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

    @FXML
    private Button menuButton;

    @FXML
    void deleteCreation(ActionEvent event) {
        ObservableList<Creation> allCreations = creationTableView.getItems();
        Object creationSelected = creationTableView.getSelectionModel().getSelectedItem();
        String creationName = ((Creation) creationSelected).get_creationName();
        Boolean answer = methodHelper.addConfirmationAlert("Deleting Creation", "Are you sure you want to delete \"" + creationName + "\"?");
        if (answer) {

            allCreations.remove(creationSelected);
            String command = "rm -r -f src/creations/" + creationName;
            methodHelper.command(command);
        }
    }

    @FXML
    void playCreation(ActionEvent event) throws Exception {

        String creationName = creationTableView.getSelectionModel().getSelectedItem().toString();
        File videoFile = new File("src/creations/" + creationName + "/" + creationName + ".mp4");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../scenes/Media.fxml"));
        MediaController controller = new MediaController(videoFile);
        loader.setController(controller);
        Parent newSceneParent = loader.load();
        Scene newScene = new Scene(newSceneParent);
        Stage window = (Stage) (((Node)event.getSource()).getScene().getWindow());
        window.setScene(newScene);
        window.show();
    }


    @FXML
    void quizCreation(ActionEvent event) {

    }

    @FXML
    void returnToMenu(ActionEvent event) throws Exception {
        methodHelper.changeScene(event, "scenes/MainMenu.fxml");
    }

    /**
     * This method is for getting the creations from the list.
     * @return
     */
    private ObservableList<Creation> getCreations() {

        ObservableList<Creation> creations = FXCollections.observableArrayList();
        String path = System.getProperty("user.dir") + "/src/creations";
        File[] directories = new File(path).listFiles(File::isDirectory);
        for (File directory: directories) {

            Creation creation = new Creation(directory);
            creations.add(creation);
        }
        return creations;
    }

    /**
     * updates the table
     */
    public void updateTable() {

        creationTableView.setItems(getCreations());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateTable();
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("_creationName"));
        searchTermColumn.setCellValueFactory(new PropertyValueFactory<>("_searchTerm"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("_timeCreated"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("_duration"));

        playButton.disableProperty().bind(Bindings.isEmpty(creationTableView.getSelectionModel().getSelectedItems()));
        deleteButton.disableProperty().bind(Bindings.isEmpty(creationTableView.getSelectionModel().getSelectedItems()));
    }
    public class Creation {

        private String _creationName;
        private String _searchTerm;
        private String _timeCreated;
        private String _duration;

        public Creation(File directory) {
            _creationName = directory.getName();
            _timeCreated = getCreationDate(directory);
            _duration = calculateDuration(directory);
            _searchTerm = findSearchTerm(directory);
        }

        public String get_creationName() {

            return _creationName;
        }

        public void set_creationName(String _creationName) {

            this._creationName = _creationName;
        }

        public String get_timeCreated() {

            return _timeCreated;
        }

        public void set_timeCreated(String _timeCreated) {

            this._timeCreated = _timeCreated;
        }

        public void set_duration(String duration) {

            this._duration = duration;
        }

        public String get_duration() {

            return _duration;
        }

        public void set_searchTerm(String searchTerm) {

            this._searchTerm = searchTerm;
        }

        public String get_searchTerm() {

            return _searchTerm;
        }

        private String getCreationDate(File directory) {

            Path p = Paths.get(directory.getAbsolutePath());
            BasicFileAttributes view = null;
            String dateCreated = null;
            try {

                view = Files.getFileAttributeView(p, BasicFileAttributeView.class).readAttributes();
                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy-hh:mm a");
                FileTime date = view.creationTime();
                dateCreated = df.format(date.toMillis());
            } catch (IOException e) {

                e.printStackTrace();
            }
            return dateCreated.trim();
        }

        @Override
        public String toString() {

            return _creationName;
        }

        public String calculateDuration(File directory) {

            String command = "tail -1 " + directory.getPath() + "/info.txt";
            String durationString = methodHelper.command(command);
            command = "eval \"echo $(date -ud \"@" + durationString + "\" +'%M mins %S secs')\"";
            durationString = methodHelper.command(command);
            return durationString.trim();
        }

        public String findSearchTerm(File directory) {

            //get file name for video
            String command = "head -1 " + directory.getPath() + "/info.txt";
            String name = methodHelper.command(command);
            return name.trim();
        }
    }
}

