package com.jhlc.material.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.gson.GsonBuilder;
import com.jhlc.material.*;
import com.jhlc.material.bean.PaymentDetailList;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

import java.util.ArrayList;

public class PaymentDetailAdapter extends BaseAdapter {
    private AdapterCallback myCallback;
    private LayoutInflater inflater;
    private Context mContent;
    private String  imageurl;
    private String  currttime;
    private BaseAdapter ba;
    private int UpOrDown = -1;
    private ArrayList<PaymentDetailList>  paymentDetailLists = new ArrayList<PaymentDetailList>();
    private PayOptDialog dialog;
    private PaymentDetailAdapter paymentDetailAdapter;

    public PaymentDetailAdapter(ArrayList<PaymentDetailList> paymentDetailLists, Context content, int UpOrDown) {
        ba = this;
        this.UpOrDown   = UpOrDown;
        this.paymentDetailLists = paymentDetailLists;
        this.mContent   = content;
        paymentDetailAdapter = this;
        inflater        = (LayoutInflater) content.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialog = new PayOptDialog(mContent,R.style.MyDialog);
    }

    @Override
    public int getCount() {
        return paymentDetailLists.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HolderView holderView ;
        if(convertView == null){
            holderView = new HolderView();
            if(Constant.ZXL_UP_USER == UpOrDown){
                convertView = inflater.inflate(R.layout.down_pay_items, null);
            } else if(Constant.ZXL_DOWN_USER == UpOrDown){
                convertView = inflater.inflate(R.layout.up_pay_items, null);
            }

            holderView.rl_lastreply_content = (RelativeLayout) convertView.findViewById(R.id.rl_lastreply_content);
            holderView.ll_payment_opt       = (LinearLayout) convertView.findViewById(R.id.ll_payment_opt);

            holderView.tv_report_time      = (TextView) convertView.findViewById(R.id.tv_lastreply_time);

            holderView.tv_lastreply_content             = (TextView) convertView.findViewById(R.id.tv_lastreply_content);
            holderView.tv_task               = (TextView) convertView.findViewById(R.id.tv_my_task);
            holderView.tv_sendtask_time      = (TextView) convertView.findViewById(R.id.tv_sendtask_time);
            holderView.tv_sendtask_address      = (TextView) convertView.findViewById(R.id.tv_sendtask_address);

            holderView.iv_other_userhead     = (ImageView) convertView.findViewById(R.id.iv_other_userhead);
            holderView.iv_my_userhead2       = (ImageView) convertView.findViewById(R.id.iv_my_userhead2);
            holderView.img_invoice           = (ImageView) convertView.findViewById(R.id.img_invoice);

            holderView.btn_confirm           = (ImageButton) convertView.findViewById(R.id.btn_confirm);
            holderView.btn_cancle            = (ImageButton) convertView.findViewById(R.id.btn_cancle);

            convertView.setTag(holderView);
        } else {
            holderView = (HolderView) convertView.getTag();
        }

        LogZ.d("getHeadImage--> ",PreferenceUtils.getHeadImage());

        if(Constant.ZXL_UP_USER == UpOrDown){
            LoaderBusiness.loadImage(MYURL.URL_HEAD + PreferenceUtils.getHeadImage(),holderView.iv_other_userhead);
            if (StringUtils.isNotBlank(imageurl)) {
                LoaderBusiness.loadImage(MYURL.URL_HEAD + imageurl, holderView.iv_my_userhead2);
            }
        } else if(Constant.ZXL_DOWN_USER == UpOrDown){
            if (StringUtils.isNotBlank(imageurl)) {
                LoaderBusiness.loadImage(MYURL.URL_HEAD + imageurl, holderView.iv_other_userhead);
            }
            LoaderBusiness.loadImage(MYURL.URL_HEAD + PreferenceUtils.getHeadImage(), holderView.iv_my_userhead2);
        }

        holderView.tv_task.setText("在"+paymentDetailLists.get(position).getPosition()+","
                                +paymentDetailLists.get(position).getMessage()+","
                                +"消费"+paymentDetailLists.get(position).getExpenseMoney() + "元");

        if (StringUtils.isNotBlank(paymentDetailLists.get(position).getSmallimage())) {
            LoaderBusiness.loadPayImage(MYURL.img_HEAD + paymentDetailLists.get(position).getSmallimage(),holderView.img_invoice);
            holderView.img_invoice.setEnabled(true);
        }else{
            holderView.img_invoice.setImageResource(R.drawable.take_invoice_normal_white);
            holderView.img_invoice.setEnabled(false);
        }

        holderView.tv_sendtask_address.setText(paymentDetailLists.get(position).getSysPostion());

        if("1".equals(paymentDetailLists.get(position).getExpenseStatus())){
            holderView.rl_lastreply_content.setVisibility(View.GONE);
            holderView.tv_report_time.setVisibility(View.GONE);
            if(null != holderView.ll_payment_opt) {
                holderView.ll_payment_opt.setVisibility(View.VISIBLE);
            }
        } else if("2".equals(paymentDetailLists.get(position).getExpenseStatus())){
            holderView.rl_lastreply_content.setVisibility(View.VISIBLE);
            holderView.tv_report_time.setVisibility(View.VISIBLE);
            if(null != holderView.ll_payment_opt) {
                holderView.ll_payment_opt.setVisibility(View.GONE);
            }
//            LogZ.d("getCurrenttime--> ", paymentDetailLists.get(position).getConfirmTime().replace("/", "-") + " - " + currttime.replace("/", "-"));
            holderView.tv_report_time.setText(TimeUtil.getSimpleChatTime(paymentDetailLists.get(position).getConfirmTime()));
//            TimeUtil.getInterval(paymentDetailLists.get(position).getConfirmTime().replace("/", "-"),
//                    currttime.replace("/", "-")));
            holderView.tv_lastreply_content.setText("同意本次报销，请带上发票到财务张经理处领钱！");

        }else if("4".equals(paymentDetailLists.get(position).getExpenseStatus())){
            holderView.rl_lastreply_content.setVisibility(View.VISIBLE);
            holderView.tv_report_time.setVisibility(View.VISIBLE);
            if(null != holderView.ll_payment_opt) {
                holderView.ll_payment_opt.setVisibility(View.GONE);
            }
            holderView.tv_report_time.setText(TimeUtil.getSimpleChatTime(paymentDetailLists.get(position).getConfirmTime()));
//            LogZ.d("getCurrenttime--> ", paymentDetailLists.get(position).getConfirmTime().replace("/", "-") + " - " + currttime.replace("/", "-"));
//            holderView.tv_report_time.setText(TimeUtil.getInterval(paymentDetailLists.get(position).getConfirmTime().replace("/", "-"),
//                    currttime.replace("/", "-")));
            holderView.tv_lastreply_content.setText("拒绝本次报销！");

        }

     //   holderView.tv_sendtask_time.setText(TimeUtil.getSendTime(paymentDetailLists.get(position).getAddTime().replace("/", "-"))+" "+paymentDetailLists.get(position).getPosition());
        holderView.img_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContent, PhotoViewer.class); d
                Intent intent=new Intent(mContent,DisplayImageActivity.class);
//                intent.putExtra("imgkey",paymentDetailLists.get(position).getBigimage());
                intent.putExtra("bigurl",paymentDetailLists.get(position).getBigimage());
                intent.putExtra("imgkey",paymentDetailLists.get(position).getExpenseID());

//                intent.putExtra("PhotoViewer", paymentDetailLists.get(position).getBigimage());
                mContent.startActivity(intent);
            }
        });

        if(Constant.ZXL_DOWN_USER == UpOrDown){
            holderView.btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.setPtype(2);
                    dialog.setExpenseid(paymentDetailLists.get(position).getExpenseID());
                    dialog.setPositiveListener(new PayOptDialog.PositiveListener() {
                        @Override
                        public void positiveClick() {
                            paymentOpt(2,paymentDetailLists.get(position).getExpenseID());
                        }
                    });
                    dialog.show();

                }
            });

            holderView.btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.setPtype(4);
                    dialog.setPositiveListener(new PayOptDialog.PositiveListener() {
                        @Override
                        public void positiveClick() {
                            paymentOpt(4, paymentDetailLists.get(position).getExpenseID());
                        }
                    });
                    dialog.setExpenseid(paymentDetailLists.get(position).getExpenseID());
                    dialog.show();
                }
            });
        }

        dialog.setPaymentListener(new PayOptDialog.OnPaymentListener() {
            @Override
            public void opType(int type) {
                paymentDetailAdapter.notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public void setUserHead(String workuserhead) {
        this.imageurl   = workuserhead;
    }

    public void setCurrttime(String currttime) {
        this.currttime = currttime;
    }

    public void setMyCallback(AdapterCallback myCallback) {
        this.myCallback = myCallback;
    }

    class HolderView{
        RelativeLayout rl_lastreply_content;
        LinearLayout   ll_payment_opt;
        TextView tv_task,tv_result,tv_lastreply_content;
        TextView tv_report_time,tv_sendtask_time,tv_sendtask_address;
        ImageView iv_other_userhead,iv_my_userhead2;
        ImageView img_invoice;

        ImageButton btn_confirm,btn_cancle;
    }

    /**
     *  当用户是上级时可以对下级任务进行
     *  1.确认完成操作
     *  2.删除操作
     * */
    private void paymentOpt(final int optype, String expenseid) {
        // 发送请求到服务器
        AsyncHttpClient client = new AsyncHttpClient();
        // 创建请求参数
        RequestParams params = new RequestParams();
        params.put("opcode", "pass_expense");
        params.put("Username", PreferenceUtils.getUserName());
        params.put("eid", Constant.EID);
        params.put("expenseid", expenseid);
        params.put("ptype", optype);
        LogZ.d("getUpUserData--> ", Constant.EID + PreferenceUtils.getUserName() + expenseid + optype);
        //http://zhixingli.shilehui.com/?opcode= pass_expense& expenseid=1&Username=董事长&eid=X&ptype=2

        // 执行post方法
        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {
            //todo 这里需要加入loading 条

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // 设置值
                LogZ.d("getUpUserData--> ", new String(responseBody));
                PostBackDataBean postBackDataBean = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if(postBackDataBean.getCode() == 100){
                    ZXLApplication.getInstance().showTextToast("操作成功！");
//                    onPaymentListener.opType(ptype);
                    dialog.dismiss();
                    if (myCallback!=null) {
                        myCallback.refreshView();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // 打印错误信息
                error.printStackTrace();

            }

        });
    }



}
