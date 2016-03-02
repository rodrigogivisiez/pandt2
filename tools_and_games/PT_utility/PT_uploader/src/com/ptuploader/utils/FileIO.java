package com.ptuploader.utils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by SiongLeng on 1/3/2016.
 */
public class FileIO {

    public static void write(File f, String msg){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(f, false));
            writer.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(writer != null) writer.close();
        }
    }

    public static String read(File f) {
        if(!f.exists()) return "";
        String result = "";
        String line;
        try
        {
            InputStream fis = new FileInputStream(f.getAbsolutePath());
            InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                result += line;
            }
            fis.close();
            isr.close();
            br.close();
        } catch (IOException e) {

        }
        return result;
    }
}
