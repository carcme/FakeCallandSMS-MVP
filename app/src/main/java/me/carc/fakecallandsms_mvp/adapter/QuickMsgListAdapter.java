package me.carc.fakecallandsms_mvp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.listeners.QuickMsgListListener;
import me.carc.fakecallandsms_mvp.model.PreSetMsg;
/**
 * A custom adapter to use with the RecyclerView widget.
 */
public class QuickMsgListAdapter extends RecyclerView.Adapter<QuickMsgListAdapter.Holder> {

    private ArrayList<PreSetMsg> mItems = new ArrayList<>();
    private QuickMsgListListener onClickListener;

    public QuickMsgListAdapter(QuickMsgListListener listener) {
        onClickListener = listener;
    }

    @Override
    public @NonNull Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.quickmsg_list_item_layout, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int pos) {

        PreSetMsg item = mItems.get(pos);

        holder.name.setText(item.getMsg());

        if(!TextUtils.isEmpty(item.getName()))
            holder.desc.setText(item.getName());
        else if(!TextUtils.isEmpty(item.getNumber()))
            holder.desc.setText(item.getNumber());
        else
            holder.desc.setText(R.string.shared_string_unknown);

        holder.icon.setImageResource(R.drawable.ic_dialpad);
        holder.more.setVisibility(View.VISIBLE);

        /* IMAGE CLICKED*/
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItems.size() > 0)
                    onClickListener.onClickImage(mItems.get(holder.getAdapterPosition()));
            }
        });

        /* VIEW CLICKED*/
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItems.size() > 0)
                    onClickListener.onClick(mItems.get(holder.getAdapterPosition()));
            }
        });

        /* VIEW LONG CLICKED*/
        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mItems.size() > 0)
                    onClickListener.onLongTouch(mItems.get(holder.getAdapterPosition()));
                return true;
            }
        });

        /* OVERFLOW CLICKED*/
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItems.size() > 0) {
                    onClickListener.onClickOverflow(mItems.get(holder.getAdapterPosition()));
                    removeItem(holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addItems(List<PreSetMsg> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    private void removeItem(int position) {
        if (mItems.size() >= position + 1) {
            mItems.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeAll() {
        mItems.clear();
        notifyDataSetChanged();
    }

    /**
     * View Holder
     */
    static class Holder extends RecyclerView.ViewHolder {

        View mView;

        ImageView icon;
        TextView name;
        TextView desc;

        ImageView more;

        private Holder(View itemView) {
            super(itemView);
            mView = itemView;

            this.icon = itemView.findViewById(R.id.quickMsgListIcon);
            this.name = itemView.findViewById(R.id.quickMsgMsg);
            this.desc = itemView.findViewById(R.id.quickMsgName);
            this.more = itemView.findViewById(R.id.quickMsgMore);
        }
    }
}
