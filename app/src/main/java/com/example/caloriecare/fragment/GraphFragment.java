package com.example.caloriecare.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.caloriecare.DBrequest.AtoBDaylogsRequest;
import com.example.caloriecare.DBrequest.MonthlogRequest;
import com.example.caloriecare.MainActivity;
import com.example.caloriecare.R;
import com.example.caloriecare.calendar.BodyData;
import com.example.caloriecare.calendar.DayLog;
import com.example.caloriecare.calendar.TextDecorator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String userID;
    private LineChart lineChart;

    private String getDay(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
    private String getToday(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        return getDay(date);
    }

    public GraphFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GraphFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GraphFragment newInstance(String param1, String param2) {
        GraphFragment fragment = new GraphFragment();
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
        View v = inflater.inflate(R.layout.fragment_graph, container, false);

        Button btnGraph = v.findViewById(R.id.button5);
        Button btnCalendar = v.findViewById(R.id.button6);

        btnCalendar.setBackgroundColor(Color.parseColor("#FFEB3B"));
        btnCalendar.setEnabled(true);
        btnGraph.setBackgroundColor(Color.WHITE);
        btnGraph.setEnabled(false);

        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CalendarFragment calendarFragment = new CalendarFragment();
                FragmentTransaction transaction = ((MainActivity)getActivity()).getfragmentManager().beginTransaction();
                transaction.replace(R.id.main_layout, calendarFragment).commitAllowingStateLoss();
            }
        });

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");

                    if (success) {
                        JSONArray jsonArray = jsonObject.getJSONArray("logs");
                        List<DayLog> daylogs = new ArrayList<>();

                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject temp = jsonArray.getJSONObject(i);

                            String logDate = temp.getString("logDate");     // ?????? ???????????? ??????
                            double intake = temp.getDouble("intake");       // ?????? ????????? ?????????
                            double burn = temp.getDouble("burn");           // ?????? ????????? ?????????
                            double dayCalorie = temp.getDouble("dayCalorie");// ?????? ??? ?????????

                            daylogs.add(new DayLog(logDate,intake,burn,dayCalorie));
                        }
                        
                        jsonArray = jsonObject.getJSONArray("bodyData");
                        List<BodyData> bodyData = new ArrayList<>();
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject temp = jsonArray.getJSONObject(i);

                            String logDate = temp.getString("logDate");     // ?????? ???????????? ??????
                            double height = temp.getDouble("height");       // ?????? ???
                            double weight = temp.getDouble("weight");       // ?????? ??????
                            double BMR = temp.getDouble("BMR");             // ?????? BMR (???????????????)

                            bodyData.add(new BodyData(logDate,height,weight,BMR));
                        }
                        
                        // daylogs ?????? ??????
                        // bodyData ?????? ???????????????
                        // log ??????????????? ?????? ????????? ??????


                        // ???, ???, ???, ????????????
                        // ?????????1
                        // ??? ?????????, ???????????????, ??????????????? ??? ?????????????????? (?????? ???????????? ?????? ???????????? ??????????????????)
                        //
                        // ?????????2
                        // ??? ?????? BMR ????????? ?????? ????????????
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
        AtoBDaylogsRequest atobdaylogsRequest = new AtoBDaylogsRequest(userID, "","", responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(atobdaylogsRequest);



        lineChart = v.findViewById(R.id.chart);

        //????????? ??????, db?????? ????????? ???????????? ????????? y??? ???????????????
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 1));
        entries.add(new Entry(2, 2));
        entries.add(new Entry(3, 0));
        entries.add(new Entry(4, 4));
        entries.add(new Entry(5, 3));
        entries.add(new Entry(6, 12));
        entries.add(new Entry(7, 11));
        entries.add(new Entry(8, 9));
        entries.add(new Entry(9, 7));
        entries.add(new Entry(10, 4));
        entries.add(new Entry(11, 8));
        entries.add(new Entry(12, 6));

        //?????? ????????? ?????? ??? ????????? ?????????
        LineDataSet lineDataSet = new LineDataSet(entries, "??? ?????? ?????????");
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(4);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setColor(Color.GREEN);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setValueTextSize(13);


        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);

        //x??? ?????????
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(12,true);

        //y??? ?????? ????????? ??? ??????
        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setDrawAxisLine(true);
        yLAxis.setDrawGridLines(false);
        yLAxis.setDrawLabels(false);

        //y??? ????????? ????????? ??? ??????
        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        //description ?????? ????????? ??? ??????
        Description description = new Description();
        description.setText("x : ???, y : ?????? ?????????");
        description.setTextSize(8);

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(description);
        lineChart.invalidate();

        return v;
    }
}