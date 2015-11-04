package com.jhlc.material.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.jhlc.material.AddNoteBookContent;
import com.jhlc.material.R;
import com.jhlc.material.bean.NoteBookBean;
import com.jhlc.material.utils.LogZ;

import java.util.ArrayList;

public class NoteBookAdapter extends BaseAdapter {
    HolderView             holderView = null;
    private ArrayList<NoteBookBean> noteBookBeans = new ArrayList<NoteBookBean>();
    private LayoutInflater inflater;
    private TextView       tv_task;
    private Context        mContent;
    private BaseAdapter    ba;
    private String         type;
    private int[] pos = {0,0,0,0};

    public NoteBookAdapter(ArrayList<NoteBookBean> noteBookBeans, Context content) {
        this.noteBookBeans = noteBookBeans;
        this.mContent   = content;
        inflater        = (LayoutInflater) content.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ba              = this;
        for(int i=0; i<noteBookBeans.size(); i++){
            switch (Integer.valueOf(noteBookBeans.get(i).getType())){
                case 1:
                    pos[0]++;
                    break;
                case 2:
                    pos[1]++;
                    break;
                case 3:
                    pos[2]++;
                    break;
                case 4:
                    pos[3]++;
                    break;
            }
        }

    }

    @Override
    public int getCount() {
        int count = noteBookBeans.size();
        if(pos[0]==0){
            count++;
        }
        if(pos[1]==0){
            count++;
        }
        if(pos[2]==0){
            count++;
        }
        if(pos[3]==0){
            count++;
        }
        LogZ.d("count--> ", count + "");

        return 1;
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

        if(convertView == null ){
            holderView  = new HolderView();
            convertView = inflater.inflate(R.layout.notebook_items, null);

            holderView.rl_today    = (RelativeLayout) convertView.findViewById(R.id.rl_today);
            holderView.rl_tomorrow = (RelativeLayout) convertView.findViewById(R.id.rl_tomorrow);
            holderView.rl_recent   = (RelativeLayout) convertView.findViewById(R.id.rl_recent);
            holderView.rl_later    = (RelativeLayout) convertView.findViewById(R.id.rl_later);

            holderView.img_note_today    = (ImageButton) convertView.findViewById(R.id.img_note_today);
            holderView.img_note_tomorrow = (ImageButton) convertView.findViewById(R.id.img_note_tomorrow);
            holderView.img_note_recent   = (ImageButton) convertView.findViewById(R.id.img_note_recent);
            holderView.img_note_later    = (ImageButton) convertView.findViewById(R.id.img_note_later);

            holderView.ll_operate_notebook = (LinearLayout) convertView.findViewById(R.id.ll_operate_notebook);

            holderView.tv_note_content = (TextView) convertView.findViewById(R.id.tv_note_content);
            holderView.tv_note_time    = (TextView) convertView.findViewById(R.id.tv_note_time);

            convertView.setTag(holderView);
        }else{
            holderView = (HolderView) convertView.getTag();
        }

//      holderView.ll_operate_notebook.setVisibility(View.GONE);
        LogZ.d("position--> ",""+position);

        if(noteBookBeans.size()>position) {
            holderView.tv_note_content.setText(noteBookBeans.get(position).getTitle());
            holderView.tv_note_time.setText(noteBookBeans.get(position).getDate());
        }

        holderView.img_note_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContent, AddNoteBookContent.class);
                intent.putExtra("type","1");
                mContent.startActivity(intent);
            }
        });
        holderView.img_note_tomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContent, AddNoteBookContent.class);
                intent.putExtra("type","2");
                mContent.startActivity(intent);
            }
        });
        holderView.img_note_recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContent, AddNoteBookContent.class);
                intent.putExtra("type","3");
                mContent.startActivity(intent);
            }
        });
        holderView.img_note_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContent, AddNoteBookContent.class);
                intent.putExtra("type","4");
                mContent.startActivity(intent);
            }
        });

        /*if(position == 0){
            holderView.rl_today.setVisibility(View.VISIBLE);
            holderView.rl_tomorrow.setVisibility(View.GONE);
            holderView.rl_recent.setVisibility(View.GONE);
            holderView.rl_later.setVisibility(View.GONE);
        } else if(position == pos[0]){
            holderView.rl_today.setVisibility(View.GONE);
            holderView.rl_tomorrow.setVisibility(View.VISIBLE);
            holderView.rl_recent.setVisibility(View.GONE);
            holderView.rl_later.setVisibility(View.GONE);
        } else if(position == pos[1]+pos[0]){
            holderView.rl_today.setVisibility(View.GONE);
            holderView.rl_tomorrow.setVisibility(View.GONE);
            holderView.rl_recent.setVisibility(View.VISIBLE);
            holderView.rl_later.setVisibility(View.GONE);
        } else if(position == pos[2]+pos[1]+pos[0]){
            holderView.rl_today.setVisibility(View.GONE);
            holderView.rl_tomorrow.setVisibility(View.GONE);
            holderView.rl_recent.setVisibility(View.GONE);
            holderView.rl_later.setVisibility(View.VISIBLE);
        }*/

        return convertView;
    }

    class HolderView{
        RelativeLayout rl_today,rl_tomorrow,rl_recent,rl_later;
        ImageButton    img_note_today,img_note_tomorrow,img_note_recent,img_note_later;
        LinearLayout   ll_operate_notebook;
        TextView       tv_note_content,tv_note_time;

    }

}
