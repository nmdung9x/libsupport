package com.nmd.utility.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
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

    public FrameLayout layout_root;
    public TextInputLayout layout;
    public TextInputEditText editText;
    public View view_clear;
    public boolean select = false;
    public Drawable icon;

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
        setAttributes(attrs);
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

    private void setAttributes(AttributeSet attrs) {
        TypedArray type = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomMaterialTextView, 0, 0);

        try {
            select = type.getBoolean(R.styleable.CustomMaterialTextView_actionSelect, false);
            icon = type.getDrawable(R.styleable.CustomMaterialTextView_android_drawable);
            final Drawable iconSelected = type.getDrawable(R.styleable.CustomMaterialTextView_drawableSelected);
            final Drawable iconStart = type.getDrawable(R.styleable.CustomMaterialTextView_drawableStart);
            final Drawable iconEnd = type.getDrawable(R.styleable.CustomMaterialTextView_drawableEnd);
            if (iconEnd != null) icon = iconEnd;

            int padding = type.getDimensionPixelSize(R.styleable.CustomMaterialTextView_textPadding, 0);
            int paddingTop = type.getDimensionPixelSize(R.styleable.CustomMaterialTextView_textPaddingTop, 0);
            int paddingBottom = type.getDimensionPixelSize(R.styleable.CustomMaterialTextView_textPaddingBottom, 0);
            int paddingStart = type.getDimensionPixelSize(R.styleable.CustomMaterialTextView_textPaddingStart, 0);
            int paddingEnd = type.getDimensionPixelSize(R.styleable.CustomMaterialTextView_textPaddingEnd, 0);

            String text = type.getString(R.styleable.CustomMaterialTextView_android_text);
            String hint = type.getString(R.styleable.CustomMaterialTextView_android_hint);
            int textColor = type.getColor(R.styleable.CustomMaterialTextView_android_textColor, Color.BLACK);
            int textSize = type.getDimensionPixelSize(R.styleable.CustomMaterialTextView_textSize, 0);
            int imeOptions = type.getInt(R.styleable.CustomMaterialTextView_android_imeOptions, IME_ACTION_NONE);
            int inputType = type.getInt(R.styleable.CustomMaterialTextView_android_inputType, TYPE_NULL);
            int lines = type.getInt(R.styleable.CustomMaterialTextView_android_lines, 0);
            int maxLines = type.getInt(R.styleable.CustomMaterialTextView_android_maxLines, 0);
            int nextFocusDown = type.getInt(R.styleable.CustomMaterialTextView_android_nextFocusDown, -1);
            int gravity = type.getInt(R.styleable.CustomMaterialTextView_android_gravity, Gravity.NO_GRAVITY);

            if (initView()) {
                layout.setHint(hint);
                editText.setText(text);
                editText.setTextColor(textColor);

                if (textSize > 0 && type.hasValue(R.styleable.CustomMaterialTextView_textSize)) {
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                }

                if (padding > 0) {
                    editText.setPadding(padding, padding, padding, padding);
                } else {
                    if (paddingStart > 0 || paddingTop > 0 || paddingEnd > 0 || paddingBottom > 0) {
                        editText.setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom);
                    }
                }

                editText.setImeOptions(imeOptions);

                if (inputType != TYPE_NULL && type.hasValue(R.styleable.CustomMaterialTextView_android_inputType)) editText.setInputType(inputType);

                if (lines > 0 && type.hasValue(R.styleable.CustomMaterialTextView_android_lines)) editText.setLines(lines);
                if (maxLines > 0 && type.hasValue(R.styleable.CustomMaterialTextView_android_maxLines)) editText.setMaxLines(maxLines);

                if (nextFocusDown != -1) editText.setNextFocusDownId(nextFocusDown);

                if (gravity != Gravity.NO_GRAVITY && type.hasValue(R.styleable.CustomMaterialTextView_android_gravity)) editText.setGravity(gravity);

                if (icon != null) {
                    layout.setEndIconDrawable(icon);
                    layout.setEndIconMode(END_ICON_CUSTOM);
                }

                if (iconStart != null) {
                    layout.setStartIconDrawable(iconStart);
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
                            clear();
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
                            if (editText.getText().toString().length() > 0) {
                                if (iconSelected != null) {
                                    layout.setEndIconDrawable(iconSelected);
                                    layout.setEndIconMode(END_ICON_CUSTOM);
                                    view_clear.setVisibility(View.VISIBLE);
                                } else DebugLog.logi("iconSelected == null");
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

    public void clear() {
        setText("");
        if (editText != null) editText.clearFocus();
        if (view_clear != null) view_clear.setVisibility(View.GONE);
        if (icon != null && layout != null) {
            layout.setEndIconDrawable(icon);
            layout.setEndIconMode(END_ICON_CUSTOM);
        }
        if (callback != null) callback.onClear(CustomMaterialTextView.this);
    }
}
