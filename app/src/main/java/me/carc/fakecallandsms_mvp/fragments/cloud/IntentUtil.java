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

/**
 * Holding intent extra names and utility methods for intent handling.
 */
public class IntentUtil {
    public static final String FONT_SIZE = "fontSize";
    public static final String PADDING = "padding";
    public static final String TEXT_COLOR = "color";

    static final String PHOTO = "photo";
    static final String RELEVANT_PHOTOS = "relevant";
    static final String SELECTED_ITEM_POSITION = "selected";
    static final int REQUEST_CODE = 412;//R.id.requestCode;

    /**
     * Checks if all extras are present in an intent.
     *
     * @param intent The intent to check.
     * @param extras The extras to check for.
     * @return <code>true</code> if all extras are present, else <code>false</code>.
     */
    public static boolean hasAll(Intent intent, String... extras) {
        for (String extra : extras) {
            if (!intent.hasExtra(extra)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if any extra is present in an intent.
     *
     * @param intent The intent to check.
     * @param extras The extras to check for.
     * @return <code>true</code> if any checked extra is present, else <code>false</code>.
     */
    public static boolean hasAny(Intent intent, String... extras) {
        for (String extra : extras) {
            if (intent.hasExtra(extra)) {
                return true;
            }
        }
        return false;
    }
}
