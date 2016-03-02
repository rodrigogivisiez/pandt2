package com.ptuploader.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by SiongLeng on 2/3/2016.
 */
public class ProgressInputStream extends InputStream {
    private final long size;
    private long progress, lastUpdate = 0;
    private final InputStream inputStream;
    private boolean closed = false;

    public ProgressInputStream(InputStream inputStream, long size) {
        this.size = size;
        this.inputStream = inputStream;
    }


    @Override
    public void close() throws IOException {
        super.close();
        if (closed) throw new IOException("already closed");
        closed = true;
    }

    @Override
    public int read() throws IOException {
        int count = inputStream.read();
        if (count > 0)
            progress += count;

        return count;
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count = inputStream.read(b, off, len);
        if (count > 0)
            progress += count;
        return count;
    }

    public Double getCurrentProgress(){
        return Math.floor((double) (progress / size * 100));
    }

}