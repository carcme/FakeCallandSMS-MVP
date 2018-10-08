package me.carc.fakecallandsms_mvp.db;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import me.carc.fakecallandsms_mvp.model.PreSetMsg;

/**
 * Created by bamptonm on 12/09/2018.
 */

public class PreSetMsgViewModel extends AndroidViewModel {

    private PreSetMsgRepository mRepository;
    private LiveData<List<PreSetMsg>> mAllBlocks;

    public PreSetMsgViewModel(@NonNull Application application) {
        super(application);
        mRepository = new PreSetMsgRepository(application);
        mAllBlocks = mRepository.getAllMessages();
    }

    public LiveData<List<PreSetMsg>> getAllMessages() {
        return mAllBlocks;
    }

    public void insert(PreSetMsg msg) { mRepository.insert(msg); }

    public void delete(PreSetMsg msg) { mRepository.delete(msg); }

    public void nuke() { mRepository.nuke(); }
}
