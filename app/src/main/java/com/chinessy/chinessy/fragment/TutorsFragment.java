package com.chinessy.chinessy.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.Config;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.activity.TutorActivity;
import com.chinessy.chinessy.adapter.TutorListAdapter;
import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.handlers.SimpleJsonHttpResponseHandler;
import com.chinessy.chinessy.models.User;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TutorsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TutorsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TutorsFragment extends Fragment {
    final String tag = "TutorsFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    final int HANDLER_AFTER_LOADING = 100;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    PtrFrameLayout mPtrFrameLayout;

    ListView mLvTutor;

    TutorListAdapter mLaTutor;

//    ProgressView mPvLoading = null;

    Handler mHandler = new TutorsHandler();

    Activity mActivity;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TutorsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TutorsFragment newInstance(String param1, String param2) {
        TutorsFragment fragment = new TutorsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TutorsFragment() {
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
        mActivity = getActivity();

        View rootView = inflater.inflate(R.layout.fragment_tutors, container, false);

        mPtrFrameLayout = (PtrFrameLayout) rootView.findViewById(R.id.tutors_pf_layout);
        // header
        final MaterialHeader header = new MaterialHeader(mActivity);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, PtrLocalDisplay.dp2px(15), 0, PtrLocalDisplay.dp2px(10));
        header.setPtrFrameLayout(mPtrFrameLayout);
        mPtrFrameLayout.setLoadingMinTime(1000);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.setPtrHandler(new TutorsPtrHandler());
        mPtrFrameLayout.setPinContent(true);

//        mPvLoading = (ProgressView)rootView.findViewById(R.id.tutors_pv_loading);
        mLvTutor = (ListView) rootView.findViewById(R.id.tutors_lv_list);

        mLaTutor = new TutorListAdapter(mActivity);

        mLvTutor.setAdapter(mLaTutor);

        mLvTutor.setOnItemClickListener(new LvTutorItemClickListener());

//        fillTutorList();
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
        return rootView;
    }

    class TutorsPtrHandler implements PtrHandler {
        @Override
        public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view1) {

            return !canChildScrollUp(mLvTutor);
        }

        @Override
        public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
            fillTutorList();
        }

        public boolean canChildScrollUp(View view) {
            if (Build.VERSION.SDK_INT < 14) {
                if (!(view instanceof AbsListView)) {
                    return view.getScrollY() > 0;
                } else {
                    AbsListView absListView = (AbsListView) view;
                    return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
                }
            } else {
                return view.canScrollVertically(-1);
            }
        }
    }

    void fillTutorList() {
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
            InternalClient.postInternalJson(mActivity, "teacher/find", jsonParams, new SimpleJsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                    super.onSuccess(statusCode, headers, response);

                    Log.w(tag, response.toString()+"teacherteacherteacherteacher");
                    try {
                        switch (response.getInt("code")) {
                            case 10000:
                                Log.w(tag, response.toString());
                                JSONObject data = response.getJSONObject("data");
                                JSONArray availableArray = data.getJSONArray("available");
                                JSONArray busyArray = data.getJSONArray("busy");
                                JSONArray offlineArray = data.getJSONArray("offline");

                                ArrayList<User> availableList = User.loadTeachersFromJsonArray(availableArray);
                                ArrayList<User> busyList = User.loadTeachersFromJsonArray(busyArray);
                                ArrayList<User> offlineList = User.loadTeachersFromJsonArray(offlineArray);

                                mLaTutor.setTutorList(availableList, busyList, offlineList);

                                Message message = mHandler.obtainMessage(HANDLER_AFTER_LOADING);
                                message.sendToTarget();
                                break;
                            default:
                                Log.w(tag, response.toString());
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    void checkProgress(){
//        if(mPvLoading == null){
//            mPvLoading = (ProgressView)mActivity.findViewById(R.id.tutors_pv_loading);
//        }
//    }
//    public void showProgress(){
//        checkProgress();
//        mPvLoading.setVisibility(View.VISIBLE);
//    }
//    public void hideProgress(){
//        checkProgress();
//        mPvLoading.setVisibility(View.INVISIBLE);
//    }

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

        fillTutorList();
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

    class TutorsHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_AFTER_LOADING:
                    mLaTutor.notifyDataSetChanged();

                    mPtrFrameLayout.refreshComplete();
                    break;
            }
        }
    }

    class LvTutorItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            User tutor = mLaTutor.getTutorList().get(position);
            if (!tutor.getListTag().equals("")) {
                return;
            }

            Intent intent = new Intent();
            intent.setClass(mActivity, TutorActivity.class);
            intent.putExtra("tutor", tutor);
            intent.putExtra("position", position);
            TutorsFragment.this.startActivityForResult(intent, Config.RC_MAIN_TO_TUTOR);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Config.RC_MAIN_TO_TUTOR:
                if (resultCode == Activity.RESULT_OK) {
                    int position = data.getIntExtra("position", -1);
                    if (position != -1) {
                        mLaTutor.onFavouritesChanged(data, position);
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        fillTutorList();
        MobclickAgent.onPageStart("TutorsFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("TutorsFragment");
    }
}
