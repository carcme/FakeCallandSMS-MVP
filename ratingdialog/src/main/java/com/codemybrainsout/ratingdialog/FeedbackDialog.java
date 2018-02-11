package com.codemybrainsout.ratingdialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Inspired / converted from com.codemybrainsout.rating:ratingdialog
 *
 * Created by carc on 21-02-2017.
 */

public class FeedbackDialog extends AppCompatDialog implements View.OnClickListener {

    private Context context;
    private Builder builder;
    private TextView tvFeedback, tvSubmit, tvCancel;
    private EditText etFeedback;


    public FeedbackDialog(Context context, Builder builder) {
        super(context);
        this.context = context;
        this.builder = builder;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_feedback);

        tvFeedback = (TextView) findViewById(R.id.dlg_feedback_title);
        tvSubmit = (TextView) findViewById(R.id.dlg_btn_feedback_submit);
        tvCancel = (TextView) findViewById(R.id.dlg_btn_feedback_cancel);
        etFeedback = (EditText) findViewById(R.id.dlg_feedback_edit);

        init();
    }

    private void init() {

        tvFeedback.setText(builder.formTitle);
        tvSubmit.setText(builder.submitText);
        tvCancel.setText(builder.cancelText);
        etFeedback.setHint(builder.feedbackFormHint);
        etFeedback.setText(builder.formText);

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        int color = typedValue.data;

        tvFeedback.setTextColor(builder.titleTextColor != 0 ? ContextCompat.getColor(context, builder.titleTextColor) : ContextCompat.getColor(context, R.color.black));
        tvSubmit.setTextColor(builder.positiveTextColor != 0 ? ContextCompat.getColor(context, builder.positiveTextColor) : color);
        tvCancel.setTextColor(builder.negativeTextColor != 0 ? ContextCompat.getColor(context, builder.negativeTextColor) : ContextCompat.getColor(context, R.color.grey_500));

        if (builder.feedBackTextColor != 0) {
            etFeedback.setTextColor(ContextCompat.getColor(context, builder.feedBackTextColor));
        }

        if (builder.positiveBackgroundColor != 0) {
            tvSubmit.setBackgroundResource(builder.positiveBackgroundColor);

        }
        if (builder.negativeBackgroundColor != 0) {
            tvCancel.setBackgroundResource(builder.negativeBackgroundColor);
        }

        tvSubmit.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.dlg_btn_feedback_submit) {

            String feedback = etFeedback.getText().toString().trim();
            if (!builder.allowEmpty && TextUtils.isEmpty(feedback)) {

                Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
                etFeedback.startAnimation(shake);
                return;
            }

            if (builder.feedbackDialogFormListener != null) {
                builder.feedbackDialogFormListener.onFormSubmitted(feedback);
            }

            dismiss();

        } else if (view.getId() == R.id.dlg_btn_feedback_cancel) {

            if (builder.feedbackDialogFormListener != null) {
                builder.feedbackDialogFormListener.onFormCancel();
            }

            dismiss();
        }
    }

    public TextView getFormTitleTextView() {
        return tvFeedback;
    }

    public TextView getFormSumbitTextView() {
        return tvSubmit;
    }

    public TextView getFormCancelTextView() {
        return tvCancel;
    }

    @Override
    public void show() {

        super.show();
    }

    public static class Builder {

        private final Context context;
        private String formTitle, formText, submitText, cancelText, feedbackFormHint;
        private boolean allowEmpty;
        private int positiveTextColor, negativeTextColor, titleTextColor, feedBackTextColor;
        private int positiveBackgroundColor, negativeBackgroundColor;
        private FeedbackDialogFormListener feedbackDialogFormListener;


        public interface FeedbackDialogFormListener {
            void onFormSubmitted(String feedback);
            void onFormCancel();
        }

        public Builder(Context context) {
            this.context = context;
            initText();
        }

        private void initText() {
            formTitle = context.getString(R.string.rating_dialog_feedback_title);
            submitText = context.getString(R.string.rating_dialog_submit);
            cancelText = context.getString(R.string.rating_dialog_cancel);
            feedbackFormHint = context.getString(R.string.rating_dialog_suggestions);
        }

        public Builder titleTextColor(int titleTextColor) {
            this.titleTextColor = titleTextColor;
            return this;
        }

        public Builder positiveButtonTextColor(int positiveTextColor) {
            this.positiveTextColor = positiveTextColor;
            return this;
        }

        public Builder negativeButtonTextColor(int negativeTextColor) {
            this.negativeTextColor = negativeTextColor;
            return this;
        }

        public Builder positiveButtonBackgroundColor(int positiveBackgroundColor) {
            this.positiveBackgroundColor = positiveBackgroundColor;
            return this;
        }

        public Builder negativeButtonBackgroundColor(int negativeBackgroundColor) {
            this.negativeBackgroundColor = negativeBackgroundColor;
            return this;
        }

        public FeedbackDialog.Builder onFeedbackFormSumbit(FeedbackDialog.Builder.FeedbackDialogFormListener fbDlgFormListener) {
            this.feedbackDialogFormListener = fbDlgFormListener;
            return this;
        }

        public Builder formTitle(String formTitle) {
            this.formTitle = formTitle;
            return this;
        }

        public Builder formHint(String formHint) {
            this.feedbackFormHint = formHint;
            return this;
        }

        public Builder formText(String formText) {
            this.formText = formText;
            return this;
        }

        public Builder formSubmitText(String submitText) {
            this.submitText = submitText;
            return this;
        }

        public Builder allowEmpty(boolean allowEmpty) {
            this.allowEmpty = allowEmpty;
            return this;
        }


        public Builder formCancelText(String cancelText) {
            this.cancelText = cancelText;
            return this;
        }


        public Builder feedbackTextColor(int feedBackTextColor) {
            this.feedBackTextColor = feedBackTextColor;
            return this;
        }

        public FeedbackDialog build() {
            return new FeedbackDialog(context, this);
        }
    }
}
