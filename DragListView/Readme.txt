DragListView

实现总结:
1.在onInterceptTouchEvent拦截down事件判断是否在某一项上,如果是则返回false,表示不拦截与此同时发送一个延时命令,如果在延时内移动不超过一小段距离则认为已经出发拖拽准备
2.拖拽准备:在window上添加某一项的cacheDrawable的view,该view为该项的悬浮view;
3.在onTouch里监听move事件,如果移动dragview的位置,并且交换当前的item和dragItem的位置
4.在up里取消整个拖拽,但是在准备阶段就触发的up事件不要消费,因为会禁止触发onItemClick
5.当处在上半部分或者下半部分的时候移动时,移动listview setSelectionFromTop;



注意:
1.获取listview当前触摸位置的itemview
	public View getViewInPoint(int x,int y){
		//该position是真正的position
		int position = pointToPosition(x, y);
	
		if(position == INVALID_POSITION){
			return null;
		}
		
		return getChildAt(position - getFirstVisiblePosition());
	}
	
2.RawX和X的区别: RawX是基于Screen的位置,X是基于该View的位置

3.获取View的当前截图:
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
	
4.将view显示在屏幕上而不依附于其他layout:
	WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
	wm.addView(view, layoutParams);
	
	
5.WindowLayoutParams的属性
		//背景设置为透明
		wmLayoutParams.format = PixelFormat.TRANSLUCENT;
		
		//窗口占满屏幕,有效的避免了statusBar的高度
		wmLayoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		
		
6.ListView中onInterceptTouchEvent只接收到了down而没有后续的move事件?
因为abslistview中对scroll做了处理,当滑动过程中,不可用onInterceptTouchEvent进行拦截,所以放入touch中

7.保存MotionEvent对象是不可取的,该对象getX或者其他的值会发生变化

8.移动到上下一定范围的区域是滚动listview
	setSelectionFromTop(mDragPosition, getChildAt(mDragPosition-getFirstVisiblePosition()).getTop()+scrollHeight);
	改变该postion距离顶部的距离
	
	
9.拖拽状态震动效果,需要android.permission.VIBRATE权限;可在代码中判断是否有这个权限:
		PackageManager pm = getPackageManager();  
        boolean permission = (PackageManager.PERMISSION_GRANTED ==   pm.checkPermission("android.permission.VIBRATE", "packageName"));  
        if (permission) {  
            showToast("有这个权限");  
        }else {  
            showToast("木有这个权限");  
        }  