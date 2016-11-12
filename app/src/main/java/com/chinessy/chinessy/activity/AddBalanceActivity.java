package com.chinessy.chinessy.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.R;
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
import com.umeng.analytics.MobclickAgent;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AddBalanceActivity extends AppCompatActivity {
    public static final int HANDLER_INIT_SELL_PART = 100;

    public static final int RC_PAYPAL_FOR_PACKAGE_PRODUCT = 200;
    public static final int RC_DISCOUNT_FROM_PROMOTION_CODE = 201;
    public static final int RC_CHOOSE_MINUTES = 202;
    public static final int RC_CHOOSE_PERIOD = 203;
    public static final int RC_BUY_EXTRA_MINUTES = 204;
    Activity mActivity;

    LinearLayout mLlBuyPackageProduct;
    RelativeLayout mRlSubcription;
    RelativeLayout mRlExtraMinutes;
    RelativeLayout mRlChoosePlan;
    RelativeLayout mRlMonths;

    TextView mTvTodaysRemain;
    TextView mTvBalancePackage;
    TextView mTvExpiredAt;
    TextView mTvBalanceExtra;
    TextView mTvChooseMinutes;
    TextView mTvChoosePeriod;
    TextView mTvOriginalPrice;
    TextView mTvTotalPrice;
    TextView mTvAddPromoCode;
    TextView mTvPromotionCode;
    TextView mTvUsing;

    Button mBtnBuy;

    ProgressBar mProgressBar;

    List<Product> mPackageProductList = new ArrayList<Product>();
    List<Integer> mPeriodList = new ArrayList<>();
    List<Integer> mMinutesList = new ArrayList<Integer>(){{
        add(15);
        add(30);
        add(60);
        add(120);
    }};
    int mDefaultMinutesIndex = 0;
    int mDefaultPeriodIndex = 0;
    int mChosenProductIndex = 0;

    Handler mHandler = new BalanceHandler();
    PromotionCode mPromotionCode = null;

    String mTotalPrice = null;
    String mOriginalPrice = null;
    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_balance);
        mActivity = this;
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.black));
        actionBar.setElevation(0f);

        mLlBuyPackageProduct = (LinearLayout)findViewById(R.id.balance_ll_buypackageproduct);
        mRlSubcription = (RelativeLayout)findViewById(R.id.balance_rl_subcription);
        mRlExtraMinutes = (RelativeLayout)findViewById(R.id.balance_rl_extraminutes);
        mRlChoosePlan = (RelativeLayout)findViewById(R.id.balance_rl_chooseplan);
        mRlMonths = (RelativeLayout)findViewById(R.id.balance_rl_months);

        mTvTodaysRemain = (TextView)findViewById(R.id.balance_tv_todaysremain);
        mTvBalancePackage = (TextView)findViewById(R.id.balance_tv_balancepackage);
        mTvExpiredAt = (TextView)findViewById(R.id.balance_tv_expiredat);
        mTvBalanceExtra = (TextView)findViewById(R.id.balance_tv_balanceextra);
        mTvChooseMinutes = (TextView)findViewById(R.id.balance_tv_choosepackageproduct);
        mTvChoosePeriod = (TextView)findViewById(R.id.balance_tv_choosemonths);
        mTvOriginalPrice = (TextView)findViewById(R.id.balance_tv_originalprice);
        mTvTotalPrice = (TextView)findViewById(R.id.balance_tv_totalprice);
        mTvAddPromoCode = (TextView)findViewById(R.id.balance_tv_addpromocode);
        mTvPromotionCode = (TextView)findViewById(R.id.balance_tv_promotioncode);
        mTvUsing = (TextView)findViewById(R.id.balance_tv_using);
        mBtnBuy = (Button)findViewById(R.id.balance_btn_buy);

        mProgressBar = (ProgressBar)findViewById(R.id.balance_pb_loadingpackageproduct);

        mRlSubcription.setOnClickListener(new RlSubcriptionOnClickListener());
        mRlExtraMinutes.setOnClickListener(new RlExtraMinutesOnClickListener());
        mRlChoosePlan.setOnClickListener(new RlChoosePlanOnClickListener());
        mRlMonths.setOnClickListener(new RlMonthsOnClickListener());
        mTvAddPromoCode.setOnClickListener(new TvAddPromotionCodeOnClickListener());
        mBtnBuy.setOnClickListener(new BtnBuyOnClickListener());

        mTvOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中间横线
        mTvOriginalPrice.getPaint().setAntiAlias(true);// 抗锯齿

        getPackageProduct();

        initBalanceData();
    }

    void initBalanceData(){
        mTvTodaysRemain.setText(Chinessy.chinessy.getUser().getUserProfile().getBalance() + " minutes");
        mTvBalanceExtra.setText(Chinessy.chinessy.getUser().getUserProfile().getBalanceFree()+" mins");
        UserBalancePackage userBalancePackage = Chinessy.chinessy.getUser().getUserBalancePackage();
        if(userBalancePackage.getEndAt()!=null && userBalancePackage.getEndAt().getTime()!=0){
            mTvBalancePackage.setVisibility(View.VISIBLE);
            mTvBalancePackage.setText(userBalancePackage.getMinutes() + " mins/day");
            SimpleDateFormat format = new SimpleDateFormat("MMM d,yyyy");
            mTvExpiredAt.setText("Expired On" + format.format(userBalancePackage.getEndAt()));
        }else{
            mTvExpiredAt.setText("N/A");
            mTvBalancePackage.setVisibility(View.GONE);
        }
    }

    void getPackageProduct(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", Chinessy.chinessy.getUser().getAccessToken());

            InternalClient.postJson(mActivity, "product/get_package_list", jsonObject, new SimpleJsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        switch (response.getInt("code")){
                            case 10000:
                                List<Product> packageProductList = new ArrayList<Product>();
                                JSONObject dataJson = response.getJSONObject("data");

                                mDefaultPeriodIndex = dataJson.getInt("default_period");

                                JSONArray jsonArray = dataJson.getJSONArray("product_list");
                                int length = jsonArray.length();
                                for(int i=0; i<length; i++){
                                    Product product = new Product(jsonArray.getJSONObject(i));
                                    packageProductList.add(product);
                                }
                                mPackageProductList = packageProductList;

                                JSONArray periodJson = dataJson.getJSONArray("period_list");
                                length = periodJson.length();
                                for(int i=0; i<length; i++){
                                    mPeriodList.add(periodJson.getInt(i));
                                }

                                mProgressBar.setVisibility(View.GONE);
                                mLlBuyPackageProduct.setVisibility(View.VISIBLE);
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

    class RlSubcriptionOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            UserBalancePackage userBalancePackage = Chinessy.chinessy.getUser().getUserBalancePackage();
            if(userBalancePackage.getEndAt()!=null && userBalancePackage.getEndAt().getTime()!=0){
                Intent intent = new Intent();
                intent.setClass(mActivity, SubscriptionsActivity.class);
                mActivity.startActivity(intent);
            }else{
                // do nothing
            }
        }
    }

    class RlExtraMinutesOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mActivity, AddExtraMinutesActivity.class);
            mActivity.startActivityForResult(intent, RC_BUY_EXTRA_MINUTES);
        }
    }

    class RlChoosePlanOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mActivity, ChoosePlanActivity.class);
            intent.putExtra("minutes_list", (Serializable) mMinutesList);
            intent.putExtra("default_minutes_index", mDefaultMinutesIndex);
            mActivity.startActivityForResult(intent, RC_CHOOSE_MINUTES);
        }
    }

    class RlMonthsOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mActivity, SelectPeriodActivity.class);
            intent.putExtra("period_list", (Serializable) mPeriodList);
            intent.putExtra("default_period_index", mDefaultPeriodIndex);
            intent.putExtra("default_minutes", mMinutesList.get(mDefaultMinutesIndex));
            intent.putExtra("package_product_list", (Serializable) mPackageProductList);
            mActivity.startActivityForResult(intent, RC_CHOOSE_PERIOD);
        }
    }

    class TvAddPromotionCodeOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mActivity, GetPromotionActivity.class);
            mActivity.startActivityForResult(intent, AddBalanceActivity.RC_DISCOUNT_FROM_PROMOTION_CODE);
        }
    }

    class BtnBuyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Product product = mPackageProductList.get(mChosenProductIndex);
            getFinalPrice(product, mPromotionCode);

            mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setMessage(getString(R.string.Waiting));
            mProgressDialog.show();

            PayPalPayment payment = new PayPalPayment(new BigDecimal(mTotalPrice), product.getCurrency(), product.getName(),
                        PayPalPayment.PAYMENT_INTENT_SALE);
            Intent intent = new Intent(AddBalanceActivity.this, PaymentActivity.class);
            // send the same configuration for restart resiliency
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, Chinessy.getPaypalConfig());
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
            startActivityForResult(intent, RC_PAYPAL_FOR_PACKAGE_PRODUCT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_balance, menu);
        return true;
    }

    class BalanceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_INIT_SELL_PART:
                    int defaultMinutes = mMinutesList.get(mDefaultMinutesIndex);
                    int defaultPeriod = mPeriodList.get(mDefaultPeriodIndex);

                    int length = mPackageProductList.size();
                    for(int i=0; i<length; i++){
                        Product product = mPackageProductList.get(i);
                        if(product.getDaysLast()==defaultPeriod && product.getMinutes()==defaultMinutes){
                            mChosenProductIndex = i;
                        }
                    }

                    Product product = mPackageProductList.get(mChosenProductIndex);
                    mTvChooseMinutes.setText(product.getMinutes() + " mins/day");
                    mTvChoosePeriod.setText(product.getHumanReadPeriod());

                    getFinalPrice(product, null);
                    mTvTotalPrice.setText(getShowPrice(mTotalPrice));
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home){
            mActivity.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Toast.makeText(mActivity, "requestCode: " + requestCode, Toast.LENGTH_SHORT).show();

        switch (requestCode){
            case RC_PAYPAL_FOR_PACKAGE_PRODUCT:
                if (resultCode == Activity.RESULT_OK) {
                    PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                    if (confirm != null) {
                        try {
                            Log.i("paymentExample", confirm.toJSONObject().toString(4));
                            JSONObject jsonObject = confirm.toJSONObject();
                            String paypalId = jsonObject.getJSONObject("response").getString("id");
//                            Toast.makeText(mActivity, paypalId, Toast.LENGTH_SHORT).show();
                            // TODO: send 'confirm' to your server for verification.
                            // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                            // for more details.

                            JSONObject jsonObject1 = new JSONObject();
                            jsonObject1.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
                            jsonObject1.put("payment_id", paypalId);
                            jsonObject1.put("product_id", mPackageProductList.get(mChosenProductIndex).getId());
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
                                                initBalanceData();
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
                            Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                        }
                    }
                }
                else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.i("paymentExample", "The user canceled.");
                    mProgressDialog.dismiss();
                }
                else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                    Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                    mProgressDialog.dismiss();
                }
                break;
            case RC_DISCOUNT_FROM_PROMOTION_CODE:
                if(resultCode == Activity.RESULT_OK){
                    mPromotionCode = (PromotionCode)data.getSerializableExtra("promotion_code");
                }
                checkPromocode(mPromotionCode);
                break;
            case RC_CHOOSE_MINUTES:
                if(resultCode == Activity.RESULT_OK){
                    int defaultProductIndex = data.getIntExtra("default_minutes_index", 0);
                    onDefaultMinutesChanged(defaultProductIndex);
                }
                break;
            case RC_CHOOSE_PERIOD:
                if(resultCode == Activity.RESULT_OK){
                    int default_quantity_index = data.getIntExtra("default_period_index", 0);
                    onDefaultPeriodChanged(default_quantity_index);
                }
                break;
            case RC_BUY_EXTRA_MINUTES:
                if(resultCode == Activity.RESULT_OK){
                    initBalanceData();
                }
                break;
        }
    }

//    void refreshPeriodList(){
//        int length = mPackageProductList.size();
//        int defaultMinutes = mMinutesList.get(mDefaultMinutesIndex);
//        for(int i=0; i<length; i++){
//            Product product = mPackageProductList.get(i);
//            if(product.getMinutes() == defaultMinutes){
//                mPeriodChoosenProductList.add(product);
//            }
//        }
//        Collections.sort(mPeriodList, new Comparator<Object>() {
//            @Override
//            public int compare(Object lhs, Object rhs) {
//                Product product1 = (Product)lhs;
//                Product product2 = (Product)rhs;
//                if(product1.getDaysLast() > product2.getDaysLast()){
//                    return 1;
//                }
//                return 0;
//            }
//        });
//    }

    void checkPromocode(PromotionCode pc){
        if(pc!=null && !pc.getCode().equals("")){
            mTvPromotionCode.setText(pc.getCode() + " applied.");
            mTvUsing.setVisibility(View.VISIBLE);
            mTvPromotionCode.setVisibility(View.VISIBLE);
            mTvAddPromoCode.setText(R.string.Change);

            getFinalPrice(mPackageProductList.get(mChosenProductIndex), mPromotionCode);
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
        BigDecimal bdOriginal = new BigDecimal(product.getPrice()*product.getUnitNum()+"");
        bdOriginal = bdOriginal.setScale(2, BigDecimal.ROUND_HALF_UP);
        int priceQuantity = 1;

        mOriginalPrice = bdOriginal.toString();
        if(promotionCode != null){
            BigDecimal bdTotal = new BigDecimal(product.getPrice()*product.getUnitNum()*promotionCode.getProduct().getPrice()+"");
            bdTotal = bdTotal.setScale(2,BigDecimal.ROUND_HALF_UP);

            mTotalPrice = bdTotal.toString();
            priceQuantity = 2;
        }else{
            mTotalPrice = mOriginalPrice;
        }
        return priceQuantity;
    }

    private void onDefaultMinutesChanged(int defaultIndex){
        if(mDefaultMinutesIndex == defaultIndex){
            return;
        }

        mDefaultMinutesIndex = defaultIndex;
        chooseProductThroughMinutesAndPeriod(mDefaultMinutesIndex, mDefaultPeriodIndex);

        Product product = mPackageProductList.get(mChosenProductIndex);
        mTvChooseMinutes.setText(product.getMinutes() + " mins/day");
        mTvChoosePeriod.setText(product.getHumanReadPeriod());

        if(getFinalPrice(mPackageProductList.get(mChosenProductIndex), mPromotionCode)==2){
            mTvOriginalPrice.setText(getShowPrice(mOriginalPrice));
        }
        mTvTotalPrice.setText(getShowPrice(mTotalPrice));
    }

    int chooseProductThroughMinutesAndPeriod(int minutesIndex, int periodIndex){
        int length = mPackageProductList.size();
        for(int i=0; i<length; i++){
            Product product = mPackageProductList.get(i);
            if(product.getMinutes()==mMinutesList.get(mDefaultMinutesIndex) && product.getDaysLast()==mPeriodList.get(mDefaultPeriodIndex)){
                mChosenProductIndex = i;
            }
        }
        return mChosenProductIndex;
    }

    private void onDefaultPeriodChanged(int defaultIndex){
        if(mDefaultPeriodIndex == defaultIndex){
            return;
        }

        mDefaultPeriodIndex = defaultIndex;
        chooseProductThroughMinutesAndPeriod(mDefaultMinutesIndex, mDefaultPeriodIndex);

        Product product = mPackageProductList.get(mChosenProductIndex);
        mTvChooseMinutes.setText(product.getMinutes() + " mins/day");
        mTvChoosePeriod.setText(product.getHumanReadPeriod());

        if(getFinalPrice(mPackageProductList.get(mChosenProductIndex), mPromotionCode)==2){
            mTvOriginalPrice.setText(getShowPrice(mOriginalPrice));
        }
        mTvTotalPrice.setText(getShowPrice(mTotalPrice));
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public PromotionCode getPromotionCode() {
        return mPromotionCode;
    }

    public void setPromotionCode(PromotionCode pc) {
        mPromotionCode = pc;
    }

    private String getShowPrice(String price){
        return "US$ "+price;
    }
}
