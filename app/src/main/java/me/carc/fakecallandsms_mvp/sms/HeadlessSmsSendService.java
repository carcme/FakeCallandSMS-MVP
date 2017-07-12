package me.carc.fakecallandsms_mvp.sms;

import android.app.IntentService;
import android.content.Intent;

import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.common.utils.U;

/**
 * Created by bamptonm on 7/4/17.
 */

public class HeadlessSmsSendService extends IntentService {
    private static final String TAG = C.DEBUG + U.getTag();

    public HeadlessSmsSendService() {
        super(HeadlessSmsSendService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}