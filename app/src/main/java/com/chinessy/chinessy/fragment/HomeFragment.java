package com.chinessy.chinessy.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chinessy.chinessy.AddBalanceActivity;
import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.TutorActivity;
import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.models.User;
import com.chinessy.chinessy.models.UserBalancePackage;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.rey.material.app.SimpleDialog;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Activity mActivity;

    Button mBtnPracticeNow;
    Button mBtnGetMoreMinutes;
    TextView mTvBalance;
    TextView mTvBalancePackage;

    ProgressDialog mPdPracticeNow;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mActivity = getActivity();

        mBtnPracticeNow = (Button)rootView.findViewById(R.id.home_btn_practicenow);
        mBtnGetMoreMinutes = (Button)rootView.findViewById(R.id.home_btn_getmoreminutes);
        mTvBalance = (TextView)rootView.findViewById(R.id.home_tv_balance);
        mTvBalancePackage = (TextView)rootView.findViewById(R.id.home_tv_balance_package);

        mBtnPracticeNow.setOnClickListener(new BtnPracticeNowClickListener());
        mBtnGetMoreMinutes.setOnClickListener(new BtnGetMoreMinutesClickListener());

//        refreshHomeBalance();

        return rootView;
    }

    public void refreshHomeBalance(){
        User user = Chinessy.chinessy.getUser();
        mTvBalance.setText(user.getUserProfile().getBalance() + "");
        UserBalancePackage userBalancePackage = Chinessy.chinessy.getUser().getUserBalancePackage();
        if(userBalancePackage.getEndAt() != null && userBalancePackage.getEndAt().getTime()!=0){
            SimpleDateFormat format = new SimpleDateFormat("MMM d,yyyy");
            mTvBalancePackage.setText(userBalancePackage.getMinutes() + "mins/day, Expired On " + format.format(userBalancePackage.getEndAt()));
            mTvBalancePackage.setVisibility(View.VISIBLE);
        }
    }

    class BtnPracticeNowClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            mPdPracticeNow = new ProgressDialog(mActivity);
            mPdPracticeNow.setMessage(getString(R.string.Picking_up_a_tutor_for_you));
            mPdPracticeNow.show();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("access_token", Chinessy.chinessy.getUser().getAccessToken());

                InternalClient.postInternalJson(mActivity, "teacher/random", jsonObject, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            switch (response.getInt("code")) {
                                case 10000:
                                    JSONObject data = response.getJSONObject("data");
                                    User tutor = User.generateTeacher(data);

                                    Intent intent = new Intent();
                                    intent.setClass(mActivity, TutorActivity.class);
                                    intent.putExtra("tutor", tutor);
                                    intent.putExtra("practice_now", true);
                                    mActivity.startActivity(intent);

                                    break;
                                case 20004:
                                    final SimpleDialog simpleDialog = new SimpleDialog(mActivity);
                                    simpleDialog.title(R.string.dialog_no_available_tutor_title);
                                    simpleDialog.message(R.string.dialog_no_available_tutor_message);
                                    simpleDialog.positiveAction(R.string.OK);
                                    simpleDialog.positiveActionClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            simpleDialog.cancel();
                                        }
                                    });
                                    simpleDialog.show();
                                    break;
                                default:
                                    break;
                            }
                            mPdPracticeNow.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class BtnGetMoreMinutesClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(mActivity, AddBalanceActivity.class);
            mActivity.startActivity(intent);
        }
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

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("HomeFragment");
        refreshHomeBalance();
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("HomeFragment");
    }
}
