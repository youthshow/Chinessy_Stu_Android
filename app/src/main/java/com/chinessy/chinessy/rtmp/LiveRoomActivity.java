package com.chinessy.chinessy.rtmp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.beans.BasicBean;
import com.chinessy.chinessy.beans.liveBeans;
import com.chinessy.chinessy.clients.ConstValue;
import com.chinessy.chinessy.utils.DisplayUtil;
import com.chinessy.chinessy.utils.LogUtils;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.util.TextUtils;

public class LiveRoomActivity extends AppCompatActivity implements View.OnClickListener {
    //直播头部
    private LinearLayout Lltop;
    private TextView TvHostName;
    private TextView TvHostMoney;
    private CustomRoundView CrvHostAvatar;
    private RecyclerView rvAudience;
    private RelativeLayout RlHostTotalBeansMin;
    //直播底部
    private RelativeLayout RlMainBottom;
    private ImageView IvChat;
    private ImageView IvBinde;
    //输入法 底部
    private LinearLayout LlInputBottom;
    private EditText etInput;
    private TextView sendInput;
    //绑定时间 底部
    private RelativeLayout RlBindMinutesBottom;
    private TextView BtnBindMin;
    // 礼物 底部
    private RelativeLayout RlGiftBottom;
    private TextView TvRechargeMoney;
    private TextView BtnGiftMoney;
    //Message
    private ListView LvMessage;
    private List<String> messageData = new LinkedList<>();
    private MessageAdapter messageAdapter;
    //bindMinute
    private TextView Tv1min;
    private TextView Tv10min;
    private TextView Tv30min;
    private TextView Tv60min;
    private TextView Tv100min;
    private TextView Tv200min;
    private TextView btnInvisbleSend;

    //gift
    private NumAnim giftNumAnim;
    private TranslateAnimation inAnim;
    private TranslateAnimation outAnim;


    private RelativeLayout RlGift;
    private RelativeLayout RlBindminsList;


    private ImageView iv_choose_bravo;
    private ImageView iv_choose_applause;
    private ImageView iv_choose_flower;
    private ImageView iv_choose_bouquet;
    private ImageView iv_choose_water;
    private ImageView iv_choose_chocolate;
    private ImageView iv_choose_star;
    private ImageView iv_choose_medal;


    private TextView tv_charge_bravo;
    private TextView tv_charge_applause;
    private TextView tv_charge_flower;
    private TextView tv_charge_bouquet;
    private TextView tv_charge_water;
    private TextView tv_charge_chocolate;
    private TextView tv_charge_star;
    private TextView tv_charge_medal;

    //recharge
    private LinearLayout LlRechargeList;
    private TextView tv_1;
    private TextView tv_10;
    private TextView tv_30;
    private TextView tv_60;
    private TextView tv_100;
    private TextView tv_200;

    //奖牌动画
    private ImageView IvAnim;

    private LinearLayout LlGiftContent;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    break;
            }
        }
    };

    String user_id;
    String room_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room);
        Intent intent = getIntent();

        user_id = intent.getStringExtra("user_id");
        room_id = intent.getStringExtra("room_id");
        SystemSetting();
       // webRequest();

        findViewById(R.id.content_layout).setOnClickListener(this);
        //直播头部
        Lltop = (LinearLayout) findViewById(R.id.top);
        TvHostName = (TextView) findViewById(R.id.tv_host_name);
        CrvHostAvatar = (CustomRoundView) findViewById(R.id.crv_host_avatar);
        //Beans:238742  Binded minutes:129
        TvHostMoney = (TextView) findViewById(R.id.tv_host_money);
        RlHostTotalBeansMin = (RelativeLayout) findViewById(R.id.rl_host_total_beans_min);
        rvAudience = (RecyclerView) findViewById(R.id.rv_audience);

        rvAudience.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvAudience.setLayoutManager(layoutManager);

        List list = new ArrayList<Integer>();
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);
        list.add(1);

        AudienceListAdapter mAdapter = new AudienceListAdapter(LiveRoomActivity.this, list);
        mAdapter.setOnItemClickListener(new AudienceListAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Dialog_AudienceInfo();
            }
        });
        // specify an adapter (see also next example)
        rvAudience.setAdapter(mAdapter);

        //直播底部
        RlMainBottom = (RelativeLayout) findViewById(R.id.rl_main_bottom);
        IvChat = (ImageView) findViewById(R.id.ivChat);
        IvChat.setOnClickListener(this);
        IvBinde = (ImageView) findViewById(R.id.ivbinde);
        IvBinde.setOnClickListener(this);
        findViewById(R.id.ivExit).setOnClickListener(this);
        findViewById(R.id.ivGift).setOnClickListener(this);

        //输入法 底部
        LlInputBottom = (LinearLayout) findViewById(R.id.llinput_bottom);
        etInput = (EditText) findViewById(R.id.etInput);
        sendInput = (TextView) findViewById(R.id.sendInput);
        sendInput.setOnClickListener(this);
        softKeyboardListnenr();
        //绑定时间 底部
        RlBindMinutesBottom = (RelativeLayout) findViewById(R.id.rl_bindminutes_bottom);
        findViewById(R.id.btn_bind).setOnClickListener(this);
        BtnBindMin = (TextView) findViewById(R.id.btn_bind_min);
        BtnBindMin.setOnClickListener(this);
        //  礼物 底部
        RlGiftBottom = (RelativeLayout) findViewById(R.id.rl_gift_bottom);
        findViewById(R.id.ll_recharge).setOnClickListener(this);
        TvRechargeMoney = (TextView) findViewById(R.id.tv_recharge_money);
        BtnGiftMoney = (TextView) findViewById(R.id.btn_gift_money);
        findViewById(R.id.btn_gift_send).setOnClickListener(this);
        //Message
        LvMessage = (ListView) findViewById(R.id.lvmessage);
        for (int x = 0; x < 20; x++) {
            messageData.add("Johnny: 默认聊天内容" + x);
        }
        messageAdapter = new MessageAdapter(this, messageData);
        LvMessage.setAdapter(messageAdapter);
        LvMessage.setSelection(messageData.size());
        //bindMinute
        Tv1min = (TextView) findViewById(R.id.tv_1_min);
        Tv10min = (TextView) findViewById(R.id.tv_10_min);
        Tv30min = (TextView) findViewById(R.id.tv_30_min);
        Tv60min = (TextView) findViewById(R.id.tv_60_min);
        Tv100min = (TextView) findViewById(R.id.tv_100_min);
        Tv200min = (TextView) findViewById(R.id.tv_200_min);
        RlBindminsList = (RelativeLayout) findViewById(R.id.rl_bindmins_list);
        Tv1min.setOnClickListener(this);
        Tv10min.setOnClickListener(this);
        Tv30min.setOnClickListener(this);
        Tv60min.setOnClickListener(this);
        Tv100min.setOnClickListener(this);
        Tv200min.setOnClickListener(this);
        btnInvisbleSend = (TextView) findViewById(R.id.btn_invisble_send);
        //gift
        inAnim = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.gift_in);
        outAnim = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.gift_out);
        giftNumAnim = new NumAnim();

        RlGift = (RelativeLayout) findViewById(R.id.rl_gift);
        findViewById(R.id.rl_bravo).setOnClickListener(this);
        findViewById(R.id.rl_applause).setOnClickListener(this);
        findViewById(R.id.rl_flower).setOnClickListener(this);
        findViewById(R.id.rl_bouquet).setOnClickListener(this);
        findViewById(R.id.rl_water).setOnClickListener(this);
        findViewById(R.id.rl_chocolate).setOnClickListener(this);
        findViewById(R.id.rl_star).setOnClickListener(this);
        findViewById(R.id.rl_medal).setOnClickListener(this);


        iv_choose_bravo = (ImageView) findViewById(R.id.iv_choose_bravo);
        iv_choose_applause = (ImageView) findViewById(R.id.iv_choose_applause);
        iv_choose_flower = (ImageView) findViewById(R.id.iv_choose_flower);
        iv_choose_bouquet = (ImageView) findViewById(R.id.iv_choose_bouquet);
        iv_choose_water = (ImageView) findViewById(R.id.iv_choose_water);
        iv_choose_chocolate = (ImageView) findViewById(R.id.iv_choose_chocolate);
        iv_choose_star = (ImageView) findViewById(R.id.iv_choose_star);
        iv_choose_medal = (ImageView) findViewById(R.id.iv_choose_medal);

        tv_charge_bravo = (TextView) findViewById(R.id.tv_charge_bravo);
        tv_charge_applause = (TextView) findViewById(R.id.tv_charge_applause);
        tv_charge_flower = (TextView) findViewById(R.id.tv_charge_flower);
        tv_charge_bouquet = (TextView) findViewById(R.id.tv_charge_bouquet);
        tv_charge_water = (TextView) findViewById(R.id.tv_charge_water);
        tv_charge_chocolate = (TextView) findViewById(R.id.tv_charge_chocolate);
        tv_charge_star = (TextView) findViewById(R.id.tv_charge_star);
        tv_charge_medal = (TextView) findViewById(R.id.tv_charge_medal);

        LlRechargeList = (LinearLayout) findViewById(R.id.ll_rechargelist);
        tv_1 = (TextView) findViewById(R.id.tv_1);
        tv_10 = (TextView) findViewById(R.id.tv_10);
        tv_30 = (TextView) findViewById(R.id.tv_30);
        tv_60 = (TextView) findViewById(R.id.tv_60);
        tv_100 = (TextView) findViewById(R.id.tv_100);
        tv_200 = (TextView) findViewById(R.id.tv_200);

        tv_1.setOnClickListener(this);
        tv_10.setOnClickListener(this);
        tv_30.setOnClickListener(this);
        tv_60.setOnClickListener(this);
        tv_100.setOnClickListener(this);
        tv_200.setOnClickListener(this);

        LlGiftContent = (LinearLayout) findViewById(R.id.llgiftcontent);
        //奖牌动画
        IvAnim = (ImageView) findViewById(R.id.iv_anim);


    }

    private void SystemSetting() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);/*同时将界面改为resize已达到软键盘弹出时LiveFragment不会跟随移动*/
    }

    /*直播--start*/
    private void webRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.BasicUrl + ConstValue.getPlayUrl,
                // StringRequest stringRequest = new StringRequest(Request.Method.GET, ConstValue.BasicUrl + "getPlayUrl"+"?roomId=002",
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        LogUtils.d(ConstValue.getPlayUrl + " :-->" + response.toString());
                        BasicBean basicBean = new Gson().fromJson(response.toString(), BasicBean.class);
                        if ("true".equals(basicBean.getStatus().toString())) {
                            liveBeans Beans = new Gson().fromJson(response.toString(), liveBeans.class);
                            String playUrl = Beans.getData();
                            ShowLive(playUrl);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.d(ConstValue.getPlayUrl + " :error-->" + error.toString());
            }
        }) {
            @Override
            protected Map getParams() {
                //在这里设置需要post的参数
                Map map = new HashMap();
                map.put("roomId", room_id);

                return map;
            }
        };

        Chinessy.requestQueue.add(stringRequest);
    }

  //  private LivePlayerActivity mPlayerVodFragment;

    private void ShowLive(String playurl) {

        if (!TextUtils.isEmpty(playurl)) {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            mPlayerVodFragment = new LivePlayerActivity();
//            Bundle bundle = new Bundle();
//            bundle.putString("url", playurl);
//            mPlayerVodFragment.setArguments(bundle);
//            transaction.replace(R.id.content_layout, mPlayerVodFragment);
//            transaction.commit();
        }
    }
       /*直播---end */


    public void Dialog_AudienceInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_audienceinfo, null);
        ImageView cancel = (ImageView) view.findViewById(R.id.iv_cancel);

        builder.setView(view);
        final AlertDialog dialog = builder.create();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 显示聊天布局
     */
    private void showChat() {

        LlInputBottom.requestFocus();
        showKeyboard();
    }

    /**
     * 显示软键盘并因此头布局
     */
    private void showKeyboard() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etInput, InputMethodManager.SHOW_FORCED);
            }
        }, 100);
    }

    /**
     * 隐藏软键盘并显示头布局
     */
    public void hideKeyboard() {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etInput.getWindowToken(), 0);


    }

    /**
     * 软键盘显示与隐藏的监听
     */
    private void softKeyboardListnenr() {
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {/*软键盘显示：执行隐藏title动画，并修改listview高度和装载礼物容器的高度*/
                if (RlMainBottom.getVisibility() == View.VISIBLE) {
                    RlMainBottom.setVisibility(View.GONE);
                }
                if (LlInputBottom.getVisibility() == View.GONE) {
                    LlInputBottom.setVisibility(View.VISIBLE);
                }


                animateToHide();
                dynamicChangeListviewH(100);
                dynamicChangeGiftParentH(true);
            }

            @Override
            public void keyBoardHide(int height) {/*软键盘隐藏：隐藏聊天输入框并显示聊天按钮，执行显示title动画，并修改listview高度和装载礼物容器的高度*/
                if (RlMainBottom.getVisibility() == View.GONE) {
                    RlMainBottom.setVisibility(View.VISIBLE);
                }
                if (LlInputBottom.getVisibility() == View.VISIBLE) {
                    LlInputBottom.setVisibility(View.GONE);
                }
                animateToShow();
                dynamicChangeListviewH(150);
                dynamicChangeGiftParentH(false);
            }
        });
    }

    /**
     * 动态的修改listview的高度
     *
     * @param heightPX
     */
    private void dynamicChangeListviewH(int heightPX) {
        ViewGroup.LayoutParams layoutParams = LvMessage.getLayoutParams();
        layoutParams.height = DisplayUtil.dip2px(this, heightPX);
        LvMessage.setLayoutParams(layoutParams);
    }

    /**
     * 动态修改礼物父布局的高度
     *
     * @param showhide
     */
    private void dynamicChangeGiftParentH(boolean showhide) {
        if (showhide) {/*如果软键盘显示中*/
            if (LlGiftContent.getChildCount() != 0) {
                /*判断是否有礼物显示，如果有就修改父布局高度，如果没有就不作任何操作*/
                ViewGroup.LayoutParams layoutParams = LlGiftContent.getLayoutParams();
                layoutParams.height = LlGiftContent.getChildAt(0).getHeight();
                LlGiftContent.setLayoutParams(layoutParams);
            }
        } else {/*如果软键盘隐藏中*/
            /*就将装载礼物的容器的高度设置为包裹内容*/
            ViewGroup.LayoutParams layoutParams = LlGiftContent.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            LlGiftContent.setLayoutParams(layoutParams);
        }
    }

    /**
     * 动画相关
     */
    private AnimatorSet animatorSetHide = new AnimatorSet();
    private AnimatorSet animatorSetShow = new AnimatorSet();

    /**
     * 标示判断
     */
    private boolean isOpen;

    /**
     * 头部布局执行显示的动画
     */
    private void animateToShow() {
        ObjectAnimator leftAnim = ObjectAnimator.ofFloat(RlHostTotalBeansMin, "translationX", -RlHostTotalBeansMin.getWidth(), 0);
        ObjectAnimator topAnim = ObjectAnimator.ofFloat(Lltop, "translationY", -Lltop.getHeight(), 0);
        animatorSetShow.playTogether(leftAnim, topAnim);
        animatorSetShow.setDuration(300);
        animatorSetShow.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isOpen = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isOpen = true;
            }
        });
        if (!isOpen) {
            animatorSetShow.start();
        }
    }


    /**
     * 头部布局执行退出的动画
     */
    private void animateToHide() {
        ObjectAnimator leftAnim = ObjectAnimator.ofFloat(RlHostTotalBeansMin, "translationX", 0, -RlHostTotalBeansMin.getWidth());
        ObjectAnimator topAnim = ObjectAnimator.ofFloat(Lltop, "translationY", 0, -Lltop.getHeight());
        animatorSetHide.playTogether(leftAnim, topAnim);
        animatorSetHide.setDuration(300);
        animatorSetHide.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isOpen = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isOpen = true;
            }
        });
        if (!isOpen) {
            animatorSetHide.start();
        }
    }

    /**
     * 发送消息
     */
    private void sendText() {
        if (!etInput.getText().toString().trim().isEmpty()) {
            messageData.add("Johnny: " + etInput.getText().toString().trim());
            etInput.setText("");
            messageAdapter.NotifyAdapter(messageData);
            LvMessage.setSelection(messageData.size());
            hideKeyboard();
        } else
            hideKeyboard();
    }


//    public void DismissGiftsView() {
//        if (RlMainBottom.getVisibility() == View.GONE) {
//            RlMainBottom.setVisibility(View.VISIBLE);
//        }
//        if (LvMessage.getVisibility() == View.GONE) {
//            LvMessage.setVisibility(View.VISIBLE);
//        }
//
//        if (RlGift.getVisibility() == View.VISIBLE) {
//            RlGift.setVisibility(View.GONE);
//        }
//        if (RlGiftBottom.getVisibility() == View.VISIBLE) {
//            RlGiftBottom.setVisibility(View.GONE);
//        }
//    }

    public void ShowGiftsView() {
        if (RlMainBottom.getVisibility() == View.VISIBLE) {
            RlMainBottom.setVisibility(View.GONE);
        }
        if (LvMessage.getVisibility() == View.VISIBLE) {
            LvMessage.setVisibility(View.GONE);
        }

        if (RlGift.getVisibility() == View.GONE) {
            RlGift.setVisibility(View.VISIBLE);
        }
        if (RlGiftBottom.getVisibility() == View.GONE) {
            RlGiftBottom.setVisibility(View.VISIBLE);
        }
    }

    String GiftArray[] = new String[]{"bravo", "applause", "flower", "bouquet", "water", "chocolate", "star", "medal"};
    int ChooseGiftIndex = -1;
    String ChargeCoin;


    String MinuteArray[] = new String[]{"1min", "10min", "30min", "60min", "100min", "200min"};
    int ChooseMinuteIndex = -1;

    private void RestChoose() {
        if (ChooseGiftIndex >= 0) {
            switch (GiftArray[ChooseGiftIndex]) {
                case "bravo":
                    iv_choose_bravo.setVisibility(View.INVISIBLE);
                    break;
                case "applause":
                    iv_choose_applause.setVisibility(View.INVISIBLE);
                    break;
                case "flower":
                    iv_choose_flower.setVisibility(View.INVISIBLE);
                    break;
                case "bouquet":
                    iv_choose_bouquet.setVisibility(View.INVISIBLE);
                    break;
                case "water":
                    iv_choose_water.setVisibility(View.INVISIBLE);
                    break;
                case "chocolate":
                    iv_choose_chocolate.setVisibility(View.INVISIBLE);
                    break;
                case "star":
                    iv_choose_star.setVisibility(View.INVISIBLE);
                    break;
                case "medal":
                    iv_choose_medal.setVisibility(View.INVISIBLE);
                    break;

            }
        }


        if (ChooseMinuteIndex >= 0) {
            switch (MinuteArray[ChooseMinuteIndex]) {
                case "1min":
                    Tv1min.setTextColor(getResources().getColor(R.color.white));
                    break;
                case "10min":
                    Tv10min.setTextColor(getResources().getColor(R.color.white));
                    break;
                case "30min":
                    Tv30min.setTextColor(getResources().getColor(R.color.white));
                    break;
                case "60min":
                    Tv60min.setTextColor(getResources().getColor(R.color.white));
                    break;
                case "100min":
                    Tv100min.setTextColor(getResources().getColor(R.color.white));
                    break;
                case "200min":
                    Tv200min.setTextColor(getResources().getColor(R.color.white));
                    break;
            }
        }
    }

    /**
     * 显示礼物的方法
     */
    private void showGift(String tag) {
        View giftView = LlGiftContent.findViewWithTag(tag);
        if (giftView == null) {/*该用户不在礼物显示列表*/

            if (LlGiftContent.getChildCount() > 2) {/*如果正在显示的礼物的个数超过两个，那么就移除最后一次更新时间比较长的*/
                View giftView1 = LlGiftContent.getChildAt(0);
                CustomRoundView picTv1 = (CustomRoundView) giftView1.findViewById(R.id.crvheadimage);
                long lastTime1 = (Long) picTv1.getTag();
                View giftView2 = LlGiftContent.getChildAt(1);
                CustomRoundView picTv2 = (CustomRoundView) giftView2.findViewById(R.id.crvheadimage);
                long lastTime2 = (Long) picTv2.getTag();
                if (lastTime1 > lastTime2) {/*如果第二个View显示的时间比较长*/
                    removeGiftView(1);
                } else {/*如果第一个View显示的时间长*/
                    removeGiftView(0);
                }
            }

            giftView = addGiftView();/*获取礼物的View的布局*/
            giftView.setTag(tag);/*设置view标识*/

            CustomRoundView crvheadimage = (CustomRoundView) giftView.findViewById(R.id.crvheadimage);
            final MagicTextView giftNum = (MagicTextView) giftView.findViewById(R.id.giftNum);/*找到数量控件*/
            giftNum.setText("x1");/*设置礼物数量*/
            crvheadimage.setTag(System.currentTimeMillis());/*设置时间标记*/
            giftNum.setTag(1);/*给数量控件设置标记*/

            LlGiftContent.addView(giftView);/*将礼物的View添加到礼物的ViewGroup中*/
            LlGiftContent.invalidate();/*刷新该view*/
            giftView.startAnimation(inAnim);/*开始执行显示礼物的动画*/
            inAnim.setAnimationListener(new Animation.AnimationListener() {/*显示动画的监听*/
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    giftNumAnim.start(giftNum);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else {/*该用户在礼物显示列表*/
            CustomRoundView crvheadimage = (CustomRoundView) giftView.findViewById(R.id.crvheadimage);/*找到头像控件*/
            MagicTextView giftNum = (MagicTextView) giftView.findViewById(R.id.giftNum);/*找到数量控件*/
            int showNum = (Integer) giftNum.getTag() + 1;
            giftNum.setText("x" + showNum);
            giftNum.setTag(showNum);
            crvheadimage.setTag(System.currentTimeMillis());
            giftNumAnim.start(giftNum);
        }
    }


    /**
     * 删除礼物view
     */
    private void removeGiftView(final int index) {
        final View removeView = LlGiftContent.getChildAt(index);
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                LlGiftContent.removeViewAt(index);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeView.startAnimation(outAnim);
            }
        });
    }


    private List<View> giftViewCollection = new ArrayList<View>();

    /**
     * 添加礼物view,(考虑垃圾回收)
     */
    private View addGiftView() {
        View view = null;
        if (giftViewCollection.size() <= 0) {
            /*如果垃圾回收中没有view,则生成一个*/
            view = LayoutInflater.from(this).inflate(R.layout.item_gift, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = 10;
            view.setLayoutParams(lp);
            LlGiftContent.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view) {
                }

                @Override
                public void onViewDetachedFromWindow(View view) {
                    giftViewCollection.add(view);
                }
            });
        } else {
            view = giftViewCollection.get(0);
            giftViewCollection.remove(view);
        }
        return view;
    }

    /**
     * 数字放大动画
     */
    public class NumAnim {
        private Animator lastAnimator = null;

        public void start(View view) {
            if (lastAnimator != null) {
                lastAnimator.removeAllListeners();
                lastAnimator.end();
                lastAnimator.cancel();
            }
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1.3f, 1.0f);
            ObjectAnimator anim2 = ObjectAnimator.ofFloat(view, "scaleY", 1.3f, 1.0f);
            AnimatorSet animSet = new AnimatorSet();
            lastAnimator = animSet;
            animSet.setDuration(200);
            animSet.setInterpolator(new OvershootInterpolator());
            animSet.playTogether(anim1, anim2);
            animSet.start();
        }
    }

    public class MedalAnim {
        private Animator lastAnimator = null;

        public void start(View view) {
            if (lastAnimator != null) {
                lastAnimator.removeAllListeners();
                lastAnimator.end();
                lastAnimator.cancel();
            }
            ObjectAnimator reduceX = ObjectAnimator.ofFloat(view, "scaleX", 1F, 0.7F, 1.0f, 0.7f, 1.0f);
            ObjectAnimator reduceY = ObjectAnimator.ofFloat(view, "scaleY", 1F, 0.7F, 1.0f, 0.7f, 1.0f);
            ObjectAnimator rotationright = ObjectAnimator.ofFloat(view, "rotation", 0F, 10F, 0f, -10f, 0f);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 0F, 1f, 1f, 1f, 0f);

            AnimatorSet animSet = new AnimatorSet();
            lastAnimator = animSet;
            animSet.setDuration(3000);
            animSet.playTogether(reduceX, reduceY, rotationright, alpha);
            animSet.start();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivChat:/*聊天*/
                showChat();
                break;
            case R.id.sendInput:/*发送*/
                sendText();
                break;
            case R.id.ivExit:/*退出房间*/
                finish();
                break;
            case R.id.ivGift:/*显示礼物布局*/
                ShowGiftsView();
                break;
            case R.id.ivbinde:/*绑定时间*/
                if (RlBindminsList.getVisibility() == View.GONE) {
                    RlBindminsList.setVisibility(View.VISIBLE);
                }
                if (RlMainBottom.getVisibility() == View.VISIBLE) {
                    RlMainBottom.setVisibility(View.GONE);
                }

                if (RlBindMinutesBottom.getVisibility() == View.GONE) {
                    RlBindMinutesBottom.setVisibility(View.VISIBLE);
                }
                if (btnInvisbleSend.getVisibility() == View.GONE) {
                    btnInvisbleSend.setVisibility(View.INVISIBLE);
                }
                break;

            case R.id.btn_bind:/*绑定时间*/
                Toast.makeText(LiveRoomActivity.this, "绑定时间", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ll_recharge:/*显示 recharge菜单*/
                if (LlRechargeList.getVisibility() == View.GONE) {
                    LlRechargeList.setVisibility(View.VISIBLE);
                } else {
                    LlRechargeList.setVisibility(View.GONE);
                }
                if (btnInvisbleSend.getVisibility() == View.GONE) {
                    btnInvisbleSend.setVisibility(View.INVISIBLE);
                }

                break;
            case R.id.btn_gift_send:/*送礼物动画*/
                showGift("Johnny1");
                RestBottoms();
                // showGift("Johnny2");
                // showGift("Johnny3");
                // showGift("Johnny4");
                //奖牌动画
                if (IvAnim.getVisibility() == View.GONE) {
                    IvAnim.setVisibility(View.VISIBLE);
                }
                MedalAnim medalAnim = new MedalAnim();
                medalAnim.start(IvAnim);
                break;

            case R.id.rl_bravo:
                // iv_choose_bravo tv_charge_bravo
                RestChoose();
                ChooseGiftIndex = 0;
                iv_choose_bravo.setVisibility(View.VISIBLE);
                ChargeCoin = tv_charge_bravo.getText().toString();
                BtnGiftMoney.setText(ChargeCoin);
                break;
            case R.id.rl_applause:
                // iv_choose_applause
                RestChoose();
                ChooseGiftIndex = 1;
                iv_choose_applause.setVisibility(View.VISIBLE);
                ChargeCoin = tv_charge_applause.getText().toString();
                BtnGiftMoney.setText(ChargeCoin);
                break;
            case R.id.rl_flower:
                //     iv_choose_flower
                RestChoose();
                ChooseGiftIndex = 2;
                iv_choose_flower.setVisibility(View.VISIBLE);
                ChargeCoin = tv_charge_flower.getText().toString();
                BtnGiftMoney.setText(ChargeCoin);
                break;
            case R.id.rl_bouquet:
                // iv_choose_bouquet
                RestChoose();
                ChooseGiftIndex = 3;
                iv_choose_bouquet.setVisibility(View.VISIBLE);
                ChargeCoin = tv_charge_bouquet.getText().toString();
                BtnGiftMoney.setText(ChargeCoin);
                break;
            case R.id.rl_water:
                //  iv_choose_water
                RestChoose();
                ChooseGiftIndex = 4;
                iv_choose_water.setVisibility(View.VISIBLE);
                ChargeCoin = tv_charge_water.getText().toString();
                BtnGiftMoney.setText(ChargeCoin);
                break;
            case R.id.rl_chocolate:
                //  iv_choose_chocolate
                RestChoose();
                ChooseGiftIndex = 5;
                iv_choose_chocolate.setVisibility(View.VISIBLE);
                ChargeCoin = tv_charge_chocolate.getText().toString();
                BtnGiftMoney.setText(ChargeCoin);
                break;
            case R.id.rl_star:
                //  iv_choose_star
                RestChoose();
                ChooseGiftIndex = 6;
                iv_choose_star.setVisibility(View.VISIBLE);
                ChargeCoin = tv_charge_star.getText().toString();
                BtnGiftMoney.setText(ChargeCoin);
                break;
            case R.id.rl_medal:
                //iv_choose_medal
                RestChoose();
                ChooseGiftIndex = 7;
                iv_choose_medal.setVisibility(View.VISIBLE);
                ChargeCoin = tv_charge_medal.getText().toString();
                BtnGiftMoney.setText(ChargeCoin);
                break;
            case R.id.tv_200:
                charge(tv_200);
                break;
            case R.id.tv_100:
                charge(tv_100);
                break;
            case R.id.tv_60:
                charge(tv_60);
                break;
            case R.id.tv_30:
                charge(tv_30);
                break;
            case R.id.tv_10:
                charge(tv_10);
                break;
            case R.id.tv_1:
                charge(tv_1);
                break;
            case R.id.tv_1_min:
                RestChoose();
                Tv1min.setTextColor(getResources().getColor(R.color.main_color));
                ChooseMinuteIndex = 0;
                chargeMin(Tv1min);
                break;
            case R.id.tv_10_min:
                RestChoose();
                Tv10min.setTextColor(getResources().getColor(R.color.main_color));
                ChooseMinuteIndex = 1;
                chargeMin(Tv10min);
                break;
            case R.id.tv_30_min:
                RestChoose();
                Tv30min.setTextColor(getResources().getColor(R.color.main_color));
                ChooseMinuteIndex = 2;
                chargeMin(Tv30min);
                break;
            case R.id.tv_60_min:
                RestChoose();
                Tv60min.setTextColor(getResources().getColor(R.color.main_color));
                ChooseMinuteIndex = 3;
                chargeMin(Tv60min);
                break;
            case R.id.tv_100_min:
                RestChoose();
                Tv100min.setTextColor(getResources().getColor(R.color.main_color));
                ChooseMinuteIndex = 4;
                chargeMin(Tv100min);
                break;
            case R.id.tv_200_min:
                RestChoose();
                Tv200min.setTextColor(getResources().getColor(R.color.main_color));
                ChooseMinuteIndex = 5;
                chargeMin(Tv200min);
                break;
            case R.id.content_layout:
                //显示原来的底部
                RestBottoms();
                hideKeyboard();

                break;

        }
    }

    private void RestBottoms() {
        if (RlGift.getVisibility() == View.VISIBLE) {
            RlGift.setVisibility(View.GONE);
        }
        if (RlBindminsList.getVisibility() == View.VISIBLE) {
            RlBindminsList.setVisibility(View.GONE);
        }
        if (btnInvisbleSend.getVisibility() == View.VISIBLE) {
            btnInvisbleSend.setVisibility(View.GONE);
        }

        if (LlInputBottom.getVisibility() == View.VISIBLE) {
            LlInputBottom.setVisibility(View.GONE);
        }
        if (RlBindMinutesBottom.getVisibility() == View.VISIBLE) {
            RlBindMinutesBottom.setVisibility(View.GONE);
        }
        if (RlGiftBottom.getVisibility() == View.VISIBLE) {
            RlGiftBottom.setVisibility(View.GONE);
        }
        if (RlMainBottom.getVisibility() == View.GONE) {
            RlMainBottom.setVisibility(View.VISIBLE);
        }
        if (LlRechargeList.getVisibility() == View.VISIBLE) {
            LlRechargeList.setVisibility(View.GONE);
        }
    }

    private void charge(TextView textView) {
        Toast.makeText(LiveRoomActivity.this, textView.getText().toString(), Toast.LENGTH_SHORT).show();
        if (LlRechargeList.getVisibility() == View.VISIBLE) {
            LlRechargeList.setVisibility(View.GONE);
        }
    }

    private void chargeMin(TextView textView) {
        BtnBindMin.setText(textView.getText().toString());
    }

}
