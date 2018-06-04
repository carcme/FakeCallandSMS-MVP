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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toolbar;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import java.util.ArrayList;

import me.carc.fakecallandsms_mvp.Base;
import me.carc.fakecallandsms_mvp.BuildConfig;
import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.utils.ViewUtils;
import me.carc.fakecallandsms_mvp.fragments.ImageSourceFragment;
import me.carc.fakecallandsms_mvp.fragments.cloud.data.model.Photo;
import me.carc.fakecallandsms_mvp.fragments.cloud.ui.DetailSharedElementEnterCallback;
import me.carc.fakecallandsms_mvp.fragments.cloud.ui.pager.DetailViewPagerAdapter;
import me.carc.fakecallandsms_mvp.fragments.cloud.ui.pager.FavDB;
import me.carc.fakecallandsms_mvp.widgets.ClickableViewPager;
import me.carc.fakecallandsms_mvp.widgets.DepthPageTransformer;
import me.carc.fakecallandsms_mvp.widgets.ZoomOutPageTransformer;


public class CloudDetailActivity extends Base {

    private static final String STATE_INITIAL_ITEM = "initial";
    private ClickableViewPager cloudPager;

    private int initialItem;

    private final View.OnClickListener navigationOnClickListener =
            new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (C.HAS_L)
                        finishAfterTransition();
                    else
                        finish();
                }
            };
    private DetailSharedElementEnterCallback sharedElementCallback;

    private ClickableViewPager.OnClickListener onPagerClickListener = new ClickableViewPager.OnClickListener() {
        @Override
        public void onViewPagerClick(ViewPager pager) {
        }

        @Override
        public void onDoubleTap(ViewPager viewPager) {
            setActivityResultSelected();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cloud_detail);

        Intent intent = getIntent();
        sharedElementCallback = new DetailSharedElementEnterCallback(intent);
        setEnterSharedElementCallback(sharedElementCallback);
        initialItem = intent.getIntExtra(IntentUtil.SELECTED_ITEM_POSITION, 0);
        setUpViewPager(intent.<Photo>getParcelableArrayListExtra(IntentUtil.PHOTO));

        Toolbar toolbar = findViewById(R.id.cloud_detail_toolbar);
//        setActionBar(toolbar);
//        getActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(navigationOnClickListener);

        ViewGroup.LayoutParams params = toolbar.getLayoutParams();
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        params.height += getStatusBarHeight();
        toolbar.setLayoutParams(params);


//        setStatusBarTranslucent(true, R.id.cloud_detail_toolbar);

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        super.onCreate(savedInstanceState);
    }


    private void setUpViewPager(ArrayList<Photo> photos) {
        cloudPager = findViewById(R.id.cloud_pager);
        cloudPager.setAdapter(new DetailViewPagerAdapter(this, photos, sharedElementCallback));
        cloudPager.setCurrentItem(initialItem);
        cloudPager.setOffscreenPageLimit(5);


        cloudPager.setOnViewPagerClickListener(onPagerClickListener);
        CloudDetailActivity.InitAsync task = new CloudDetailActivity.InitAsync();
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    class InitAsync extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(CloudDetailActivity.this);
            return Integer.parseInt(sp.getString("animationType", "1"));
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);

            if(cloudPager !=  null) {
                switch (result) {
                    case 0:
                        cloudPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.margin_tiny));
                        cloudPager.setPageMarginDrawable(R.drawable.page_margin);
                        break;
                    case 1:
                        cloudPager.setPageTransformer(true, new ZoomOutPageTransformer());
                        break;
                    case 2:
                        cloudPager.setPageTransformer(true, new DepthPageTransformer());
                        break;
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_INITIAL_ITEM, initialItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        initialItem = savedInstanceState.getInt(STATE_INITIAL_ITEM, 0);
        super.onRestoreInstanceState(savedInstanceState);
    }


    private boolean checkIsFavourite(Photo image) {

        for (Photo photo : FavDB.getFavDB().getList()) {
            if (photo.id == image.id)
                return true;
        }
        return false;
    }

    private void setFavIcon(ImageButton btn, boolean isFav) {
        if (isFav)
            btn.setImageDrawable(ViewUtils.changeIconColour(this, R.drawable.ic_heart, R.color.colorAccent));
        else
            btn.setImageDrawable(ViewUtils.changeIconColour(this, R.drawable.ic_heart, android.R.color.white));
    }

    public void toggleFavorite(View v) {
        final ImageButton btn = (ImageButton) v;
        final Photo image = ((DetailViewPagerAdapter) cloudPager.getAdapter()).getPhoto(cloudPager.getCurrentItem());

        if (checkIsFavourite(image)) {
            setFavIcon(btn, false);
            FavDB.getFavDB().del(image);

            if (BuildConfig.USE_CRASHLYTICS)
                Answers.getInstance().logContentView(new ContentViewEvent()
                        .putContentName("Remove Favourite")
                        .putContentType(image.author)
                        .putContentId(String.valueOf(image.id)));
        } else {
/*
            final FeedbackDialog fbDialog = new FeedbackDialog.Builder(this)
                    .titleTextColor(R.color.black)
                    .formTitle("Add Label")
                    .formHint("Add your image comment here")
                    .formSubmitText(getString(android.R.string.ok))
                    .formCancelText(getString(android.R.string.cancel))

                    .positiveButtonTextColor(R.color.colorAccent)
                    .positiveButtonBackgroundColor(R.drawable.button_selector_positive)

                    .negativeButtonTextColor(R.color.colorPrimaryDark)
                    .negativeButtonBackgroundColor(R.drawable.button_selector_negative)

                    .onFeedbackFormSumbit(new FeedbackDialog.Builder.FeedbackDialogFormListener() {
                        @Override
                        public void onFormSubmitted(String comment) {

                            // TODO: 2/25/17 add the comment
                            setFavIcon(btn, true);
                            FavDB.getFavDB().add(image);
                        }
                    }).build();

            fbDialog.show();
*/
            setFavIcon(btn, true);
            FavDB.getFavDB().add(image);

            if (BuildConfig.USE_CRASHLYTICS)
                Answers.getInstance().logContentView(new ContentViewEvent()
                        .putContentName("Add Favourite")
                        .putContentType(image.author)
                        .putContentId(String.valueOf(image.id)));
        }
    }

    public void authorTextBack(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        setActivityResult();
        super.onBackPressed();
    }

    @Override
    public void finishAfterTransition() {
        setActivityResult();
        super.finishAfterTransition();
    }

    /**
     * Back pressed... account for item has been paged and not the same as selected from grid
     */
    private void setActivityResult() {
        if (initialItem == cloudPager.getCurrentItem()) {
            setResult(RESULT_OK);
            return;
        }
        FavDB.getFavDB().saveList();
        Intent intent = new Intent();
        intent.putExtra(IntentUtil.SELECTED_ITEM_POSITION, cloudPager.getCurrentItem());
        setResult(RESULT_OK, intent);
    }

    /**
     * Selected image... send to Cropping fragment
     */
    private void setActivityResultSelected() {
        FavDB.getFavDB().saveList();
        Intent intent = new Intent();
        intent.putExtra(IntentUtil.SELECTED_ITEM_POSITION, cloudPager.getCurrentItem());
        setResult(ImageSourceFragment.IMAGE_CLOUD, intent);
        finish();
    }
}
