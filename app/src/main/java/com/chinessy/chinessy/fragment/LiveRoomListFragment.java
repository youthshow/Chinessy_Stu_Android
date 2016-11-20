package com.chinessy.chinessy.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.adapter.LiveRoomListAdapter;
import com.chinessy.chinessy.beans.getStudioList;
import com.chinessy.chinessy.clients.ConstValue;
import com.chinessy.chinessy.rtmp.LiveRoomActivity;
import com.chinessy.chinessy.utils.LogUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LiveRoomListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveRoomListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LiveRoomListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LiveRoomListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LiveRoomListFragment newInstance(String param1, String param2) {
        LiveRoomListFragment fragment = new LiveRoomListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private RecyclerView mRv_roomlists;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_liveroomlist, container, false);

        mRv_roomlists = (RecyclerView) rootView.findViewById(R.id.rv_roomlists);
        mRv_roomlists.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRv_roomlists.setLayoutManager(layoutManager);
        webRequest();


        return rootView;
    }


    private void webRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.BasicUrl + ConstValue.getStudioList,
                // StringRequest stringRequest = new StringRequest(Request.Method.GET, ConstValue.BasicUrl + "getPlayUrl"+"?roomId=002",
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        LogUtils.d(ConstValue.getStudioList + " :-->" + response.toString());
                        getStudioList Beans = new Gson().fromJson(response.toString(), getStudioList.class);
                        if ("true".equals(Beans.getStatus().toString())) {
                            getStudioList.DataBean dataBean = Beans.getData();
                            final List<getStudioList.DataBean.StudioBean> studioBeen = dataBean.getStudio();


                            LiveRoomListAdapter mAdapter = new LiveRoomListAdapter(getContext(), studioBeen);
                            mAdapter.setOnItemClickListener(new LiveRoomListAdapter.ItemClickListener() {
                                @Override
                                public void onItemClick(View view, int position) {
                                    Intent intent = new Intent(getContext(), LiveRoomActivity.class);
                                    intent.putExtra("room_id", studioBeen.get(position).getRoom_id());
                                    intent.putExtra("user_id", studioBeen.get(position).getUser_id());
                                    startActivity(intent);
                                }
                            });
                            // specify an adapter (see also next example)
                            mRv_roomlists.setAdapter(mAdapter);

                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.d(ConstValue.getStudioList + " :error-->" + error.toString());
            }
        }) {
            /*
            @Override
            protected Map getParams() {
                //在这里设置需要post的参数
                Map map = new HashMap();
                map.put("roomId", "002");

                return map;
            }
            */
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
