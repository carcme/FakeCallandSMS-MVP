/*
 * Copyright (C) 2012 The Android Open Source Project
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
package me.carc.fakecallandsms_mvp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.fakecallandsms_mvp.MainTabActivity;
import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.utils.AndroidUtils;
import me.carc.fakecallandsms_mvp.common.utils.U;

public class ImageSourceFragment extends Fragment {

    private static final String TAG = C.DEBUG + U.getTag();
    public static final String TAG_ID = "ImageSourceFragment";

    public static final int IMAGE_NONE    = 0;
    public static final int IMAGE_RES     = 1;
    public static final int IMAGE_CAMERA  = 2;
    public static final int IMAGE_GALLERY = 3;
    public static final int IMAGE_CLOUD   = 4;
    public static final int IMAGE_START   = 100;



    private Animation animation;
    private boolean requestingGallery;

    @BindView(R.id.cloud_holder) RelativeLayout cloud_holder;
    @BindView(R.id.gallery_holder) RelativeLayout gallery_holder;
    @BindView(R.id.camera_holder) RelativeLayout camera_holder;

    OnSourceSelectedListener srcCallback;

    public interface OnSourceSelectedListener {
        void onSourceSelected(int src);
    }

    private void storageAllowed() {
        if (PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE))
            flyOut(IMAGE_GALLERY);
        else
            requestGalleryPermissions();
    }

    private void cameraAllowed() {
        if (PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA))
            flyOut(IMAGE_CAMERA);
        else
            requestCameraPermissions();
    }

    /**
     * request storage access
      */
    private void requestGalleryPermissions() {
        final String[] STORAGE_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(getActivity(), STORAGE_PERMISSIONS, MainTabActivity.PERMISSION_STORAGE_RESULT);
    }

    /**
     * request camera access
     */
    private void requestCameraPermissions() {
        final String[] CAMERA_PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(getActivity(), CAMERA_PERMISSIONS, MainTabActivity.PERMISSION_CAMERA_RESULT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInst) {

        View view = inflater.inflate(R.layout.image_source_view, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    /**
     * Attach  - used to send msgs to Activity
     */
    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);

        try {
            srcCallback = (OnSourceSelectedListener) ctx;
        } catch (ClassCastException e) {
            throw new ClassCastException(ctx.toString() + " must implement OnSourceSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        srcCallback = null;
        super.onDetach();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        flyIn();
    }

    @OnClick(R.id.cloud_holder)
    void startCloud() {
        // TODO: 02/06/2018 check internet connection is valid
        flyOut(IMAGE_CLOUD);
    }

    @OnClick(R.id.gallery_holder)
    void startGallery() {
        if (C.HAS_M)
            storageAllowed();
        else
            flyOut(IMAGE_GALLERY);
    }

    @OnClick(R.id.camera_holder)
    void startCamera() {
        if (C.HAS_M)
            cameraAllowed();
        else
            flyOut(IMAGE_CAMERA);
    }

    private void flyIn() {

        AndroidUtils.hideSoftKeyboard(getActivity(), cloud_holder);

        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.holder_top);
        cloud_holder.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.holder_bottom);
        gallery_holder.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.step_number);
        camera_holder.startAnimation(animation);
    }

    public void flyOut(final int method_name) {

        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.step_number_back);
        camera_holder.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.holder_top_back);
        cloud_holder.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(getActivity(), R.anim.holder_bottom_back);
        gallery_holder.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation a) { }

            @Override
            public void onAnimationRepeat(Animation a) { }

            @Override
            public void onAnimationEnd(Animation a) {
                    srcCallback.onSourceSelected(method_name);
            }
        });
    }
}