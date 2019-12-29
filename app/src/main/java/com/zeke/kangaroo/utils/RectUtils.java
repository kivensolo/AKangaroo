package com.zeke.kangaroo.utils;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * author: King.Z <br>
 * date:  2017/7/9 22:33 <br>
 * description: 绘制的工具类 <br>
 */
public class RectUtils {

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

}
