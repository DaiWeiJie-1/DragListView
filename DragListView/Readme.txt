DragListView

ʵ���ܽ�:
1.��onInterceptTouchEvent����down�¼��ж��Ƿ���ĳһ����,������򷵻�false,��ʾ���������ͬʱ����һ����ʱ����,�������ʱ���ƶ�������һС�ξ�������Ϊ�Ѿ�������ק׼��
2.��ק׼��:��window�����ĳһ���cacheDrawable��view,��viewΪ���������view;
3.��onTouch�����move�¼�,����ƶ�dragview��λ��,���ҽ�����ǰ��item��dragItem��λ��
4.��up��ȡ��������ק,������׼���׶ξʹ�����up�¼���Ҫ����,��Ϊ���ֹ����onItemClick
5.�������ϰ벿�ֻ����°벿�ֵ�ʱ���ƶ�ʱ,�ƶ�listview setSelectionFromTop;



ע��:
1.��ȡlistview��ǰ����λ�õ�itemview
	public View getViewInPoint(int x,int y){
		//��position��������position
		int position = pointToPosition(x, y);
	
		if(position == INVALID_POSITION){
			return null;
		}
		
		return getChildAt(position - getFirstVisiblePosition());
	}
	
2.RawX��X������: RawX�ǻ���Screen��λ��,X�ǻ��ڸ�View��λ��

3.��ȡView�ĵ�ǰ��ͼ:
	public Bitmap createViewCapture(View view){
		if(view == null){
			return null;
		}
		
		//��ȡ��ͼǰ����Ϊenabled
		view.setDrawingCacheEnabled(true);
		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
		//��ȡ��ͼ������enabledΪfalse,��ֹ�´�ȥ��ͼʱ��ˢ��
		view.setDrawingCacheEnabled(false);
		return bitmap;
	}
	
4.��view��ʾ����Ļ�϶�������������layout:
	WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
	wm.addView(view, layoutParams);
	
	
5.WindowLayoutParams������
		//��������Ϊ͸��
		wmLayoutParams.format = PixelFormat.TRANSLUCENT;
		
		//����ռ����Ļ,��Ч�ı�����statusBar�ĸ߶�
		wmLayoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
		
		
6.ListView��onInterceptTouchEventֻ���յ���down��û�к�����move�¼�?
��Ϊabslistview�ж�scroll���˴���,������������,������onInterceptTouchEvent��������,���Է���touch��

7.����MotionEvent�����ǲ���ȡ��,�ö���getX����������ֵ�ᷢ���仯

8.�ƶ�������һ����Χ�������ǹ���listview
	setSelectionFromTop(mDragPosition, getChildAt(mDragPosition-getFirstVisiblePosition()).getTop()+scrollHeight);
	�ı��postion���붥���ľ���
	
	
9.��ק״̬��Ч��,��Ҫandroid.permission.VIBRATEȨ��;���ڴ������ж��Ƿ������Ȩ��:
		PackageManager pm = getPackageManager();  
        boolean permission = (PackageManager.PERMISSION_GRANTED ==   pm.checkPermission("android.permission.VIBRATE", "packageName"));  
        if (permission) {  
            showToast("�����Ȩ��");  
        }else {  
            showToast("ľ�����Ȩ��");  
        }  