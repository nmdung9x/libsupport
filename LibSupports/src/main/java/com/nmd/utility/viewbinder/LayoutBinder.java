package com.nmd.utility.viewbinder;

public class LayoutBinder {

    public static int getViewLayout(Object view) {
        Class<?> clazz = view.getClass();
        while (clazz != null) {
            Layout ano = clazz.getAnnotation(Layout.class);
            if (ano != null) {
                return ano.value();
            } else {
                clazz = clazz.getSuperclass();
            }
        }
        throw new RuntimeException("Must declare Layout anotation at the head of activity class");
    }

    public static int getViewLayout(Class<?> viewClass) {
        Layout ano = viewClass.getAnnotation(Layout.class);
        if (ano != null) {
            return ano.value();
        } else {
            throw new RuntimeException("Must declare Layout anotation at the head of activity class");
        }
    }

}
