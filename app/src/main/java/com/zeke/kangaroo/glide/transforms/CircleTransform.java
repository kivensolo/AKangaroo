package com.zeke.kangaroo.glide.transforms;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * author：KingZ
 * date：2020/1/14
 * description：对图片进行圆形处理
 */
public class CircleTransform extends BitmapTransformation {
    private static final String ID = "com.zeke.kangaroo.glide.transforms.CircleTransform";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    public CircleTransform() {
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return circle(pool, toTransform);
    }

    private static Bitmap circle(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        int min = source.getWidth() < source.getHeight() ? source.getWidth() : source.getHeight();
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawCircle((float) (min / 2), (float) (min / 2), (float) (min / 2), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        int left = -(source.getWidth() - min) / 2;
        int top = -(source.getHeight() - min) / 2;
        canvas.drawBitmap(source, (float) left, (float) top, paint);
        return result;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof RoundTransform;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }
}
