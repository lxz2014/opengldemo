package com.lxz.opengl;

import android.util.Log;

public class Lg {
    private static String pre = "opengl-";
    public static void e(String msg) {
        Log.e(pre + "tag", msg);
    }

    public static void e(String tag, String msg) {
        Log.e(pre + tag, msg);
    }

    public static void e(String tag, String msg, Object ...args) {
        Log.e(pre + tag, String.format(msg, args));
    }

    public static void d(String msg) {
        Log.i(pre + "tag", msg);
    }

    public static void d(String tag, String msg) {
        Log.i(pre + tag, msg);
    }

    public static void d(String tag, String msg, Object ...args) {
        Log.i(pre + tag, String.format(msg, args));
    }

    /**
     * 根据类名获取当前调用的方法和行号
     * @param tag 类名
     * @return 前调用的方法和行号
     */
    private static String getTag(String tag) {
        /* 获取该线程堆栈存储的堆栈跟踪元素数组。如果该线程尚未启动或已经终止，则该方法将返回一个零长度数组。
        如果返回的数组不是零长度的，最后一个元素代表堆栈底。*/

        StackTraceElement[] temp = Thread.currentThread().getStackTrace();
        // temp[0] 表示getStackTrace方法，故排除，此处也可从下标记2取做默认值
        StackTraceElement method = temp[1];

        // 由于数组的第一个元素代表栈顶，它是该序列中最新的方法调用，所以正序遍历
        for (StackTraceElement stackTraceElement : temp) {
            // 根据程序类名全路径进行过滤，筛选出当前该类中被最新调用的方法
            if (stackTraceElement.getClassName().contains(tag)) {
                method = stackTraceElement;
                break;
            }
        }

        return method.getClassName() + "." + method.getMethodName() + "() (" + method.getLineNumber() + ")";
    }
}
