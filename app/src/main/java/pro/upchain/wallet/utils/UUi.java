package pro.upchain.wallet.utils;

import android.app.Activity;
import android.util.SparseArray;
import android.view.View;

import pro.upchain.wallet.UpChainWalletApp;


/**
 * UI类工具
 * Created by shidawei on 16/8/4.
 */
public class UUi {

    /**
     * 获取view
     * @param activity
     * @param mViews
     * @param id
     * @param <T>
     * @return
     */
    @Deprecated
    public static  <T extends View> T getView(Activity activity, SparseArray<View> mViews, int id) {
        T view = (T) mViews.get(id);
        if (view == null) {
            view = (T) activity.findViewById(id);
            mViews.put(id, view);
        }
        return view;
    }


    public static  <T extends View> T getView(View mView, SparseArray<View> mViews, int id) {
        T view = (T) mViews.get(id);
        if (view == null) {
            view = (T) mView.findViewById(id);
            mViews.put(id, view);
        }
        return view;
    }


    /**
     * 给多个view添加点击事件
     * @param listener
     * @param views
     */
    @Deprecated
    public static void setOnClickListener(View.OnClickListener listener, View... views) {
        if (views == null) {
            return;
        }
        for (View view : views) {
            view.setOnClickListener(listener);
        }

    }

//    /**
//     * 弹出自定义toast
//     * @param msg
//     * @param duration
//     */
//    public static void toast(Activity activity, String msg, int duration) {
//        if(activity==null){
//            return;
//        }
//        View layout = activity.getLayoutInflater().inflate(R.layout.deleber_toast, null);
//        TextView txt = (TextView) layout.findViewById(R.id.main_toast_text);
//        txt.setText(msg);
//        Toast toast = new Toast(activity);
//        //设置Toast的位置
//        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//        toast.setDuration(duration);
//        //让Toast显示为我们自定义的样子
//        toast.setView(layout);
//        toast.show();
//
//    }

    /**
     * dip 转 px
     * @param dipValue
     * @return
     */
    public static int dip2px(float dipValue) {
        float scale = UpChainWalletApp.getsInstance().getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5F);
    }

    /**
     * px 转 dp
     * @param pxValue
     * @return
     */
    public static int px2dip(float pxValue) {
        float scale = UpChainWalletApp.getsInstance().getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5F);
    }

    /**
     * px转sp
     * @param pxValue
     * @return
     */
    public static int px2sp(float pxValue) {
        float fontScale = UpChainWalletApp.getsInstance().getResources().getDisplayMetrics().scaledDensity;
        return (int)(pxValue / fontScale + 0.5F);
    }

    /**
     * sp转px
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue) {
        float fontScale = UpChainWalletApp.getsInstance().getResources().getDisplayMetrics().scaledDensity;
        return (int)(spValue * fontScale + 0.5F);
    }

}
