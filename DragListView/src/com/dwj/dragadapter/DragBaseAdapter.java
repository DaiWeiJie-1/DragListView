package com.dwj.dragadapter;

import android.widget.BaseAdapter;

public abstract class DragBaseAdapter extends BaseAdapter{

	public abstract void onDragRemovePosition(int position);
	
	public abstract void onDragInsertPosition(int position,Object obj);
}
