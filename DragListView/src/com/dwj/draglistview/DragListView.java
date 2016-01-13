package com.dwj.draglistview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;

import com.dwj.dragadapter.DragBaseAdapter;
import com.example.draglistview.R;

public class DragListView extends ListView{
	
	private static final String TAG = DragListView.class.getSimpleName();

	private static final String VIBRATOR_PERMISSION = "android.permission.VIBRATE";
	
	private static final int MSG_DRAG_START = 0x00000001;
	
	private Point mDragViewPointInScreen;
	
	/**
	 * 用来记录拖动起始准备相对屏幕的点
	 */
	private Point mDragPreparingPointInScreen;
	private Point mDragPreparingPointInListView;
	
	/**
	 * 最初dragview的Y
	 */
	private int mDragPreparingY;
	
	private int mDragPosition;
	
	private ImageView mDragImageView;
	private DragState mDragState = DragState.IDEL;
	
	private boolean mIsVibrator = false;
	
	public DragListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public DragListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.DragListView);
		mIsVibrator = arr.getBoolean(R.styleable.DragListView_is_vibrate, false);
		arr.recycle();
	}

	public DragListView(Context context) {
		super(context);
	}
	
	public void setVibrate(boolean vibrate){
		mIsVibrator = vibrate;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(ev);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.d(TAG, "onIntercept: down" + "; x = " + ev.getX() + "; y = " + ev.getY());
			if(mDragState == DragState.IDEL){
				Message msg = new Message();
				msg.what = MSG_DRAG_START;
				
				mDragHandler.sendMessageDelayed(msg, 2000);
				mDragPreparingPointInScreen = new Point((int)ev.getRawX(), (int)ev.getRawY());
				mDragPreparingPointInListView = new Point((int)ev.getX(), (int)ev.getY());
				mDragState = DragState.PREPARING;
				return false;
			}
			
			break;
			
		default:
			break;
		}
		
		return super.onInterceptTouchEvent(ev);
	}
	

	public Handler mDragHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_DRAG_START:
				View dragView = getViewInPoint(mDragPreparingPointInListView.x,mDragPreparingPointInListView.y);
				mDragViewPointInScreen = calculateDragViewPointInScreen(dragView);
				Bitmap dragBitmap = createViewCapture(dragView);
				mDragImageView = initDragImageView(dragBitmap);
				
				boolean hasVibratorPermission = checkVibratorPermission();
				if(hasVibratorPermission && mIsVibrator){
					vibrator();
				}
				mDragState = DragState.DRAGING;
				break;

			default:
				break;
			}
		};
	};
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_MOVE:
				if(mDragState == DragState.PREPARING){
					if((Math.abs((int)ev.getRawX() - mDragPreparingPointInScreen.x) > 3) || (Math.abs((int)ev.getRawY() - mDragPreparingPointInScreen.y) > 3)){
						//移动超过一定距离
						mDragHandler.removeMessages(MSG_DRAG_START);
						mDragState = DragState.IDEL;
						return true;
					}
				}else if(mDragState == DragState.DRAGING){
					dragImageView(mDragImageView, ev);
					changeDragItemAndCurrentItem((int)ev.getX(), (int)ev.getY());
					scrollyListView((int)ev.getY());
					return true;
				}
				
				break;
				
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if(mDragState == DragState.PREPARING){
					mDragHandler.removeMessages(MSG_DRAG_START);
					mDragState = DragState.IDEL;
					
				}else if(mDragState == DragState.DRAGING){
					cancelDrag(mDragImageView);
					mDragState = DragState.IDEL;
					return true;
				}
				
				break;
		}
		return super.onTouchEvent(ev);
	}
	
	/**
	 * 滚动listview
	 * @param y
	 */
	public void scrollyListView(int y){
		 //滚动
	    int scrollHeight = 0;
	    if(y< getHeight()/3){
	        scrollHeight = 8;//定义向上滚动8个像素，如果可以向上滚动的话
	    }else if(y>getHeight() *2/3){
	        scrollHeight = -8;//定义向下滚动8个像素，，如果可以向上滚动的话
	    }
	     
	    if(scrollHeight!=0){
//	        //真正滚动的方法setSelectionFromTop()
	        setSelectionFromTop(mDragPosition, getChildAt(mDragPosition-getFirstVisiblePosition()).getTop()+scrollHeight);
	    }
	    
	}
	
	/**
	 * 获得对应坐标点View
	 * @param x
	 * @param y
	 * @return
	 */
	public View getViewInPoint(int x,int y){
		//该position是真正的position
		int position = pointToPosition(x, y);

		if(position == INVALID_POSITION){
			return null;
		}
		
		mDragPosition = position;
		
		Log.d(TAG, "getViewInPoint: position = " + position + "; x = " + x + "; y = " + y);
		return getChildAt(position - getFirstVisiblePosition());
	}
	
	/**
	 * 计算拖拽view基于屏幕的坐标
	 * @param targetView
	 * @param ev
	 * @return
	 */
	public Point calculateDragViewPointInScreen(View targetView){
		//触摸点对于屏幕的X,Y坐标
		int rawX = mDragPreparingPointInScreen.x;
		int rawY = mDragPreparingPointInScreen.y;
		
		//触摸点对于listview的X,Y坐标
		int X = mDragPreparingPointInListView.x;
		int Y = mDragPreparingPointInListView.y;
		
		int listviewTopY = rawY - Y;
		int listviewLeftX = rawX - X;
		
		Point pt = new Point();
		pt.x = listviewLeftX + targetView.getLeft();
		pt.y = listviewTopY + targetView.getTop();
		
		return pt;
	}
	

	/**
	 * 获得view的截图
	 * @param view
	 * @return
	 */
	public Bitmap createViewCapture(View view){
		if(view == null){
			return null;
		}
		
		//获取截图前设置为enabled
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
		//获取截图后设置enabled为false,防止下次去截图时不刷新
		view.setDrawingCacheEnabled(false);
		return bitmap;
	}
	
	@SuppressLint("NewApi") public ImageView initDragImageView(Bitmap bitmap){
		//由于拖拽view是放置在window中的,所以要配置WindowManager.LayoutParams
		android.view.WindowManager.LayoutParams wmLayoutParams = new android.view.WindowManager.LayoutParams();
		
		mDragPreparingY = mDragViewPointInScreen.y;
		
		wmLayoutParams.x = mDragViewPointInScreen.x;
		wmLayoutParams.y = mDragViewPointInScreen.y;
		wmLayoutParams.gravity = Gravity.TOP|Gravity.LEFT;
		
		wmLayoutParams.height = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
		wmLayoutParams.width = android.view.WindowManager.LayoutParams.WRAP_CONTENT;
		
		//背景设置为透明
		wmLayoutParams.format = PixelFormat.TRANSLUCENT;
		
		//窗口占满屏幕,有效的避免了statusBar的高度
		wmLayoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		
		ImageView dragView = new ImageView(getContext());
		dragView.setAlpha(0.7f);
		dragView.setImageBitmap(bitmap);
		dragView.setBackgroundDrawable(null);
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		wm.addView(dragView, wmLayoutParams);
		return dragView;
	}
	
	public void dragImageView(ImageView dragView,MotionEvent ev){
		if(dragView == null){
			return;
		}
		
		Log.d(TAG, "dragImageView");
		
		android.view.WindowManager.LayoutParams lp = (android.view.WindowManager.LayoutParams) dragView.getLayoutParams();
		
		lp.y = mDragPreparingY + ((int)ev.getRawY() - mDragPreparingPointInScreen.y);

		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		wm.updateViewLayout(dragView, lp);
	}
	
	
	private void cancelDrag(ImageView dragView) {
		if(dragView == null){
			return;
		}
		Log.d(TAG, "cancelDragView");
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		wm.removeView(dragView);
	}
	
	/**
	 * 交换dragIte和当前item的位置
	 * @param x
	 * @param y
	 */
	private void changeDragItemAndCurrentItem(int x, int y){
		if(mDragState == DragState.DRAGING && mDragImageView != null){
			DragBaseAdapter adapter = (DragBaseAdapter) getAdapter();
			int curPosition = pointToPosition(x, y);
			
			if(curPosition != INVALID_POSITION && curPosition != mDragPosition){
				Object curObj = adapter.getItem(curPosition);
				Object dragObj = adapter.getItem(mDragPosition);
				
				//先删除后面的位置,再插入前面的位置
				if(curPosition < mDragPosition){
					adapter.onDragRemovePosition(mDragPosition);
					adapter.onDragRemovePosition(curPosition);
					
					adapter.onDragInsertPosition(curPosition,dragObj);
					adapter.onDragInsertPosition(mDragPosition,curObj);
				}else{
					adapter.onDragRemovePosition(curPosition);
					adapter.onDragRemovePosition(mDragPosition);

					adapter.onDragInsertPosition(mDragPosition, curObj);
					adapter.onDragInsertPosition(curPosition, dragObj);
				}
		
				
				mDragPosition = curPosition;
				adapter.notifyDataSetChanged();
			}
		}
	}
	

	private boolean checkVibratorPermission(){
		PackageManager pm = getContext().getPackageManager();
		return (PackageManager.PERMISSION_GRANTED == pm.checkPermission(VIBRATOR_PERMISSION, getContext().getPackageName()));
	}
	
	private void vibrator(){
		Vibrator vb = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
		vb.vibrate(300);
	}
}
