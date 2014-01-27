package com.salvadordalvik.fastlibrary.list;

import android.app.Activity;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * FastLib
 * Created by Matthew Shepard on 11/17/13.
 */
public class FastAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
    private Activity act;
    private Fragment frag;
    private ArrayList<FastItem> itemList = new ArrayList<FastItem>();
    private LayoutInflater inflater;

    private boolean allEnabled = true;
    private int maxTypeCount;
    private int[] typeList;

    public FastAdapter(Activity activity, Fragment fragment){
        this(activity, fragment, 1);
    }

    public FastAdapter(Activity activity, Fragment fragment, int maxTypeCount) {
        this.act = activity;
        this.frag = fragment;
        this.typeList = null;
        this.maxTypeCount = maxTypeCount;
        inflater = LayoutInflater.from(activity);
    }

    private int generateViewType(int itemLayout){
        if(typeList == null){
            typeList = new int[]{itemLayout};
        }
        for(int ix=0;ix<typeList.length;ix++){
            if(typeList[ix] == itemLayout){
                return ix;
            }
        }
        if(typeList.length >= maxTypeCount){
            throw new RuntimeException("FastAdapter: Number of unique view types exceed maxTypeCount");
        }
        typeList = Arrays.copyOf(typeList, typeList.length+1);
        typeList[typeList.length-1] = itemLayout;
        return typeList.length-1;
    }

    public void addItems(List<? extends FastItem> list){
        itemList.addAll(list);
        for(FastItem item : list){
            allEnabled = allEnabled && item.isEnabled();
            item.setType(generateViewType(item.getLayoutId()));
        }
        notifyDataSetChanged();
    }

    public void addItems(FastItem... list){
        for(FastItem item : list){
            allEnabled = allEnabled && item.isEnabled();
            item.setType(generateViewType(item.getLayoutId()));
            itemList.add(item);
        }
        notifyDataSetChanged();
    }

    public void clearList(){
        itemList.clear();
        allEnabled = true;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public FastItem getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return itemList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FastItem item = itemList.get(position);
        Object viewHolder;
        if(convertView == null){
            convertView = inflater.inflate(item.getLayoutId(), parent, false);
            viewHolder = item.generateViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = convertView.getTag();
        }
        item.updateView(convertView, viewHolder);
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        itemList.get(position).onItemClick(act, frag);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return allEnabled;
    }

    @Override
    public boolean isEnabled(int position) {
        return itemList.get(position).isEnabled();
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return maxTypeCount;
    }

    @Override
    public boolean isEmpty() {
        return itemList.isEmpty();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
