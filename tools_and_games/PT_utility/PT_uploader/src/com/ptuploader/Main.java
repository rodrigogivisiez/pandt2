package com.ptuploader;

import com.ptuploader.process.*;
import com.ptuploader.utils.Logs;

import java.io.*;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Logs logs = new Logs();
        boolean testing = askForIsTest(logs);

        FireDB fireDB = new FireDB(testing, logs);
        Thread.sleep(2000);

        Paths paths = new Paths(logs);
        if(!paths.checkAll()) return;

        Dx dxRunner = new Dx(paths, logs);
        if(!dxRunner.run()) return;

        Zippings zippings = new Zippings(paths, logs);
        if(!zippings.run()) return;

        Details details = new Details(paths, logs);
        if(!details.extract(zippings.hasModified(), testing)) return;
        details.setGameSize(zippings.getNewFileSize());

        final int[] uploadCount = {0};

        Uploads uploads = new Uploads(paths, logs, testing);
        uploads.uploadIcon(details, new Runnable() {
            @Override
            public void run() {
                uploadCount[0]++;
            }
        });
        uploads.uploadGame(details, new Runnable() {
            @Override
            public void run() {
                uploadCount[0]++;
            }
        });

        while (uploadCount[0] < 2){
            Thread.sleep(500);
        }

        details.print();

        final boolean[] fireDBEnded = new boolean[1];

        fireDB.save(details, new Runnable() {
            @Override
            public void run() {
                fireDBEnded[0] = true;
            }
        });

        while (!fireDBEnded[0]){
            Thread.sleep(500);
        }

        try {
            logs.write("You may close the PTUploader now.");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean askForIsTest(Logs logs){
        logs.write("Upload to production/test client?");
        logs.write("1: production, 2: test");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input = br.readLine();
            if(input.equals("1")){
                logs.write("Uploading to production client....");
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        logs.write("Uploading to test client....");
        return true;
    }



}
