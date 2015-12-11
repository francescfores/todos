package com.iesebre.dam2.francesc.todos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

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
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listitem,null);
        } else {

        }

        TextView tv  = (TextView) convertView.findViewById(R.id.todolistitemtext);


        tv.setText(list.get(position).getName()+ " p: " + list.get(position).getPriority() + " done: " + list.get(position).isDone());

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

        tv.setTag(position); //For passing the list item index
        tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {



                EditText taskNameText;


                MaterialDialog dialog = new MaterialDialog.Builder((Activity) v.getContext()).
                        title("Editar tasca").
                        customView(R.layout.form_add_task, true).
                        negativeText("Cancel·la").
                        positiveText("Acepta").
                        negativeColor(Color.parseColor("#ff3333")).
                        positiveColor(Color.parseColor("#2196F3")).
                        onPositive(new MaterialDialog.SingleButtonCallback() {

                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                final TodoItem todoItem = new TodoItem();
                                todoItem.setName(list.get(position).getName());
                                todoItem.setPriority(list.get(position).getPriority());
                                todoItem.setDone(list.get(position).isDone());

                                notifyDataSetChanged();
                            }
                        }).
                        build();
                dialog.show();

                taskNameText = (EditText) dialog.getCustomView().findViewById(R.id.task_tittle);
                taskNameText.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        list.get(position).setName(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });



            }


        });




        return convertView;
    }
    public void onClick(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(
                    R.layout.listitem,
                    null);
        } else {

        }

        TextView tv  = (TextView) convertView.findViewById(R.id.todolistitemtext);


        tv.setText(list.get(position).getName()
                + " p: " + list.get(position).getPriority() +
                " done: " + list.get(position).isDone());
    }



}
