/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.carc.fakecallandsms_mvp.fragments.cloud;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.carc.fakecallandsms_mvp.Base;
import me.carc.fakecallandsms_mvp.MainTabActivity;
import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.ImageSize;
import me.carc.fakecallandsms_mvp.common.TinyDB;
import me.carc.fakecallandsms_mvp.common.utils.ViewUtils;
import me.carc.fakecallandsms_mvp.databinding.CloudPhotoItemBinding;
import me.carc.fakecallandsms_mvp.fragments.ImageSourceFragment;
import me.carc.fakecallandsms_mvp.fragments.cloud.data.UnsplashService;
import me.carc.fakecallandsms_mvp.fragments.cloud.data.model.MenuDataModel;
import me.carc.fakecallandsms_mvp.fragments.cloud.data.model.Photo;
import me.carc.fakecallandsms_mvp.fragments.cloud.ui.DrawerItemCustomAdapter;
import me.carc.fakecallandsms_mvp.fragments.cloud.ui.grid.GridMarginDecoration;
import me.carc.fakecallandsms_mvp.fragments.cloud.ui.grid.OnItemSelectedListener;
import me.carc.fakecallandsms_mvp.fragments.cloud.ui.grid.PhotoAdapter;
import me.carc.fakecallandsms_mvp.fragments.cloud.ui.grid.PhotoViewHolder;
import me.carc.fakecallandsms_mvp.fragments.cloud.ui.pager.FavDB;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CloudGridActivity extends Base {
    private static final String TAG = "CloudGridActivity";

    private static final String COLUMN_COUNT = "COLUMN_COUNT"; // use sp to save user preferences
    private static final String TMP_LAST_BROWSE_LOCATION = "TMP_LAST_BROWSE_LOCATION";

    private static final int COL_SINGLE = 1;
    private static final int COL_GRID = 3;

    public static final String EXTRA_UNSPLASH_ERROR = "EXTRA_UNSPLASH_ERROR";
    public static final String EXTRA_SELECTED_CLOUD_PHOTO = "EXTRA_SELECTED_CLOUD_PHOTO";


    private static final int PHOTO_COUNT = 10; // first 10 or so images appear to be test images

    private final View.OnClickListener navigationOnClickListener =
            new View.OnClickListener() {

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    if(C.HAS_L)
                        finishAfterTransition();
                    else
                        finish();
                }
            };

    private RecyclerView grid;
    private ProgressBar empty;
    private ArrayList<Photo> relevantPhotos;

    FloatingActionButton toggleFab;

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    DrawerItemCustomAdapter adapter;

    //    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_grid);

        if(TinyDB.getTinyDB() == null)
            new TinyDB(CloudGridActivity.this);

//        TinyDB.getTinyDB().remove(C.SP_UNSPLASH_PHOTO_LIST);

        grid = findViewById(R.id.image_grid);
        empty = findViewById(android.R.id.empty);

        toggleFab = findViewById(R.id.toggleFab);
        toggleFab.setOnClickListener(onToggleViewLayout);

        setupRecyclerView();

        if (savedInstanceState != null) {
            relevantPhotos = savedInstanceState.getParcelableArrayList(IntentUtil.RELEVANT_PHOTOS);
        }

        toolbar = findViewById(R.id.cloud_toolbar);
        setActionBar(toolbar);
//        getActionBar().setDisplayShowTitleEnabled(false);
//        getActionBar().setTitle(Photo.UNSPLASH_IT);
        toolbar.setNavigationOnClickListener(navigationOnClickListener);

        displayData();

        new CloudGridActivity.SetupUI(savedInstanceState).run();
    }

    class SetupUI implements Runnable {

        final Bundle bundle;

        SetupUI(Bundle savedInstanceState) {
            bundle = savedInstanceState;
        }

        @Override
        public void run() {

            // SET UP NAVIGATION MENU
            mDrawerLayout = findViewById(R.id.drawer_layout);
            mDrawerList = findViewById(R.id.pager_drawer);

            List<MenuDataModel> drawerItem = new ArrayList<>(0);
            for(Photo photo : FavDB.getFavDB().getList()) {
                drawerItem.add(new MenuDataModel(photo.getPhotoUrl(ImageSize.THUMB[0]), 0, photo.author, photo.id));
            }

            View header = getLayoutInflater().inflate(R.layout.favorites_header, null);

            if(!drawerItem.isEmpty()) {
                ImageView favImage = header.findViewById(R.id.favImage);

                // use a random favourite image for the header
                Glide.with(CloudGridActivity.this)
                        .load(FavDB.getFavDB().randomFromList().getPhotoUrl(ImageSize.HEADER[0]))
                        .placeholder(R.color.colorPrimary)
                        .override(ImageSize.HEADER[0], ImageSize.HEADER[1])
                        .into(favImage);
            }
            mDrawerList.addHeaderView(header);

            adapter = new DrawerItemCustomAdapter(CloudGridActivity.this, R.layout.list_view_item_row, drawerItem);
            mDrawerList.setAdapter(adapter);
            mDrawerList.setOnItemClickListener(new CloudGridActivity.SetupUI.DrawerItemClickListener());
            mDrawerLayout = findViewById(R.id.drawer_layout);
            mDrawerLayout.addDrawerListener(mDrawerToggle);
            setupDrawerToggle();
        }

        private class DrawerItemClickListener implements ListView.OnItemClickListener {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        }

        private void selectItem(int position) {

            if(position == 0) {
                // TODO: 02/03/17 add a click handler for touchign the header image
                Log.d(TAG, "selectItem: Add some dialog box with info for Upslash");
            }else {
                Photo photo = FavDB.getFavDB().getList().get(position - 1);
                for (int i = 0; i < relevantPhotos.size(); i++) {
                    Photo p  = relevantPhotos.get(i);
                    if(p.id == photo.id) {
                        grid.scrollToPosition(i);
                        break;
                    }
                }
            }

//            mDrawerList.setItemChecked(position, true);
//            mDrawerList.setSelection(position);
            mDrawerLayout.closeDrawer(mDrawerList);

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupDrawerToggle(){
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name);

        mDrawerToggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, null);
        mDrawerToggle.setHomeAsUpIndicator(drawable);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        mDrawerToggle.syncState();
    }

    private View.OnClickListener onToggleViewLayout = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateUiFab();
        }
    };

    void updateUiFab() {

        int scrollPosition = 0;
        if (grid.getLayoutManager() != null)
            scrollPosition = ((LinearLayoutManager) grid.getLayoutManager()).findFirstCompletelyVisibleItemPosition();

        GridLayoutManager layoutManager = null;

        switch ((int)toggleFab.getTag()) {
            case COL_GRID:
                ViewUtils.changeFabIcon(this, toggleFab, R.drawable.ic_column_single);
                layoutManager = new GridLayoutManager(this, COL_GRID);
                toggleFab.setTag(COL_SINGLE);
                TinyDB.getTinyDB().putInt(COLUMN_COUNT, COL_GRID);
                break;

            case COL_SINGLE:
                ViewUtils.changeFabIcon(this, toggleFab, R.drawable.ic_column_multi);
                layoutManager = new GridLayoutManager(this, COL_SINGLE);
                toggleFab.setTag(COL_GRID);
                TinyDB.getTinyDB().putInt(COLUMN_COUNT, COL_SINGLE);
                break;

            default:
                Log.d(TAG, "updateUiFab: UNKNOWN TAG");
        }

        if(layoutManager != null) {
            grid.setLayoutManager(layoutManager);
            grid.scrollToPosition(scrollPosition);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cloud, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(NoSuchMethodException e){
                    Log.e(TAG, "onMenuOpened", e);
                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    /**
     * Menu click listener
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // reject all calls until the list is populated... easier than enabling / disabling at the moment
        if(empty.getVisibility() == View.GONE) {

            int scrollPosition = 0;
            if (grid.getLayoutManager() != null) {
                scrollPosition = ((LinearLayoutManager) grid.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            }
            GridLayoutManager layoutManager = null;
            switch (item.getItemId()) {
                case R.id.shuffle:
                    TinyDB.getTinyDB().remove(TMP_LAST_BROWSE_LOCATION);
                    Collections.shuffle(relevantPhotos);
                    savePhotoList();
                    grid.getAdapter().notifyDataSetChanged();
                    grid.scrollToPosition(0); // reset to start of list
                    break;

                case R.id.single_grid:
                    TinyDB.getTinyDB().putInt(COLUMN_COUNT, 1);
                    layoutManager = new GridLayoutManager(this, 1);
                    break;

                case R.id.multi_grid:
                    TinyDB.getTinyDB().putInt(COLUMN_COUNT, 3);
                    layoutManager = new GridLayoutManager(this, 3);
                    break;
            }

            if (layoutManager != null) {
                grid.setLayoutManager(layoutManager);
                grid.scrollToPosition(scrollPosition);
                return true;
            }

        }
        return super.onOptionsItemSelected(item);
    }

    private void displayData() {
        if (relevantPhotos != null) {
            populateGrid();
        } else if(getPhotoList()) {
            populateGrid();
        } else {
            UnsplashService unsplashApi = new RestAdapter.Builder()
                    .setEndpoint(UnsplashService.ENDPOINT)
                    .build()
                    .create(UnsplashService.class);

            unsplashApi.getFeed(new Callback<List<Photo>>() {
                @Override
                public void success(List<Photo> photos, Response response) {

                    if(photos.size() > PHOTO_COUNT)
                        relevantPhotos = new ArrayList<>(photos.subList(PHOTO_COUNT, photos.size()));
                    else
                        relevantPhotos = new ArrayList<>(photos);

                    if(photos.size() > 0) {
                        savePhotoList();
                        populateGrid();
                    } else {
                        Log.e(TAG, "Strange error... should have images!!");
                        Intent intent = getIntent();
                        intent.putExtra(EXTRA_UNSPLASH_ERROR, "Error with photo list from Unsplash feed");
                        setResult(MainTabActivity.UNSPLASH_ERROR, intent);
                        finish();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "Error retrieving Unsplash feed:", error);
                    Intent intent = getIntent();
                    intent.putExtra(EXTRA_UNSPLASH_ERROR, "Error retrieving Unsplash feed:" +  error);
                    setResult(MainTabActivity.UNSPLASH_ERROR, intent);
                    finish();
                }
            });
        }
    }

    /**
     * Save list to prefs
     * TODO: make a refresh of the list in a background task at some point
     */
    public void savePhotoList() {
        TinyDB.getTinyDB().putListPhoto(C.SP_UNSPLASH_PHOTO_LIST, relevantPhotos);
    }

    /**
     * get the saved list from prefs
     * @return
     */
    private boolean getPhotoList() {
        if(TinyDB.getTinyDB() == null)
            new TinyDB(CloudGridActivity.this);

        relevantPhotos = TinyDB.getTinyDB().getListPhoto(C.SP_UNSPLASH_PHOTO_LIST);
        return relevantPhotos.size() > 0 ;
    }

    private void populateGrid() {
        grid.setAdapter(new PhotoAdapter(CloudGridActivity.this, relevantPhotos));
        grid.addOnItemTouchListener(new OnItemSelectedListener(CloudGridActivity.this) {
            public void onItemSelected(RecyclerView.ViewHolder holder, int position) {
                if (!(holder instanceof PhotoViewHolder)) {
                    return;
                }
                // center list on last clicked item when returning
                TinyDB.getTinyDB().putInt(TMP_LAST_BROWSE_LOCATION, position);

                CloudPhotoItemBinding binding = ((PhotoViewHolder) holder).getBinding();

                final Intent intent = getDetailActivityStartIntent(CloudGridActivity.this, relevantPhotos, position, binding);

                CloudGridActivity.this.startActivityForResult(intent, IntentUtil.REQUEST_CODE);
            }
        });
        empty.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null) {
            if (resultCode == ImageSourceFragment.IMAGE_CLOUD) {
                final int selectedItem = data.getIntExtra(IntentUtil.SELECTED_ITEM_POSITION, 0);

                Photo selectedPhoto = relevantPhotos.get(selectedItem);

                Intent intent = getIntent();
                intent.putExtra(EXTRA_SELECTED_CLOUD_PHOTO, selectedPhoto);

                setResult(RESULT_OK, intent);
                finish();

            } else {
                // Repopulate the favourite list
                adapter.clear();
                for (Photo photo : FavDB.getFavDB().getList()) {
                    adapter.add(new MenuDataModel(photo.getPhotoUrl(ImageSize.THUMB[0]), 0, photo.author, photo.id));
                }

                // scroll to new grid item (user has paged though some images)
                final int selectedItem = data.getIntExtra(IntentUtil.SELECTED_ITEM_POSITION, 0);
                grid.scrollToPosition(selectedItem);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(IntentUtil.RELEVANT_PHOTOS, relevantPhotos);
        super.onSaveInstanceState(outState);
    }

    private void setupRecyclerView() {

        int columnCount = TinyDB.getTinyDB().getInt(COLUMN_COUNT, COL_GRID);
        boolean gridMode = columnCount == COL_GRID;
        GridLayoutManager layoutManager = new GridLayoutManager(this, columnCount);
        grid.setLayoutManager(layoutManager);
        grid.addItemDecoration(new GridMarginDecoration(getResources().getDimensionPixelSize(R.dimen.grid_item_spacing_none)));
        grid.setHasFixedSize(true);

        ViewUtils.changeFabIcon(this, toggleFab, gridMode ? R.drawable.ic_column_single : R.drawable.ic_column_multi );
        toggleFab.setTag(gridMode ? COL_SINGLE : COL_GRID);

        grid.scrollToPosition(TinyDB.getTinyDB().getInt(TMP_LAST_BROWSE_LOCATION, 0));

    }

    @NonNull
    private static Intent getDetailActivityStartIntent(Activity host, ArrayList<Photo> photos,
                                                       int position, CloudPhotoItemBinding binding) {
        final Intent intent = new Intent(host, CloudDetailActivity.class);
        intent.setAction(Intent.ACTION_VIEW);

        intent.putParcelableArrayListExtra(IntentUtil.PHOTO, photos);
        intent.putExtra(IntentUtil.SELECTED_ITEM_POSITION, position);
        intent.putExtra(IntentUtil.FONT_SIZE, binding.author.getTextSize());

        intent.putExtra(IntentUtil.PADDING, new Rect(binding.author.getPaddingLeft(),
                        binding.author.getPaddingTop(), binding.author.getPaddingRight(),
                        binding.author.getPaddingBottom()));

        intent.putExtra(IntentUtil.TEXT_COLOR, binding.author.getCurrentTextColor());
        return intent;
    }
}
