package com.zeke.kangaroo.utils;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * author: King.Z <br>
 * date:  2017/7/9 22:33 <br>
 * description: 绘制的工具类 <br>
 */
public class RectUtils {

	public static int roundToInt(float val) {
		if (val >= 0) {
			return (int) (val + 0.5f);
		} else {
			return (int) (val - 0.5f);
		}
	}

	public static void copyRect(Rect src, Rect dst) {
		dst.left = src.left;
		dst.top = src.top;
		dst.right = src.right;
		dst.bottom = src.bottom;
	}

	public static void copyRect(Rect src, RectF dst) {
		dst.left = src.left;
		dst.top = src.top;
		dst.right = src.right;
		dst.bottom = src.bottom;
	}

	public static void copyRect(RectF src, RectF dst) {
		dst.left = src.left;
		dst.top = src.top;
		dst.right = src.right;
		dst.bottom = src.bottom;
	}

	public static void copyRect(RectF src, Rect dst) {
		dst.left = roundToInt(src.left);
		dst.top = roundToInt(src.top);
		dst.right = roundToInt(src.right);
		dst.bottom = roundToInt(src.bottom);
	}

    public static void offsetRect(Rect rc, int offX, int offY) {
		rc.left += offX;
		rc.right += offX;
		rc.top += offY;
		rc.bottom += offY;
	}

	public static void offsetRect(RectF rc, float offX, float offY) {
		rc.left += offX;
		rc.right += offX;
		rc.top += offY;
		rc.bottom += offY;
	}

	public static void resizeRect(Rect rect, int newWidth, int newHeight) {
		rect.right = rect.left + newWidth;
		rect.bottom = rect.top + newHeight;
	}

	public static int calRectHeight(Rect rc) {
		return rc.bottom - rc.top;
	}

	public static float calRectHeight(RectF rc) {
		return rc.bottom - rc.top;
	}




}
