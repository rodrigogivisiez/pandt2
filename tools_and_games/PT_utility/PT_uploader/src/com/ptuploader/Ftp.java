package com.ptuploader;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


/**
 * Created by SiongLeng on 12/12/2015.
 */
public class Ftp {

    private static final int BUFFER_SIZE = 4096;

    public Ftp()  {


    }


    public UploadedGame uploadEverything(Details details, Paths paths, ScreenShots screenShots) throws IOException {
        UploadedGame uploadedGame = new UploadedGame();
        String path = details.abbr;
        uploadedGame.assetUrl = uploadFile(new File("assets.zip"), path);
        uploadedGame.gameUrl = uploadFile(new File(paths.jar), path);
        uploadedGame.description = details.description;
        uploadedGame.maxPlayers = details.max_players;
        uploadedGame.minPlayers = details.min_players;
        uploadedGame.version = details.version;
        uploadedGame.abbr = details.abbr;
        uploadedGame.iconUrl = uploadFile(new File(paths.icon), path);
        uploadedGame.name = details.name;
        uploadedGame.teamMaxPlayers = details.team_max_players;
        uploadedGame.teamMinPlayers = details.team_min_players;
        uploadedGame.teamCount = details.team_count;

        ArrayList<String> screenShotsArr = new ArrayList<>();
        for(File f : screenShots.getAllScreenShotsPath()){
            screenShotsArr.add(uploadFile(f, path + "/screenshots"));
        }

        uploadedGame.screenShots = screenShotsArr;
        return uploadedGame;
    }

    public static String uploadFile(File f, String toPath) throws IOException{
        String filePath = f.getAbsolutePath();
        String uploadPath = "http://www.potato-and-tomato.com/" + toPath + "/" + f.getName();
        String server = "www.potato-and-tomato.com";
        int port = 21;
        String user = "siongleng";
        String pass = "ab984025!";
        String dirPath = "potato-and-tomato.com/" + toPath;

        FTPClient ftpClient = new FTPClient();

        try {
            // connect and login to the server
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);

            // use local passive mode to pass firewall
            ftpClient.enterLocalPassiveMode();
            makeDirectories(ftpClient, dirPath);

            FileInputStream fis = new FileInputStream(f.getAbsoluteFile());
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
            ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            ftpClient.storeFile(f.getName(), fis);
            fis.close();

            // log out and disconnect from the server
            ftpClient.logout();
            ftpClient.disconnect();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return uploadPath;
    }


    public static boolean makeDirectories(FTPClient ftpClient, String dirPath)
            throws IOException {
        String[] pathElements = dirPath.split("/");
        if (pathElements != null && pathElements.length > 0) {
            for (String singleDir : pathElements) {
                boolean existed = ftpClient.changeWorkingDirectory(singleDir);
                if (!existed) {
                    boolean created = ftpClient.makeDirectory(singleDir);
                    if (created) {
                        System.out.println("CREATED directory: " + singleDir);
                        ftpClient.changeWorkingDirectory(singleDir);
                    } else {
                        System.out.println("COULD NOT create directory: " + singleDir);
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
