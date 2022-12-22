package com.nmd.utility.viewbinder;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nmd.utility.view.ViewClickAnimation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by QuangPH on 6/15/2016.
 */
public class ViewBinder {

    /**
     * @param v
     * @param obj contain v
     */
    public static void bind(View v, Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        ViewFinder finder = new ViewFinder.ByView(v);
        for (Field field : fields) {
            if (View.class.isAssignableFrom(field.getType())) {
                setFieldValue(field, obj, finder);
            }
        }
        Method[] methods = obj.getClass().getDeclaredMethods();
        for (Method method : methods) {
            createOnClickListener(obj, method, finder);
        }
    }

    public static void bind(Activity activity) {
        Field[] fields = activity.getClass().getDeclaredFields();
        ViewFinder finder = new ViewFinder.ByActivity(activity);
        for (Field field : fields) {
            if (View.class.isAssignableFrom(field.getType())) {
                setFieldValue(field, activity, finder);
            }
        }
        Method[] methods = activity.getClass().getDeclaredMethods();
        for (Method method : methods) {
            createOnClickListener(activity, method, finder);
        }
    }

    public static void bind(View view) {
        bind(view, view);
    }

    public static void bind(Activity activity, Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        ViewFinder finder = new ViewFinder.ByActivity(activity);
        for (Field field : fields) {
            if (View.class.isAssignableFrom(field.getType())) {
                setFieldValue(field, obj, finder);
            }
        }
        Method[] methods = obj.getClass().getDeclaredMethods();
        for (Method method : methods) {
            createOnClickListener(obj, method, finder);
        }
    }

    private static void setFieldValue(Field field, Object obj, ViewFinder finder) {
        try {
            BindView ano= field.getAnnotation(BindView.class);
            if (ano != null) {
                field.setAccessible(true);
                field.set(obj, finder.findViewById(ano.value()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void compoundView(ViewGroup container) {
        Class<?> clazz = getAnnoSuperClass(container.getClass());
        Annotation ano = clazz.getAnnotation(Layout.class);
        if (ano != null) {
            int layout = ((Layout)ano).value();
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            inflater.inflate(layout, container, true);
            bind(clazz, container);
        }
    }

    private static void bind(Class<?> parentView, View view) {
        Field[] fields = parentView.getDeclaredFields();
        ViewFinder finder = new ViewFinder.ByView(view);
        for (Field field : fields) {
            if (View.class.isAssignableFrom(field.getType())) {
                setFieldValue(field, view, finder);
            }
        }
        Method[] methods = parentView.getDeclaredMethods();
        for (Method method : methods) {
            createOnClickListener(view, method, finder);
        }
    }

    public static int getViewLayout(Class<?> viewClass) {
        Layout ano = viewClass.getAnnotation(Layout.class);
        if (ano != null) {
            return ano.value();
        } else {
            throw new RuntimeException("Must declare Layout anotation at the head of activity class");
        }
    }

    public static void bindAttributesForView(View view, AttributeSet attrs, int[] styleable) {
        TypedArray a = view.getContext().obtainStyledAttributes(attrs, styleable);
        bindAttrsForClass(view.getClass(), view, a);
        a.recycle();
    }

    public static void bindAttributesForCompoundView(View view, AttributeSet attrs, int[] styleable) {
        Class<?> clazz = getAnnoSuperClass(view.getClass());
        TypedArray a = view.getContext().obtainStyledAttributes(attrs, styleable);
        bindAttrsForClass(clazz, view, a);
        a.recycle();
    }

    private static void bindAttrsForClass(Class clazz, View v, TypedArray a){
        for(Field field : clazz.getDeclaredFields()){
            if (field.isAnnotationPresent(Attrs.class)) {
                Attrs attrs = field.getAnnotation(Attrs.class);
                if (attrs != null) {
                    setAttrsVal(v, field, a, attrs);
                }
            }
        }
    }

    private static void setAttrsVal(View v, Field field, TypedArray a, Attrs attrs) {
        field.setAccessible(true);
        try {
            switch (attrs.type()) {
                case Attrs.DIMENSION:
                    field.setFloat(v, a.getDimension(attrs.index(), Float.parseFloat(attrs.defVal())));
                    break;
                case Attrs.COLOR:
                    field.setInt(v, a.getColor(attrs.index(), Color.parseColor(attrs.defVal())));
                    break;
                case Attrs.INTEGER:
                    field.setInt(v, a.getInt(attrs.index(), Integer.parseInt(attrs.defVal())));
                    break;
                case Attrs.FLOAT:
                    field.setFloat(v, a.getFloat(attrs.index(), Float.parseFloat(attrs.defVal())));
                    break;
                case Attrs.BOOLEAN:
                    field.setBoolean(v, a.getBoolean(attrs.index(), Boolean.parseBoolean(attrs.defVal())));
                    break;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private static void createOnClickListener(final Object obj, Method method, ViewFinder finder){
        OnClick onClick = method.getAnnotation(OnClick.class);
        if(onClick != null){
            View view = finder.findViewById(onClick.value());
            if (view == null) return;
            view.setOnClickListener(v -> {
                try {
                    method.setAccessible(true);
                    method.invoke(obj);
                } catch (Exception e){
                    e.printStackTrace();
                }
            });
        }

        OnClickAnimation onClickAnimation = method.getAnnotation(OnClickAnimation.class);
        if(onClickAnimation != null){
            View view = finder.findViewById(onClickAnimation.value());
            if (view == null) return;
            view.setOnClickListener(new ViewClickAnimation() {
                @Override
                public void onClickAnimationListener(View view) {
                    try {
                        method.setAccessible(true);
                        method.invoke(obj);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private static Class<?> getAnnoSuperClass(Class<?> clazz) {
        Class superclass = clazz;
        while (superclass != null) {
            if (isCompound(superclass)) {
                return superclass;
            }

            superclass = superclass.getSuperclass();
        }

        return clazz;
    }

    private static boolean isCompound(Class<?> clazz) {
        Layout ano = clazz.getAnnotation(Layout.class);
        return ano != null;
    }
}
