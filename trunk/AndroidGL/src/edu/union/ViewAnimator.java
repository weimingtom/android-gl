package edu.union;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;

public class ViewAnimator extends Handler {
	boolean running;
	View view;
	long nextTime;
	int diff;
	
	public static final int NEXT = 0;
	
	public ViewAnimator(View view) {
		this(view, -1);
	}
	
	public ViewAnimator(View view, int fps) {
		running = false;
		this.view = view;
		this.diff = 1000/fps;
	}
	
	public void start() {
		if (!running) {
			running = true;
			Message msg = obtainMessage(NEXT);
			sendMessageAtTime(msg, SystemClock.uptimeMillis());
		}
	}
	
	public void stop() {
		running = false;
	}
	
	public void handleMessage(Message msg) {
		if (running && msg.what == NEXT) {
			view.invalidate();
			msg = obtainMessage(NEXT);
			long current = SystemClock.uptimeMillis();
			if (nextTime < current) {
				nextTime = current + diff;
			}
			sendMessageAtTime(msg, nextTime);
			nextTime += diff;
		}
	}
}
