package com.ccss.youthvolunteer.activity;

import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.model.Theme;
import com.ccss.youthvolunteer.model.UserCategoryPoints;
import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.StackBarChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VolunteerLogChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VolunteerLogChartFragment extends Fragment {
    public static final String LINECHART_BACK_COLOR = "#eef1f6";
    public static final String LINECHART_LABEL_COLOR = "#FF8E9196";
    public static final int MAX_DATA_LIMIT = 12;
    private int mMonthlyGoal = 20;
    private static final int STACKED_CHART_INDEX = 1;
    private static final int LINE_CHART_INDEX = 2;

    private List<UserCategoryPoints> mUserPointsByCategory;
    private final String[] mChartMonthLabels = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
    private final float [][] mChartDataValues = {  {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 6.4f, 0f},
            {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 2.3f, 0f},
            {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0.4f, 0f}};

    private int mDoGoodCategoryIndicator;
    private int mGreenCategoryIndicator;
    private int mAdvocacyCategoryIndicator;
    private ImageButton mRefreshCharts;
    private boolean mUpdateCharts;

    /** Stacked chart */
    private StackBarChartView mStackedActivityChart;
    private TextView mLegendDoGood;
    private TextView mLegendGreen;
    private TextView mLegendAdvocacy;

    /** Line chart */
    private LineChartView mActivityLineChart;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment VolunteerLogChartFragment.
     */
    public static VolunteerLogChartFragment newInstance() {
        VolunteerLogChartFragment fragment = new VolunteerLogChartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public VolunteerLogChartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserCategoryPoints.findCurrentUserPointsInBackground(ParseUser.getCurrentUser(), MAX_DATA_LIMIT, new FindCallback<UserCategoryPoints>() {
            @Override
            public void done(List<UserCategoryPoints> objects, ParseException e) {
                if (e == null) {
                    createMissingDataPoints(objects);
                } else {
                    mUserPointsByCategory = Lists.newArrayList();
                }
            }
        });
    }

    private void createMissingDataPoints(List<UserCategoryPoints> objects) {
        mUserPointsByCategory = objects;

        if(mUserPointsByCategory.size() != MAX_DATA_LIMIT) {
            //Need to handle nulls
//            for(int ctr = 0; ctr >= 0; ctr--){
//                //add and reverse?
//                DateTimeFormatter format = DateTimeFormat.forPattern("MMM-yyyy");
//                DateTime instance = format.parseDateTime(mUserPointsByCategory.get(ctr).getActionMonthYear());
//                int monthNumber = instance.getMonthOfYear();
//            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.volunteer_log_chart_fragment, container, false);
        mRefreshCharts = (ImageButton) layout.findViewById(R.id.refresh_charts);
        mUpdateCharts = true;

        mDoGoodCategoryIndicator = getResources().getColor(R.color.do_good_action);
        mGreenCategoryIndicator = getResources().getColor(R.color.green_action);
        mAdvocacyCategoryIndicator = getResources().getColor(R.color.advocacy_action);

        mRefreshCharts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUpdateCharts)
                    updateCharts();
                else {
                    dismissCharts();
                }
                mUpdateCharts = !mUpdateCharts;
            }
        });

        // Init Stacked chart
        mStackedActivityChart = (StackBarChartView) layout.findViewById(R.id.stacked_category_log);
        mLegendDoGood = (TextView) layout.findViewById(R.id.cat_do_good);
        mLegendGreen = (TextView) layout.findViewById(R.id.cat_green);
        mLegendAdvocacy = (TextView) layout.findViewById(R.id.cat_advocacy);

        // Init Line chart
        mActivityLineChart = (LineChartView) layout.findViewById(R.id.activity_log_linechart);

        showCharts();

        return layout;
    }

    private void showCharts() {
        showChart(STACKED_CHART_INDEX, mStackedActivityChart);
        showChart(LINE_CHART_INDEX, mActivityLineChart);
    }

    private void showChart(int chartId, final ChartView chart) {
        dismissRefresh();
        Runnable action =  new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        showRefresh();
                    }
                }, 500);
            }
        };

        if(chartId == STACKED_CHART_INDEX){
            produceStackChart(chart, action);
        } else {
            produceLineChart(chart, action);
        }
    }

    /**
     * Update the values of a CardView chart
     */
    private void updateCharts(){
        dismissRefresh();
        updateChart(mStackedActivityChart);
        updateChart(mActivityLineChart);
    }

    /**
     * Dismiss a CardView chart
     */
    private void dismissCharts(){

        dismissRefresh();

        Runnable action =  new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        showRefresh();
                        showCharts();
                    }
                }, 500);
            }
        };

        dismissStackChart(action);
        dismissLineChart(action);
    }

    /**
     * Show CardView refresh button
     */
    private void showRefresh(){
        mRefreshCharts.setEnabled(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            mRefreshCharts.animate().alpha(1).scaleX(1).scaleY(1);
        else
            mRefreshCharts.setVisibility(View.VISIBLE);
    }


    /**
     * Dismiss CardView refresh button
     */
    private void dismissRefresh(){
        mRefreshCharts.setEnabled(false);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            mRefreshCharts.animate().alpha(0).scaleX(0).scaleY(0);
        else
            mRefreshCharts.setVisibility(View.INVISIBLE);
    }

    //region Stack chart
    private void produceStackChart(ChartView chart, Runnable action){
        StackBarChartView stackedChart = (StackBarChartView) chart;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            stackedChart.setOnEntryClickListener(new OnEntryClickListener() {
                @SuppressLint("NewApi")
                @Override
                public void onClick(int setIndex, int entryIndex, Rect rect) {
                    if(setIndex == 2)
                        mLegendDoGood.animate()
                                .scaleY(1.1f)
                                .scaleX(1.1f)
                                .setDuration(100)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        mLegendDoGood.animate()
                                                .scaleY(1.0f)
                                                .scaleX(1.0f)
                                                .setDuration(100);
                                    }
                                });
                    else if(setIndex == 1){
                        mLegendGreen.animate()
                                .scaleY(1.1f)
                                .scaleX(1.1f)
                                .setDuration(100)
                                .withEndAction(new Runnable(){
                                    @Override
                                    public void run() {
                                        mLegendGreen.animate()
                                                .scaleY(1.0f)
                                                .scaleX(1.0f)
                                                .setDuration(100);
                                    }
                                });
                    }else{
                        mLegendAdvocacy.animate()
                                .scaleY(1.1f)
                                .scaleX(1.1f)
                                .setDuration(100)
                                .withEndAction(new Runnable(){
                                    @Override
                                    public void run() {
                                        mLegendAdvocacy.animate()
                                                .scaleY(1.0f)
                                                .scaleX(1.0f)
                                                .setDuration(100);
                                    }
                                });
                    }
                }
            });

        Paint thresholdPaint = new Paint();
        thresholdPaint.setColor(Color.parseColor("#dad8d6"));
        thresholdPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        thresholdPaint.setStyle(Paint.Style.STROKE);
        thresholdPaint.setAntiAlias(true);
        thresholdPaint.setStrokeWidth(Tools.fromDpToPx(.75f));

        BarSet stackBarSet = new BarSet(mChartMonthLabels, mChartDataValues[0]);
        stackBarSet.setColor(mDoGoodCategoryIndicator); //Color.parseColor("#a1d949"));
        stackedChart.addData(stackBarSet);

        stackBarSet = new BarSet(mChartMonthLabels, mChartDataValues[1]);
        stackBarSet.setColor(mGreenCategoryIndicator); //Color.parseColor("#ffcc6a"));
        stackedChart.addData(stackBarSet);

        stackBarSet = new BarSet(mChartMonthLabels, mChartDataValues[2]);
        stackBarSet.setColor(mAdvocacyCategoryIndicator); //Color.parseColor("#ff7a57"));
        stackedChart.addData(stackBarSet);

        stackedChart.setBarSpacing(Tools.fromDpToPx(15));
        stackedChart.setRoundCorners(Tools.fromDpToPx(1));

        stackedChart.setXAxis(false)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                .setYAxis(false)
                .setYLabels(YController.LabelPosition.NONE)
                .setValueThreshold(mMonthlyGoal, mMonthlyGoal, thresholdPaint);

        int[] order = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        stackedChart.show(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(action))
        //.show()
        ;
    }

    private void updateChart(ChartView chart){
    //This should refresh the data values. Re-issue the query
        chart.updateValues(0, mChartDataValues[0]);
        chart.updateValues(1, mChartDataValues[1]);
        chart.updateValues(2, mChartDataValues[2]);
        chart.notifyDataUpdate();
    }

    private void dismissStackChart(Runnable action){
        int[] order = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        mStackedActivityChart.dismiss(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(action));
    }
    //endregion

    //region Line chart
    public void produceLineChart(ChartView chart, Runnable action){

        Tooltip tip = new Tooltip(getActivity(), R.layout.linechart_tooltip, R.id.value);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {

            tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f));

            tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA,0),
                    PropertyValuesHolder.ofFloat(View.SCALE_X,0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y,0f));
        }

        chart.setTooltips(tip);

        LineSet dataset = new LineSet(mChartMonthLabels, mChartDataValues[0]);
        dataset.setColor(mDoGoodCategoryIndicator)
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(mDoGoodCategoryIndicator)
                .setDotsColor(Color.parseColor(LINECHART_BACK_COLOR));
        chart.addData(dataset);

        dataset = new LineSet(mChartMonthLabels, mChartDataValues[1]);
        dataset.setColor(mGreenCategoryIndicator)
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(mGreenCategoryIndicator)
                .setDotsColor(Color.parseColor(LINECHART_BACK_COLOR));
        chart.addData(dataset);

        dataset = new LineSet(mChartMonthLabels, mChartDataValues[2]);
        dataset.setColor(mAdvocacyCategoryIndicator)
                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsStrokeColor(mAdvocacyCategoryIndicator)
                .setDotsColor(Color.parseColor(LINECHART_BACK_COLOR));
        chart.addData(dataset);

        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#308E9196"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(1f));

        chart.setBorderSpacing(1)
                .setAxisBorderValues(0, 10, 2)
                .setXLabels(AxisController.LabelPosition.OUTSIDE)
                .setYLabels(AxisController.LabelPosition.OUTSIDE)
                .setLabelsColor(Color.parseColor(LINECHART_LABEL_COLOR))
                .setXAxis(false)
                .setYAxis(false)
                .setBorderSpacing(Tools.fromDpToPx(5))
                .setGrid(ChartView.GridType.VERTICAL, gridPaint);

        Animation anim = new Animation().setEndAction(action);

        chart.show(anim);
    }

    public void dismissLineChart(Runnable action){

        mActivityLineChart.dismissAllTooltips();
        mActivityLineChart.dismiss(new Animation().setEndAction(action));
    }
    //endregion
}
