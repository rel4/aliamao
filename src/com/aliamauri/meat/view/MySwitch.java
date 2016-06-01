package com.aliamauri.meat.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.aliamauri.meat.R;

/**
 * 自定义滑动开关
 * 
 * @author jiang
 * 
 */
public class MySwitch extends View {
	private final String namespace = "http://com/limaoso/InstantMessage";
	private Bitmap bg_close, switch_open_bg;
	private Bitmap slide_close, slide_open_bg;// 滑块对应的图片
	private int LEFT;
	private boolean open = false;
	int l = 0; // 滑块当前的位置
	// 定义了一个成员变量
	private OnCheckedChangeListener changeListener;

	private boolean currentOpen = open;

	// 回调方法
	public void setOnChangeListener(OnCheckedChangeListener changeListener) {
		this.changeListener = changeListener;
	}

	// 步骤1 声明接口
	public interface OnCheckedChangeListener {
		void onCheckedChanged(MySwitch mySwitch, boolean isOpen);
	}

	// 在代码中使用自定义控件
	public MySwitch(Context context) {
		super(context);
		init();
	}

	//
	public MySwitch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);

	}

	// 在布局文件中声明自定义控件
	public MySwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	private void init() {

		bg_close = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_close_bg);
		switch_open_bg = BitmapFactory.decodeResource(getResources(),
				R.drawable.switch_open_bg);
		slide_close = BitmapFactory.decodeResource(getResources(),
				R.drawable.slide_close);
		slide_open_bg = BitmapFactory.decodeResource(getResources(),
				R.drawable.slide_open_bg);
		LEFT = bg_close.getWidth() - slide_close.getWidth();

	}

	private void init(AttributeSet attrs) {
		// TypedArray a =
		// context.obtainStyledAttributes(attrs,R.styleable.MyView);
		// int textColor = a.getColor(R.styleable.MyView_myColor, 003344);
		// float textSize = a.getDimension(R.styleable.MyView_myTextSize, 33);
		String msType = attrs.getAttributeValue(namespace, "msType");
		if (msType == null || "".equals(msType)) {
			msType = "border";
		}
		if ("rectangle".equals(msType)) {
			bg_close = BitmapFactory.decodeResource(getResources(),
					R.drawable.switch_rectangle_close_bg);
			switch_open_bg = BitmapFactory.decodeResource(getResources(),
					R.drawable.switch_rectangle_open_bg);
			slide_close = BitmapFactory.decodeResource(getResources(),
					R.drawable.slide_rectangle_close);
			slide_open_bg = BitmapFactory.decodeResource(getResources(),
					R.drawable.slide_rectangle_open_bg);
			LEFT = bg_close.getWidth() - slide_close.getWidth();
		} else {
			bg_close = BitmapFactory.decodeResource(getResources(),
					R.drawable.switch_close_bg);
			switch_open_bg = BitmapFactory.decodeResource(getResources(),
					R.drawable.switch_open_bg);
			slide_close = BitmapFactory.decodeResource(getResources(),
					R.drawable.slide_close);
			slide_open_bg = BitmapFactory.decodeResource(getResources(),
					R.drawable.slide_open_bg);
			LEFT = bg_close.getWidth() - slide_close.getWidth();
		}

	}

	int startX;

	// int startY;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		currentOpen = getOpen();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 按下的时候记录开始的坐标
			startX = (int) event.getX();
			// if (Math.abs(l - 0) < Math.abs(l - LEFT)) {
			// l = 0;
			// open = false;
			// } else {
			// l = LEFT;
			// open = true;
			// }
			break;
		case MotionEvent.ACTION_MOVE:// 移动的时候
			int newX = (int) event.getX();
			int dX = newX - startX;
			l += dX;
			if (l < 0) {
				l = 0;
			} else if (l > LEFT) {
				l = LEFT;
			}

			startX = newX;
			invalidate();

			// if (Math.abs(l - 0) < Math.abs(l - LEFT)) {
			// l = 0;
			// open = false;
			// } else {
			// l = LEFT;
			// open = true;
			// }
			break;
		case MotionEvent.ACTION_UP:
			if (l <= LEFT / 2) {
				l = 0;
				open = false;
			} else {
				l = LEFT;
				open = true;
			}
			invalidate();

			// if (Math.abs(l - 0) < Math.abs(l - LEFT)) {
			// l = 0;
			// open = false;
			// } else {
			// l = LEFT;
			// open = true;
			// }
//			if (changeListener != null) {
//				changeListener.onCheckedChanged(this, open);
//			}
			setOpen(!currentOpen);
			break;
		}

		return true;
	}

	/* 1是开其他是关 */
	public void setOpen(String open) {
		if ("1".equals(open)) {
			this.open = true;
			l = LEFT;
		} else {
			this.open = false;
			l = 0;
		}
		if (changeListener != null) {
			changeListener.onCheckedChanged(this, this.open);
		}
	}

	public void setOpen(boolean open) {
		this.open = open;
		if (this.open) {
			l = LEFT;
		} else {
			l = 0;
		}
		if (changeListener != null) {
			changeListener.onCheckedChanged(this, this.open);
		}
	}

	public boolean getOpen() {
		return this.open;

	}

	// 设置测量的规则
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 设置控件的宽和高
		setMeasuredDimension(bg_close.getWidth(), bg_close.getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Paint paint=new Paint();
		// paint.setColor(Color.RED);
		// canvas.drawRect(0, 0, 200, 200, paint);
		// 参数1 bitMap 图片 参数2 和参数3 分别代表 x坐标和y坐标
		canvas.drawBitmap(open ? bg_close : switch_open_bg, 0, 0, null); // 把背景画到控件上
		// 画滑块
		canvas.drawBitmap(open ? slide_close : slide_open_bg, l, 0, null);
	}

}
