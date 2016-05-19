package com.mygdx.potatoandtomato.utils;

import com.potatoandtomato.common.utils.Pair;

/**
 * Created by SiongLeng on 10/5/2016.
 */
public class BytesUtils {
    public static byte[] prependBytes(byte[] original, byte[] bytesToPrepend){
        byte[] output = new byte[bytesToPrepend.length + original.length];

        for(int i = 0; i < bytesToPrepend.length; i++){
            output[i] = bytesToPrepend[i];
        }

        int q = 0;
        for(int i = bytesToPrepend.length; i < (original.length + bytesToPrepend.length); i++){
            output[i] = original[q];
            q++;
        }

        return output;
    }

    public static Pair<byte[], byte[]> splitBytes(byte[] original, int index){
        byte[] output1 = new byte[index];
        byte[] output2 = new byte[original.length - index];

        for(int i = 0; i < index; i++){
            output1[i] = original[i];
        }

        int q = 0;
        for(int i = index; i < original.length; i++){
            output2[q] = original[i];
            q++;
        }

        return new Pair<>(output1, output2);
    }

}
