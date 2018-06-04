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

package me.carc.fakecallandsms_mvp.fragments.cloud.data;


import java.util.List;

import me.carc.fakecallandsms_mvp.fragments.cloud.data.model.Photo;
import retrofit.Callback;
import retrofit.http.GET;

/**
 * Modeling the unsplash.it API.
 */
public interface UnsplashService {

    String ENDPOINT = "https://unsplash.it";
//    String ENDPOINT = "https://source.unsplash.com";

    @GET("/list")
//    @GET("/user/erondu/1600x900")
    void getFeed(Callback<List<Photo>> callback);

}
