package com.jhlc.material;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.ExpandableListView.OnGroupClickListener;
import com.google.gson.GsonBuilder;
import com.idunnololz.widgets.AnimatedExpandableListView;
import com.idunnololz.widgets.AnimatedExpandableListView.AnimatedExpandableListAdapter;
import com.jhlc.material.bean.*;
import com.jhlc.material.db.NoteBookDB;
import com.jhlc.material.service.LoaderBusiness;
import com.jhlc.material.utils.*;
import com.jhlc.material.view.PullToRefreshView;
import com.jhlc.material.view.RoundImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
import org.apache.http.Header;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This is an example usage of the AnimatedExpandableListView class.
 * 
 * It is an activity that holds a listview which is populated with 100 groups
 * where each group has from 1 to 100 children (so the first group will have one
 * child, the second will have two children and so on...).
 */
public class LogSystemActivity extends Activity  implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener {

    //友盟统计
    private Context mContext;
    private final  String mPageName = "LogSystemActivity";

    private TextView        tv_user_name;
    private RoundImageView  img_head_photo;
    private LinearLayout ll_my_info;

    private AnimatedExpandableListView listView;
    private ExampleAdapter adapter;

    private NoteBookDB noteBookDB = new NoteBookDB(this);
    private static String   path = ImageUtils.path;//sd路径
    private ArrayList<NoteBookBean> noteBookBeans = new ArrayList<NoteBookBean>();

    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");


    private PullToRefreshView mPullToRefreshView;
    /**
     * 当前页
     */
    private String username;
    private String headimage;


    private int currentPage=0;
    private int pageSize=0;
    private int recordCount=0;

    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newmain_tab_notebook);

        bindData();
        mContext=this;
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.pull_refresh_view);
        mPullToRefreshView.setNeedfootfresh(true);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);


        username = getIntent().getStringExtra("username");
        headimage = getIntent().getStringExtra("headimage");
        if("我的日志".equals(username)){
            username = PreferenceUtils.getUserName();
            headimage = PreferenceUtils.getHeadImage();
        }

        ll_my_info= (LinearLayout) findViewById(R.id.ll_my_info);
        ll_my_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LogSystemActivity.this,SettingActivity.class);
                startActivity(intent);
            }
        });

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                LogZ.d("lyjtest","its on ItemClick");
////                Toast.makeText(getApplicationContext(), "group:item:"+l, Toast.LENGTH_SHORT).show();
//                AnimatedExpandableListView lv= (AnimatedExpandableListView) adapterView;
////                Objects bb= (Objects) lv.getItemAtPosition(i);
//
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContent();
        setUserInfo();
       // if(noteBookBeans.size()==0){
            getMemosFromServer(1);
       // }
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(mContext);
    }

    // 增量更新
    private void autoupdate() {
        ArrayList<NoteBookBean> result= noteBookDB.findNeedUpdate();
        if(result.size()==0){//说明没有找到需要更新的操作。
            return ;
        }

        String test=getMemos(result, PreferenceUtils.getUserName(), Constant.EID);
       // ZXLApplication.getInstance().showTextToast("找到需要更新的记录："+result.size()+"json:"+test);
        AsyncHttpClient client = new AsyncHttpClient();




        RequestParams params = new RequestParams();
        try {
            //String m_szAndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            params.put("opcode", "multi_add_memos");
            String userName = PreferenceUtils.getUserName();
            params.put("Username", userName);
            params.put("eid", Constant.EID);
            String memos = getMemos(result, userName, Constant.EID);
            params.put("memos", memos);
            LogZ.d("autoupdate--> ", memos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                pd = ProgressDialog.show(LogSystemActivity.this, "正在提交...", "请等待", true);


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LogZ.d("autoupdate--> ", new String(responseBody));
                //{"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201411080338542207.JPG"}

                NoteBookServerReturn noteBookServerReturn = new GsonBuilder().create().fromJson(new String(responseBody), NoteBookServerReturn.class);
                if (noteBookServerReturn!=null&&"get_memo_success".equals(noteBookServerReturn.getOpcode())&&noteBookServerReturn.getList()!=null) {
                   // ZXLApplication.getInstance().showTextToast("备份成功:"+noteBookServerReturn.getList().length);
                    noteBookDB.updateMid(noteBookServerReturn.getList());
                }
                //  pd.dismiss();




            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

              //  pd.dismiss();
            }
        });
    }

    private String getMemos(ArrayList<NoteBookBean> result, String userName, String eid) {
       return getUpdateNoteBookMemos(result);
    }

    private String getUpdateNoteBookMemos(ArrayList<NoteBookBean> result) {
        List<NoteBookUpdate> noteBookUpdateList=changeDBToBean(result);
        String temp=new GsonBuilder().create().toJson(noteBookUpdateList);
        LogZ.d("lyjtest", "转换得到的json：" + temp);
        return temp;
    }

    private List<NoteBookUpdate> changeDBToBean(ArrayList<NoteBookBean> result) {
        List<NoteBookUpdate> noteBookUpdateList=new ArrayList<NoteBookUpdate>();
        for (Iterator<NoteBookBean> iterator = result.iterator(); iterator.hasNext(); ) {
            NoteBookBean db = iterator.next();
            noteBookUpdateList.add(changedB(db));
        }
        return noteBookUpdateList;
    }

    private NoteBookUpdate changedB(NoteBookBean db) {
        NoteBookUpdate dbu=new NoteBookUpdate();
        dbu.setMid(db.getMid());
        dbu.setLocalid(String.valueOf(db.getId()));
        try {
            dbu.setMsg(URLEncoder.encode(db.getTitle(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        dbu.setTop(String.valueOf(db.getIstop()));
        dbu.setOpcode(getRightOpCode(db));
        dbu.setTime(db.getDate());
        dbu.setType(getServerType(db.getType()));
        //overtime和time都是过期时间。
        dbu.setOvertime(db.getDate());
        return dbu;
    }


    private String getRightOpCode(NoteBookBean db) {
        if(db.getOpcode()==null||"".equals(db.getOpcode())){
            return "new";
        }else{
            return db.getOpcode();
        }
    }

    private String getServerType(String type) {
        if("1".equals(type)){
            return "today";
        }else if("2".equals(type)){
            return "tomorrow";
        }else if("3".equals(type)){
            return "recent";
        }else if("4".equals(type)){
            return "later";
        }else{
            return "later";
        }
    }




    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd( mPageName );
        MobclickAgent.onPause(mContext);
    }

    private void getMemosFromServer(final int pageindex) {
        AsyncHttpClient client = new AsyncHttpClient();




        RequestParams params = new RequestParams();
        try {
            //String m_szAndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            params.put("opcode", "ls_getpagememo");
            //String userName = PreferenceUtils.getUserName();

            LogZ.d("getMemosFromServer--->username",username);

            params.put("username", username);
            params.put("pageIndex", pageindex);
            params.put("pageSize", 10);
            params.put("eid", Constant.EID);
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
                LogZ.d("getMemosFromServer--> ", new String(responseBody));
                //{"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201411080338542207.JPG"}

                NoteBookFirstServerReturn noteBookServerReturn = new GsonBuilder().create().fromJson(new String(responseBody), NoteBookFirstServerReturn.class);
                if (noteBookServerReturn!=null&&"ls_getpagememo".equals(noteBookServerReturn.getOpcode())&&noteBookServerReturn.getList()!=null) {
                  //  ZXLApplication.getInstance().showTextToast("获得成功:"+noteBookServerReturn.getList().length);


                    if(pageindex==1){
                        noteBookBeans.clear();
                    }
                    noteBookBeans.addAll(change2NotebookBeans(noteBookServerReturn.getList()));
                    currentPage=noteBookServerReturn.getPageIndex();
                    pageSize= noteBookServerReturn.getPageSize();
                    recordCount= noteBookServerReturn.getRecordCount();
                }
                //noteBookBeans = noteBookDB.getNoteBook(0);
                List<GroupItem> items=changeNoteBookBeans2GroupItemList(noteBookBeans);
                adapter.setData(items);
                adapter.notifyDataSetChanged();
                for (int i = 0; i < items.size(); i++) {
                    listView.expandGroup(i);
                }
                inItRefreshView();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ZXLApplication.getInstance().showNetWorkingErrorTextToast();
            }
        });
    }

    private ArrayList<NoteBookBean> change2NotebookBeans(NoteBookServerMemo[] list) {
        ArrayList<NoteBookBean> temp=new ArrayList<NoteBookBean>();
        if(list!=null){
            for (int i = 0; i < list.length; i++) {
                NoteBookServerMemo noteBookServerMemo = list[i];
                NoteBookBean nb=change2NotebookBean(noteBookServerMemo);
                temp.add(nb);
            }
        }
        return temp;
    }

    private NoteBookBean change2NotebookBean(NoteBookServerMemo noteBookServerMemo) {
        NoteBookBean result=new NoteBookBean();
        result.setDate(noteBookServerMemo.getOverTime());
        result.setIstop(Integer.parseInt(noteBookServerMemo.getTop()));
        result.setMid(noteBookServerMemo.getID());
        result.setTitle(noteBookServerMemo.getMemoContent());
        result.setType(StringUtils.getLocalType(noteBookServerMemo.getMType()));
        return result;
    }

    private void setContent() {
//        noteBookBeans = noteBookDB.getNoteBook(0);
        List<GroupItem> items=changeNoteBookBeans2GroupItemList(noteBookBeans);



      /* final  List<GroupItem> items = new ArrayList<GroupItem>();

        // Populate our list with groups and it's children
        for(int i = 1; i < 100; i++) {
            GroupItem item = new GroupItem();

            item.title = "Group " + i;

            for(int j = 0; j < i; j++) {
                ChildItem child = new ChildItem();
                child.title = "Awesome item " + j;
                child.hint = "Too awesome";

                item.items.add(child);
            }

            items.add(item);
        }*/

        adapter = new ExampleAdapter(this);
        adapter.setData(items);

        listView = (AnimatedExpandableListView) findViewById(R.id.notebook_listView);
        listView.setAdapter(adapter);
        listView.setGroupIndicator(null);
        listView.setDivider(null);

        for (int i = 0; i < items.size(); i++) {
            listView.expandGroup(i);
        }


        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        listView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
//                d
                LogZ.d("lyjtest", "its on onGroupClick！");
                if (listView.isGroupExpanded(groupPosition)) {
                   // listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                LogZ.d("lyjtest", "its on ItemClick");
                if(username.equals(PreferenceUtils.getUserName())){
                    ChildHolder holder = (ChildHolder) view.getTag();
                    ChildItem childItem=adapter.getChild(i, i1);
                    if( holder.down_ll.getVisibility()==View.GONE){
                        childItem.showButtonGroup=true;
                        holder.down_ll.setVisibility(View.VISIBLE);
                    }else{
                        childItem.showButtonGroup=false;
                        holder.down_ll.setVisibility(View.GONE);
                    }
                    GroupItem gi= adapter.getGroup(i);
                    for (int j = 0; j < gi.items.size(); j++) {
                        ChildItem item = gi.items.get(j);
                        if(i1!=j){
                            item.showButtonGroup=false;

                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                else {
//                    Toast.makeText(getApplicationContext(),"i="+i+"  i1="+i1+"我是子项",Toast.LENGTH_SHORT).show();
                    String title=noteBookBeans.get(i1).getTitle();
                    String date=noteBookBeans.get(i1).getDate();
//                    Toast.makeText(getApplicationContext(),title+date,Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(LogSystemActivity.this,LogSystemItemActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("username",username);
                    bundle.putString("context",title);
                    bundle.putString("time",date);
                    bundle.putString("headimage",headimage);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }

              //  Toast.makeText(getApplicationContext(), "group:" + i + " item:" + i1 + " id:" + l, Toast.LENGTH_SHORT).show();
                return false;
            }
        });

       // listView.setOnGroupClickListener(null);


        listView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int i) {
                GroupItem groupItem=adapter.getGroup(i);
                List<ChildItem> items= groupItem.items;
                for (int j = 0; j < items.size(); j++) {
                    ChildItem childItem = items.get(j);
                    childItem.showButtonGroup=false;
                }
            }
        });
    }


    /**
     * 将 notebookbean 的集合转成 GroupItem 集合
     * @param noteBookBeans
     * @return
     */
    private List<GroupItem> changeNoteBookBeans2GroupItemList(ArrayList<NoteBookBean> noteBookBeans) {
        String nowDateStr=TimeUtil.sdf.format(new Date());

//        int
        List<GroupItem> result=new ArrayList<GroupItem>();
        Map<String,GroupItem> groupItemMap=new LinkedHashMap<String, GroupItem>() ;


            GroupItem delayGi = new GroupItem();
            delayGi.title = "日志";
            delayGi.type = Constant.notebook_delay;
            delayGi.items = new ArrayList<ChildItem>();
            groupItemMap.put(Constant.notebook_delay, delayGi);




                for (Iterator<NoteBookBean> iterator = noteBookBeans.iterator(); iterator.hasNext(); ) {
                    NoteBookBean bookBean = iterator.next();
                    GroupItem gi=delayGi;
                    ChildItem ci=new ChildItem();
                    ci.title=bookBean.getTitle();
                    try {
                        if(StringUtils.isNotBlank(bookBean.getDate())) {
                            ci.hint = sdf.format(sdf.parse(bookBean.getDate()));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ci.id=bookBean.getId();
                    ci.top=bookBean.getIstop();
                    ci.mid=bookBean.getMid();
                    gi.items.add(ci);
                }





        for (Iterator<GroupItem> iterator = groupItemMap.values().iterator(); iterator.hasNext(); ) {
            GroupItem next = iterator.next();
            result.add(next);


//            if(Constant.notebook_delay.equals(next.type)){   //延期的类型有值的时候才加入到界面上显示。
//                if(next.items.size()>0){
//                    result.add(next);
//                }
//            }else{
//                result.add(next);
//            }

        }

        return result;
    }

    private void bindData() {
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);

//        img_head_photo = (RoundImageView) findViewById(R.id.img_head_photo);
//        img_head_photo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //startActivity(new Intent(LogSystemActivity.this, SettingActivity.class));
//            }
//
//        });


        ImageView ibtn_close_actvity = (ImageView) findViewById(R.id.ibtn_close_actvity);
        ibtn_close_actvity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //setUserInfo();

    }

    private static class GroupItem {
        String title;
        String type;
        List<ChildItem> items = new ArrayList<ChildItem>();
    }

    private static class ChildItem {

        String title;
        String hint;
        int id;
        boolean showButtonGroup=false;
        int top=0;
        String mid;
    }

    private static class ChildHolder {
        TextView title;
        TextView hint;
        ImageButton topButton;
        ImageButton editButton;
        ImageButton deleteButton;
        RelativeLayout down_ll;
    }

    private static class GroupHolder {
        TextView title;
        ImageButton group;
    }

    /**
     * Adapter for our list of {@link GroupItem}s.
     */
    private class ExampleAdapter extends AnimatedExpandableListAdapter {
        private LayoutInflater inflater;
        
        private List<GroupItem> items;

        private Context context;
        
        public ExampleAdapter(Context context) {
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

            if(!username.equals(PreferenceUtils.getUserName())){
                holder.title.setSingleLine();
            }
            if(item.showButtonGroup){
                holder.down_ll.setVisibility(View.VISIBLE);
            }else{
                holder.down_ll.setVisibility(View.GONE);
            }

            if(item.top>0){
                holder.title.setTextColor(getResources().getColor(R.color.main_red));
            }else{
                holder.title.setTextColor(getResources().getColor(R.color.black));
            }

            if(item.top==1){
                holder.topButton.setImageDrawable(getResources().getDrawable(R.drawable.set_top_pressed));
            }else{
                holder.topButton.setImageDrawable(getResources().getDrawable(R.drawable.set_top_normal));
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
                        holder.topButton.setImageDrawable(getResources().getDrawable(R.drawable.set_top_pressed));
                    }else{
                        holder.topButton.setImageDrawable(getResources().getDrawable(R.drawable.set_top_normal));
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
            return items.size();
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
                    intent.putExtra("title","新建日志");
                    intent.putExtra("type",adapter.getGroup(groupPosition).type);
                    startActivity(intent);
                }
            });
            //不是自己不显示+号
            if(username.equals(PreferenceUtils.getUserName())){
                holder.group.setVisibility(View.VISIBLE);
            }else{
                holder.group.setVisibility(View.INVISIBLE);
            }
            
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
                pd = ProgressDialog.show(LogSystemActivity.this, "正在提交...", "请等待", true);

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
                        adapter.notifyDataSetChanged();

                        pd.dismiss();
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
                pd.dismiss();
            }
        });
    }

    protected void deleteNoteDialog(final ChildItem notebookItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认删除吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNoteBook2server(notebookItem.mid);
                //noteBookDB.deleteByID(notebookItem.id);
                adapter.removeChildItemByMId(notebookItem.mid);
                //测试提交，随意输入字符
                dialog.dismiss();
                adapter.notifyDataSetChanged();
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
        Intent intent = new Intent(getApplicationContext(), AddLogSystem.class);
        intent.putExtra("content",notebookItem.title);
        intent.putExtra("title","编辑日志");
        intent.putExtra("date",notebookItem.hint);
        intent.putExtra("editId",notebookItem.mid);
        intent.putExtra("type",type);
        startActivity(intent);
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
                if (noteBookServerReturn!=null&&"delete_memo_success".equals(noteBookServerReturn.getOpcode())) {
                    ZXLApplication.getInstance().showTextToast("删除成功");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ZXLApplication.getInstance().showNetWorkingErrorTextToast();
            }
        });
    }

    //---------------notebook 业务方法

    private void setUserInfo() {
        try {
            LogZ.d("getIsLogin--> ",""+ PreferenceUtils.getIsLogin());
           // LoaderBusiness.loadImage(MYURL.URL_HEAD + PreferenceUtils.getInstance().getHeadImage(), img_head_photo);
            setHeadImage();
            if(username.equals(PreferenceUtils.getUserName())) {
                tv_user_name.setText("我");
            } else {
                tv_user_name.setText(username);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setHeadImage() {
        if(StringUtils.isNotBlank(headimage)){
            LoaderBusiness.loadImage(MYURL.img_HEAD+ headimage,img_head_photo);
        }else{
            img_head_photo.setImageDrawable(this.getResources().getDrawable(R.drawable.defult_user_headimg));
        }
    }


    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        if (currentPage*pageSize<recordCount) {
            getMemosFromServer(currentPage+1);
        }else{
            inItRefreshView();
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        getMemosFromServer(1);
    }

    private void getServerMemo() {
        AsyncHttpClient client = new AsyncHttpClient();




        RequestParams params = new RequestParams();
        try {
            //String m_szAndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            params.put("opcode", "getServerMemo");
            String userName = PreferenceUtils.getUserName();
            params.put("Username", userName);
            params.put("eid", Constant.EID);
            if(StringUtils.isNotBlank(PreferenceUtils.getServerMemoDT())){
                //params.put("dt",PreferenceUtils.getServerMemoDT());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.post(MYURL.URL_HEAD, params, new AsyncHttpResponseHandler() {



            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                LogZ.d("getMemosFromServer--> ", new String(responseBody));
                //{"code":100,"msg":"success","opcode":"reguser_eid_success","headimg":"/UserImage/201411080338542207.JPG"}

                NoteBookFirstServerReturn noteBookServerReturn = new GsonBuilder().create().fromJson(new String(responseBody), NoteBookFirstServerReturn.class);
                if (noteBookServerReturn!=null&&"getServerMemo_success".equals(noteBookServerReturn.getOpcode())) {
                    //  ZXLApplication.getInstance().showTextToast("获得成功:"+noteBookServerReturn.getList().length);
                    PreferenceUtils.getInstance().setServerMemoDT(noteBookServerReturn.getDt());
                    if(noteBookServerReturn.getList()!=null){
                        noteBookDB.updateFromServer(noteBookServerReturn.getList());
                    }
                }
                noteBookBeans = noteBookDB.getNoteBook(0);
                List<GroupItem> items=changeNoteBookBeans2GroupItemList(noteBookBeans);
                adapter.setData(items);
                adapter.notifyDataSetChanged();
                inItRefreshView();


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                ZXLApplication.getInstance().showTextToast("更新失败，请在网络好的时候再次尝试");
//                pd.dismiss();
            }
        });
    }

    private void inItRefreshView(){
        mPullToRefreshView.onHeaderRefreshComplete(TimeUtil.getRefreshTimeStr());
        mPullToRefreshView.onFooterRefreshComplete();
        mPullToRefreshView.onHeaderRefreshComplete();
    }
}
