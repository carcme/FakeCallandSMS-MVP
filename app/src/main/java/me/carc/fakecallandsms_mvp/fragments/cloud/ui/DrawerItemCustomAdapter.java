package me.carc.fakecallandsms_mvp.fragments.cloud.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.async.ThumbNailLoader;
import me.carc.fakecallandsms_mvp.common.ImageSize;
import me.carc.fakecallandsms_mvp.fragments.cloud.data.model.MenuDataModel;

/**
 * Drawer navigation menu items
 * Created by bamptonm on 2/14/17.
 */
public class DrawerItemCustomAdapter extends ArrayAdapter<MenuDataModel> {

    private Activity ctx;
    private int layoutResourceId;
    private List<MenuDataModel> data = null;

    public DrawerItemCustomAdapter(Activity mContext, int layoutResourceId, List<MenuDataModel> data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.ctx = mContext;
        this.data = data;
    }

    @Override
    public @NonNull View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View listItem;

        LayoutInflater inflater = ctx.getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView thumbnail = listItem.findViewById(R.id.thumbnail);
        TextView eventTxt = listItem.findViewById(R.id.eventTxt);
        TextView timeTxt = listItem.findViewById(R.id.timeTxt);

        MenuDataModel folder = data.get(position);

        if (!folder.cloudRef.isEmpty()) {
            thumbnail.setVisibility(View.VISIBLE);
            thumbnail.setScaleType(ImageView.ScaleType.CENTER);
            Glide.with(ctx)
                    .load(folder.cloudRef)
                    .placeholder(R.color.placeholder)
                    .override(ImageSize.THUMB[0], ImageSize.THUMB[1])
                    .into(thumbnail);
        } else if(folder.iconRef != 0) {
            ThumbNailLoader task = new ThumbNailLoader(thumbnail);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, folder.iconRef);
        }

        if(folder.event.isEmpty())
            eventTxt.setVisibility(View.GONE);
        else
            eventTxt.setText(folder.event);

        if(folder.timeRef != 0) {
            String date = DateFormat.format("EEE, d MMM yyyy HH:mm", folder.timeRef).toString();
            timeTxt.setText(date);
        } else
            timeTxt.setVisibility(View.GONE);

        return listItem;
    }
}
