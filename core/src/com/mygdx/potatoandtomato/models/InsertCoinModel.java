package com.mygdx.potatoandtomato.models;

import com.mygdx.potatoandtomato.statics.Global;
import com.potatoandtomato.common.statics.Vars;
import com.shaded.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by SiongLeng on 31/7/2016.
 */
public class InsertCoinModel{

    private String transactionId;
    private int totalInsertedCoin;

    public InsertCoinModel() {
    }

    public InsertCoinModel(String transactionId, int totalInsertedCoin) {
        this.transactionId = transactionId;
        this.totalInsertedCoin = totalInsertedCoin;
    }

    public InsertCoinModel(String json){
        ObjectMapper objectMapper = Vars.getObjectMapper();
        try {
            InsertCoinModel newInsertCoinModel = objectMapper.readValue(json, InsertCoinModel.class);
            this.transactionId = newInsertCoinModel.getTransactionId();
            this.totalInsertedCoin = newInsertCoinModel.getTotalInsertedCoin();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTransactionId() {
        return transactionId;
    }

    public int getTotalInsertedCoin() {
        return totalInsertedCoin;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setTotalInsertedCoin(int totalInsertedCoin) {
        this.totalInsertedCoin = totalInsertedCoin;
    }

    public String toJson(){
        ObjectMapper objectMapper = Vars.getObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

}