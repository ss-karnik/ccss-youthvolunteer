package com.ccss.youthvolunteer.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ccss.youthvolunteer.R;
import com.ccss.youthvolunteer.adapter.RankingViewAdapter;
import com.ccss.youthvolunteer.model.RankingModel;
import com.ccss.youthvolunteer.model.VolunteerUser;
import com.google.common.collect.Lists;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

public class RankingActivity extends BaseActivity {

    private List<RankingModel> mVolunteers = Lists.newArrayList();
    private RankingViewAdapter mAdapter;
    private RecyclerView mVolunteerView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        initializeData();

        mVolunteerView = (RecyclerView)findViewById(R.id.ranking_recycler_view);
        mVolunteerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mVolunteerView.setLayoutManager(layoutManager);
        mContext = this;
    }

    private void initializeData(){
        VolunteerUser.findUsersRanked(new FindCallback<VolunteerUser>() {
            @Override
            public void done(List<VolunteerUser> objects, ParseException e) {
               // for(int rank = 0; rank < objects.size(); rank++){
                for(VolunteerUser user : objects){
                    String userPointsAndRank = String.format("# %s with %s points", objects.indexOf(user), user.getPointsAccrued());
                    mVolunteers.add(new RankingModel(user.getFullName(), user.getUserLevel().getTitle(),
                            user.getSchoolName(), userPointsAndRank, user.getProfileImageUri()));
                }
                //mAdapter.notifyDataSetChanged();
                mAdapter = new RankingViewAdapter(mContext, mVolunteers);
                mVolunteerView.setAdapter(mAdapter);
            }
        });
    }
}
