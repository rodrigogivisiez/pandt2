package com.mygdx.potatoandtomato.android;

import android.content.Context;
import android.content.Intent;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;

/**
 * Created by SiongLeng on 21/6/2016.
 */
public class InAppPurchaseHelper implements BillingProcessor.IBillingHandler {

    private AndroidLauncher androidLauncher;
    private BillingProcessor billingProcessor;
    private final String id = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgWZWubBpukO0cNeJDX7PM3Ck+I+CKmSHxcJbf60qK+5kSd/d2hZl72QfEJz0XVRVRLxR9fZMVtAyONfgW8v7lkbZHyX3WGtiqYPV+oPnuCjXa8XaXD1cuzbe7H6Mu1aNy0oSyOp6AhDRb4ENe8/H/G6ce3Ior8ug6KkkwdvPB4wZXDtzgNllJK+Au2vqAYKsaiP4ey2SkIQLRsmhKxd9t45kBWTwzMeGPHD3yyytku1KB8RoIUq4PqUS+jrHiMzfmiJkedRTAeXa+hIljVzdcue1fKXgpk/WdNzEPtSnR5d3nNzKUEks0P0x1kFHSwAJmI9TeNnALzd6/dPeQyCLwQIDAQAB";

    public InAppPurchaseHelper(AndroidLauncher androidLauncher) {
        this.androidLauncher = androidLauncher;
        billingProcessor = new BillingProcessor(androidLauncher, id, this);
    }

    @Override
    public void onProductPurchased(String s, TransactionDetails transactionDetails) {
        billingProcessor.consumePurchase(s);
    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    public void onBillingError(int i, Throwable throwable) {
    }

    @Override
    public void onBillingInitialized() {
        SkuDetails skuDetails = billingProcessor.getPurchaseListingDetails("coin_count_5");
        billingProcessor.purchase(androidLauncher, skuDetails.productId);
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
