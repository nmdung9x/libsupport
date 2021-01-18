package com.nmd.utility.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.nmd.utility.DebugLog;
import com.nmd.utility.R;

import static android.text.InputType.TYPE_NULL;
import static android.view.inputmethod.EditorInfo.IME_ACTION_NONE;
import static com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM;

public class CustomMaterialTextView extends FrameLayout {
    Context context;

    FrameLayout layout_root;
    TextInputLayout layout;
    TextInputEditText editText;
    View view_clear;

    ViewActionCallback callback;

    public interface ViewActionCallback {
        void onAction(CustomMaterialTextView view);
        void onClear(CustomMaterialTextView view);
    }

    public CustomMaterialTextView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public CustomMaterialTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        TypedArray type = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomMaterialTextView, 0, 0);

        try {
            boolean select = type.getBoolean(R.styleable.CustomMaterialTextView_action_select, false);
            final Drawable icon = type.getDrawable(R.styleable.CustomMaterialTextView_android_drawable);
            final Drawable iconSelected = type.getDrawable(R.styleable.CustomMaterialTextView_drawable_selected);
            int padding = type.getDimensionPixelSize(R.styleable.CustomMaterialTextView_text_padding, 0);

            String text = type.getString(R.styleable.CustomMaterialTextView_android_text);
            String hint = type.getString(R.styleable.CustomMaterialTextView_android_hint);
            int textColor = type.getColor(R.styleable.CustomMaterialTextView_android_textColor, Color.BLACK);
            int textSize = type.getDimensionPixelSize(R.styleable.CustomMaterialTextView_text_size, 0);
            int imeOptions = type.getInt(R.styleable.CustomMaterialTextView_android_imeOptions, IME_ACTION_NONE);
            int inputType = type.getInt(R.styleable.CustomMaterialTextView_android_inputType, TYPE_NULL);
            int lines = type.getInt(R.styleable.CustomMaterialTextView_android_lines, 0);
            int maxLines = type.getInt(R.styleable.CustomMaterialTextView_android_maxLines, 0);
            int nextFocusDown = type.getInt(R.styleable.CustomMaterialTextView_android_nextFocusDown, -1);

            if (initView()) {
                layout.setHint(hint);
                editText.setText(text);
                editText.setTextColor(textColor);

                if (textSize > 0) {
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                }

                editText.setPadding(padding, 0, padding, 0);
                editText.setImeOptions(imeOptions);

                if (inputType != TYPE_NULL) editText.setInputType(inputType);

                if (lines > 0) editText.setLines(lines);
                if (maxLines > 0) editText.setMaxLines(maxLines);

                if (nextFocusDown != -1) editText.setNextFocusDownId(nextFocusDown);

                if (icon != null) {
                    layout.setEndIconDrawable(icon);
                    layout.setEndIconMode(END_ICON_CUSTOM);
                }

                if (select) {
                    editText.setOnFocusChangeListener(new OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus && callback != null) {
                                editText.clearFocus();
                                callback.onAction(CustomMaterialTextView.this);
                            }
                        }
                    });

                    layout_root.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editText.clearFocus();
                            if (callback != null) callback.onAction(CustomMaterialTextView.this);
                        }
                    });

                    view_clear.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setText("");
                            if (callback != null) callback.onClear(CustomMaterialTextView.this);
                        }
                    });

                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            view_clear.setVisibility(editText.getText().toString().isEmpty() ? View.GONE : View.VISIBLE);
                            if (editText.getText().toString().isEmpty()) {
                                if (icon != null) {
                                    layout.setEndIconDrawable(icon);
                                    layout.setEndIconMode(END_ICON_CUSTOM);
                                }
                            } else {
                                if (iconSelected != null) {
                                    layout.setEndIconDrawable(iconSelected);
                                    layout.setEndIconMode(END_ICON_CUSTOM);
                                }
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            DebugLog.loge(e);
        } finally {
            type.recycle();
        }


    }

    private boolean initView() {
        View viewRoot = LayoutInflater.from(context).inflate(R.layout.view_material_custom, null);
        addView(viewRoot);

        layout_root = viewRoot.findViewById(R.id.v_root);
        layout = viewRoot.findViewById(R.id.ti_layout);
        editText = viewRoot.findViewById(R.id.ti_et_view);
        view_clear = viewRoot.findViewById(R.id.v_clear);

        return true;
    }

    public void setViewActionCallback(ViewActionCallback callback) {
        this.callback = callback;
    }

    public void setHint(String hint) {
        if (layout != null) layout.setHint(hint);
    }

    public String getHint() {
        return layout != null ? layout.getHint().toString() : "";
    }

    public void setText(String text) {
        if (editText != null) editText.setText(text);
    }

    public String getText() {
        return editText != null ? editText.getText().toString() : "";
    }
}
