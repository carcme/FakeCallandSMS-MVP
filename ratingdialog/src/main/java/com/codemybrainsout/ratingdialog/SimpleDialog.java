package com.codemybrainsout.ratingdialog;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialog;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Display the feedback form only
 *
 * Created by carc on 21-02-2017.
 */

public class SimpleDialog extends AppCompatDialog implements View.OnClickListener {

    private Context context;
    private Builder builder;
    private TextView tvTitle, btnPos, btnNeg, btnNeut;
    private TextView tvMsg;


    public SimpleDialog(Context context, Builder builder) {
        super(context);
        this.context = context;
        this.builder = builder;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_simple);

        tvTitle = (TextView) findViewById(R.id.dlg_title);
        tvMsg = (TextView) findViewById(R.id.dlg_message);

        btnPos = (TextView) findViewById(R.id.dlg_btn_positive);
        btnNeg = (TextView) findViewById(R.id.dlg_btn_negative);
        btnNeut = (TextView) findViewById(R.id.dlg_btn_neutral);

        init();
    }

    private void init() {

        tvTitle.setText(builder.title);
        btnPos.setText(builder.posText);

        if (builder.negText.isEmpty())
            btnNeg.setVisibility(View.GONE);
        else
            btnNeg.setText(builder.negText);

        if (builder.neutText.isEmpty())
            btnNeut.setVisibility(View.GONE);
        else
            btnNeut.setText(builder.neutText);


        tvMsg.setText(builder.msgText);

        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        int color = typedValue.data;

        tvTitle.setTextColor(builder.titleTextColor != 0 ? ContextCompat.getColor(context, builder.titleTextColor) : ContextCompat.getColor(context, R.color.black));
        btnPos.setTextColor(builder.positiveTextColor != 0 ? ContextCompat.getColor(context, builder.positiveTextColor) : color);
        btnNeut.setTextColor(builder.neutralTextColor != 0 ? ContextCompat.getColor(context, builder.negativeTextColor) : ContextCompat.getColor(context, R.color.grey_500));
        btnNeg.setTextColor(builder.negativeTextColor != 0 ? ContextCompat.getColor(context, builder.negativeTextColor) : ContextCompat.getColor(context, R.color.grey_300));

        if (builder.msgTextColor != 0) {
            tvMsg.setTextColor(ContextCompat.getColor(context, builder.msgTextColor));
        }

        if (builder.positiveBackgroundColor != 0) {
            btnPos.setBackgroundResource(builder.positiveBackgroundColor);

        }
        if (builder.negativeTextColor != 0) {
            btnNeg.setBackgroundResource(builder.negativeTextColor);
        }
        if (builder.neutralBackgroundColor!= 0) {
            btnNeg.setBackgroundResource(builder.neutralBackgroundColor);
        }

        btnPos.setOnClickListener(this);
        btnNeg.setOnClickListener(this);
        btnNeut.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.dlg_btn_positive) {

            if (builder.dlgListener != null) {
                builder.dlgListener.onPositiveClick();
            }

            dismiss();

        } else if (view.getId() == R.id.dlg_btn_neutral) {

            if (builder.dlgListener != null) {
                builder.dlgListener.onNeutralClick();
            }

            dismiss();

        } else if (view.getId() == R.id.dlg_btn_negative) {

            if (builder.dlgListener != null) {
                builder.dlgListener.onNegativeClick();
            }

            dismiss();

        }
    }

    @Override
    public void show() {

        super.show();
    }

    public static class Builder {

        private final Context context;
        private String title, posText, negText, neutText, msgText;
        private int positiveTextColor, negativeTextColor, neutralTextColor, titleTextColor, msgTextColor;
        private int positiveBackgroundColor, neutralBackgroundColor, negativeBackgroundColor;
        private SimpleDlgListener dlgListener;


        public interface SimpleDlgListener {
            void onPositiveClick();
            void onNeutralClick();
            void onNegativeClick();
        }

        public Builder(Context context) {
            this.context = context;
            initText();
        }

        private void initText() {
            title = context.getString(R.string.todo);
            posText = context.getString(android.R.string.ok);
            negText = "";
            neutText = "";
            msgText = context.getString(R.string.todo);
        }

        public Builder titleTextColor(int titleTextColor) {
            this.titleTextColor = titleTextColor;
            return this;
        }

        public Builder positiveButtonTextColor(int resColor) {
            this.positiveTextColor = resColor;
            return this;
        }

        public Builder negativeButtonTextColor(int resColor) {
            this.negativeTextColor = resColor;
            return this;
        }

        public Builder neutralButtonTextColor(int resColor) {
            this.neutralTextColor = resColor;
            return this;
        }

        public Builder positiveButtonBackgroundColor(int resColor) {
            this.positiveBackgroundColor = resColor;
            return this;
        }

        public Builder negativeButtonBackgroundColor(int resColor) {
            this.negativeBackgroundColor = resColor;
            return this;
        }

        public Builder neutralButtonBackgroundColor(int resColor) {
            this.neutralBackgroundColor = resColor;
            return this;
        }

        public SimpleDialog.Builder onSimpleListener (SimpleDialog.Builder.SimpleDlgListener listener) {
            this.dlgListener = listener;
            return this;
        }

        public Builder title(String s) {
            this.title = s;
            return this;
        }

        public Builder message(String s) {
            this.msgText = s;
            return this;
        }

        public Builder postiveText(String s) {
            this.posText = s;
            return this;
        }

        public Builder negativeText(String s) {
            this.negText = s;
            return this;
        }

        public Builder neutralText(String s) {
            this.neutText = s;
            return this;
        }


        public Builder msgTextColor(int resColor) {
            this.msgTextColor = resColor;
            return this;
        }

        public SimpleDialog build() {
            return new SimpleDialog(context, this);
        }
    }
}
