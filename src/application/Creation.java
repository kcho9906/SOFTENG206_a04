package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;

/**
 * Creation class which contains the fields which are displayed
 * on the table view. The class contains the name, search term,
 * time created, duration and if guess is correct.
 */
public class Creation {

    private static MethodHelper methodHelper = Main.getMethodHelper();

    private String _creationName;
    private String _searchTerm;
    private String _timeCreated;
    private String _duration;
    private String _correct;

    /**
     * Creation constructor for creations in the table view
     * @param directory
     */
    public Creation(File directory) {
        _creationName = directory.getName();
        _timeCreated = getCreationDate(directory);
        _duration = calculateDuration(directory);
        _searchTerm = findSearchTerm(directory);
    }

    /**
     * Creations constructor for the creations as a part of the quiz.
     * @param creationName
     * @param correct
     */
    public Creation(String creationName, boolean correct) {
        _creationName = creationName;

        if (correct) {
            _correct = "Correct";
        } else {
            _correct = "Incorrect";
        }
    }

    /**
     * Method to get the creation date of the creation.
     * @param directory
     * @return
     */
    private String getCreationDate(File directory) {

        Path p = Paths.get(directory.getAbsolutePath());
        BasicFileAttributes view = null;
        String dateCreated = null;
        try {

            view = Files.getFileAttributeView(p, BasicFileAttributeView.class).readAttributes();
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/YY hh:mma");
            FileTime date = view.creationTime();
            dateCreated = df.format(date.toMillis());
        } catch (IOException e) {

            e.printStackTrace();
        }
        return dateCreated.trim();
    }

    /**
     * Method to calculate the duration of a creation
     * @param directory
     * @return
     */
    public String calculateDuration(File directory) {

        String command = "tail -1 " + directory.getPath() + "/info.txt";
        String durationString = methodHelper.command(command);
        command = "eval \"echo $(date -ud \"@" + durationString + "\" +'%M mins %S secs')\"";
        durationString = methodHelper.command(command);

        return durationString.trim();
    }

    /**
     * Method to find the search term.
     * @param directory
     * @return
     */
    public String findSearchTerm(File directory) {

        //get file name for video
        String command = "head -1 " + directory.getPath() + "/info.txt";
        String name = methodHelper.command(command);
        return name.trim();
    }

    /**
     * Method to override the toString() method to return the creation name
     * @return
     */
    @Override
    public String toString() {

        return _creationName;
    }

    //-------------------------------Getters and Setters--------------------------------//
    public String get_creationName() {

        return _creationName;
    }

    public void set_creationName(String _creationName) {

        this._creationName = _creationName;
    }

    public void set_correct(String correct) {
        _correct = correct;
    }

    public String get_correct() {
        return _correct;
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
}