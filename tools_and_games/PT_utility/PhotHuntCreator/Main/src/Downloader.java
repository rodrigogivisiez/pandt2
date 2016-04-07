import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;

/**
 * Created by SiongLeng on 6/4/2016.
 */
public class Downloader {

    public void downloadFileToPath(final String urlString, final File targetFile, final Runnable onFinish){

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                HttpURLConnection con = null;
                long completeFileSize;
                long downloadedFileSize = 0;

                try {
                    url = new URL(urlString);
                    con = (HttpURLConnection) url.openConnection();
                    if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        //listener.onCallback(null, Status.FAILED);
                        return;
                    }
                    completeFileSize = con.getContentLength();
                    File file = targetFile;
                    if (file.exists()) file.delete();

                    if(!file.getAbsoluteFile().getParentFile().exists()){
                        file.getAbsoluteFile().getParentFile().mkdirs();
                    }

                    file.createNewFile();
                    BufferedInputStream bis = new BufferedInputStream(
                            con.getInputStream());

                    BufferedOutputStream bos = new BufferedOutputStream(
                            new FileOutputStream(file), 1024);
                    byte[] data = new byte[1024];
                    int x = 0;

                    while ((x = bis.read(data, 0, 1024)) >= 0) {


                        downloadedFileSize += x;

                        // calculate progress
                        final int currentProgress = (int) ((((double) downloadedFileSize) / ((double) completeFileSize)) * 100d);


                        bos.write(data, 0, x);
                    }

                    bos.flush();
                    bis.close();
                    bos.close();

                    onFinish.run();

                } catch (MalformedInputException malformedInputException) {
                    malformedInputException.printStackTrace();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

    }


}


