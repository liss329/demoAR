package com.example.demoar;


import java.math.BigDecimal;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

@SuppressLint("Instantiatable")
public class ARView extends View {

	// 逕ｻ蜒上�ｮ隱ｭ縺ｿ霎ｼ縺ｿ
	Resources res = this.getContext().getResources();
	Bitmap yajirusi = BitmapFactory.decodeResource(res, R.drawable.yajirusi);
	Bitmap migiyajirusi = BitmapFactory.decodeResource(res,R.drawable.migiyajirusi);
	Bitmap hidariyajirusi = BitmapFactory.decodeResource(res,R.drawable.hidariyajirusi);


	// 蜷代″繧剃ｿ晄戟縺吶ｋ螟画焚
	float direction;
	float drDistance;
	int fpno;
	boolean flag = false;
	float refDistance;
	float arDistance;
	int placeNo;
	int counter;

	public ARView(Context context) {
		super(context);
	}

	// (1)描画処理
	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		Paint paint2 = new Paint();
		paint2.setAntiAlias(true);
		// コンパスを描画する
		drawCompass(canvas, paint, paint2);

	}

	// (2)コンパスの描画
	private void drawCompass(Canvas canvas, Paint paint, Paint paint2) {

		if (direction > 220 && direction < 290)	canvas.drawBitmap(yajirusi, 250, 350, paint);
		if (direction >= 0 && direction < 70)	canvas.drawBitmap(hidariyajirusi, 1000, 0, paint);
		if (direction >= 290 && direction <= 360) canvas.drawBitmap(hidariyajirusi, 1000, 0, paint);
		if (direction >= 70 && direction <= 220) canvas.drawBitmap(migiyajirusi, 0, 0, paint);

		paint2.setColor(Color.WHITE);
		paint2.setTextSize(400);
		canvas.drawText(Integer.toString(counter), 700, 1000, paint2);
		//canvas.drawText(Float.toString(direction), 200, 200, paint2);  //端末の向いている方向を画面に表示する

	}

	// (3)センサー値の取得と再描画
	public void drawScreen(float preDirection, float predrDistance, int prefpno, int precounter) {
		// センサーの値から端末の向きを計算する
		direction = (preDirection + 450) % 360;
		drDistance = predrDistance;
		fpno = prefpno + 1;
		counter = precounter;
		// onDrawを呼び出して再描画
		invalidate();
	}

}