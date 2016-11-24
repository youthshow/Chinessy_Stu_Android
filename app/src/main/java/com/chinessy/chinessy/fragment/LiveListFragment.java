package com.chinessy.chinessy.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chinessy.chinessy.Chinessy;
import com.chinessy.chinessy.R;
import com.chinessy.chinessy.adapter.LiveListAdapter;
import com.chinessy.chinessy.beans.BasicBean;
import com.chinessy.chinessy.beans.getStudioList;
import com.chinessy.chinessy.clients.ConstValue;
import com.chinessy.chinessy.rtmp.PlayerActivity;
import com.chinessy.chinessy.utils.LogUtils;
import com.google.gson.Gson;

import java.util.List;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LiveListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LiveListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LiveListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LiveListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LiveListFragment newInstance(String param1, String param2) {
        LiveListFragment fragment = new LiveListFragment();
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

    private LiveListAdapter mAdapter;
    private PtrFrameLayout mPtrFrameLayout;
    private ListView mLiveList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_live_list, container, false);
        mPtrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.tutors_pf_layout);
        // header
        final MaterialHeader header = new MaterialHeader(getContext());
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

        mLiveList = (ListView) view.findViewById(R.id.tutors_lv_list);


        mPtrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrameLayout.autoRefresh(false);
            }
        }, 100);
        return view;
    }
    class TutorsPtrHandler implements PtrHandler {
        @Override
        public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view1) {

            return !canChildScrollUp(mLiveList);
        }

        @Override
        public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
            webRequest();
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

    private void webRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstValue.BasicUrl + ConstValue.getStudioList,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        LogUtils.d(ConstValue.getStudioList + " :-->" + response.toString());

                        BasicBean basicBean = new Gson().fromJson(response.toString(), BasicBean.class);
                        if ("true".equals(basicBean.getStatus().toString())) {
                            getStudioList Beans = new Gson().fromJson(response.toString(), getStudioList.class);
                            getStudioList.DataBean dataBean = Beans.getData();
                            final List<getStudioList.DataBean.StudioBean> studioBeen = dataBean.getStudio();

                            if (mAdapter == null) {
                                mAdapter = new LiveListAdapter(getContext(), studioBeen);
                                mLiveList.setAdapter(mAdapter);
                            } else {
                                mAdapter.notifyDataSetChanged();
                            }
                            mPtrFrameLayout.refreshComplete();
                            mLiveList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                                    LogUtils.d(ConstValue.getPlayUrl + " :-->" + studioBeen.get(position).getPlay_url());
                                    Intent intent = new Intent();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("TITLE", "");
                                    bundle.putString("URI", "rtmp://live.hkstv.hk.lxdns.com/live/hks");
                                    bundle.putInt("decode_type", 1);
                                    intent.putExtras(bundle);
                                    intent.setClass(getContext(), PlayerActivity.class);
                                    startActivity(intent);
                                }
                            });


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
