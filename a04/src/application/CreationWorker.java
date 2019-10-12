package application;

import javafx.concurrent.Task;
import java.io.File;

/**
 * This is a worker which extends JavaFX Task for creating the creation
 * in the background/different thread. This is to ensure we have concurrency
 */
public class CreationWorker extends Task<Boolean> {

    MethodHelper methodHelper = new MethodHelper();
    private double duration;
    private File _creationDir;
    private int _numImages, imagesFound;
    private String command, _query, _path, _action;

    public CreationWorker(int numImages, String query, String action, File creationDir) {

        _creationDir = creationDir;
        _numImages = numImages;
        _path = _creationDir.getPath();
        _query = query;
        _action = action;
    }

    @Override
    protected Boolean call() throws Exception {

        // depending on the input, will create or overwrite the file
        boolean create = false;
        switch (_action) {

            case "overwrite":
                command = "rm -rfv " + _path + "/; mkdir " + _creationDir.getPath();
                create = true;
                break;
            case "create":
                command = "mkdir " + _path;
                create = true;
                break;
            case "rename":
                command = "";
                create = false;
        }

        // if create is true, then we create the creation
        if (create) {

            methodHelper.command(command);
//            duration = _audio.mergeAudio(_creationDir); // merges audio
//            if (duration != -1) {
//
//                //create the video
//                String creationName = _creationDir.getName();
//                String path = _creationDir.getPath();
//                createVideo(creationName, path);
//            } else {
//
//                String command = "rm -r -f " + _creationDir.getPath();
//                methodHelper.command(command);
//                return "No audio files selected for creation";
////                }
//            } else{
//
//                return "Could not make directory for creation";
//            }
//
//            return message;
            return true;
        }
        return false;
    }


    private void createVideo(String creationName, String path) {

        // merge the images
        command = "cat " + path + "/.*.jpg | ffmpeg -f image2pipe -framerate $((" + imagesFound + "))/" + duration + " -i - -vcodec libx264 -pix_fmt yuv420p -vf \"scale=w=1920:h=1080:force_original_aspect_ratio=1,pad=1920:1080:(ow-iw)/2:(oh-ih)/2\" -r 25 " + path + "/" + creationName + "Temp.mp4";
        methodHelper.command(command);

//        // add the name onto the video
//        command = "ffmpeg -i " + path + "/" + creationName + "Temp.mp4 -vf drawtext=\"fontfile=/Library/Fonts/Verdana.ttf: text='" + _query + "': fontcolor=white: fontsize=100: box=1: boxcolor=black@0.5: boxborderw=5: x=(w-text_w)/2: y=(h-text_h)/2\" -r 25 -codec:a copy " + path + "/" + creationName + "Text.mp4";
//        methodHelper.command(command);
//
//        // merge the video and images
//        command = "ffmpeg -i " + path + "/" + creationName + "Text.mp4 -i " + path + "/." + _query + ".wav -c:v copy -c:a aac -strict experimental " + path + "/" + creationName + ".mp4";
//        methodHelper.command(command);
//
//        //remove unnecessary files
//        command = "rm " + path + "/" + creationName + "Temp.mp4; rm " + path + "/" + creationName + "Text.mp4";
//        methodHelper.command(command);
    }
}

