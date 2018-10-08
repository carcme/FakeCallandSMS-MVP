package me.carc.fakecallandsms_mvp.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codemybrainsout.ratingdialog.PredefMsgDialog;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.adapter.QuickMsgListAdapter;
import me.carc.fakecallandsms_mvp.common.utils.ViewUtils;
import me.carc.fakecallandsms_mvp.db.PreSetMsgViewModel;
import me.carc.fakecallandsms_mvp.listeners.QuickMsgListListener;
import me.carc.fakecallandsms_mvp.model.PreSetMsg;
import me.carc.fakecallandsms_mvp.widgets.DividerItemDecoration;

import static me.carc.fakecallandsms_mvp.fragments.FakeSmsFragment.REQ_QUICK_MSG;

/**
 * Show the favorites list
 * Created by bamptonm on 31/08/2017.
 */
public class QuickMsgListDialogFragment extends DialogFragment {

    public static final String TAG = QuickMsgListDialogFragment.class.getName();
    public static final String ID_TAG = "HistoryListDialogFragment";

    public static final String QUICK_MSG_MSG = "QUICK_MSG_MSG";
    public static final String QUICK_MSG_FROM = "QUICK_MSG_FROM";

    private Unbinder unbinder;
    private PreSetMsgViewModel mViewModel;

    @BindView(R.id.quickMsgListCollapsingToolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.quickMsgListToolbar) Toolbar toolbar;
    @BindView(R.id.quickMsgListHeader) ImageView imageHeader;
    @BindView(R.id.quickMsgListRecyclerview) RecyclerView recyclerView;
    @BindView(R.id.emptyListView) TextView emptyListView;
    @BindView(R.id.fabClose) FloatingActionButton fabClose;
    @BindView(R.id.fabAdd) FloatingActionButton fabAdd;

    public static void showInstance(AppCompatActivity activity, Fragment callingFragment) {
        try {
            Bundle bundle = new Bundle();
            QuickMsgListDialogFragment fragment = new QuickMsgListDialogFragment();
            fragment.setTargetFragment(callingFragment, FakeSmsFragment.REQ_QUICK_MSG);
            fragment.setArguments(bundle);
            fragment.show(activity.getSupportFragmentManager(), ID_TAG);

        } catch (RuntimeException e) {
            Log.d(TAG, "showInstance: ");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(STYLE_NO_FRAME, R.style.Dialog_Fullscreen);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Objects.requireNonNull(getDialog().getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_list_recyclerview_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        Bundle args = getArguments();
        if (args != null) {


            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//            layoutManager.setReverseLayout(true);
//            layoutManager.setStackFromEnd(true);

            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(Objects.requireNonNull(getActivity()), DividerItemDecoration.VERTICAL_LIST);
            recyclerView.addItemDecoration(itemDecoration);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(scrollListener);

            Drawable drawable = ViewUtils.changeIconColor(getContext(), R.drawable.ic_arrow_back, R.color.white);
            toolbar.setNavigationIcon(drawable);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            collapsingToolbar.setTitleEnabled(true);
            collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getActivity(), R.color.white));
        }

        loadDatabase();
        return view;
    }


    private void loadDatabase() {
        mViewModel = ViewModelProviders.of(this).get(PreSetMsgViewModel.class);
        mViewModel.getAllMessages().observe(this, new Observer<List<PreSetMsg>>() {
            @Override
            public void onChanged(@Nullable final List<PreSetMsg> preSetMsgs) {
                if(preSetMsgs != null && preSetMsgs.size() >  0) {
                    QuickMsgListAdapter adapter = new QuickMsgListAdapter(listListener);
                    adapter.addItems(preSetMsgs);
                    recyclerView.setAdapter(adapter);
                    emptyListView.setVisibility(View.GONE);
                } else
                    emptyListView.setVisibility(View.VISIBLE);

            }
        });
    }

    private void removeAllItems() {
        AlertDialog.Builder dlg = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setIcon(R.drawable.ic_delete_all)
                .setTitle("Remove Unlocked Entries?")
                .setMessage("This will delete all unlocked entries? You can not undo this")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((QuickMsgListAdapter) recyclerView.getAdapter()).removeAll();
                        nukeDatabase();
                        dialog.dismiss();
                    }
                });
        dlg.show();
    }

    private void nukeDatabase() {
        mViewModel.nuke();
    }

    private final RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 0)
                fabClose.hide();
            else
                fabClose.show();
        }
    };

    @OnClick(R.id.fabAdd)
    void addMsg() {
        PredefMsgDialog builder = new PredefMsgDialog.Builder(getActivity())
                .onSimpleListener(new PredefMsgDialog.Builder.SimpleDlgListener() {
                    @Override
                    public void onPositiveClick(String msg, String from) {
                        PreSetMsg preSetMsg = new PreSetMsg();
                        preSetMsg.setMsg(msg);
                        preSetMsg.setName(from);
                        preSetMsg.setTimeMs();

                        mViewModel.insert(preSetMsg);

                        sendResult(preSetMsg);
                        close();
                    }

                    @Override
                    public void onNegativeClick() {
                    }
                })
                .build();
        builder.show();
    }

    @OnClick(R.id.fabClose)
    void exit() {
        ViewUtils.createAlphaAnimator(fabClose, false, getResources()
                .getInteger(R.integer.fab_fade_duration) * 2)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                    }
                }).start();
    }

    public void hide() {
        getDialog().hide();
    }

    public void show() {
        getDialog().show();
    }

    public void close() {
        dismiss();
    }
    
    private final QuickMsgListListener listListener = new QuickMsgListListener() {
        @Override
        public void onClick(PreSetMsg item) {
            sendResult(item);
            close();
        }

        @Override
        public void onClickImage(PreSetMsg item) {

        }

        @Override
        public void onClickOverflow(PreSetMsg item) {
            mViewModel.delete(item);
        }

        @Override
        public void onLongTouch(final PreSetMsg item) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                    .setIcon(R.drawable.ic_delete)
                    .setTitle("Delete Record?")
                    .setMessage("Do you really want to remove this record?")
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
                            mViewModel.delete(item);
                        }
                    });
            dlg.show();
        }
    };


    private void sendResult(PreSetMsg item) {
        Intent intent = new Intent();
        intent.putExtra(QUICK_MSG_MSG, item.getMsg());
        intent.putExtra(QUICK_MSG_FROM, item.getName());
        Objects.requireNonNull(getTargetFragment()).onActivityResult(
                getTargetRequestCode(), REQ_QUICK_MSG, intent);
    }
}
