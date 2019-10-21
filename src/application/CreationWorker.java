package application;

import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import java.io.File;

/**
 * This is a worker which extends JavaFX Task for creating the creation
 * in the background/different thread. This is to ensure we have concurrency
 */
public class CreationWorker extends Task<Boolean> {

    private static MethodHelper methodHelper = Main.getMethodHelper();
    private double duration;
    private File _creationDir;
    private int _numImages, imagesFound;
    private String command, _query, _creationPath, _action;
    private ObservableList<File> _selectedList;

    public CreationWorker(ObservableList<File> selectedList, String query, String action, File creationDir) {

        _creationDir = creationDir;
        _numImages = selectedList.size();
        _creationPath = _creationDir.getPath();
        _query = query;
        _action = action;
        _selectedList = selectedList;
    }

    @Override
    protected Boolean call() throws Exception {

        // depending on the input, will create or overwrite the file
        boolean create = false;
        switch (_action) {

            case "overwrite":
                command = "rm -rfv " + _creationPath + "/; mkdir " + _creationDir.getPath();
                create = true;
                break;
            case "create":
                command = "mkdir " + _creationPath;
                create = true;
                break;
            case "rename":
                command = "";
                create = false;
        }

        // if create is true, then we create the creation
        if (create) {
            methodHelper.command(command);
            System.out.println("creating creation");
            copyImages();

            duration = methodHelper.getDuration();
            imagesFound = _selectedList.size();
            storeInfo();
            System.out.println(duration);
            System.out.println(imagesFound);
            if (duration > 0) {

                //create the video
                String creationName = _creationDir.getName();
                String path = _creationDir.getPath();
                createVideo(creationName, path);
            } else {

                String command = "rm -r -f " + _creationDir.getPath();
                methodHelper.command(command);
            }
        }
        return false;
    }

    private void storeInfo() {
        String command = "echo \"" + _query + "\n" + duration + "\" > " + _creationPath + "/info.txt";
        methodHelper.command(command);
        System.out.println(command);
    }


    private void createVideo(String creationName, String path) {

        // merge the images
        command = "cat " + _creationPath + "/*.jpg | ffmpeg -f image2pipe -framerate " + imagesFound/duration + " -i - -vcodec libx264 -pix_fmt yuv420p -vf \"scale=w=1920:h=1080:force_original_aspect_ratio=1,pad=1920:1080:(ow-iw)/2:(oh-ih)/2\" -r 25 " + path + "/imageOnly.mp4";
        System.out.println(command);
        methodHelper.command(command);

        // add the name onto the video
        command = "ffmpeg -i " + path + "/imageOnly.mp4 -vf drawtext=\"fontfile=/Library/Fonts/Verdana.ttf: text='" + _query + "': fontcolor=white: fontsize=100: box=1: boxcolor=black@0.5: boxborderw=5: x=(w-text_w)/2: y=(h-text_h)/2\" -r 25 -codec:a copy " + path + "/noAudio.mp4";
        System.out.println(command);
        methodHelper.command(command);

        // merge the video and images
        String audio = "src/audio/" + _query + "/" + "output.mp3";
        command = "ffmpeg -i " + path + "/noAudio.mp4 -i " + audio + " -c:v copy -c:a aac -strict experimental " + path + "/" + creationName + ".mp4";
        System.out.println(command);
        methodHelper.command(command);

        methodHelper.command("rm " + audio + "; rm " + _creationPath + "/*.jpg");
        System.out.println("removed audio");


    }

    private void copyImages() {
        for (File file: _selectedList) {
            System.out.println(file.getName());
            command = "cp src/tempImages/" + _query + "/" + file.getName() + " " + _creationPath + "/" + file.getName();
            methodHelper.command(command);
            System.out.println(command);
        }
    }
}

