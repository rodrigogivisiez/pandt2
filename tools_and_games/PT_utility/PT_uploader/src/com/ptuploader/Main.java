package com.ptuploader;

import com.ptuploader.models.FileData;
import com.ptuploader.process.*;
import com.ptuploader.utils.Logs;

import java.io.*;
import java.util.Map;

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

        Proguard proguard = new Proguard(paths, logs);
        if(!proguard.run()) return;


        AssetsHelper assetsHelper = new AssetsHelper(paths.getAssetsDir(), paths.getJarFile());
        assetsHelper.run();

        Details details = new Details(paths, logs, fireDB, assetsHelper);
        if(!details.extract(testing, assetsHelper.getFileDatasMap(), assetsHelper.getGameSize())) return;


        final int[] uploadCount = {0};
        final int[] deleteCount = {0};

        int totalUploadFiles = details.getToUploadFiles().size();

        Uploads uploads = new Uploads(paths, logs, testing);
        if(details.isIconModified()){
            uploads.uploadIcon(details, 1, new Runnable() {
                @Override
                public void run() {
                    uploadCount[0]++;
                }
            });
            totalUploadFiles++;
        }


        for (Map.Entry<String, FileData> entry : details.getToUploadFiles().entrySet()) {
            String fileName = entry.getKey();
            final FileData fileData = assetsHelper.getFileDatasMap().get(fileName);
            final boolean[] finish = new boolean[1];
            uploads.uploadFile(details, fileName, fileData, 1,new Runnable() {
                @Override
                public void run() {
                    finish[0] = true;
                    uploadCount[0]++;
                }
            });
        }

        for (Map.Entry<String, FileData> entry : details.getToDeleteFromCloudFiles().entrySet()) {
            String fileName = entry.getKey();
            uploads.deleteFile(details, fileName, new Runnable() {
                @Override
                public void run() {
                    deleteCount[0]++;
                }
            });
        }

        while (uploadCount[0] < (totalUploadFiles) || deleteCount[0] < details.getToDeleteFromCloudFiles().size()){
            Thread.sleep(500);
        }

        details.setGameDataFilesJson(assetsHelper.getFileDatasMap());

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
