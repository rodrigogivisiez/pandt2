package com.mygdx.potatoandtomato.android;

import android.content.Context;
import android.content.Intent;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.services.IRestfulApi;
import com.mygdx.potatoandtomato.absintflis.services.RestfulApiListener;
import com.mygdx.potatoandtomato.models.CoinProduct;
import com.mygdx.potatoandtomato.models.Profile;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.BroadcastListener;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.utils.Pair;
import com.potatoandtomato.common.utils.Threadings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SiongLeng on 21/6/2016.
 */
public class InAppPurchaseHelper implements BillingProcessor.IBillingHandler {

    private AndroidLauncher androidLauncher;
    private BillingProcessor billingProcessor;
    private Broadcaster broadcaster;
    private IRestfulApi restfulApi;
    private Profile myProfile;
    private final String id = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgWZWubBpukO0cNeJDX7PM3Ck+I+CKmSHxcJbf60qK+5kSd/d2hZl72QfEJz0XVRVRLxR9fZMVtAyONfgW8v7lkbZHyX3WGtiqYPV+oPnuCjXa8XaXD1cuzbe7H6Mu1aNy0oSyOp6AhDRb4ENe8/H/G6ce3Ior8ug6KkkwdvPB4wZXDtzgNllJK+Au2vqAYKsaiP4ey2SkIQLRsmhKxd9t45kBWTwzMeGPHD3yyytku1KB8RoIUq4PqUS+jrHiMzfmiJkedRTAeXa+hIljVzdcue1fKXgpk/WdNzEPtSnR5d3nNzKUEks0P0x1kFHSwAJmI9TeNnALzd6/dPeQyCLwQIDAQAB";

    public InAppPurchaseHelper(AndroidLauncher androidLauncher, Broadcaster broadcaster) {
        this.androidLauncher = androidLauncher;
        this.broadcaster = broadcaster;
        billingProcessor = new BillingProcessor(androidLauncher, id, this);
        subscribeBroadcaster();
    }

    public void subscribeBroadcaster(){
        broadcaster.subscribe(BroadcastEvent.IAB_PRODUCTS_REQUEST, new BroadcastListener<IDatabase>() {
            @Override
            public void onCallback(IDatabase database, Status st) {
                getAllProductsFromDatabase(database);
            }
        });

        broadcaster.subscribe(BroadcastEvent.IAB_PRODUCT_PURCHASE, new BroadcastListener<Pair<String, IRestfulApi>>() {
            @Override
            public void onCallback(Pair<String, IRestfulApi> pair, Status st) {
                purchaseProduct(pair.getFirst(), pair.getSecond());
            }
        });

        broadcaster.subscribe(BroadcastEvent.USER_READY, new BroadcastListener<Profile>() {
            @Override
            public void onCallback(Profile profile, Status st) {
                myProfile = profile;
            }
        });
    }

    public void getAllProductsFromDatabase(IDatabase database){
        database.getAllProducts(new DatabaseListener<ArrayList<CoinProduct>>(CoinProduct.class) {
            @Override
            public void onCallback(ArrayList<CoinProduct> coinProducts, Status st) {
                if(st == Status.SUCCESS){
                    getAllProductsFromStore(coinProducts);
                }
            }
        });
    }

    public void getAllProductsFromStore(final ArrayList<CoinProduct> coinProducts){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> productIds = new ArrayList();
                for(CoinProduct coinProduct : coinProducts){
                    productIds.add(coinProduct.getId());
                }

                List<SkuDetails> skuDetails = billingProcessor.getPurchaseListingDetails(productIds);

                for(SkuDetails skuDetail : skuDetails){
                    for(CoinProduct coinProduct : coinProducts){
                        if(coinProduct.getId().equals(skuDetail.productId)){
                            coinProduct.setCurrency(skuDetail.currency);
                            coinProduct.setDescription(skuDetail.description);
                            coinProduct.setPrice(skuDetail.priceValue);
                        }
                    }
                }

                broadcaster.broadcast(BroadcastEvent.IAB_PRODUCTS_RESPONSE, coinProducts);
            }
        });
    }

    public void purchaseProduct(final String productId, IRestfulApi restfulApi){
        this.restfulApi = restfulApi;
        billingProcessor.purchase(androidLauncher, productId);
    }


    @Override
    public void onProductPurchased(final String productId, final TransactionDetails transactionDetails) {
        goToPhaseZero(productId, transactionDetails, 0);
    }

    public void goToPhaseZero(final String productId, final TransactionDetails transactionDetails, final int tryCount){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                restfulApi.purchasedProducts(productId, transactionDetails.purchaseToken, transactionDetails.orderId, myProfile, 0,
                        new RestfulApiListener<String>() {
                            @Override
                            public void onCallback(String obj, Status st) {
                                if(st == Status.FAILED){
                                    if(tryCount == 0){
                                        Threadings.sleep(3000);     //retry again 3secs
                                        goToPhaseZero(productId, transactionDetails, 1);
                                    }
                                    else{
                                        broadcaster.broadcast(BroadcastEvent.IAB_PRODUCT_PURCHASE_RESPONSE, "failed phase 0", Status.FAILED);
                                    }
                                }
                                else{
                                    goToPhaseOne(productId, transactionDetails);
                                }
                            }
                        });
            }
        });


    }

    public void goToPhaseOne(final String productId, final TransactionDetails transactionDetails){
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                billingProcessor.consumePurchase(productId);
                restfulApi.purchasedProducts(productId, transactionDetails.purchaseToken, transactionDetails.orderId, myProfile, 1,
                        new RestfulApiListener<String>() {
                            @Override
                            public void onCallback(String obj, Status st) {
                                if(st == Status.FAILED){
                                    broadcaster.broadcast(BroadcastEvent.IAB_PRODUCT_PURCHASE_RESPONSE, "failed phase 1", Status.FAILED);
                                }
                                else{
                                    broadcaster.broadcast(BroadcastEvent.IAB_PRODUCT_PURCHASE_RESPONSE, Status.SUCCESS);
                                }
                            }
                        });
            }
        });
    }


    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    public void onBillingError(int i, Throwable throwable) {
        broadcaster.broadcast(BroadcastEvent.IAB_PRODUCT_PURCHASE_RESPONSE,
                            throwable != null ? throwable.getMessage() : "", Status.FAILED);
    }

    @Override
    public void onBillingInitialized() {
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!billingProcessor.handleActivityResult(requestCode, resultCode, data))
            return false;
        else
            return true;
    }

    public void onDestroy(){
        if (billingProcessor != null)
            billingProcessor.release();

    }
}
