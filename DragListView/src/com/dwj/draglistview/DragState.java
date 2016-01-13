package com.dwj.draglistview;

public enum DragState {
	
	IDEL(0),
	
	PREPARING(1),
	
	DRAGING(2);
	
	private int state;
	
	private DragState(int state){
		this.state = state;
	}
	
	public int getState(){
		return state;
	}
	
}
