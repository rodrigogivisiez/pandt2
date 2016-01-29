import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by SiongLeng on 12/12/2015.
 */
public class Ftp {

    public static String uploadFile(File f, String toPath) throws IOException{
        String filePath = f.getAbsolutePath();
        String uploadPath = "http://www.potato-and-tomato.com/" + toPath + "/" + f.getName();
        String server = "www.potato-and-tomato.com";
        int port = 21;
        String user = "siongleng";
        String pass = "ab984025!";
        String dirPath = "potato-and-tomato.com/" + toPath;

        FTPClient ftpClient = new FTPClient();

        // connect and login to the server
        ftpClient.connect(server, port);
        ftpClient.login(user, pass);

        // use local passive mode to pass firewall
        ftpClient.enterLocalPassiveMode();
        makeDirectories(ftpClient, dirPath);

        FileInputStream fis = new FileInputStream(f.getAbsoluteFile());
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
        ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
        ftpClient.setBufferSize(1048576);

        ftpClient.storeFile(f.getName(), fis);
        fis.close();

        // log out and disconnect from the server
        ftpClient.logout();
        ftpClient.disconnect();


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
