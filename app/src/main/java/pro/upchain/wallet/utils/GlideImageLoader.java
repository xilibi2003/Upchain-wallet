package pro.upchain.wallet.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.youth.banner.loader.ImageLoader;


public class GlideImageLoader extends ImageLoader {

    public GlideImageLoader(int defaultImage) {
        this.defaultImage = defaultImage;
    }

    private int defaultImage;

    /**
     * Url加载图片
     *
     * @param view
     * @param url
     * @param defaultImage
     */
    public static void loadImage(ImageView view, String url, int defaultImage) {
        loadImage(view, url, defaultImage, -1);
    }

    /**
     * 加载Bitmap
     *
     * @param imageView
     * @param bitmap
     * @param defaultImage
     */
    public static void loadBmpImage(ImageView imageView, Bitmap bitmap, int defaultImage) {
        loadImage(imageView, bitmap, defaultImage, -1);
    }

    @SuppressLint("CheckResult")
    private static void loadImage(final ImageView view, Object img, @DrawableRes int defaultImage, @DrawableRes int errorImage) {
        // 不能崩
        if (view == null) {
            LogUtils.e("GlideUtils -> display -> imageView is null");
            return;
        }
        Context context = view.getContext();
        // View你还活着吗？
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) {
                return;
            }
        }

        RequestOptions options = new RequestOptions().centerCrop();
        if (defaultImage != -1) {
            options.placeholder(defaultImage);
        }
        if (errorImage != -1) {
            options.error(errorImage);
        }

        Glide.with(context)
                .load(img)
                .apply(options)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                }).into(view);
    }

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        /**
         注意：
         1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
         2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
         传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
         切记不要胡乱强转！
         */

        loadImage(imageView, path, defaultImage, -1);

    }

}
