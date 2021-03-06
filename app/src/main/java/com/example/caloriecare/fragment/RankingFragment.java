package com.example.caloriecare.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.caloriecare.DBrequest.getAllUserLogRequest;
import com.example.caloriecare.MainActivity;
import com.example.caloriecare.R;
import com.example.caloriecare.calendar.DayLog;
import com.example.caloriecare.ranking.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RankingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RankingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String userID;


    // Rank
    private RankListAdapter mAdapter;
    private RecyclerView recyclerView;
    private TextView noRankView;
    private List<HashMap<String, User>> MapList = new ArrayList<HashMap<String, User>>();
    RecyclerView.LayoutManager mLayout;

    public RankingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RankingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RankingFragment newInstance(String param1, String param2) {
        RankingFragment fragment = new RankingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userID = ((MainActivity)getActivity()).getUserID();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ranking, container, false);

        recyclerView = v.findViewById(R.id.recycle_view);
        noRankView = v.findViewById(R.id.empty);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {
                        JSONArray jsonArray = jsonObject.getJSONArray("users");
                        HashMap<String, User> users = new HashMap<>();

                        for(int i=0;i<jsonArray.length();i++){
                            //log ??? jsonArray ????????? ????????????
                            JSONObject temp = jsonArray.getJSONObject(i);

                            String userID = temp.getString("userID");                   // ?????? ????????? <- ????????? ?????? ????????? ???????????? ????????????
                            String userName = temp.getString("userName");               // ?????? ??????
                            String userEmail = temp.getString("userEmail");             // ?????? ?????????
                            String userBirth = temp.getString("userBirth");             // ?????? ??????
                            boolean userGender = temp.getBoolean("userGender");         // ?????? ??????
                            String userProfile = temp.getString("userProfile");         // ?????? ????????? ??????

                            users.put(userID, new User(userID,userName,userEmail,userBirth,userGender,userProfile));
                        }

                        jsonArray = jsonObject.getJSONArray("logs");
                        for(int i=0;i<jsonArray.length();i++){
                            //log ??? jsonArray ????????? ????????????
                            JSONObject temp = jsonArray.getJSONObject(i);

                            String userID = temp.getString("userID");
                            String logDate = temp.getString("logDate");         // ?????? ???????????? ??????
                            double intake = temp.getDouble("intake");           // ?????? ?????? ?????????
                            double burn = temp.getDouble("burn");               // ?????? ?????? ?????????
                            double dayCalorie = temp.getDouble("dayCalorie");   // ?????? ??? ?????? ?????????

                            users.get(userID).pushLog(new DayLog(logDate,intake,burn,dayCalorie));
                        }

                        MapList.add(users);

                        mAdapter = new RankListAdapter(getContext(),MapList);


                        // hash map??? ????????? ???????????? ????????? ??????
                        //
                        //
                        // ??? ???????????? ??? ???????????? ????????????, ????????? ?????????
                        // ???????????? ???????????? ????????? ????????????, ??????????????? ?????? ????????????
                        // ?????? ?????????, ????????? ?????????????????? ?????? ??? ??? ??????
                        //
                        // ?????? ??????????????? ??????  ????????????        ????????? ??????
                        //
                        //
                        //

                    } else {
                        Toast.makeText(getActivity(),jsonObject.toString(),Toast.LENGTH_LONG).show();
                        return;
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        };
        getAllUserLogRequest allUserLogRequestRequest = new getAllUserLogRequest("week", responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(allUserLogRequestRequest);


        setListView();

        return v;
    }

    private void setListView()
    {
        mLayout = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayout);
        recyclerView.setAdapter(mAdapter);
    }
}