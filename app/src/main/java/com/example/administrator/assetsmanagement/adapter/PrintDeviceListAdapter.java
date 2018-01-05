package com.example.administrator.assetsmanagement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dothantech.printer.IDzPrinter;
import com.example.administrator.assetsmanagement.R;

import java.util.List;

/**
 * Created by Administrator on 2018/1/5.
 */

public class PrintDeviceListAdapter extends BaseAdapter {
    private TextView tv_name = null;
    private TextView tv_mac = null;
    private List<IDzPrinter.PrinterAddress>  printerList;
    Context mContext;
    public PrintDeviceListAdapter(Context context,List<IDzPrinter.PrinterAddress> list) {
        mContext =context;
        printerList = list;
    }
    @Override
    public int getCount() {
        return printerList.size();
    }

    @Override
    public Object getItem(int position) {
        return printerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView= LayoutInflater.from(mContext).inflate(R.layout.printer_item,parent,false);
        }
        tv_name = (TextView) convertView.findViewById(R.id.tv_device_name);
        tv_mac = (TextView) convertView.findViewById(R.id.tv_macaddress);

        if (printerList != null && printerList.size() > position) {
            IDzPrinter.PrinterAddress printer = printerList.get(position);
            tv_name.setText(printer.shownName);
            tv_mac.setText(printer.macAddress);
        }
        return convertView;
    }
}
