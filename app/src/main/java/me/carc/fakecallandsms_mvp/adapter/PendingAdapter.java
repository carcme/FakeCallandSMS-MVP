package me.carc.fakecallandsms_mvp.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.fragments.PendingFragment;
import me.carc.fakecallandsms_mvp.model.FakeContact;

/**
 * Show other apps from Carc
 * Created by bamptonm on 20/01/2018.
 */

public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.ViewHolder> {

    private List<FakeContact> mList;
    private PendingFragment.ClickListener clickListener;

    public PendingAdapter(PendingFragment.ClickListener listener) {
        mList = new ArrayList<>();
        clickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FakeContact data = mList.get(position);

        holder.type.setText(data.getDatabaseType());

        if (data.getDatabaseType().startsWith("SMS"))
            holder.smsMessage.setText(data.getSmsMsg());
        else
            holder.smsMessageLayout.setVisibility(View.GONE);

        if (TextUtils.isEmpty(data.getName())) {
            if (!TextUtils.isEmpty(data.getNumber()))
                holder.contact.setText(data.getNumber());
            else
                holder.contact.setText("Unknown Caller");
        } else
            holder.contact.setText(data.getName());

        Date date = new Date(data.getTime());
        holder.date.setText(DateFormat.getMediumDateFormat(holder.date.getContext()).format(date));
        holder.time.setText(DateFormat.getTimeFormat(holder.date.getContext()).format(date));

        holder.elementHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onClick(data);
            }
        });
        holder.elementHolder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clickListener.onLongClick(data);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public FakeContact getItem(int pos) { return mList.get(pos); }

    public int getItemPosition(FakeContact contact) {
        return mList.indexOf(contact);
    }

    public void addList(List<FakeContact> list) {
        mList.addAll(list);
        Collections.sort(mList, new Sorter());
        notifyDataSetChanged();
    }

    public boolean removeItem(int position) {
        if (mList.size() >= position + 1) {
            mList.remove(position);
            return true;
        }
        return false;
    }
    public void removeItem(FakeContact contact) {
        mList.remove(contact);
    }

    public void addFakeContact(FakeContact contact) {
        mList.add(contact);
        Collections.sort(mList, new Sorter());
        notifyDataSetChanged();
    }

    private class Sorter implements Comparator<FakeContact> {

        @Override
        public int compare(FakeContact lhs, FakeContact rhs) {
            long d1 = lhs.getTime();
            long d2 = rhs.getTime();
            if (d1 < d2) {
                return -1;
            } else if (d1 > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.msgType)
        TextView type;
        @BindView(R.id.msgContact)
        TextView contact;
        @BindView(R.id.msgDate)
        TextView date;
        @BindView(R.id.msgTime)
        TextView time;
        @BindView(R.id.pendingCard)
        CardView elementHolder;

        @BindView(R.id.smsMessageLayout)
        TableRow smsMessageLayout;
        @BindView(R.id.smsMessage)
        TextView smsMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
