package com.jhlc.material.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.idunnololz.widgets.AnimatedExpandableListView;
import com.jhlc.material.AddLogSystem;
import com.jhlc.material.R;
import com.jhlc.material.ZXLApplication;
import com.jhlc.material.bean.PostBackDataBean;
import com.jhlc.material.utils.Constant;
import com.jhlc.material.utils.LogZ;
import com.jhlc.material.utils.MYURL;
import com.jhlc.material.utils.PreferenceUtils;
import com.jhlc.material.utils.StringUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2015/10/8.
 */
public class LogAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private LayoutInflater inflater;

    private List<GroupItem> items;

    private Context context;

    public LogAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context=context;
    }

    public void removeChildItemByMId(String childItemId){
        ChildItem temp=null;
        for (Iterator<GroupItem> iterator = items.iterator(); iterator.hasNext(); ) {
            GroupItem next = iterator.next();
            List itemList=next.items;
            for (int i = 0; i < itemList.size(); i++) {
                ChildItem childItem = (ChildItem) itemList.get(i);
                if(StringUtils.isNotBlank(childItem.mid)&&childItem.mid.equals(childItemId)){
                    temp=childItem;
                }
            }
            next.items.remove(temp);
        }
    }

    public void setData(List<GroupItem> items) {
        this.items = items;
    }

    @Override
    public ChildItem getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).items.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildHolder holder;
        final ChildItem item = getChild(groupPosition, childPosition);
        final GroupItem gi=getGroup(groupPosition);
        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.notebook_list_item, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.textTitle);
            holder.hint = (TextView) convertView.findViewById(R.id.textHint);
            holder.down_ll = (RelativeLayout) convertView.findViewById(R.id.down_ll);
            holder.deleteButton = (ImageButton) convertView.findViewById(R.id.button_c);
            holder.editButton = (ImageButton) convertView.findViewById(R.id.button_b);
            holder.topButton = (ImageButton) convertView.findViewById(R.id.button_a);
            //  holder.deleteButton.setTag(childPosition);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
            // holder.deleteButton.setTag(childPosition);
        }

        if(item.showButtonGroup){
            holder.down_ll.setVisibility(View.VISIBLE);
        }else{
            holder.down_ll.setVisibility(View.GONE);
        }

        if(item.top>0){
            holder.title.setTextColor(context.getResources().getColor(R.color.main_red));
        }else{
            holder.title.setTextColor(context.getResources().getColor(R.color.black));
        }

        if(item.top==1){
            holder.topButton.setImageDrawable(context.getResources().getDrawable(R.drawable.set_top_pressed));
        }else{
            holder.topButton.setImageDrawable(context.getResources().getDrawable(R.drawable.set_top_normal));
        }

        if(gi.type.equals(Constant.notebook_delay)){
            holder.hint.setVisibility(View.VISIBLE);
        }else{
            holder.hint.setVisibility(View.GONE);
        }

        holder.title.setText(item.title);
        holder.hint.setText(item.hint);

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteNoteDialog(item);
                //Toast.makeText(getApplicationContext(), "c click:group:"+groupPosition+"item:"+ view.getTag(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editItem(item,gi.type);
                //Toast.makeText(getApplicationContext(), "c click:group:"+groupPosition+"item:"+ view.getTag(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.topButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                topItem(item);
                if(item.top==1){
                    holder.topButton.setImageDrawable(context.getResources().getDrawable(R.drawable.set_top_pressed));
                }else{
                    holder.topButton.setImageDrawable(context.getResources().getDrawable(R.drawable.set_top_normal));
                }
                //Toast.makeText(getApplicationContext(), "c click:group:"+groupPosition+"item:"+ view.getTag(), Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }


    @Override
    public int getRealChildrenCount(int groupPosition) {
        return items.get(groupPosition).items.size();
    }

    @Override
    public GroupItem getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public int getGroupCount() {

        return items != null ?items.size() : 0;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder holder;
        GroupItem item = getGroup(groupPosition);
        if (convertView == null) {
            holder = new GroupHolder();
            convertView = inflater.inflate(R.layout.notebook_group_item, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.textTitle);
            holder.group = (ImageButton) convertView.findViewById(R.id.gbutton);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }

        holder.title.setText(item.title);
        holder.group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Toast.makeText(getApplicationContext(), "group:"+groupPosition, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, AddLogSystem.class);
                intent.putExtra("title","新建内容");
                intent.putExtra("type", getGroup(groupPosition).type);
                context.startActivity(intent);
            }
        });
        //不是自己不显示+号
        holder.group.setVisibility(View.VISIBLE);


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }


    public static class GroupItem {
        public String title;
        public String type;
        public List<ChildItem> items = new ArrayList<ChildItem>();
    }

    public static class ChildItem {

        public  String title;
        public String hint;
        public  int id;
        public boolean showButtonGroup=false;
        public int top=0;
        public  String mid;
    }

    public static class ChildHolder {
        public TextView title;
        public TextView hint;
        public ImageButton topButton;
        public ImageButton editButton;
        public ImageButton deleteButton;
        public RelativeLayout down_ll;
    }

    public static class GroupHolder {
        TextView title;
        ImageButton group;
    }

    private void topItem(ChildItem item) {
        if (item.top==0) {
            top2server(item,"1");
            // noteBookDB.updateNoteBookTop(1,item.id);
        }else{
            top2server(item,"0");
            //noteBookDB.updateNoteBookTop(0,item.id);
        }


    }

    private void top2server(final ChildItem item,String top) {
        AsyncHttpClient client = new AsyncHttpClient();




        RequestParams params = new RequestParams();
        try {
            //String m_szAndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            params.put("opcode", "update_memo");
            String userName = PreferenceUtils.getUserName();
            params.put("username", userName);
            params.put("eid", Constant.EID);
            params.put("msg",item.title);
            params.put("type","today");
            params.put("top",top);
            params.put("mid",item.mid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
//                pd = ProgressDialog.show(context, "正在提交...", "请等待", true);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LogZ.d("add_memo_success--> ", new String(responseBody));
                //{"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201411080338542207.JPG"}

                PostBackDataBean noteBookServerReturn = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if(noteBookServerReturn!=null){
                    if ("update_memo_success".equals(noteBookServerReturn.getOpcode())) {
                        ZXLApplication.getInstance().showTextToast("修改成功");
                        if (item.top==0) {
                            item.top=1;
                        }else{
                            item.top=0;
                        }
                        notifyDataSetChanged();

//                        pd.dismiss();
                    }else if("memo_notexist".equals(noteBookServerReturn.getOpcode())){
                        ZXLApplication.getInstance().showTextToast("日志不存在");
                    }
                }else{
                    ZXLApplication.getInstance().showTextToast("连接出错");
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ZXLApplication.getInstance().showNetWorkingErrorTextToast();
//                pd.dismiss();
            }
        });
    }

    protected void deleteNoteDialog(final ChildItem notebookItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确认删除吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNoteBook2server(notebookItem.mid);
                //noteBookDB.deleteByID(notebookItem.id);
                removeChildItemByMId(notebookItem.mid);
                //测试提交，随意输入字符
                dialog.dismiss();
                notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    protected void editItem(final ChildItem notebookItem, String type) {
        Intent intent = new Intent(context, AddLogSystem.class);
        intent.putExtra("content",notebookItem.title);
        intent.putExtra("title", "编辑日志");
        intent.putExtra("date", notebookItem.hint);
        intent.putExtra("editId", notebookItem.mid);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    private void deleteNoteBook2server( String editId) {
        AsyncHttpClient client = new AsyncHttpClient();




        RequestParams params = new RequestParams();
        try {
            //String m_szAndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            params.put("opcode", "del_memo");
            String userName = PreferenceUtils.getUserName();
            params.put("Username", userName);
            params.put("eid", Constant.EID);
            params.put("mid",editId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LogZ.d("add_memo_success--> ", new String(responseBody));
                //{"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201411080338542207.JPG"}

                PostBackDataBean noteBookServerReturn = new GsonBuilder().create().fromJson(new String(responseBody), PostBackDataBean.class);
                if (noteBookServerReturn != null && "delete_memo_success".equals(noteBookServerReturn.getOpcode())) {
                    ZXLApplication.getInstance().showTextToast("删除成功");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ZXLApplication.getInstance().showNetWorkingErrorTextToast();
            }
        });
    }

}
