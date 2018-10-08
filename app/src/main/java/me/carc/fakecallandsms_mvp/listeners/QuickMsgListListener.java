package me.carc.fakecallandsms_mvp.listeners;

import me.carc.fakecallandsms_mvp.model.PreSetMsg;

public interface QuickMsgListListener {
    void onClick(PreSetMsg historyItem);

    void onLongTouch(PreSetMsg historyItem);

    void onClickImage(PreSetMsg historyItem);

    void onClickOverflow(PreSetMsg historyItem);
}
