package com.nmd.utility.viewbinder;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.viewbinding.ViewBinding;

import com.nmd.utility.DebugLog;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public class ViewBindingCreator {
    public static <Binding extends ViewBinding> Binding create(Class<?> bindingClazz, LayoutInflater inflater, ViewGroup root) {
        return create(bindingClazz, inflater, root, false);
    }

    @SuppressWarnings("unchecked")
    public static <Binding extends ViewBinding> Binding create(Class<?> bindingClazz, LayoutInflater inflater, ViewGroup root, boolean attachToRoot) {
        try {
            ParameterizedType parameterizedType = (ParameterizedType) bindingClazz.getGenericSuperclass();
            Type[] types = Objects.requireNonNull(parameterizedType).getActualTypeArguments();
            Class<?> clazz = null;
            for (Type type : types) {
                Class<?> temp = (Class<?>) type;
                if (ViewBinding.class.isAssignableFrom(temp)) {
                    clazz = temp;
                }
            }
            if (clazz != null) {
                Method method = clazz.getMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
                return (Binding) method.invoke(null, inflater, root, attachToRoot);
            }
        } catch (Exception e) {
            DebugLog.loge("Binding class not found! Ignored.");
        }
        
        return null;
    }
}
