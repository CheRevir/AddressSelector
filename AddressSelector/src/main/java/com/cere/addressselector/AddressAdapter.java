package com.cere.addressselector;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cere.addressselector.model.Address;

import java.util.List;

/**
 * Created by CheRevir on 2020/11/4
 */
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> implements View.OnClickListener {
    private List<Address> list;
    private OnItemClickListener mOnItemClickListener;

    public void setList(List<Address> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public List<Address> getList() {
        return list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(list.get(position).getName());
        holder.view.setTag(position);
        holder.view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick((int) v.getTag());
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name = view.findViewById(R.id.item_address_tv);
        }
    }

    interface OnItemClickListener {
        void onItemClick(int position);
    }
}
