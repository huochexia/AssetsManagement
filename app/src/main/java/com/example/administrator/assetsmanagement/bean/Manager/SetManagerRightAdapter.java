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

import java.util.List;

/**
 * 人员权限设置列表适配器
 * Created by Administrator on 2017/12/14 0014.
 */

public class SetManagerRightAdapter extends RecyclerView.Adapter<SetManagerRightAdapter.ManagerViewHolder> {
    Context mContext;
    LayoutInflater mInflater;
    List<Person> mMangerList;
    SelectManagerClickListener listener;
    public SetManagerRightAdapter(Context context, List<Person> list) {
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
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.select(mMangerList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mMangerList.size();
    }

    class ManagerViewHolder extends RecyclerView.ViewHolder {
        CheckBox mCheckBox;
        TextView name;
        public ManagerViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_tree_item_checkbox_name);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.cb_tree_item_checkbox_select);
            mCheckBox.setVisibility(View.INVISIBLE);
        }
    }
}
