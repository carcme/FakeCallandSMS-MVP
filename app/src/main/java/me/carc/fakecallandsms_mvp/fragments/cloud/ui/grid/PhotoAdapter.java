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

package me.carc.fakecallandsms_mvp.fragments.cloud.ui.grid;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.common.ImageSize;
import me.carc.fakecallandsms_mvp.databinding.CloudPhotoItemBinding;
import me.carc.fakecallandsms_mvp.fragments.cloud.data.model.Photo;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoViewHolder> {

    private final ArrayList<Photo> photos;
//    private final int requestedPhotoWidth;
    private final LayoutInflater layoutInflater;

    public PhotoAdapter(@NonNull Context context, @NonNull ArrayList<Photo> photos) {
        this.photos = photos;
//        requestedPhotoWidth = context.getResources().getDisplayMetrics().widthPixels;
//        layoutInflater = LayoutInflater.from(context);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public PhotoViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        return new PhotoViewHolder((CloudPhotoItemBinding) DataBindingUtil.inflate(layoutInflater,
                    R.layout.cloud_photo_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final PhotoViewHolder holder, final int position) {
        CloudPhotoItemBinding binding = holder.getBinding();
        Photo data = photos.get(position);
        binding.setData(data);
        binding.executePendingBindings();

        Glide.with(layoutInflater.getContext())
                .load(holder.getBinding().getData().getPhotoUrl(ImageSize.SCREEN[0])) /*  requestedPhotoWidth   300*/
                .placeholder(R.color.placeholder)
                .override(ImageSize.SCREEN[0], ImageSize.SCREEN[1])
                .centerCrop()
                .into(holder.getBinding().photo);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    @Override
    public long getItemId(int position) {
        return photos.get(position).id;
    }
}
