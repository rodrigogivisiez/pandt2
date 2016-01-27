package com.ptuploader;

import net.lingala.zip4j.exception.ZipException;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.InvalidPathException;

public class Main {



    public static void main(String[] args) {
        Logs logs = null;
        try {
            System.out.println("Potato and Tomato upload game processing...");
            logs = new Logs();

            Paths paths = new Paths();
            paths.getAll();
            paths.checkAllPathsExist();

            String commonVersion = readFile(paths.commonVersion).trim();

            Zippings zippings = new Zippings(paths.assets);
            zippings.run();

            Dx dx = new Dx(paths.dx, paths.jar);
            dx.run();

            Details details = new Details(paths.details);
            details.run();

            ScreenShots screenShots = new ScreenShots(paths.screenshots);
            screenShots.run();

            if(zippings.hasModified() || dx.hasModified()){
                details.addVersion();
            }

            Ftp ftp = new Ftp();
            UploadedGame uploadedGame = ftp.uploadEverything(details, paths, screenShots);
            uploadedGame.commonVersion = commonVersion;


            FireDB db = new FireDB();
            db.save(uploadedGame);

            while (!db.isFinished()){Thread.sleep(500);}
            if(db.isSuccess()){
                details.writeBackJson();
                logs.writeSuccess(details, screenShots.getAllScreenShotsPath().size(), commonVersion);
            }
            else{
                logs.writeFailed("Firebase failed to update.");
            }

        } catch (InvalidPathException e) {
            e.printStackTrace();
            logs.writeFailed(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            logs.writeFailed(e.getMessage());
        } catch (ZipException e) {
            e.printStackTrace();
            logs.writeFailed(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
            logs.writeFailed(e.getMessage());
        } catch (IllegalThreadStateException e) {
            e.printStackTrace();
            logs.writeFailed(e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
            logs.writeFailed(e.getMessage());
        }
        catch (Exception e){
            e.printStackTrace();
            logs.writeFailed(e.getMessage());
        }



    }


    private static String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }

}
