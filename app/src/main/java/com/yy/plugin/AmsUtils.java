package com.yy.plugin;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Created by andy on 2020/12/15.
 */
public class AmsUtils {

    public static String EXTRA_INTENT = "extra_intent";

    public static void hookIntent() {
        try {
            Class<?> clazz = Class.forName("android.app.ActivityManager");
            Field singletonField = clazz.getDeclaredField("IActivityManagerSingleton");
            singletonField.setAccessible(true);
            Object sington = singletonField.get(null);


            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            final Object mInstance = mInstanceField.get(sington);

            Class<?> aIActivityManageCLazz = Class.forName("android.app.IActivityManager");

            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{aIActivityManageCLazz}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args)
                                throws Throwable {
                            if ("startActivity".equals(method.getName())) {
                                int index = -1;
                                for (int i = 0; i < args.length; i++) {
                                    if (args[i] instanceof Intent) {
                                        index = i;
                                        break;
                                    }
                                }
                                Intent oldIntent = (Intent) args[index];
                                Intent newIntent = new Intent();
                                newIntent.setComponent(new ComponentName("com.yy.plugin",
                                        "com.yy.plugin.ProxyActivity"));
                                newIntent.putExtra(EXTRA_INTENT, oldIntent);
                                args[index] = newIntent;

                            }
                            return method.invoke(mInstance, args);
                        }
                    });
            mInstanceField.set(sington, proxy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hookHandle() {
        try {
            Log.e("sihai", "hookHandle.......");
            Class<?> aClass = Class.forName("android.app.ActivityThread");
            Field sCurrentActivityThreadFiled = aClass.getDeclaredField("sCurrentActivityThread");
            sCurrentActivityThreadFiled.setAccessible(true);
            Object sCurrentActivityThread = sCurrentActivityThreadFiled.get(null);

            Field mH = aClass.getDeclaredField("mH");
            mH.setAccessible(true);
            Handler handler = (Handler) mH.get(sCurrentActivityThread);

            Class<?> handlerClazz = Class.forName("android.os.Handler");
            Field mCallbackField = handlerClazz.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);
            Handler.Callback callback = new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.what == 100) {
                        try {
                            Log.e("sihai", "hanele.......100");
                            Field intentField = msg.obj.getClass().getDeclaredField("intent");
                            intentField.setAccessible(true);
                            Intent intent = (Intent) intentField.get(msg.obj);
                            Intent parcelableExtra =
                                    (Intent) intent.getParcelableExtra(EXTRA_INTENT);
                            if (parcelableExtra != null) {
                                Log.i("sihai",
                                        "parcelableExtra.getComponent()=" +
                                                parcelableExtra.getComponent());
                                intentField.set(msg.obj, parcelableExtra);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("sihai", "Callback 100=" + e.getMessage());
                        }
                    } else if (msg.what == 159) {
                        try {
                            // 获取 mActivityCallbacks 对象
                            Field mActivityCallbacksField = msg.obj.getClass()
                                    .getDeclaredField("mActivityCallbacks");
                            mActivityCallbacksField.setAccessible(true);
                            List mActivityCallbacks = (List) mActivityCallbacksField.get(msg.obj);

                            for (int i = 0; i < mActivityCallbacks.size(); i++) {
                                if (mActivityCallbacks.get(i).getClass().getName()
                                        .equals("android.app.servertransaction.LaunchActivityItem")) {
                                    Object launchActivityItem = mActivityCallbacks.get(i);

                                    // 获取启动代理的 Intent
                                    Field mIntentField = launchActivityItem.getClass()
                                            .getDeclaredField("mIntent");
                                    mIntentField.setAccessible(true);
                                    Intent proxyIntent =
                                            (Intent) mIntentField.get(launchActivityItem);

                                    // 目标 intent 替换 proxyIntent
                                    Intent intent = proxyIntent.getParcelableExtra(EXTRA_INTENT);
                                    if (intent != null) {
                                        mIntentField.set(launchActivityItem, intent);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }
            };
            mCallbackField.set(handler, callback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public static void hookHandler2() {
    //     try {
    //         // 获取 ActivityThread 类的 Class 对象
    //         Class<?> clazz = Class.forName("android.app.ActivityThread");
    //
    //         // 获取 ActivityThread 对象
    //         Field activityThreadField = clazz.getDeclaredField("sCurrentActivityThread");
    //         activityThreadField.setAccessible(true);
    //         Object activityThread = activityThreadField.get(null);
    //
    //         // 获取 mH 对象
    //         Field mHField = clazz.getDeclaredField("mH");
    //         mHField.setAccessible(true);
    //         final Handler mH = (Handler) mHField.get(activityThread);
    //
    //         Field mCallbackField = Handler.class.getDeclaredField("mCallback");
    //         mCallbackField.setAccessible(true);
    //
    //         // 创建的 callback
    //         Handler.Callback callback = new Handler.Callback() {
    //
    //             @Override
    //             public boolean handleMessage(Message msg) {
    //                 // 通过msg  可以拿到 Intent，可以换回执行插件的Intent
    //
    //                 // 找到 Intent的方便替换的地方  --- 在这个类里面 ActivityClientRecord --- Intent intent 非静态
    //                 // msg.obj == ActivityClientRecord
    //                 switch (msg.what) {
    //                     case 100:
    //                         try {
    //                             Field intentField = msg.obj.getClass().getDeclaredField("intent");
    //                             intentField.setAccessible(true);
    //                             // 启动代理Intent
    //                             Intent proxyIntent = (Intent) intentField.get(msg.obj);
    //                             // 启动插件的 Intent
    //                             Intent intent = proxyIntent.getParcelableExtra(EXTRA_INTENT);
    //                             if (intent != null) {
    //                                 intentField.set(msg.obj, intent);
    //                             }
    //                         } catch (Exception e) {
    //                             e.printStackTrace();
    //                         }
    //                         break;
    //                     case 159:
    //                         try {
    //                             // 获取 mActivityCallbacks 对象
    //                             Field mActivityCallbacksField = msg.obj.getClass()
    //                                     .getDeclaredField("mActivityCallbacks");
    //
    //                             mActivityCallbacksField.setAccessible(true);
    //                             List mActivityCallbacks =
    //                                     (List) mActivityCallbacksField.get(msg.obj);
    //
    //                             for (int i = 0; i < mActivityCallbacks.size(); i++) {
    //                                 if (mActivityCallbacks.get(i).getClass().getName()
    //                                         .equals("android.app.servertransaction.LaunchActivityItem")) {
    //                                     Object launchActivityItem = mActivityCallbacks.get(i);
    //
    //                                     // 获取启动代理的 Intent
    //                                     Field mIntentField = launchActivityItem.getClass()
    //                                             .getDeclaredField("mIntent");
    //                                     mIntentField.setAccessible(true);
    //                                     Intent proxyIntent =
    //                                             (Intent) mIntentField.get(launchActivityItem);
    //
    //                                     // 目标 intent 替换 proxyIntent
    //                                     Intent intent =
    //                                             proxyIntent.getParcelableExtra(EXTRA_INTENT);
    //                                     if (intent != null) {
    //                                         mIntentField.set(launchActivityItem, intent);
    //                                     }
    //                                 }
    //                             }
    //                         } catch (Exception e) {
    //                             e.printStackTrace();
    //                         }
    //                         break;
    //                 }
    //                 // 必须 return false
    //                 return false;
    //             }
    //         };
    //
    //         // 替换系统的 callBack
    //         mCallbackField.set(mH, callback);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

}
