package com.mygdx.potatoandtomato.models;

import com.mygdx.potatoandtomato.enums.ShopProducts;
import com.shaded.fasterxml.jackson.annotation.JsonIgnore;
import com.shaded.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by SiongLeng on 22/6/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinProduct {

    private String id;
    private int count;
    private String currency;
    private String description;
    private Double price;

    public CoinProduct() {
        price = -1.0;   //disable this product if is negative
    }

    public CoinProduct(String id, int count, String description) {
        this.id = id;
        this.count = count;
        this.description = description;
        this.price = 0.0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @JsonIgnore
    public String getTextureNameFromCoinCount(){
        String textureName = null;
        if(count == 1){
            textureName = "TV_ICON";
        }
        else if(count == 5){
            textureName = "COIN_FIVE";
        }
        else if(count == 15){
            textureName = "COIN_FIFTEEN";
        }
        else if(count == 100){
            textureName = "COIN_BAG";
        }
        return textureName;
    }

    @JsonIgnore
    public ShopProducts getShopProductType(){
        if(count == 1){
            return ShopProducts.ONE_COIN;
        }
        else if(count == 5){
            return ShopProducts.FIVE_COINS;
        }
        else if(count == 15){
            return ShopProducts.FIFTEEN_COINS;
        }
        else if(count == 100){
            return ShopProducts.HUNDRED_COINS;
        }
        else{
            return null;
        }
    }

}
