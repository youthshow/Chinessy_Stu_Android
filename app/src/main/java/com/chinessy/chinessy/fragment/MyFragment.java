package com.chinessy.chinessy.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.activity.AddBalanceActivity;
import com.chinessy.chinessy.activity.BindedTeacherListActivity;
import com.chinessy.chinessy.activity.GuideActivity;
import com.chinessy.chinessy.activity.HistoryActivity;
import com.chinessy.chinessy.activity.PersonInfoActivity;
import com.chinessy.chinessy.activity.PromotionActivity;
import com.chinessy.chinessy.beans.BasicBean;
import com.chinessy.chinessy.beans.getMoneyInfo;
import com.chinessy.chinessy.clients.ConstValue;
import com.chinessy.chinessy.utils.LogUtils;
import com.google.gson.Gson;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.ThemeManager;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Activity mActivity;

    RelativeLayout mRlEditProfile;
    RelativeLayout mRlAddBalance;
    RelativeLayout mRlPromotion;
    RelativeLayout mRlTellFriends;
    RelativeLayout mRlEmailSupport;
    RelativeLayout mRlAbout;
    RelativeLayout mRlBindedMinutes;
    RelativeLayout mRlHistory;
    RelativeLayout mRlPersonalInfo;
    Button mBtnLogout;
    TextView mTvName;
    TextView mTvMoney;
    TextView mReferralCode;
    TextView mTvBalance;
    TextView mTvBindedMinutes;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyFragment newInstance(String param1, String param2) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    class EditProfileOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mActivity, PersonInfoActivity.class);
            mActivity.startActivity(intent);
        }
    }

    class AddBalanceOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mActivity, AddBalanceActivity.class);
            mActivity.startActivity(intent);
        }
    }

    class PromotionOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mActivity, PromotionActivity.class);
            mActivity.startActivity(intent);
        }
    }


    private class BindedMinutesOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getContext(), BindedTeacherListActivity.class));
        }
    }


    private class HistoryOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getContext(), HistoryActivity.class));

        }
    }

    class TellFriendsOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Chinessy.shareReferralCode(mActivity);
        }
    }

    class EmailSupportOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("plain/text");

            //调用系统发送邮件
            intent.putExtra(Intent.EXTRA_EMAIL, "help@chinessy.com");
            intent.putExtra(Intent.EXTRA_SUBJECT, "I need help using Chinessy");
            intent.putExtra(Intent.EXTRA_TEXT, "User Name:" + Chinessy.chinessy.getUser().getUserProfile().getName());
            mActivity.startActivity(intent);
        }
    }

    class AboutOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }

    class BtnLogoutClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            final SimpleDialog simpleDialog = new SimpleDialog(getContext());
            simpleDialog.title(R.string.Logout);
            simpleDialog.message(R.string.dialog_logout_message);
            simpleDialog.positiveAction(R.string.OK);
            simpleDialog.positiveActionClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    simpleDialog.cancel();
                    Intent intent = new Intent();
                    intent.setClass(mActivity, GuideActivity.class);
                    mActivity.startActivity(intent);
                    mActivity.finish();
                    Chinessy.chinessy.logout();
                }
            });
            simpleDialog.negativeAction(R.string.Cancle);
            simpleDialog.negativeActionClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    simpleDialog.cancel();
                }
            });
            simpleDialog.show();
        }
    }

    class RlPersonalInfoClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mActivity, PersonInfoActivity.class);
            mActivity.startActivity(intent);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        webRequest();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_me, container, false);
        mActivity = getActivity();

        mRlEditProfile = (RelativeLayout) rootView.findViewById(R.id.my_rl_editprofile);
        mRlAddBalance = (RelativeLayout) rootView.findViewById(R.id.my_rl_addbalance);
        mRlPromotion = (RelativeLayout) rootView.findViewById(R.id.my_rl_promotion);
        mRlTellFriends = (RelativeLayout) rootView.findViewById(R.id.my_rl_tellfriends);
        mRlEmailSupport = (RelativeLayout) rootView.findViewById(R.id.my_rl_emailsupport);
        mRlBindedMinutes = (RelativeLayout) rootView.findViewById(R.id.my_rl_binded_minutes);
        mRlHistory = (RelativeLayout) rootView.findViewById(R.id.my_rl_history);
        mRlAbout = (RelativeLayout) rootView.findViewById(R.id.my_rl_about);
        mTvName = (TextView) rootView.findViewById(R.id.my_tv_name);
        mTvMoney = (TextView) rootView.findViewById(R.id.my_tv_money);
        mTvBalance = (TextView) rootView.findViewById(R.id.my_tv_balance);
        mTvBindedMinutes = (TextView) rootView.findViewById(R.id.my_tv_binded_minutes);
        mRlPersonalInfo = (RelativeLayout) rootView.findViewById(R.id.my_rl_personalinfo);

        mReferralCode = (TextView) rootView.findViewById(R.id.my_tv_referralcode);

        mRlEditProfile.setOnClickListener(new EditProfileOnClickListener());
        mRlBindedMinutes.setOnClickListener(new BindedMinutesOnClickListener());
        mRlHistory.setOnClickListener(new HistoryOnClickListener());
        mRlAddBalance.setOnClickListener(new AddBalanceOnClickListener());
        mRlPromotion.setOnClickListener(new PromotionOnClickListener());
        mRlTellFriends.setOnClickListener(new TellFriendsOnClickListener());
        mRlEmailSupport.setOnClickListener(new EmailSupportOnClickListener());
        mRlAbout.setOnClickListener(new AboutOnClickListener());
        mRlPersonalInfo.setOnClickListener(new RlPersonalInfoClickListener());

        mBtnLogout = (Button) rootView.findViewById(R.id.my_btn_logout);
        mBtnLogout.setOnClickListener(new BtnLogoutClickListener());

        return rootView;
    }

    private void webRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.BasicUrl + ConstValue.getMoneyInfo,
                // StringRequest stringRequest = new StringRequest(Request.Method.GET, ConstValue.BasicUrl + "getPlayUrl"+"?roomId=002",
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        LogUtils.d(ConstValue.getMoneyInfo + " :-->" + response.toString());

                        BasicBean basicBean = new Gson().fromJson(response.toString(), BasicBean.class);
                        if ("true".equals(basicBean.getStatus().toString())) {
                            getMoneyInfo Beans = new Gson().fromJson(response.toString(), getMoneyInfo.class);
                            getMoneyInfo.DataBean dataBean = Beans.getData();
                            String beans = dataBean.getBeans();
                            String allTime = dataBean.getAllTime();
                            String allBindingTime = dataBean.getAllBindingTime();
                            mTvMoney.setText("Balance: " + allTime + " min & " + beans + " beans");
                            // 50beans,15mins remaining
                            mTvBalance.setText(beans + " beans," + allTime + " mins remaining");
                            mTvBindedMinutes.setText(allBindingTime + " mins");
                        }


                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.d(ConstValue.getMoneyInfo + " :error-->" + error.toString());
            }
        }) {
            @Override
            protected Map getParams() {
                //在这里设置需要post的参数
                Map map = new HashMap();
                map.put("userId", Chinessy.chinessy.getUser().getId());

                return map;
            }
        };

        Chinessy.requestQueue.add(stringRequest);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    void refreshContent() {
        String name = Chinessy.chinessy.getUser().getUserProfile().getName();
        name = name.equals("") ? Chinessy.chinessy.getUser().getEmail() : name;
        mTvName.setText(name);
        mTvMoney.setText("Balance: " + Chinessy.chinessy.getUser().getUserProfile().getBalance() + "min");
        mReferralCode.setText("Ref. Code:" + Chinessy.chinessy.getUser().getUserProfile().getReferralCode());
        mTvBalance.setText(Chinessy.chinessy.getUser().getUserProfile().getBalance() + "min remaining");
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MyFragment");
        refreshContent();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MyFragment");
    }

}
