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

public class Creation {

    private String _creationName;
    private String _searchTerm;
    private String _timeCreated;
    private String _duration;

    public Creation(File directory) {
        _creationName = directory.getName();
        _timeCreated = getCreationDate(directory);
//        _duration = calculateDuration(directory);
//        _searchTerm = findSearchTerm(directory);
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

//    public String calculateDuration(File directory) {
//
//        //get file name for video
//        String command = "ls " + directory.getPath() + " | grep .mp4$";
//        String fileName = Terminal.command(command).trim();
//        String lengthCommand = "ffmpeg -i " + directory.getPath() + "/" + fileName + " 2>&1 | grep Duration | cut -d ' ' -f 4 | sed s/,//";
//        String duration = Terminal.command(lengthCommand).trim();
//        return duration;
//    }
//
//    public String findSearchTerm(File directory) {
//
//        //get file name for video
//        String command = "ls -a " + directory.getPath() + " | grep .wav$ | sed 's/^.\\(.*\\)....$/\\1/'";
//        String name = Terminal.command(command).trim();
//        return name;
//    }
}