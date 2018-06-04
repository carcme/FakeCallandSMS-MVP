package me.carc.fakecallandsms_mvp.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.adapter.PendingAdapter;
import me.carc.fakecallandsms_mvp.alarm.AlarmHelper;
import me.carc.fakecallandsms_mvp.app.App;
import me.carc.fakecallandsms_mvp.common.C;
import me.carc.fakecallandsms_mvp.db.AppDatabase;
import me.carc.fakecallandsms_mvp.model.FakeContact;

/**
 * Show previously scheduled alarms
 */
public class PendingFragment extends Fragment {

    private static final String TAG = PendingFragment.class.getName();
    public static final String TAG_ID = "PendingFragment";

    private PendingAdapter adapter;

    public interface ClickListener {
        void onClick(FakeContact item);
        void onLongClick(FakeContact item);
    }

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.emptyContainer) LinearLayout emptyContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pending_fragment, container, false);
        ButterKnife.bind(this, rootView);
        setRetainInstance(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
        });

        adapter = new PendingAdapter(clickListener);
        recyclerView.setAdapter(adapter);

        getDatabaseEntries();

        return rootView;
    }

    public void addNewPending(FakeContact contact) {
        adapter.addFakeContact(contact);
        checkIfEmpty();
    }

    private void getDatabaseEntries() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final List<FakeContact> entries = getDatabase().fakeContactDao().getAllEntries();
                if (entries.size() > 0) {

                    // clean out old entries
                    long currentTime = System.currentTimeMillis();
                    for (Iterator<FakeContact> iterator = entries.iterator(); iterator.hasNext(); ) {
                        FakeContact contact = iterator.next();
                        if (contact.getTime() < currentTime) {
                            getDatabase().fakeContactDao().delete(contact);
                            iterator.remove();
                        }
                    }
                    // show only the valid entries
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addList(entries);
                            checkIfEmpty();
                        }
                    });
                }
            }
        });
    }


    private void checkIfEmpty() {
        if (adapter.getItemCount() == 0)
            emptyContainer.setVisibility(View.VISIBLE);
        else
            emptyContainer.setVisibility(View.GONE);
    }

    private void deleteDatabaseEntry(final FakeContact contact) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                getDatabase().fakeContactDao().delete(contact);
            }
        });
    }

    private final ClickListener clickListener = new ClickListener() {
        @Override
        public void onClick(FakeContact item) {
        }

        @Override
        public void onLongClick(final FakeContact item) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_delete)
                    .setTitle("Delete Entry?")
                    .setMessage("Do you really want to remove this entry?")
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .setNeutralButton("Remove All", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeAllItems();
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteDatabaseEntry(item);
                            if (item.getDatabaseType().startsWith("CALL"))
                                AlarmHelper.getInstance().removeAlarm(0, item);
                            else
                                AlarmHelper.getInstance().removeAlarm(1, item);

                            int index = adapter.getItemPosition(item);
                            if (adapter.removeItem(index)) {
                                adapter.notifyItemRemoved(index);
                                adapter.notifyItemRangeChanged(index, adapter.getItemCount());
                            }
                            checkIfEmpty();
                        }
                    });
            dlg.show();
        }
    };

    private void removeAllItems() {

        AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_delete_all)
                .setTitle("Remove All Entries?")
                .setMessage("Do you really want to delete all entries? You can not undo this")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAllConfirmed();
                        dialog.dismiss();
                    }
                });
        dlg.show();
    }

    private void removeAllConfirmed() {

        // TODO: 26/01/2018 maybe put a short delay on the while to animate the removal
        while (adapter.getItemCount() > 0) {
            FakeContact item = adapter.getItem(0);
            if (item.getDatabaseType().startsWith("CALL"))
                AlarmHelper.getInstance().removeAlarm(C.TYPE_CALL, item);
            else
                AlarmHelper.getInstance().removeAlarm(C.TYPE_SMS, item);
            if (adapter.removeItem(0)) {
                adapter.notifyItemRemoved(0);
                adapter.notifyItemRangeChanged(0, adapter.getItemCount());
            }
        }
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                getDatabase().fakeContactDao().nukeTable();
            }
        });
        checkIfEmpty();
    }

    private AppDatabase getDatabase() {
        return ((App) getActivity().getApplicationContext()).getDB();
    }
}