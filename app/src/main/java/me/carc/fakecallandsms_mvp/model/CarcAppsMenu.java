package me.carc.fakecallandsms_mvp.model;

import me.carc.fakecallandsms_mvp.R;

/**
 * Set up the front page menu items
 *
 * Created by bamptonm on 18/12/2017.
 */

public enum CarcAppsMenu {

    ITIMER(R.drawable.app_image_interval_timer, R.string.appTitleITimer, R.string.appDescITimer, "carcintervaltimer"),
    BTOWN(R.drawable.app_image_btown, R.string.appTitleBtown, R.string.appDescBtown, "btown"),
    AGD(R.drawable.app_image_agd, R.string.appTitleAGD, R.string.appDescAGB, "anygivendate"),
    BBOOKS(R.drawable.app_image_blackbooks, R.string.appTitleBlackBooks, R.string.appDescBlackBooks, "blackbooks");

    final int iconDrawable;
    final int titleResourceId;
    final int subTitleResourceId;
    final String urlExt;

    CarcAppsMenu(int drawable, int titleResourceId, int subTitleResourceId, String urlExt) {
        this.iconDrawable = drawable;
        this.titleResourceId = titleResourceId;
        this.subTitleResourceId = subTitleResourceId;
        this.urlExt = urlExt;
    }

    public int getTitleResourceId() {
        return titleResourceId;
    }

    public int getSubTitleResourceId() {
        return subTitleResourceId;
    }

    public String getUrlExtension() { return urlExt; }

    public int getIconDrawable() {
        return iconDrawable;
    }
}