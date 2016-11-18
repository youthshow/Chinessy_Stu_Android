package com.chinessy.chinessy.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.beans.liveBeans;
import com.chinessy.chinessy.clients.ConstValue;
import com.chinessy.chinessy.rtmp.AudienceListAdapter;
import com.chinessy.chinessy.rtmp.CustomRoundView;
import com.chinessy.chinessy.rtmp.LivePlayerActivity;
import com.chinessy.chinessy.rtmp.MagicTextView;
import com.chinessy.chinessy.rtmp.MessageAdapter;
import com.chinessy.chinessy.rtmp.SoftKeyBoardListener;
import com.chinessy.chinessy.utils.DisplayUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.util.TextUtils;

public class LiveRoomActivity extends AppCompatActivity implements View.OnClickListener {
    private FrameLayout frameLayout;
    private LivePlayerActivity mPlayerVodFragment;
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

    /**
     * 标示判断
     */
    private boolean isOpen;
    private long liveTime;

    /**
     * 界面相关
     */
    private LinearLayout llpicimage;
    private RelativeLayout rlsentimenttime;
    private RecyclerView rvAudience;
    private TextView tvtime;
    private TextView tvdate;
    private LinearLayout llgiftcontent;
    private ListView lvmessage;
    private TextView tvSendone;
    private TextView tvSendtwo;
    private TextView tvSendthree;
    private TextView tvSendfor;
    private EditText etInput;
    private ImageView IvChat;
    private TextView sendInput;
    private LinearLayout llInputParent;
    private FrameLayout Fl_bottom;
    /**
     * 动画相关
     */
    private NumAnim giftNumAnim;
    private TranslateAnimation inAnim;
    private TranslateAnimation outAnim;
    private AnimatorSet animatorSetHide = new AnimatorSet();
    private AnimatorSet animatorSetShow = new AnimatorSet();

    /**
     * 数据相关
     */
    private List<View> giftViewCollection = new ArrayList<View>();
    private List<String> messageData = new LinkedList<>();
    private MessageAdapter messageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_live_room);
        SystemSetting();
        webRequest();
        frameLayout = (FrameLayout) findViewById(R.id.content_layout);
        Fl_bottom = (FrameLayout) findViewById(R.id.layout_bottom);
        llpicimage = (LinearLayout) findViewById(R.id.llpicimage);
        rlsentimenttime = (RelativeLayout) findViewById(R.id.rlsentimenttime);
        rvAudience = (RecyclerView) findViewById(R.id.rv_audience);
        tvtime = (TextView) findViewById(R.id.tvtime);
        tvdate = (TextView) findViewById(R.id.tvdate);
        llgiftcontent = (LinearLayout) findViewById(R.id.llgiftcontent);
        lvmessage = (ListView) findViewById(R.id.lvmessage);
        IvChat = (ImageView) findViewById(R.id.ivChat);
        tvSendone = (TextView) findViewById(R.id.tvSendone);
        tvSendtwo = (TextView) findViewById(R.id.tvSendtwo);
        tvSendthree = (TextView) findViewById(R.id.tvSendthree);
        tvSendfor = (TextView) findViewById(R.id.tvSendfor);
        llInputParent = (LinearLayout) findViewById(R.id.llinputparent);
        etInput = (EditText) findViewById(R.id.etInput);
        sendInput = (TextView) findViewById(R.id.sendInput);
        IvChat.setOnClickListener(this);
        tvSendone.setOnClickListener(this);
        tvSendtwo.setOnClickListener(this);
        tvSendthree.setOnClickListener(this);
        tvSendfor.setOnClickListener(this);
        sendInput.setOnClickListener(this);
        inAnim = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.gift_in);
        outAnim = (TranslateAnimation) AnimationUtils.loadAnimation(this, R.anim.gift_out);
        giftNumAnim = new NumAnim();
        clearTiming();

        findViewById(R.id.ivExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SendGiftBtn();


        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llInputParent.getVisibility() == View.VISIBLE) {
                    IvChat.setVisibility(View.VISIBLE);
                    llInputParent.setVisibility(View.GONE);
                    hideKeyboard();
                }
            }
        });
        softKeyboardListnenr();
        for (int x = 0; x < 20; x++) {
            messageData.add("Johnny: 默认聊天内容" + x);
        }
        messageAdapter = new MessageAdapter(this, messageData);
        lvmessage.setAdapter(messageAdapter);
        lvmessage.setSelection(messageData.size());
        // hlvaudience.setAdapter(new AudienceAdapter(this));
        startTimer();

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

    }

    //礼物

    ImageView iv_choose_bravo;
    ImageView iv_choose_applause;
    ImageView iv_choose_flower;
    ImageView iv_choose_bouquet;
    ImageView iv_choose_water;
    ImageView iv_choose_chocolate;
    ImageView iv_choose_red_packet;
    ImageView iv_choose_medal;


    TextView tv_charge_bravo;
    TextView tv_charge_applause;
    TextView tv_charge_flower;
    TextView tv_charge_bouquet;
    TextView tv_charge_water;
    TextView tv_charge_chocolate;
    TextView tv_charge_red_packet;
    TextView tv_charge_medal;
    String GiftArray[] = new String[]{"bravo", "applause", "flower", "bouquet", "water", "chocolate", "redPacket", "medal"};

    int ChooseGiftIndex = -1;
    String ChargeCoin;

    private void SendGiftBtn() {
        findViewById(R.id.ivGift).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 使用不带Theme的构造器, 获得的dialog边框距离屏幕仍有几毫米的缝隙。
                Dialog dialog = new Dialog(LiveRoomActivity.this, R.style.BottomDialog);

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置Content前设定
                dialog.setContentView(R.layout.dialog_bottom_giftlist);
                dialog.setCanceledOnTouchOutside(true); // 外部点击取消
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (Fl_bottom.getVisibility() == View.GONE) {
                            Fl_bottom.setVisibility(View.VISIBLE);
                        }
                        if (lvmessage.getVisibility() == View.GONE) {
                            lvmessage.setVisibility(View.VISIBLE);
                        }
                    }
                });
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        if (Fl_bottom.getVisibility() == View.VISIBLE) {
                            Fl_bottom.setVisibility(View.GONE);
                        }
                        if (lvmessage.getVisibility() == View.VISIBLE) {
                            lvmessage.setVisibility(View.GONE);
                        }
                    }
                });
                // 设置宽度为屏宽, 靠近屏幕底部。
                Window window = dialog.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.gravity = Gravity.BOTTOM; // 紧贴底部
                lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度持平
                window.setAttributes(lp);

                dialog.findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //todo 送出礼物
                    }
                });


                dialog.findViewById(R.id.ll_recharge).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //todo       tv_recharge
                    }
                });


                iv_choose_bravo = (ImageView) dialog.findViewById(R.id.iv_choose_bravo);
                iv_choose_applause = (ImageView) dialog.findViewById(R.id.iv_choose_applause);
                iv_choose_flower = (ImageView) dialog.findViewById(R.id.iv_choose_flower);
                iv_choose_bouquet = (ImageView) dialog.findViewById(R.id.iv_choose_bouquet);
                iv_choose_water = (ImageView) dialog.findViewById(R.id.iv_choose_water);
                iv_choose_chocolate = (ImageView) dialog.findViewById(R.id.iv_choose_chocolate);
                iv_choose_red_packet = (ImageView) dialog.findViewById(R.id.iv_choose_red_packet);
                iv_choose_medal = (ImageView) dialog.findViewById(R.id.iv_choose_medal);


                tv_charge_bravo = (TextView) dialog.findViewById(R.id.tv_charge_bravo);
                tv_charge_applause = (TextView) dialog.findViewById(R.id.tv_charge_applause);
                tv_charge_flower = (TextView) dialog.findViewById(R.id.tv_charge_flower);
                tv_charge_bouquet = (TextView) dialog.findViewById(R.id.tv_charge_bouquet);
                tv_charge_water = (TextView) dialog.findViewById(R.id.tv_charge_water);
                tv_charge_chocolate = (TextView) dialog.findViewById(R.id.tv_charge_chocolate);
                tv_charge_red_packet = (TextView) dialog.findViewById(R.id.tv_charge_red_packet);
                tv_charge_medal = (TextView) dialog.findViewById(R.id.tv_charge_medal);


                dialog.findViewById(R.id.rl_bravo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //todo      iv_choose_bravo tv_charge_bravo
                        RestChoose();
                        ChooseGiftIndex = 0;
                        iv_choose_bravo.setVisibility(View.VISIBLE);
                        ChargeCoin = tv_charge_bravo.getText().toString();
                    }
                });
                dialog.findViewById(R.id.rl_applause).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //todo      iv_choose_applause
                        RestChoose();
                        ChooseGiftIndex = 1;
                        iv_choose_applause.setVisibility(View.VISIBLE);
                        ChargeCoin = tv_charge_applause.getText().toString();
                    }
                });
                dialog.findViewById(R.id.rl_flower).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //todo      iv_choose_flower
                        RestChoose();
                        ChooseGiftIndex = 2;
                        iv_choose_flower.setVisibility(View.VISIBLE);
                        ChargeCoin = tv_charge_flower.getText().toString();
                    }
                });
                dialog.findViewById(R.id.rl_bouquet).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //todo      iv_choose_bouquet
                        RestChoose();
                        ChooseGiftIndex = 3;
                        iv_choose_bouquet.setVisibility(View.VISIBLE);
                        ChargeCoin = tv_charge_bouquet.getText().toString();
                    }
                });
                dialog.findViewById(R.id.rl_water).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //todo      iv_choose_water
                        RestChoose();
                        ChooseGiftIndex = 4;
                        iv_choose_water.setVisibility(View.VISIBLE);
                        ChargeCoin = tv_charge_water.getText().toString();
                    }
                });
                dialog.findViewById(R.id.rl_chocolate).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //todo      iv_choose_chocolate
                        RestChoose();
                        ChooseGiftIndex = 5;
                        iv_choose_chocolate.setVisibility(View.VISIBLE);
                        ChargeCoin = tv_charge_chocolate.getText().toString();
                    }
                });
                dialog.findViewById(R.id.rl_red_packet).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //todo      iv_choose_red_packet
                        RestChoose();
                        ChooseGiftIndex = 6;
                        iv_choose_red_packet.setVisibility(View.VISIBLE);
                        ChargeCoin = tv_charge_red_packet.getText().toString();
                    }
                });
                dialog.findViewById(R.id.rl_medal).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //todo      iv_choose_medal
                        RestChoose();
                        ChooseGiftIndex = 7;
                        RestChoose();
                        iv_choose_medal.setVisibility(View.VISIBLE);
                        ChargeCoin = tv_charge_medal.getText().toString();
                    }
                });


                dialog.show();
            }
        });
    }

    private void RestChoose() {
        if (ChooseGiftIndex > 0) {
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
                case "redPacket":
                    iv_choose_red_packet.setVisibility(View.INVISIBLE);
                    break;
                case "medal":
                    iv_choose_medal.setVisibility(View.INVISIBLE);
                    break;

            }
        }
    }


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

    private void webRequest() {
        Log.d("VolleyPostPost", "VolleyPostPost -> ");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.BasicUrl + ConstValue.getPlayUrl,
                // StringRequest stringRequest = new StringRequest(Request.Method.GET, ConstValue.BasicUrl + "getPlayUrl"+"?roomId=002",
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.d("=============", "response -> " + response);
                        //Toast.makeText()

                        liveBeans Beans = new Gson().fromJson(response.toString(), liveBeans.class);
                        if ("true".equals(Beans.getStatus().toString())) {
                            String playUrl = Beans.getData();
                            Log.d("=============", "playUrl -> " + playUrl);

                            //    if (!TextUtils.isEmpty(playUrl)) {
                            Log.d("-----------", "playUrl" + playUrl);
                            ShowLive(playUrl);
                              /*  if (mVideoPlay) {
                                    if (mPlayType == TXLivePlayer.PLAY_TYPE_VOD_FLV || mPlayType == TXLivePlayer.PLAY_TYPE_VOD_HLS || mPlayType == TXLivePlayer.PLAY_TYPE_VOD_MP4) {
                                        if (mVideoPause) {
                                            mLivePlayer.resume();
                                            // mBtnPlay.setBackgroundResource(R.drawable.play_pause);
                                        } else {
                                            mLivePlayer.pause();
                                            //  mBtnPlay.setBackgroundResource(R.drawable.play_start);
                                        }
                                        mVideoPause = !mVideoPause;

                                    } else {
                                        stopPlayRtmp();
                                        mVideoPlay = !mVideoPlay;
                                    }

                                } else {
                                    if (startPlayRtmp()) {
                                        mVideoPlay = !mVideoPlay;
                                    }
                                }
*/
                            //   }
                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VolleyPostPost", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map getParams() {
                //在这里设置需要post的参数
                Map map = new HashMap();
                map.put("roomId", "002");

                return map;
            }
        };

        Chinessy.requestQueue.add(stringRequest);

        /*
        JSONObject jsonParams = new JSONObject();

        //todo 修改房间号
        try {
            jsonParams.put("roomId", "001");
            //  jsonParams.put("Key", Key);
            //  jsonParams.put("Time", "2016-12-12 12:00:00");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        InternalClient.HKpostInternalJson(getContext(), ConstValue.getPlayUrl, jsonParams, new SimpleJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                Log.d("PostPost", responseString + "");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("PostPost", responseString + "-----onFailure");
            }
        });
*/
    }

    private void ShowLive(String playurl) {

        if (!TextUtils.isEmpty(playurl)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            mPlayerVodFragment = new LivePlayerActivity();
            Bundle bundle = new Bundle();
            bundle.putString("url", playurl);
            mPlayerVodFragment.setArguments(bundle);
            transaction.replace(R.id.content_layout, mPlayerVodFragment);
            transaction.commit();
            Log.d("playurl", "ShowLive");
        }


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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivChat:/*聊天*/
                showChat();
                break;
            case R.id.sendInput:/*发送*/
                sendText();
                break;
            case R.id.tvSendone:/*礼物1*/
                showGift("Johnny1");
                break;
            case R.id.tvSendtwo:/*礼物2*/
                showGift("Johnny2");
                break;
            case R.id.tvSendthree:/*礼物3*/
                showGift("Johnny3");
                break;
            case R.id.tvSendfor:/*礼物4*/
                showGift("Johnny4");
                break;
        }
    }

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
            llgiftcontent.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
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
     * 删除礼物view
     */
    private void removeGiftView(final int index) {
        final View removeView = llgiftcontent.getChildAt(index);
        outAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llgiftcontent.removeViewAt(index);
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

    /**
     * 显示礼物的方法
     */
    private void showGift(String tag) {
        View giftView = llgiftcontent.findViewWithTag(tag);
        if (giftView == null) {/*该用户不在礼物显示列表*/

            if (llgiftcontent.getChildCount() > 2) {/*如果正在显示的礼物的个数超过两个，那么就移除最后一次更新时间比较长的*/
                View giftView1 = llgiftcontent.getChildAt(0);
                CustomRoundView picTv1 = (CustomRoundView) giftView1.findViewById(R.id.crvheadimage);
                long lastTime1 = (Long) picTv1.getTag();
                View giftView2 = llgiftcontent.getChildAt(1);
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

            llgiftcontent.addView(giftView);/*将礼物的View添加到礼物的ViewGroup中*/
            llgiftcontent.invalidate();/*刷新该view*/
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
     * 显示聊天布局
     */
    private void showChat() {
        IvChat.setVisibility(View.GONE);
        llInputParent.setVisibility(View.VISIBLE);
        llInputParent.requestFocus();
        showKeyboard();
    }

    /**
     * 发送消息
     */
    private void sendText() {
        if (!etInput.getText().toString().trim().isEmpty()) {
            messageData.add("Johnny: " + etInput.getText().toString().trim());
            etInput.setText("");
            messageAdapter.NotifyAdapter(messageData);
            lvmessage.setSelection(messageData.size());
            hideKeyboard();
        } else
            hideKeyboard();
    }

    /**
     * 开始计时功能
     */
    private void startTimer() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);
        calendar.add(Calendar.HOUR_OF_DAY, -8);
        Date time = calendar.getTime();
        liveTime = time.getTime();
        handler.post(timerRunnable);
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
                animateToHide();
                dynamicChangeListviewH(100);
                dynamicChangeGiftParentH(true);
            }

            @Override
            public void keyBoardHide(int height) {/*软键盘隐藏：隐藏聊天输入框并显示聊天按钮，执行显示title动画，并修改listview高度和装载礼物容器的高度*/
                IvChat.setVisibility(View.VISIBLE);
                llInputParent.setVisibility(View.GONE);
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
        ViewGroup.LayoutParams layoutParams = lvmessage.getLayoutParams();
        layoutParams.height = DisplayUtil.dip2px(this, heightPX);
        lvmessage.setLayoutParams(layoutParams);
    }

    /**
     * 动态修改礼物父布局的高度
     *
     * @param showhide
     */
    private void dynamicChangeGiftParentH(boolean showhide) {
        if (showhide) {/*如果软键盘显示中*/
            if (llgiftcontent.getChildCount() != 0) {
                /*判断是否有礼物显示，如果有就修改父布局高度，如果没有就不作任何操作*/
                ViewGroup.LayoutParams layoutParams = llgiftcontent.getLayoutParams();
                layoutParams.height = llgiftcontent.getChildAt(0).getHeight();
                llgiftcontent.setLayoutParams(layoutParams);
            }
        } else {/*如果软键盘隐藏中*/
            /*就将装载礼物的容器的高度设置为包裹内容*/
            ViewGroup.LayoutParams layoutParams = llgiftcontent.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            llgiftcontent.setLayoutParams(layoutParams);
        }
    }

    /**
     * 头部布局执行显示的动画
     */
    private void animateToShow() {
        ObjectAnimator leftAnim = ObjectAnimator.ofFloat(rlsentimenttime, "translationX", -rlsentimenttime.getWidth(), 0);
        ObjectAnimator topAnim = ObjectAnimator.ofFloat(llpicimage, "translationY", -llpicimage.getHeight(), 0);
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
        ObjectAnimator leftAnim = ObjectAnimator.ofFloat(rlsentimenttime, "translationX", 0, -rlsentimenttime.getWidth());
        ObjectAnimator topAnim = ObjectAnimator.ofFloat(llpicimage, "translationY", 0, -llpicimage.getHeight());
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
     * 循环执行线程
     */
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(timerRunnable, 1000);
            long sysTime = System.currentTimeMillis();
            liveTime += 1000;
            CharSequence sysTimeStr = DateFormat.format("HH:mm:ss", liveTime);
            CharSequence sysDateStr = DateFormat.format("yyyy/MM/dd", sysTime);
            tvtime.setText(sysTimeStr);
            tvdate.setText(sysDateStr);
        }
    };

    /**
     * 定时清除礼物
     */
    private void clearTiming() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                int count = llgiftcontent.getChildCount();
                for (int i = 0; i < count; i++) {
                    View view = llgiftcontent.getChildAt(i);
                    CustomRoundView crvheadimage = (CustomRoundView) view.findViewById(R.id.crvheadimage);
                    long nowtime = System.currentTimeMillis();
                    long upTime = (Long) crvheadimage.getTag();
                    if ((nowtime - upTime) >= 3000) {
                        removeGiftView(i);
                        return;
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 0, 3000);
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


}
