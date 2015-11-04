package com.jhlc.material;

import android.content.Context;
import android.content.Intent;
import com.jhlc.material.utils.LogZ;
import com.umeng.common.message.Log;
import com.umeng.message.UmengBaseIntentService;
import com.umeng.message.entity.UMessage;
import org.android.agoo.client.BaseConstants;
import org.json.JSONObject;

/**
 * Developer defined push intent service. 
 * Remember to call {@link com.umeng.message.PushAgent#setPushIntentServiceClass(Class)}. 
 * @author lucas
 *
 */
public class ZxlPushIntentService extends UmengBaseIntentService{
	private static final String TAG = ZxlPushIntentService.class.getName();

	@Override
	protected void onMessage(Context context, Intent intent) {
		super.onMessage(context, intent);
		try {
			String message = intent.getStringExtra(BaseConstants.MESSAGE_BODY);
			UMessage msg = new UMessage(new JSONObject(message));
			LogZ.d(TAG, "message=" + message);
			LogZ.d(TAG, "custom="+msg.custom);
			// code  to handle message here
			// ...
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}
}
