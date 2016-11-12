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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.activity.TutorActivity;
import com.chinessy.chinessy.adapter.ReservationListAdapter;
import com.chinessy.chinessy.clients.InternalClient;
import com.chinessy.chinessy.handlers.SimpleJsonHttpResponseHandler;
import com.chinessy.chinessy.models.Reservation;
import com.chinessy.chinessy.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReservationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReservationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReservationFragment extends Fragment {
    final String tag = "ReservationFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static boolean isNeed2Refresh = false;

    final int HANDLER_AFTER_UPCOMING_LOADING = 100;
    final int HANDLER_AFTER_HISTORY_LOADING = 101;
    final int HANDLER_AFTER_CANCEL_RESERVATION = 102;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    PtrFrameLayout mPtrFrameLayout;

    ListView mLvUpcoming;

    RelativeLayout mRlNoReservation;

    ReservationListAdapter mLaUpcoming;

    boolean ifUpcomingLoadingFinished = false;
    boolean ifHistoryLoadingFinished = false;

//    ProgressView mPvLoading = null;

    Handler mHandler = new ReservationHandler();

    Activity mActivity;

    int mCancelId = -1;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReservationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReservationFragment newInstance(String param1, String param2) {
        ReservationFragment fragment = new ReservationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ReservationFragment() {
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

        View rootView = inflater.inflate(R.layout.fragment_reservation, container, false);

        mPtrFrameLayout = (PtrFrameLayout)rootView.findViewById(R.id.tutors_pf_layout);
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
        mPtrFrameLayout.setPtrHandler(new ReservationPtrHandler());
        mPtrFrameLayout.setPinContent(true);

        mLvUpcoming = (ListView)rootView.findViewById(R.id.reservation_lv_upcoming);
        mRlNoReservation = (RelativeLayout)rootView.findViewById(R.id.reservation_rl_noreservation);

        mLaUpcoming = new ReservationListAdapter(mActivity);
        mLvUpcoming.setAdapter(mLaUpcoming);

        mLvUpcoming.setOnItemLongClickListener(new LvUpcomingOnLongClickListener());
        mLvUpcoming.setOnItemClickListener(new LvUpcomingOnItemClickListener());

        refreshPage();

        registerForContextMenu(mLvUpcoming);
        return rootView;
    }

    class LvUpcomingOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Reservation reservation = mLaUpcoming.getReservationList().get(position);
            if(reservation.getListTag().equals("header_upcoming") || reservation.getListTag().equals("header_history")){
                return;
            }

            User tutor = reservation.getTeacher();
            Intent intent = new Intent();
            intent.setClass(mActivity, TutorActivity.class);
            intent.putExtra("tutor", tutor);
            mActivity.startActivity(intent);
        }
    }

    void refreshPage(){
        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
    }

    class ReservationPtrHandler implements PtrHandler {
        @Override
        public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view1) {
            return !canChildScrollUp(mLvUpcoming);
        }

        @Override
        public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
            fillTutorList();
        }

        public boolean canChildScrollUp(View view) {
            if(Build.VERSION.SDK_INT < 14) {
                if(!(view instanceof AbsListView)) {
                    return view.getScrollY() > 0;
                } else {
                    AbsListView absListView = (AbsListView)view;
                    return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
                }
            } else {
                return view.canScrollVertically(-1);
            }
        }
    }

    class LvUpcomingOnLongClickListener implements AdapterView.OnItemLongClickListener{
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            Reservation reservation = mLaUpcoming.getReservationList().get(position);
            if(!reservation.getListTag().equals("upcoming")){
                return true;
            }
            setCancelId(position);
            return false;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 1:
//                Toast.makeText(mActivity, item.getItemId()+"", Toast.LENGTH_SHORT).show();
                final int cancelId = getCancelId();
                if(cancelId == -1){
                    break;
                }

//                showProgress();

                Reservation reservation = mLaUpcoming.getReservationList().get(cancelId);

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
                    jsonObject.put("reservation_id", reservation.getId());
                    InternalClient.postInternalJson(mActivity, "reservation/cancel", jsonObject, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            try {
                                switch (response.getInt("code")) {
                                    case 10000:
                                        Message message = mHandler.obtainMessage();
                                        message.what = HANDLER_AFTER_CANCEL_RESERVATION;
                                        message.arg1 = cancelId;
                                        message.sendToTarget();
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
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0, 1, Menu.NONE, R.string.Cancel_Reservation);
    }

    void fillTutorList(){
        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("access_token", Chinessy.chinessy.getUser().getAccessToken());
            jsonParams.put("from_index", 0);
            jsonParams.put("to_index", 20);
            InternalClient.postInternalJson(mActivity, "reservation/find_students_history", jsonParams, new SimpleJsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        switch (response.getInt("code")) {
                            case 10000:
//                                Log.w(tag, response.toString());
                                JSONArray results = response.getJSONObject("data").getJSONArray("results");

                                ArrayList<Reservation> reservationList = Reservation.loadReservationFromJsonArray(results);

                                mLaUpcoming.setHistoryReservationList(reservationList, true);

                                Message message = mHandler.obtainMessage(HANDLER_AFTER_HISTORY_LOADING);
                                message.sendToTarget();
                                break;
                            default:
                                Log.w(tag, response.toString());
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

//    void checkProgress(){
//        if(mPvLoading == null){
//            mPvLoading = (ProgressView)mActivity.findViewById(R.id.reservation_pv_loading);
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    class ReservationHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_AFTER_HISTORY_LOADING:
                    mLaUpcoming.notifyDataSetChanged();

                    ifHistoryLoadingFinished = true;
                    if(ifHistoryLoadingFinished){
                        mPtrFrameLayout.refreshComplete();
                        ifHistoryLoadingFinished = false;
                    }
                    break;
                case HANDLER_AFTER_CANCEL_RESERVATION:
                    int position = msg.arg1;
                    mLaUpcoming.getReservationList().remove(position);
                    mLaUpcoming.getmViewList().remove(position);
                    mLaUpcoming.notifyDataSetChanged();

//                    hideProgress();
                    break;
            }
            if(mLaUpcoming.getReservationList().size()==0){
                mRlNoReservation.setVisibility(View.VISIBLE);
            }else{
                mRlNoReservation.setVisibility(View.GONE);
            }

        }
    }

    public int getCancelId() {
        int cancelId = mCancelId;
        mCancelId = -1;
        return cancelId;
    }

    public void setCancelId(int mCancelId) {
        this.mCancelId = mCancelId;
    }

    public static boolean isIsNeed2Refresh(){
        boolean bool = isNeed2Refresh;
        ReservationFragment.isNeed2Refresh = false;
        return bool;
    }

    public static void setIsNeed2Refresh(boolean isNeed2Refresh) {
        ReservationFragment.isNeed2Refresh = isNeed2Refresh;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ReservationFragment");

        if(isIsNeed2Refresh()){
           refreshPage();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ReservationFragment");
    }
}
