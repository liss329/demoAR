package com.example.demoar;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;


import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class MainActivity extends Activity implements SensorEventListener {
	
	static SQLiteDatabase mydb;
	int scanCount = 0;
	
	//トップ画面からの受け渡しデータ
	String sintyo;
	String place; //String型のルート番号
	int placeNo;  //Int型に変換したあとのルート番号（を格納する変数）

	int rssiVal; // 測定点数
    ArrayList<String>[][] point; //ここにデータを仮格納  [測定点数][0->ssid, 1->level];

	final int INTERVAL_PERIOD = 1000;
	Timer timer = new Timer();
	int x = 0;
	Handler handle = new Handler();
	int fpno = 0;

	private SensorManager sensorManager;
	private float[] accelerometerValues = new float[3]; // 陷会ｿｽ鬨ｾ貅ｷ�ｽｺ�ｽｦ郢ｧ�ｽｻ郢晢ｽｳ郢ｧ�ｽｵ
	private float[] magneticValues = new float[3]; // 陜ｨ�ｽｰ騾寂扱�ｽｰ蜉ｱ縺晉ｹ晢ｽｳ郢ｧ�ｽｵ
	List listMag;
	List listAcc;

	private ARView arView;

	private float oldx = 0f;
	private float oldy = 0f;
	private float oldz = 0f;

	private float dx = 0f;
	private float dy = 0f;
	private float dz = 0f;

	boolean counted = false;
	int counter = -1;
	boolean vectorUp = true;
	double oldVectorSize = 0;
	double vectorSize = 0;
	double picupVectorSize = 0;
	long changeTime = 0;
	double threshold = 15;
	double thresholdMin = 1;
	long thresholdTime = 190;
	boolean vecx = true;
	boolean vecy = true;
	boolean vecz = true;
	int vecchangecount = 0;

	float pretheta;
	float direction;
	float step;
	float drDistance = -step;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				

		// フルスクリーン指定
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// ARViewの取得
		arView = new ARView(this);

		// (1)各種センサーの用意
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		listMag = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
		listAcc = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

		// (2)Viewの重ね合わせ
		setContentView(new CameraView(this));
		addContentView(arView, new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));

	}

	public void OnCre2() {


		// フルスクリーン指定
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// ARViewの取得
		arView = new ARView(this);

		// (1)各種センサーの用意
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		listMag = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
		listAcc = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

		// (2)Viewの重ね合わせ
		setContentView(new CameraView(this));
		addContentView(arView, new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.FILL_PARENT));

	}

	public float getDx() {
		return dx;
	}

	public float getDy() {
		return dy;
	}

	public float getDz() {
		return dz;
	}

	public double getVectorSize() {
		return vectorSize;
	}

	public long getCounter() {
		return counter;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// (3)郢ｧ�ｽｻ郢晢ｽｳ郢ｧ�ｽｵ郢晢ｽｼ陷�ｽｦ騾��ｿｽ邵ｺ�ｽｮ騾具ｽｻ鬪ｭ�ｽｲ
		sensorManager.registerListener(this, (Sensor) listMag.get(0),
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(this, (Sensor) listAcc.get(0),
				SensorManager.SENSOR_DELAY_NORMAL);

	}

	@Override
	public void onStop() {
		super.onStop();

		sensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			accelerometerValues = event.values.clone();
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			magneticValues = event.values.clone();
			break;
		}

		if (magneticValues != null && accelerometerValues != null) {
			float[] R = new float[16];
			float[] I = new float[16];

			SensorManager.getRotationMatrix(R, I, accelerometerValues,
					magneticValues);

			float[] actual_orientation = new float[3];

			SensorManager.getOrientation(R, actual_orientation);
			// 求まった方位角をラジアンから度に変換する
			float direction = (float) Math.toDegrees(actual_orientation[0]);
			arView.drawScreen(direction, drDistance, fpno, counter); //ARViewクラスに値の受け渡し(端末の向いている方向,累計進んだ距離,測定点の推定結果,トップ画面で選んだルート番号)	
		}

		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			// 増加量
			dx = event.values[0] - oldx;
			dy = event.values[1] - oldy;
			dz = event.values[2] - oldz;
			// ベクトル量をピタゴラスの定義から求める。
			// が正確な値は必要でなく、消費電力から平方根まで求める必要はない
			// vectorSize = Math.sqrt((double)(dx*dx+dy*dy+dz*dz));
			vectorSize = dx * dx + dy * dy + dz * dz;
			// ベクトル計算を厳密に行うと計算量が上がるため、簡易的な方向を求める。
			// 一定量のベクトル量があり向きの反転があった場合（多分走った場合）
			// vecchangecountはSENSOR_DELAY_NORMALの場合、200ms精度より
			// 加速度変化が検出できないための専用処理。精度を上げると不要
			// さらに精度がわるいことから、連続のベクトル変化は検知しない。
			long dt = new Date().getTime() - changeTime;
			boolean dxx = Math.abs(dx) > thresholdMin && vecx != (dx >= 0);
			boolean dxy = Math.abs(dy) > thresholdMin && vecy != (dy >= 0);
			boolean dxz = Math.abs(dz) > thresholdMin && vecz != (dz >= 0);
			if (vectorSize > threshold && dt > thresholdTime
					&& (dxx || dxy || dxz)) {
				vecchangecount++;
				changeTime = new Date().getTime();

			}
			// ベクトル量がある状態で向きが２回（上下運動とみなす）変わった場合
			// または、ベクトル量が一定値を下回った（静止とみなす）場合、カウント許可
			if (vecchangecount > 1 || vectorSize < 1) {
				counted = false;
				vecchangecount = 0;
			}
			// カウント許可で、閾値を超えるベクトル量がある場合、カウント
			if (!counted && vectorSize > threshold) {

				counted = true;
				vecchangecount = 0;
				counter++;

				float theta;
				drDistance = drDistance + step;

				/*
				 * if(pretheta >= 0 && pretheta < 90){ theta = pretheta;
				 * drDistance = (float)(drDistance + (step * Math.cos(theta)));
				 * } if(pretheta >= 90 && pretheta < 180){ theta = 180 -
				 * pretheta; drDistance = (float)(drDistance - (step *
				 * Math.cos(theta))); } if(pretheta >= 180 && pretheta < 270){
				 * theta = pretheta - 180; drDistance = (float)(drDistance -
				 * (step * Math.cos(theta))); } if(pretheta >= 270 && pretheta
				 * <= 360){ theta = 360 - pretheta; drDistance =
				 * (float)(drDistance + (step * Math.cos(theta))); }
				 */

			}
			// カウント自の加速度の向きを保存
			vecx = dx >= 0;
			vecy = dy >= 0;
			vecz = dz >= 0;
			// 状態更新
			oldVectorSize = vectorSize;
			// 加速度の保存
			oldx = event.values[0];
			oldy = event.values[1];
			oldz = event.values[2];
		}

	}

	

}