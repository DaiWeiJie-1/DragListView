package com.example.draglistview;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dwj.dragadapter.DragBaseAdapter;
import com.dwj.draglistview.DragListView;

public class MainActivity extends Activity implements OnItemClickListener,OnItemLongClickListener{

	private List<String> mDatas = new ArrayList<String>();
	private List<Integer> mBgColors = new ArrayList<Integer>(); 
	
	private DragListView mDragListView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDatas();
        fillColorsData();
        mDragListView = (DragListView) findViewById(R.id.drag_list_view);

        DragAdapter adapter = new DragAdapter();
        mDragListView.setAdapter(adapter);
        mDragListView.setOnItemClickListener(this);
        mDragListView.setOnItemLongClickListener(this);
        
    }
    
    private void initDatas(){
    	mDatas.add("第1行");
    	mDatas.add("第2行");
    	mDatas.add("第3行");
    	mDatas.add("第4行");
    	mDatas.add("第5行");
    	mDatas.add("第6行");
    	mDatas.add("第7行");
    	mDatas.add("第8行");
    	mDatas.add("第9行");
    	mDatas.add("第10行");
    	mDatas.add("第11行");
    	mDatas.add("第13行");
    	mDatas.add("第14行");
    	mDatas.add("第15行");
    	mDatas.add("第16行");
    	mDatas.add("第17行");
    	mDatas.add("第18行");
    	mDatas.add("第19行");
    	mDatas.add("第20行");
    	mDatas.add("第21行");
    	mDatas.add("第22行");
    	
    }
    
    private void fillColorsData(){
    	for(int i = 0; i < mDatas.size(); i ++){
    		mBgColors.add(generateRandomColor());
    	}
    }
    
    private class ViewHolder{
    	private TextView mContentTv;
    	private RelativeLayout mBglayout;
    }

    class DragAdapter extends DragBaseAdapter{

		@Override
		public int getCount() {
			return mDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return mDatas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.drag_list_view_item, null);
				holder.mContentTv = (TextView) convertView.findViewById(R.id.content_tv);
				holder.mBglayout = (RelativeLayout) convertView.findViewById(R.id.item_layout);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.mBglayout.setBackgroundColor(mBgColors.get(position));
			
			String content = mDatas.get(position);
			holder.mContentTv.setText(content);
			
			return convertView;
		}

		@Override
		public void onDragRemovePosition(int position) {
			mDatas.remove(position);
		}

		@Override
		public void onDragInsertPosition(int position, Object obj) {
			mDatas.add(position, (String)obj);
		}
    	
    }
    
    private int generateRandomColor(){
    	int red = (int) (Math.random() * 255);
    	int green = (int) (Math.random() * 255);
    	int blue = (int) (Math.random() * 255);
    	
    	return Color.argb(255, red, green, blue);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		Toast.makeText(this, "onItemLongClick: positon = " + position, Toast.LENGTH_LONG).show();
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Toast.makeText(this, "onItemClick: positon = " + position, Toast.LENGTH_LONG).show();		
	}
    
}
