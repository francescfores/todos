package com.iesebre.dam2.francesc.todos;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by sergi on 20/11/15.
 */
public class CustomListAdapter extends BaseAdapter {

    private final Context context;

    public ArrayList<TodoItem> getList() {
        return list;
    }

    private final ArrayList<TodoItem> list;
    private final LayoutInflater layoutInflater;

    public CustomListAdapter(Context context, ArrayList listData) {
        this.context = context;
        this.list = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(
                    R.layout.listitem,
                    null);
        } else {

        }

        TextView tv  = (TextView) convertView.findViewById(R.id.todolistitemtext);

        //tv.setText("PROVA");
        tv.setText(list.get(position).getName()
                + " p: " + list.get(position).getPriority() +
                " done: " + list.get(position).isDone());
        CheckBox cb = (CheckBox) convertView.findViewById(R.id.task_remove);


        CheckBox cBox  = (CheckBox) convertView.findViewById(R.id.task_remove);

        ImageView img = (ImageView) convertView.findViewById(R.id.task_remove_icon);

        cBox.setVisibility(View.VISIBLE);
        img.setVisibility(View.VISIBLE);

       /*
        if(list.get(position).isDone()) {
            cBox.setVisibility(View.VISIBLE);
            img.setVisibility(View.VISIBLE);
            cBox.setChecked(false);
        }else{
            cBox.setVisibility(View.INVISIBLE);
            img.setVisibility(View.INVISIBLE);
            cBox.setChecked(false);
        }
        */
        return convertView;
    }
    public  void onClick(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(
                    R.layout.listitem,
                    null);
        } else {

        }

        TextView tv  = (TextView) convertView.findViewById(R.id.todolistitemtext);

        //tv.setText("PROVA");
        tv.setText(list.get(position).getName()
                + " p: " + list.get(position).getPriority() +
                " done: " + list.get(position).isDone());


    }



}
