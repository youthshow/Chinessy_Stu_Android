package com.chinessy.chinessy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.handlers.SimpleJsonHttpResponseHandler;
import com.chinessy.chinessy.models.Product;
import com.chinessy.chinessy.models.PromotionCode;
import com.chinessy.chinessy.models.User;
import com.chinessy.chinessy.models.UserBalancePackage;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.rey.material.app.SimpleDialog;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AddExtraMinutesActivity extends AppCompatActivity {
    public static final int HANDLER_INIT_SELL_PART = 100;

    public static final int RC_DISCOUNT_FROM_PROMOTION_CODE = 201;
    public static final int RC_CHOOSE_AMOUNT = 202;
    public static final int RC_PAYPAL_FOR_EXTRA_PRODUCT = 203;
    Activity mActivity;

    RelativeLayout mRlChooseAmount;
    LinearLayout mLlAddExtraMinutes;
    TextView mTvChooseAmount;
    TextView mTvOriginalPrice;
    TextView mTvTotalPrice;
    TextView mTvAddPromoCode;
    TextView mTvPromotionCode;
    TextView mTvUsing;

    Button mBtnBuy;
    ProgressBar mProgressBar;

    List<Product> mExtraProductList = new ArrayList<>();
    int mChosenProductIndex = 0;
    Handler mHandler = new AddExtraMinutesHandler();

    String mOriginalPrice;
    String mTotalPrice;
    PromotionCode mPromotionCode;

    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_extra_minutes);
        mActivity = this;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setElevation(0f);

        mRlChooseAmount = (RelativeLayout)findViewById(R.id.addextraminutes_rl_chooseamount);
        mLlAddExtraMinutes = (LinearLayout)findViewById(R.id.addextraminutes_ll_addextraminutes);
        mTvChooseAmount = (TextView)findViewById(R.id.addextraminutes_tv_chooseamount);
        mTvOriginalPrice = (TextView)findViewById(R.id.addextraminutes_tv_originalprice);
        mTvTotalPrice = (TextView)findViewById(R.id.addextraminutes_tv_totalprice);
        mTvAddPromoCode = (TextView)findViewById(R.id.addextraminutes_tv_addpromocode);
        mTvPromotionCode = (TextView)findViewById(R.id.addextraminutes_tv_promotioncode);
        mTvUsing = (TextView)findViewById(R.id.addextraminutes_tv_using);
        mBtnBuy = (Button)findViewById(R.id.addextraminutes_btn_buy);
        mProgressBar = (ProgressBar)findViewById(R.id.addextraminutes_pb_loadingextraproduct);

        mTvAddPromoCode.setOnClickListener(new TvAddPromotionCodeOnClickListener());
        mRlChooseAmount.setOnClickListener(new RlChooseAmountOnClickListener());
        mBtnBuy.setOnClickListener(new BtnBuyOnClickListener());

        mTvOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中间横线
        mTvOriginalPrice.getPaint().setAntiAlias(true);// 抗锯齿

        UserBalancePackage userBalancePackage = Chinessy.chinessy.getUser().getUserBalancePackage();
        if(userBalancePackage.getEndAt()!=null && userBalancePackage.getEndAt().getTime()!=0){
            mLlAddExtraMinutes.setEnabled(true);
            mRlChooseAmount.setEnabled(true);
            mTvAddPromoCode.setEnabled(true);
            mBtnBuy.setEnabled(true);
            findViewById(R.id.addextraminutes_ll_cover).setVisibility(View.GONE);
        }else{
            mLlAddExtraMinutes.setEnabled(false);
            mRlChooseAmount.setEnabled(false);
            mTvAddPromoCode.setEnabled(false);
            mBtnBuy.setEnabled(false);
            findViewById(R.id.addextraminutes_ll_cover).setVisibility(View.VISIBLE);
        }

        getExtraProducts();
    }

    void getExtraProducts(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
            InternalClient.postJson(mActivity, "product/get_extra_list", jsonObject, new SimpleJsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        switch (response.getInt("code")){
                            case 10000:
                                JSONArray jsonArray = response.getJSONArray("data");
                                int length = jsonArray.length();
                                for(int i=0; i<length; i++){
                                    Product product = new Product(jsonArray.getJSONObject(i));
                                    if(product.isDefault()){
                                        mChosenProductIndex = i;
                                    }
                                    mExtraProductList.add(product);
                                }
                                mHandler.sendEmptyMessage(HANDLER_INIT_SELL_PART);
                                break;
                            default:
                                SimpleJsonHttpResponseHandler.defaultHandler(mActivity, response.getString("message"));
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_minutes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            AddExtraMinutesActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch ((requestCode)){
            case AddExtraMinutesActivity.RC_CHOOSE_AMOUNT:
                if(resultCode == Activity.RESULT_OK){
                    int defaultProductindex = data.getIntExtra("default_product_index", 0);
                    onDefaultProductChanged(defaultProductindex);
                }
                break;
            case AddExtraMinutesActivity.RC_DISCOUNT_FROM_PROMOTION_CODE:
                if(resultCode == Activity.RESULT_OK){
                    mPromotionCode = (PromotionCode)data.getSerializableExtra("promotion_code");
                }
                checkPromocode(mPromotionCode);
                break;
            case AddExtraMinutesActivity.RC_PAYPAL_FOR_EXTRA_PRODUCT:
                if (resultCode == Activity.RESULT_OK) {
                    PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                    if (confirm != null) {
                        try {
                            Log.i("payment", confirm.toJSONObject().toString(4));
                            JSONObject jsonObject = confirm.toJSONObject();
                            String paypalId = jsonObject.getJSONObject("response").getString("id");
//                            Toast.makeText(mActivity, paypalId, Toast.LENGTH_SHORT).show();
                            // TODO: send 'confirm' to your server for verification.
                            // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                            // for more details.

                            JSONObject jsonObject1 = new JSONObject();
                            jsonObject1.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
                            jsonObject1.put("payment_id", paypalId);
                            jsonObject1.put("product_id", mExtraProductList.get(mChosenProductIndex).getId());
                            if(mPromotionCode!=null && !mPromotionCode.equals("")){
                                jsonObject1.put("promotion_code", mPromotionCode.getCode());
                            }
                            InternalClient.postJson(mActivity, "payment/verify", jsonObject1, new SimpleJsonHttpResponseHandler(){
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    super.onSuccess(statusCode, headers, response);
                                    try {
                                        switch (response.getInt("code")){
                                            case 10000:
                                                mProgressDialog.dismiss();
                                                User.updateUserBalance(mActivity, response);
                                                setResult(Activity.RESULT_OK);
                                                AddExtraMinutesActivity.this.finish();
                                                Toast.makeText(mActivity, R.string.Transaction_completed, Toast.LENGTH_SHORT).show();
                                                break;
                                            default:
//                                                SimpleJsonHttpResponseHandler.defaultHandler(mActivity, getString(R.string.payment_failed_message));
                                                mProgressDialog.dismiss();
                                                final SimpleDialog simpleDialog = new SimpleDialog(mActivity);
                                                simpleDialog.message(R.string.payment_failed_message);
                                                simpleDialog.positiveAction(R.string.OK);
                                                simpleDialog.positiveActionClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        simpleDialog.dismiss();
                                                    }
                                                });
                                                simpleDialog.show();
                                                break;
                                        }
                                    } catch (JSONException e) {
                                        mProgressDialog.dismiss();
                                        e.printStackTrace();
                                    }
                                }
                            });

                        } catch (JSONException e) {
                            Log.e("payment", "an extremely unlikely failure occurred: ", e);
                        }
                    }
                }
                else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.i("payment", "The user canceled.");
                    mProgressDialog.dismiss();
                }
                else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                    Log.i("payment", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                    mProgressDialog.dismiss();
                }
                break;
        }
    }

    private void onDefaultProductChanged(int defaultIndex){
        if(mChosenProductIndex == defaultIndex){
            return;
        }

        mChosenProductIndex = defaultIndex;
        Product product = mExtraProductList.get(mChosenProductIndex);
        mTvChooseAmount.setText(product.getMinutes() + " mins");

        if(getFinalPrice(mExtraProductList.get(mChosenProductIndex), mPromotionCode)==2){
            mTvOriginalPrice.setText(getShowPrice(mOriginalPrice));
        }
        mTvTotalPrice.setText(getShowPrice(mTotalPrice));
    }

    void checkPromocode(PromotionCode pc){
        if(pc!=null && !pc.getCode().equals("")){
            mTvPromotionCode.setText(pc.getCode() + " applied.");
            mTvUsing.setVisibility(View.VISIBLE);
            mTvPromotionCode.setVisibility(View.VISIBLE);
            mTvAddPromoCode.setText(R.string.Change);

            getFinalPrice(mExtraProductList.get(mChosenProductIndex), mPromotionCode);
            mTvTotalPrice.setText(getShowPrice(mTotalPrice));

            mTvOriginalPrice.setVisibility(View.VISIBLE);
            mTvOriginalPrice.setText(getShowPrice(mOriginalPrice));
        }else{
            mTvPromotionCode.setText("");
            mTvUsing.setVisibility(View.INVISIBLE);
            mTvPromotionCode.setVisibility(View.INVISIBLE);
            mTvAddPromoCode.setText(R.string.Have_a_promo_code);

            mTvOriginalPrice.setVisibility(View.INVISIBLE);
        }
    }

    private int getFinalPrice(Product product, PromotionCode promotionCode){
        BigDecimal bdOriginal = new BigDecimal(product.getPrice()+"");
        bdOriginal = bdOriginal.setScale(2, BigDecimal.ROUND_HALF_UP);
        int priceQuantity = 1;

        mOriginalPrice = bdOriginal.toString();
        if(promotionCode != null){
            BigDecimal bdTotal = new BigDecimal(product.getPrice()*promotionCode.getProduct().getPrice()+"");
            bdTotal = bdTotal.setScale(2,BigDecimal.ROUND_HALF_UP);

            mTotalPrice = bdTotal.toString();
            priceQuantity = 2;
        }else{
            mTotalPrice = mOriginalPrice;
        }
        return priceQuantity;
    }

    class AddExtraMinutesHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_INIT_SELL_PART:
                    Product product = mExtraProductList.get(mChosenProductIndex);
                    getFinalPrice(product, null);
                    mTvChooseAmount.setText(product.getMinutes() + " mins");
                    mTvTotalPrice.setText(getShowPrice(mTotalPrice));

                    mLlAddExtraMinutes.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private String getShowPrice(String price){
        return "US$ "+price;
    }

    class TvAddPromotionCodeOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mActivity, GetPromotionActivity.class);
            mActivity.startActivityForResult(intent, RC_DISCOUNT_FROM_PROMOTION_CODE);
        }
    }

    class RlChooseAmountOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mActivity, ChooseAmountActivity.class);
            intent.putExtra("extra_product_list", (Serializable) mExtraProductList);
            intent.putExtra("default_product_index", mChosenProductIndex);
            mActivity.startActivityForResult(intent, RC_CHOOSE_AMOUNT);
        }
    }

    class BtnBuyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Product product = mExtraProductList.get(mChosenProductIndex);
            getFinalPrice(product, mPromotionCode);

            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setMessage(getString(R.string.Waiting));
            mProgressDialog.show();

            PayPalPayment payment = new PayPalPayment(new BigDecimal(mTotalPrice), product.getCurrency(), product.getName(),
                    PayPalPayment.PAYMENT_INTENT_SALE);
            Intent intent = new Intent(AddExtraMinutesActivity.this, PaymentActivity.class);
            // send the same configuration for restart resiliency
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, Chinessy.getPaypalConfig());
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
            startActivityForResult(intent, RC_PAYPAL_FOR_EXTRA_PRODUCT);
        }
    }
}
