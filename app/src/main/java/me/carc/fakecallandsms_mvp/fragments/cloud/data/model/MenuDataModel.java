package me.carc.fakecallandsms_mvp.fragments.cloud.data.model;

/**
 * Pager menu list
 * Created by bamptonm on 2/14/17.
 */
public class MenuDataModel {

    public long iconRef;
    public String cloudRef;
    public long timeRef;
    public String event;

    // Constructor.
    public MenuDataModel(long ref, long time, String title) {
        iconRef = ref;
        timeRef = time;
        event   = title;
        cloudRef = "";
    }

    public MenuDataModel(String url, long time, String title, long ref) {
        cloudRef = url;
        timeRef = time;
        event   = title;
        iconRef = 0;
    }
}