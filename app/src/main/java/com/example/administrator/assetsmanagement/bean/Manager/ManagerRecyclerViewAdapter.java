package com.example.administrator.assetsmanagement.bean.Manager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.administrator.assetsmanagement.Interface.SelectManagerClickListener;
import com.example.administrator.assetsmanagement.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于选择接收人或管理员时，列表适配器
 * Created by Administrator on 2017/12/14 0014.
 */

public class ManagerRecyclerViewAdapter extends RecyclerView.Adapter<ManagerRecyclerViewAdapter.ManagerViewHolder> {
    Context mContext;
    LayoutInflater mInflater;
    List<Person> mMangerList;
    Map<Integer, Boolean> map = new HashMap<>();
    SelectManagerClickListener listener;
    public ManagerRecyclerViewAdapter(Context context, List<Person> list) {
        mContext = context;
        mMangerList = list;
        mInflater = LayoutInflater.from(mContext);

    }

    public void setOnClickListener(SelectManagerClickListener listener) {
        this.listener = listener;
    }
    @Override
    public ManagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.tree_item_checkbox, parent,false);
        return new ManagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ManagerViewHolder holder, final int position) {
        holder.name.setText(mMangerList.get(position).getUsername());
       holder.cb_select.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               boolean ischeck = holder.cb_select.isChecked();
               if (ischeck) {
                   for (int i = 0; i < mMangerList.size(); i++) {
                       if (i==position) {
                           mMangerList.get(i).setSelected(ischeck);
                       } else {
                           mMangerList.get(i).setSelected(false);
                       }
                   }
                   listener.select(mMangerList.get(position));
               } else {
                   for(int j = 0;j<mMangerList.size();j++) {
                       if (j==position) {
                           mMangerList.get(j).setSelected(ischeck);
                       }
                   }
                   listener.cancelSelect();
               }
               notifyDataSetChanged();
           }
       });
        holder.cb_select.setChecked(mMangerList.get(position).getSelected());
    }

    @Override
    public int getItemCount() {
        return mMangerList.size();
    }

    class ManagerViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb_select;
        TextView name;

        public ManagerViewHolder(View itemView) {
            super(itemView);
            cb_select = (CheckBox) itemView.findViewById(R.id.cb_tree_item_checkbox_select);
            name = (TextView) itemView.findViewById(R.id.tv_tree_item_checkbox_name);
        }
    }
}
