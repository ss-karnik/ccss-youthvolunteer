package com.ccss.youthvolunteer.activity;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.model.UserCategoryPoints;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.ccss.youthvolunteer.util.CheckNetworkConnection;
import com.ccss.youthvolunteer.util.Constants;
import com.ccss.youthvolunteer.util.DateUtils;
import com.ccss.youthvolunteer.util.CommonUtils;
import com.ccss.youthvolunteer.util.TaskScheduler;
import com.db.chart.Tools;
import com.db.chart.listener.OnEntryClickListener;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.view.ChartView;
import com.db.chart.view.HorizontalBarChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.XController;
import com.db.chart.view.animation.Animation;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.DecoDrawEffect;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private VolunteerUser mCurrentUser;

    private DecoView mDecoViewChart;
    private float mSeriesMax = 20f; //Maximum value for each data series. This can be different for each data series
    private int mBackIndex;
    private int mSeries1Index;
    private float mSeries1Value;
    private int mSeries2Index;
    private float mSeries2Value;
    private int mSeries3Index;
    private float mSeries3Value;

    private HorizontalBarChartView mSgCompareChart;
    private final String[] mLabelsSgChart = {"You", "SG (avg.)"};
    private final float[] mValuesSgChart = {9.06f, 4.16f};
    private TextView mTextViewTwo;
    private TextView mTextViewMetricTwo;
    private TextView mDataAsOfText;

    private String mUserOrganization;
    private static final long REFRESH_DATA_INTERVAL = 3000;
    private static final float DEFAULT_MONTHLY_GOAL = 20f;

    private List<String> mRotatingTextItems = Lists.newArrayList();
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCurrentUser = VolunteerUser.getCurrentUserInformationFromLocalStore();

        //Profile is complete.. set up the Main screen
        setContentView(R.layout.activity_main);
        CoordinatorLayout mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator);

        mDataAsOfText = (TextView) findViewById(R.id.data_as_of);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(Constants.INTENT_SENDER) && "intro".equals(bundle.getString(Constants.INTENT_SENDER))) {
            showToastLong(getResources().getText(R.string.msg_welcome) + " " + mCurrentUser.getFullName());
        }

        loadPrefData();

        FloatingActionButton fabSearch = (FloatingActionButton) findViewById(R.id.fab_main_search);
        FloatingActionButton fabAchievement = (FloatingActionButton) findViewById(R.id.fab_main_achievement);
        FloatingActionButton fabLogActivity = (FloatingActionButton) findViewById(R.id.fab_main_log);

        if(bundle != null && bundle.containsKey(Constants.INTERNET_AVAILABLE) && bundle.getBoolean(Constants.INTERNET_AVAILABLE)
                || CheckNetworkConnection.isInternetAvailable(this)){
            initializeNavigationDrawer(mCurrentUser, toolbar);
        } else {
            fabSearch.setVisibility(View.INVISIBLE);
            fabLogActivity.setVisibility(View.INVISIBLE);
        }


        final float monthlyGoal = mCurrentUser.getMonthlyGoal() == 0 ? DEFAULT_MONTHLY_GOAL : mCurrentUser.getMonthlyGoal();
        final List<UserCategoryStats> userCategoryStatsForCurrentMonth = Lists.newArrayList();
        UserCategoryPoints.findCurrentUsersPointsForMonthYearInBackground(mCurrentUser, null, true, new FindCallback<UserCategoryPoints>() {
            @Override
            public void done(List<UserCategoryPoints> list, ParseException e) {
                if (e == null) {
                    for (UserCategoryPoints categoryItem : list) {
                        userCategoryStatsForCurrentMonth.add(
                                new UserCategoryStats(categoryItem.getActionCategory().getCategoryName(),
                                        categoryItem.getActionCategory().getCategoryColor(),
                                        DateTime.now().toString(Constants.MONTH_YEAR_FORMAT),
                                        DateUtils.minutesAsHours(categoryItem.getCategoryMinutes()), categoryItem.getCategoryPoints()));
                    }
                }

                plotUserStats(userCategoryStatsForCurrentMonth, monthlyGoal);
            }
        });


        fabAchievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(VolunteerLogActivity.class);
            }
        });

        fabLogActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(LogHoursActivity.class);
            }
        });

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(VolunteerOpportunityActivity.class);
            }
        });

    }

    private void loadPrefData() {
        SharedPreferences prefs = this.getSharedPreferences(Constants.PREF_FILE_NAME, MODE_PRIVATE);
        String announcements = prefs.getString(Constants.ANNOUNCEMENTS_KEY, null);
        if (announcements != null) {
            Collections.addAll(mRotatingTextItems, announcements.split("\\|"));
            mRotatingTextItems.add(prefs.getString(Constants.SG_STATS_KEY, null));
            mRotatingTextItems.add(prefs.getString(Constants.POINTS_RANK_KEY, null));
            mRotatingTextItems.add(prefs.getString(Constants.UPCOMING_KEY, null));
            mDataAsOfText.setText(String.format(getString(R.string.main_data_as_of),
                                                prefs.getString(Constants.STATS_LAST_UPDATE_DATE, null)));
        }

        final TextView rotatingBanner = (TextView) findViewById(R.id.main_rotating);
        TaskScheduler timer = new TaskScheduler();
        timer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (!mRotatingTextItems.isEmpty()) {
                    rotatingBanner.setText(mRotatingTextItems.get(index++));
                    if (index == mRotatingTextItems.size())
                        index = 0;
                }
            }
        }, REFRESH_DATA_INTERVAL);
    }

    private void plotUserStats(List<UserCategoryStats> userCategoryStatsForCurrentMonth, float monthlyGoal) {
        TextView monthStats = (TextView) findViewById(R.id.main_month_points);
        double achievedHours = 0;
        Map<String, Double> userCategoryHours = Maps.newHashMap();
        for (UserCategoryStats item : userCategoryStatsForCurrentMonth) {
            achievedHours += item.getHours();
            userCategoryHours.put(item.getCategory() + "|" + item.getCategoryColor(), item.getHours());
        }

        monthStats.setText(String.format("Monthly target: %s hrs. Achieved: %.2f hrs.", monthlyGoal, achievedHours));

        mDecoViewChart = (DecoView) findViewById(R.id.dynamicArcView);

        //User points for month by category
        mSeriesMax = monthlyGoal;

        mSeries1Value = 6.4f;
        mSeries2Value = 2.3f;
        mSeries3Value = 0.4f;
        createBackSeries();

        //Load CategoryData from the settings
        createDoGoodCategoryData();
        createGreenCategoryData();
        createAdvocacyCategoryData();
        // Setup events to be fired on a schedule
        createEvents();


//            mSgCompareChart = (HorizontalBarChartView) findViewById(R.id.barchartSgCompare);
//            mTextViewTwo = (TextView) findViewById(R.id.barchart_value);
//            mTextViewMetricTwo = (TextView) findViewById(R.id.barchart_metric);
//
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
//                mTextViewTwo.setAlpha(0);
//                mTextViewMetricTwo.setAlpha(0);
//            }else{
//                mTextViewTwo.setVisibility(View.INVISIBLE);
//                mTextViewMetricTwo.setVisibility(View.INVISIBLE);
//            }
//
//            mTextViewTwo.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
//                        mTextViewTwo.animate().alpha(0).setDuration(100);
//                        mTextViewMetricTwo.animate().alpha(0).setDuration(100);
//                    }
//                }
//            });

        showSgCompareChart();
    }

    private void initializeNavigationDrawer(VolunteerUser currentUser, Toolbar mToolbar) {
        if( !CheckNetworkConnection.isInternetAvailable(this)){
            return;
        }
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        View navDrawerHeader = LayoutInflater.from(this).inflate(R.layout.nav_drawer_header, null, true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.main_nav_view);
        navigationView.addHeaderView(navDrawerHeader);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        final ParseImageView navProfileImage = (ParseImageView) navDrawerHeader.findViewById(R.id.nav_user_image);
        ParseFile imageFile = currentUser.getProfileImage();
        if (imageFile != null) {
            navProfileImage.setParseFile(imageFile);
            navProfileImage.loadInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, com.parse.ParseException e) {
                }
            });
        } else {
            navProfileImage.setImageResource(R.drawable.default_avatar_small_64);
        }
        navProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ProfileActivity.class);
            }
        });

        ((TextView) navDrawerHeader.findViewById(R.id.nav_user_fullname)).setText(currentUser.getFullName());
        ((TextView) navDrawerHeader.findViewById(R.id.nav_user_email)).setText(currentUser.getEmail());

        String specialRole = currentUser.getSpecialRole();
        if (!Strings.isNullOrEmpty(specialRole)) {
            if (Constants.ADMIN_ROLE.equalsIgnoreCase(specialRole) || Constants.MODERATOR_ROLE.equalsIgnoreCase(specialRole)) {
                //Make the Admin options visible
                navigationView.getMenu().setGroupVisible(R.id.admin_manage, true);
                //Can manage volunteer actions across organizations.
                mUserOrganization = "";
            }
            if (Constants.ORGANIZER_ROLE.equalsIgnoreCase(specialRole)) {
                navigationView.getMenu().setGroupVisible(R.id.organizer_manage, true);
                //Can manage volunteer actions only for his/her organization.
                mUserOrganization = currentUser.getOrganizationName();
            }
        }
    }

    //region Compare chart
    private void showSgCompareChart() {
        Runnable action = new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                    }
                }, 500);
            }
        };

        //generateCompareChart(mSgCompareChart, action);
    }

    public void generateCompareChart(ChartView chart, Runnable action) {
        HorizontalBarChartView horChart = (HorizontalBarChartView) chart;

        Tooltip tip = new Tooltip(this, R.layout.barchart_tooltip);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            tip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1));
            tip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0));
        }

        horChart.setTooltips(tip);


        horChart.setOnEntryClickListener(new OnEntryClickListener() {
            @Override
            public void onClick(int setIndex, int entryIndex, Rect rect) {
                mTextViewTwo.setText(Integer.toString((int) mValuesSgChart[entryIndex]));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    mTextViewTwo.animate().alpha(1).setDuration(200);
                    mTextViewMetricTwo.animate().alpha(1).setDuration(200);
                } else {
                    mTextViewTwo.setVisibility(View.VISIBLE);
                    mTextViewMetricTwo.setVisibility(View.VISIBLE);
                }
            }
        });

        horChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                    mTextViewTwo.animate().alpha(0).setDuration(100);
                    mTextViewMetricTwo.animate().alpha(0).setDuration(100);
                } else {
                    mTextViewTwo.setVisibility(View.INVISIBLE);
                    mTextViewMetricTwo.setVisibility(View.INVISIBLE);
                }
            }
        });


        BarSet barSet = new BarSet();
        Bar bar;
        for (int i = 0; i < mLabelsSgChart.length; i++) {
            bar = new Bar(mLabelsSgChart[i], mValuesSgChart[i]);
            if (i == mLabelsSgChart.length - 1)
                bar.setColor(Color.parseColor("#b26657"));
            else if (i == 0)
                bar.setColor(Color.parseColor("#998d6e"));
            else
                bar.setColor(Color.parseColor("#506a6e"));
            barSet.addBar(bar);
        }
        horChart.addData(barSet);
        horChart.setBarSpacing(Tools.fromDpToPx(5));

        horChart.setBorderSpacing(0)
                .setAxisBorderValues(0, 100, 25)
                .setXAxis(false)
                .setYAxis(false)
                .setLabelsColor(Color.parseColor("#FF8E8A84"))
                .setXLabels(XController.LabelPosition.NONE);

        int[] order = {4, 3, 2, 1, 0};
        horChart.show(new Animation()
                .setOverlap(.5f, order)
                .setEndAction(action));
    }
    //endregion

    //region Deco Chart
    private void createBackSeries() {
        SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(true)
                .build();

        mBackIndex = mDecoViewChart.addSeries(seriesItem);
    }

    private void createDoGoodCategoryData() {
        final SeriesItem seriesItem = new SeriesItem.Builder(getResources().getColor(R.color.do_good_action))
                .setRange(0, mSeriesMax, 0).setInitialVisibility(false).build();

        final TextView textPercentage = (TextView) findViewById(R.id.textPercentage);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                float percentFilled = ((currentPosition - seriesItem.getMinValue()) / (seriesItem.getMaxValue() - seriesItem.getMinValue()));
                textPercentage.setText(String.format("%.0f%%", percentFilled * 100f));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        final TextView textToGo = (TextView) findViewById(R.id.textRemaining);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                textToGo.setText(String.format("%.2f hrs to goal", seriesItem.getMaxValue() - currentPosition));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        //http://stackoverflow.com/questions/6583019/dynamic-textview-in-relative-layout
        final TextView txtDoGood = (TextView) findViewById(R.id.cat_do_good);
        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                txtDoGood.setText(String.format("%.2f hrs", currentPosition));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mSeries1Index = mDecoViewChart.addSeries(seriesItem);
    }

    private void createGreenCategoryData() {
        final SeriesItem seriesItem = new SeriesItem.Builder(getResources().getColor(R.color.green_action))
                .setRange(0, mSeriesMax, 0).setInitialVisibility(false).build();

        final TextView txtGreen = (TextView) findViewById(R.id.cat_green);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                txtGreen.setText(String.format("%.2f hrs", currentPosition));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mSeries2Index = mDecoViewChart.addSeries(seriesItem);
    }

    private void createAdvocacyCategoryData() {
        final SeriesItem seriesItem = new SeriesItem.Builder(getResources().getColor(R.color.advocacy_action))
                .setRange(0, mSeriesMax, 0).setInitialVisibility(false).build();

        final TextView txtAdvocacy = (TextView) findViewById(R.id.cat_advocacy);

        seriesItem.addArcSeriesItemListener(new SeriesItem.SeriesItemListener() {
            @Override
            public void onSeriesItemAnimationProgress(float percentComplete, float currentPosition) {
                txtAdvocacy.setText(String.format("%.2f hrs", currentPosition));
            }

            @Override
            public void onSeriesItemDisplayProgress(float percentComplete) {

            }
        });

        mSeries3Index = mDecoViewChart.addSeries(seriesItem);
    }

    private void createEvents() {
        mDecoViewChart.executeReset();

        mDecoViewChart.addEvent(new DecoEvent.Builder(mSeriesMax)
                .setIndex(mBackIndex).setDuration(3000).setDelay(100).build());

        mDecoViewChart.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(mSeries1Index).setDuration(2000).setDelay(1250).build());

        mDecoViewChart.addEvent(new DecoEvent.Builder(mSeries1Value)
                .setIndex(mSeries1Index).setDelay(3250).build());

        mDecoViewChart.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(mSeries2Index).setDuration(1000).setEffectRotations(1).setDelay(7000).build());

        mDecoViewChart.addEvent(new DecoEvent.Builder(mSeries2Value)
                .setIndex(mSeries2Index).setDelay(8500).build());

        mDecoViewChart.addEvent(new DecoEvent.Builder(DecoDrawEffect.EffectType.EFFECT_SPIRAL_OUT)
                .setIndex(mSeries3Index).setDuration(1000).setEffectRotations(1).setDelay(12500).build());

        mDecoViewChart.addEvent(new DecoEvent.Builder(mSeries3Value)
                .setIndex(mSeries3Index).setDelay(14000).build());
    }
    //endregion

    private void refreshStatsData() {
        //Overall with %s points and %s hours you are ranked %s out of %s volunteers!
        final int[] userPoints = {0};
        final int[] userHours = {0};
        int userRank = 0;

        int totalUsers = VolunteerUser.getTotalUserCount();
        try {
            UserCategoryPoints.unpinAll(Constants.CURRENT_USER_POINTS);

            List<VolunteerUser> rankedUsers = VolunteerUser.findUsersRanked();
            userRank = rankedUsers.indexOf(mCurrentUser);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String totalUserHours = DateUtils.minutesToHoursRepresentation(CommonUtils.sum(UserCategoryPoints.findMinutesForAllUsers(), 0));
        UserCategoryPoints.findCurrentUserPointsInBackground(mCurrentUser, 0, new FindCallback<UserCategoryPoints>() {
            @Override
            public void done(List<UserCategoryPoints> list, ParseException e) {
                if (e == null) {
                    UserCategoryPoints.pinAllInBackground(Constants.CURRENT_USER_POINTS, list);
                    for (UserCategoryPoints item : list) {
                        userPoints[0] += item.getCategoryPoints();
                        userHours[0] += item.getCategoryMinutes();
                    }
                } else {
                    e.getMessage();
                }
            }
        });

        SharedPreferences sharedPref = this.getSharedPreferences(Constants.PREF_FILE_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.POINTS_RANK_KEY, String.format(getString(R.string.main_points_rank),
                userPoints[0], userHours[0], userRank, totalUsers));
        //Our %s volunteers have put in %s hours!
        editor.putString(Constants.SG_STATS_KEY, String.format(getString(R.string.total_sg_users_hours), totalUsers, totalUserHours));
        editor.putString(Constants.STATS_LAST_UPDATE_DATE, DateUtils.formattedDateString(new LocalDate().toDate()).toString());
        editor.commit();

        loadPrefData();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_main_refresh) {
            refreshStatsData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_view_volunteer:
                startManageOpportunityActivity("", Constants.READ_MODE);
                break;

            case R.id.nav_view_ranking:
                startActivity(RankingActivity.class);
                break;

            case R.id.nav_view_log:
                startActivity(VolunteerLogActivity.class);
                break;

            case R.id.nav_edit_profile:
                startActivity(ProfileActivity.class);
                break;

            case R.id.nav_about:
                startActivity(AboutActivity.class);
                break;

            //Admin
            case R.id.nav_manage_announcement:
                startManageResourceActivity(Constants.ANNOUNCEMENT_RESOURCE, mUserOrganization);
                break;

            case R.id.nav_manage_category:
                startManageResourceActivity(Constants.CATEGORY_RESOURCE, mUserOrganization);
                break;

            case R.id.nav_manage_interests:
                startManageResourceActivity(Constants.INTEREST_RESOURCE, mUserOrganization);
                break;

            case R.id.nav_manage_skills:
                startManageResourceActivity(Constants.SKILL_RESOURCE, mUserOrganization);
                break;

            case R.id.nav_manage_spl_user:
                startManageResourceActivity(Constants.USER_RESOURCE, mUserOrganization);
                break;

            case R.id.nav_manage_schools:
                startManageResourceActivity(Constants.SCHOOL_RESOURCE, mUserOrganization);
                break;

            case R.id.nav_manage_groups:
                startManageResourceActivity(Constants.GROUP_RESOURCE, mUserOrganization);
                break;

            case R.id.nav_manage_action:
                startManageResourceActivity(Constants.USER_ACTION_RESOURCE, mUserOrganization);
                break;

            case R.id.nav_manage_organization:
                startManageResourceActivity(Constants.ORGANIZATION_RESOURCE, mUserOrganization);
                break;

            case R.id.nav_manage_opportunity:
                startManageOpportunityActivity("", Constants.WRITE_MODE);
                break;

            //Organizer
            case R.id.nav_manage_organizer_opportunity:

                startManageOpportunityActivity(mUserOrganization, Constants.WRITE_MODE);
                break;

            case R.id.nav_manage_recognition:
                startManageResourceActivity(Constants.RECOGNITION_RESOURCE, mUserOrganization);
                break;

            case R.id.nav_manage_organizer_action:
                startManageResourceActivity(Constants.USER_ACTION_RESOURCE, mUserOrganization);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class UserCategoryStats {
        String mCategory;
        String mCategoryColor;
        String mMonthYear;
        double mHours;
        int mPoints;

        public String getCategory() {
            return mCategory;
        }

        public String getCategoryColor() {
            return mCategoryColor;
        }

        public String getmMonthYear() {
            return mMonthYear;
        }

        public double getHours() {
            return mHours;
        }

        public int getPoints() {
            return mPoints;
        }

        public UserCategoryStats(String category, String categoryColor, String monthYr, double hours, int points) {
            mCategory = category;
            mCategoryColor = categoryColor;
            mMonthYear = monthYr;
            mHours = hours;
            mPoints = points;
        }
    }
}
