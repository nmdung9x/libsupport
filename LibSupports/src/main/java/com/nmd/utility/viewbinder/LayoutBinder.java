package com.nmd.utility.viewbinder;

public class LayoutBinder {

    public static Integer getViewLayout(Object view) throws RuntimeException {
        Class<?> clazz = view.getClass();
        while (clazz != null) {
            Layout ano = clazz.getAnnotation(Layout.class);
            if (ano != null) {
                return ano.value();
            } else {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    public static Integer getViewLayout(Class<?> viewClass) throws RuntimeException {
        Layout ano = viewClass.getAnnotation(Layout.class);
        if (ano != null) {
            return ano.value();
        }
        return null;
    }

}
