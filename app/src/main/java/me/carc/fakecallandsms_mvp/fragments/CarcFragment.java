package me.carc.fakecallandsms_mvp.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.adapter.CarcAppsAdapter;
import me.carc.fakecallandsms_mvp.model.CarcAppsMenu;


public class CarcFragment extends Fragment {

    private static final String TAG = CarcFragment.class.getName();
    public static final String TAG_ID = "CarcFragment";

    private final static String MARKET_REF = "&referrer=utm_source%3Dme.carc.fakecallandsms_mvp";

    public interface ClickListener {
        void onClick(CarcAppsMenu item);
    }

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppThemeDark);
        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        // inflate the layout using the cloned inflater, not default inflater
        View rootView = localInflater.inflate(R.layout.carc_fragment, container, false);

//        View rootView = inflater.inflate(R.layout.carc_fragment, container, false);
        ButterKnife.bind(this, rootView);
        setRetainInstance(true);


        CarcAppsAdapter adapter = new CarcAppsAdapter(buildMenuItems(), new ClickListener() {
            @Override
            public void onClick(CarcAppsMenu item) {
                startActivity(openPlayStore(true, item.getUrlExtension()));
            }
        });

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    private List<CarcAppsMenu> buildMenuItems() {
        List<CarcAppsMenu> items = new LinkedList<>();
        items.add(CarcAppsMenu.AGD);
        items.add(CarcAppsMenu.BTOWN);
        items.add(CarcAppsMenu.BBOOKS);

        return items;
    }

    /* PLAY STORE HELPERS */

    private String getMarketUrl(String urlExt) {
        return String.format(getString(R.string.carc_app_base_url), urlExt).concat(MARKET_REF);
    }

    private Intent openPlayStore(boolean openInBrowser, String urlExt) {
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getMarketUrl(urlExt)));
        if (isIntentAvailable(marketIntent)) {
            return marketIntent;
        }
        if (openInBrowser) {
            return openLink(getMarketUrl(urlExt));
        }
        return marketIntent;
    }

    public boolean isIntentAvailable(Intent intent) {
        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public Intent openLink(String url) {
        // if protocol isn't defined use http by default
        if (!TextUtils.isEmpty(url) && !url.contains("://")) {
            url = "http://" + url;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        return intent;
    }
}