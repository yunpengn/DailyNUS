package ind.hailin.dailynus.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.adapter.ExpandableListAdapter;
import ind.hailin.dailynus.adapter.HomeMainAdapter;
import ind.hailin.dailynus.application.DataApplication;
import ind.hailin.dailynus.entity.ChatDialogues;
import ind.hailin.dailynus.entity.Groups;
import ind.hailin.dailynus.entity.Users;
import ind.hailin.dailynus.exception.DataExpiredException;
import ind.hailin.dailynus.exception.DesException;
import ind.hailin.dailynus.handler.QueryJsonHandler;
import ind.hailin.dailynus.utils.CacheUtils;
import ind.hailin.dailynus.utils.Constants;
import ind.hailin.dailynus.utils.DesEncryption;
import ind.hailin.dailynus.utils.MyPicUtils;
import ind.hailin.dailynus.utils.MyUtils;
import ind.hailin.dailynus.web.QueryJsonManager;

public class HomeActivity extends AppCompatActivity implements HomeMainAdapter.OnItemClickListener,
        ExpandableListView.OnChildClickListener {
    public static final String TAG = "HomeActivity";

    private QueryJsonManager queryUnreadManager, queryAvatar, queryGroupManager;

    private Toolbar toolbar;
    private LinearLayout lyNextCourse;
    private TextView tvNextCourse, tvNickname;
    private ImageView ivAvatar;
    private SwipeRefreshLayout refreshHomeMain, refreshNav;
    private RecyclerView recyclerHomeMain;
    private SearchView searchViewNav;
    private ExpandableListView expandableListView;
    private HomeMainAdapter homeMainAdapter;
    private ExpandableListAdapter expandableListAdapter;

    private List<ChatDialogues> cachedList, unreadList;
    private List<String> groupList;

    private Handler queryUnreadHandler = new QueryJsonHandler() {
        @Override
        public void success(Message msg) {
            Log.d(TAG, "queryUnreadHandler success");
            InputStream inputStream = (InputStream) msg.obj;
            try {
                String infoStr = MyUtils.readStringFromInputStream(inputStream);
                infoStr = DesEncryption.decryption(infoStr);

                ObjectMapper objectMapper = new ObjectMapper();
                unreadList = objectMapper.readValue(infoStr, new TypeReference<List<ChatDialogues>>() {
                });

                List<ChatDialogues> restructuredList = MyUtils.restructureList(unreadList);
                Map<Integer, String> senderIdMap = MyUtils.getSenderId(unreadList);

                checkAvatarsCache(senderIdMap);

                homeMainAdapter.insertRange(restructuredList);
                refreshHomeMain.setRefreshing(false);
            } catch (Exception e) {
                e.printStackTrace();
                failure(msg);
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void failure(Message msg) {
            refreshHomeMain.setRefreshing(false);
            Toast.makeText(HomeActivity.this, "load failed", Toast.LENGTH_SHORT).show();
        }
    };

    private Handler queryAvatarHandler = new QueryJsonHandler() {
        @Override
        public void success(Message msg) {
            Log.d(TAG, "success queryAvatar: "+msg.arg1);
            InputStream inputStream = null;
            ByteArrayOutputStream outputStream = null;
            try {
                inputStream = (InputStream) msg.obj;
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                outputStream = MyPicUtils.convertBitmapToOutputStream(bitmap);

                if(msg.arg1 == -1) {
                    ivAvatar.setImageBitmap(MyPicUtils.getCroppedBitmap(bitmap));
                    CacheUtils.cacheAvatarById(HomeActivity.this,
                            DataApplication.getApplication().getUser().getId(), outputStream.toByteArray());
                } else {
                    homeMainAdapter.addBitmap(msg.arg1, MyPicUtils.getCroppedBitmap(bitmap));
                    CacheUtils.cacheAvatarById(HomeActivity.this, msg.arg1, outputStream.toByteArray());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();
                    if (outputStream != null)
                        outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void failure(Message msg) {
        }
    };

    private Handler queryGroupHandler = new QueryJsonHandler() {
        @Override
        public void success(Message msg) {
            Log.d(TAG, "queryGroupHandler success");
            InputStream inputStream = (InputStream) msg.obj;
            try {
                String infoStr = MyUtils.readStringFromInputStream(inputStream);
                infoStr = DesEncryption.decryption(infoStr);

                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, List<Groups>> map = objectMapper.readValue(infoStr, new TypeReference<Map<String, List<Groups>>>() {});

                expandableListAdapter.updateChildMap(map);
            } catch (Exception e){
                e.printStackTrace();
            }
            refreshNav.setRefreshing(false);
        }
        @Override
        public void failure(Message msg) {
            refreshNav.setRefreshing(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initHttpManager();
        initToolBar();
        initView();
        initNavigationView();
    }

    private void initHttpManager() {
        queryUnreadManager = new QueryJsonManager(5000, Constants.JSON_TYPE_CHAT_DIALOG);
        queryAvatar = new QueryJsonManager(3000, Constants.JSON_TYPE_USER, -1);
        queryGroupManager = new QueryJsonManager(5000, Constants.JSON_TYPE_GROUPS);
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.home_main_collapsingtoolbarlayout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.home_main_appbarlayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Home");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void initView() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        lyNextCourse = (LinearLayout) findViewById(R.id.home_show_next_course);
        tvNextCourse = (TextView) findViewById(R.id.home_tv_show_course);

        lyNextCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, WebViewActivity.class));
            }
        });

        syncCourseSchedule();
        readFromCacheAndInitHomeMainAdapter();

        refreshHomeMain = (SwipeRefreshLayout) findViewById(R.id.home_main_swiperefreshlayout);
        recyclerHomeMain = (RecyclerView) findViewById(R.id.home_main_recyclerview);

        homeMainAdapter.setOnItemClickListener(this);

        recyclerHomeMain.setLayoutManager(new LinearLayoutManager(this));
        recyclerHomeMain.setAdapter(homeMainAdapter);

        refreshHomeMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryUnreadManager.query(HomeActivity.this, queryUnreadHandler,
                        DataApplication.getApplication().getUser().getUsername());
                Log.d(TAG, "onRefresh, query unread");
            }
        });

        queryUnreadManager.query(HomeActivity.this, queryUnreadHandler,
                DataApplication.getApplication().getUser().getUsername());

        refreshHomeMain.setRefreshing(true);
    }

    private void initNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(HomeActivity.this, ProfileActivity.class),
                        Constants.REQUEST_HOME_TO_PROFILE);
            }
        });

        tvNickname = (TextView) headerView.findViewById(R.id.nav_tv_nickname);
        ivAvatar = (ImageView) headerView.findViewById(R.id.nav_iv_avatar);

        tvNickname.setText(DataApplication.getApplication().getUser().getNickName());
        avatarIvLoadPic();

        searchViewNav = (SearchView) findViewById(R.id.nav_searchview);
//        loadSearchView();

        readCacheAndInitExpandableAdapter();

        refreshNav = (SwipeRefreshLayout) findViewById(R.id.nav_swiperefreshlayout);
        expandableListView = (ExpandableListView) findViewById(R.id.nav_expandablelistview);
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnChildClickListener(this);

        refreshNav.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryGroupManager.query(HomeActivity.this, queryGroupHandler,
                        DataApplication.getApplication().getUser().getUsername());
                Log.d(TAG, "onRefresh, query groups");
            }
        });

        queryGroupManager.query(HomeActivity.this, queryGroupHandler,
                DataApplication.getApplication().getUser().getUsername());

        refreshNav.setRefreshing(true);
    }

    private void syncCourseSchedule() {
        // TODO: 2017/6/2
    }

    private void readFromCacheAndInitHomeMainAdapter() {
        try {
            cachedList = CacheUtils.getChatDialoguesFromCache(this);
            List<ChatDialogues> restructuredList = MyUtils.restructureList(cachedList);

            Map<Integer, Bitmap> bitmapMap = new HashMap<>();

            for (int i = 0; i < restructuredList.size(); i++) {
                int id = restructuredList.get(i).getId();
                Bitmap bitmap = CacheUtils.getAvatarFromCacheById(this, id);
                bitmapMap.put(id, MyPicUtils.getCroppedBitmap(bitmap));
            }
            homeMainAdapter = new HomeMainAdapter(restructuredList, bitmapMap);
        } catch (Exception e) {
            e.printStackTrace();
            homeMainAdapter = new HomeMainAdapter();
        }
    }

    private void readCacheAndInitExpandableAdapter() {
        initGroupList();
        Map<String, List<Groups>> childMap = null;
        try{
            childMap = CacheUtils.getGroupMapFromCache(this);
        } catch (Exception e) {
            e.printStackTrace();
            childMap = new HashMap<>();
        } finally {
            expandableListAdapter = new ExpandableListAdapter(this, groupList, childMap);
        }
    }

    private void initGroupList() {
        groupList = new ArrayList<>();
        groupList.add("Module Groups");
        groupList.add("Groups");
    }

    private void avatarIvLoadPic() {
        try {
            Bitmap bitmap = CacheUtils.getAvatarFromCache(this);
            ivAvatar.setImageBitmap(MyPicUtils.getCroppedBitmap(bitmap));
        } catch (Exception e) {
            e.printStackTrace();
            Users user = DataApplication.getApplication().getUser();
            queryAvatar.query(this, queryAvatarHandler, user.getUsername(), "avatar.png");
        }
    }

    private void checkAvatarsCache(Map<Integer, String> sendMap) {
        Map<Integer, Bitmap> subMap = new HashMap<>();

        Integer[] keyArray = sendMap.keySet().toArray(new Integer[sendMap.size()]);
        for (int i = 0; i < keyArray.length; i++) {
            try {
                Bitmap bitmap = CacheUtils.getAvatarFromCacheById(this, keyArray[i]);
                if (bitmap != null) {
                    subMap.put(keyArray[i], MyPicUtils.getCroppedBitmap(bitmap));
                } else {
                    queryAvatar(keyArray[i], sendMap.get(keyArray[i]));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                queryAvatar(keyArray[i], sendMap.get(keyArray[i]));
            }
        }
        homeMainAdapter.addBitmap(subMap);
    }

    private void queryAvatar(int id, String username) {
        new QueryJsonManager(2000, Constants.JSON_TYPE_USER, id).query(this, queryAvatarHandler, username, "avatar.png");
    }

    @Override
    public void onItemClick(ChatDialogues chatDialogues) {

    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_settings:
                break;
            // TODO: 2017/6/2
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_HOME_TO_PROFILE && resultCode == Constants.RESULT_PROFILE_TO_HOME_CHANGE){
            avatarIvLoadPic();
            Log.d(TAG, "avatar changed");
        }
    }
}
