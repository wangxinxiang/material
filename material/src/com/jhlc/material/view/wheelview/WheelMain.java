package com.jhlc.material.view.wheelview;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.jhlc.material.R;
import com.jhlc.material.ZXLApplication;

public class WheelMain {

	private View view;
	private WheelView wv_day;
	private WheelView wv_hour;
	private TextView tv_limit_num;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}


	public WheelMain(View view) {
		super();

		this.view = view;
		setView(view);
	}

	public void setTv_limit_num(TextView tv_limit_num) {
		this.tv_limit_num = tv_limit_num;
	}

	/**
	 * @Description: TODO 弹出日期时间选择器
	 */
	public void initDateTimePicker() {

		// 月
        wv_day = (WheelView) view.findViewById(R.id.day);
        wv_day.setAdapter(new NumericWheelAdapter(1, 30));
        wv_day.setCyclic(true);
        wv_day.setLabel("天");
        wv_day.setCurrentItem(0);

		// 日
        wv_hour = (WheelView) view.findViewById(R.id.hour);
        wv_hour.setCyclic(true);
        wv_hour.setAdapter(new NumericWheelAdapter(0, 24));
        wv_hour.setLabel("小时");
        wv_hour.setCurrentItem(0);

		// 添加"天"监听
		OnWheelChangedListener wheelListener_day = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {

				if (tv_limit_num!=null) {
					tv_limit_num.setText((newValue+1)+"天");
				}

			}
		};
        wv_day.addChangingListener(wheelListener_day);

		// 根据屏幕密度来指定选择器字体的大小
		/*int textSize = 0;

		textSize = 70;

		wv_day.TEXT_SIZE = textSize;
        wv_hour.TEXT_SIZE = textSize;*/

		int textSize = 20;
		DisplayMetrics dm = ZXLApplication.getInstance().getResources().getDisplayMetrics();

		float density  = dm.density;
		//textSize = 70;

		wv_day.TEXT_SIZE = (int)(textSize*density);
		wv_hour.TEXT_SIZE = (int)(textSize*density);

	}

	public String getTime() {
		StringBuffer sb = new StringBuffer();
		sb.append(wv_day.getCurrentItem()).append("-").append(wv_hour.getCurrentItem());
		return sb.toString();
	}

    public int getDay() {
		//注意，getCurrentItem 是得到索引号
        return wv_day.getCurrentItem()+1;
    }

    public int getHour() {
        return wv_hour.getCurrentItem();
    }

}
