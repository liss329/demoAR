package com.example.demoar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


/*
 * �X�}�[�g�t�H�������̃J�����@�\���g�p���邾���Ȃ̂�
 * ���̃N���X�͊�{�I�ɂ͍ŏ�����قڂ�����܂���ł��� 
 */

@SuppressLint("Instantiatable")
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder surfaceHolder;
	private Camera camera;

	public CameraView(Context context) {
		super(context);

		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);

		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceholder) {
		try {
			camera = Camera.open();
			camera.setPreviewDisplay(surfaceholder);
		} catch (Exception e) {
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		camera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}
}