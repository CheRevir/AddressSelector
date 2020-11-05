package com.cere.addressselector;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cere.addressselector.model.Address;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by CheRevir on 2020/11/4
 */
public class AddressSelectorDialog extends BottomSheetDialogFragment implements AddressAdapter.OnItemClickListener,
        View.OnClickListener, Runnable {
    private AddressAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private final TextView[] mTextViews = new TextView[4];
    private ProgressBar mProgressBar;
    private Type mType = Type.PROVINCE;
    private ArrayList<Address> list;
    private Address mAddress;
    private int index = 0;
    private final int[] position = {-1, -1, -1, -1};
    private OnAddressSelectorListener mOnAddressSelectorListener;

    public static AddressSelectorDialog newInstance() {
        return new AddressSelectorDialog();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnAddressSelectorListener) {
            mOnAddressSelectorListener = (OnAddressSelectorListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_address_selector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextViews[0] = view.findViewById(R.id.dialog_address_selector_tv_name_1);
        mTextViews[1] = view.findViewById(R.id.dialog_address_selector_tv_name_2);
        mTextViews[2] = view.findViewById(R.id.dialog_address_selector_tv_name_3);
        mTextViews[3] = view.findViewById(R.id.dialog_address_selector_tv_name_4);
        for (TextView textView : mTextViews) {
            textView.setOnClickListener(this);
        }
        view.findViewById(R.id.dialog_address_selector_iv_close).setOnClickListener(this);
        mProgressBar = view.findViewById(R.id.dialog_address_selector_pb);
        mRecyclerView = view.findViewById(R.id.dialog_address_selector_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new AddressAdapter();
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        view.post(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dialog_address_selector_iv_close) {
            dismiss();
        } else if (id == R.id.dialog_address_selector_tv_name_1) {
            index = 0;
            mType = Type.PROVINCE;
            mProgressBar.setVisibility(View.VISIBLE);
            setList(loadList(mType));
            switchName(index, null);
        } else if (id == R.id.dialog_address_selector_tv_name_2) {
            if (position[0] > -1) {
                index = 1;
                mType = Type.PROVINCE;
                mProgressBar.setVisibility(View.VISIBLE);
                setList(list.get(position[0]).getChildren());
                switchName(index, null);
            }
        } else if (id == R.id.dialog_address_selector_tv_name_3) {
            if (position[1] > -1) {
                index = 2;
                mProgressBar.setVisibility(View.VISIBLE);
                List<Address> list = this.list.get(position[0]).getChildren().get(position[1]).getChildren();
                if (list == null) {
                    mType = Type.TOWN;
                    v.post(this);
                } else {
                    setList(list);
                }
                switchName(index, null);
            }
        } else if (id == R.id.dialog_address_selector_tv_name_4) {
            if (position[2] > -1) {
                index = 3;
                mType = Type.TOWN;
                mProgressBar.setVisibility(View.VISIBLE);
                v.post(this);
                switchName(index, null);
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        this.position[index] = position;
        for (int i = index; i < mTextViews.length; i++) {
            if (i == index) {
                mTextViews[i].setText(R.string.please_selector);
            } else {
                mTextViews[i].setText(R.string.please_selector);
                mTextViews[i].setVisibility(View.INVISIBLE);
            }
        }
        mAddress = mAdapter.getList().get(position);
        mProgressBar.setVisibility(View.VISIBLE);
        if (mAddress.getChildren() != null && mAddress.getChildren().size() > 0) {
            setList(mAddress.getChildren());
        } else if (mAddress.getChildren() != null && mAddress.getChildren().size() == 0 || !mAddress.hasChildren()) {
            mTextViews[index].setText(mAddress.getName());
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i <= index; i++) {
                builder.append(mTextViews[i].getText().toString());
                if (i < index) builder.append(",");
            }
            mProgressBar.setVisibility(View.INVISIBLE);
            if (mOnAddressSelectorListener != null) {
                if (mOnAddressSelectorListener.onAddressSelected(builder.toString().split(","))) {
                    dismiss();
                }
            }
            return;
        } else if (index < 3) {
            mType = Type.TOWN;
            Objects.requireNonNull(getView()).post(this);
        }
        index++;
        switchName(index, mAddress.getName());
    }

    private boolean isFirst = true;

    @Override
    public void run() {
        switch (mType) {
            case PROVINCE:
                if (isFirst) {
                    isFirst = false;
                    list = loadList(mType);
                }
                setList(list);
                break;
            case TOWN:
                ArrayList<Address> list = loadList(mType);
                ArrayList<Address> newList = new ArrayList<>(list.size());
                for (Address address : list) {
                    if (address.getProvince().equals(mAddress.getProvince())
                            && address.getCity().equals(mAddress.getCity())
                            && address.getArea().equals(mAddress.getArea())) {
                        address.setHasChildren(false);
                        newList.add(address);
                    }
                }
                setList(newList);
                break;
        }
    }

    private void switchName(int index, String name) {
        for (int i = 0; i < mTextViews.length; i++) {
            if (i == index - 1) {
                if (name != null)
                    mTextViews[i].setText(name);
                mTextViews[i].setTextColor(Color.GRAY);
            } else if (i == index) {
                mTextViews[i].setVisibility(View.VISIBLE);
                mTextViews[i].setTextColor(Color.BLACK);
            } else {
                mTextViews[i].setTextColor(Color.GRAY);
            }
        }
    }

    private void setList(List<Address> list) {
        mRecyclerView.scrollToPosition(0);
        mAdapter.setList(list);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private ArrayList<Address> loadList(Type type) {
        ArrayList<Address> list = new Gson().fromJson(getJson(type), new TypeToken<ArrayList<Address>>() {
        }.getType());
        sort(list);
        return list;
    }

    private String getJson(Type type) {
        String file = "";
        switch (type) {
            case PROVINCE:
                file = "address/level.json";
                break;
            case TOWN:
                file = "address/town.json";
                break;
        }
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        String json = "";
        try {
            inputStream = Objects.requireNonNull(getContext()).getAssets().open(file);
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            json = outputStream.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    private void sort(List<Address> list) {
        Collator collator = Collator.getInstance(Locale.CHINA);
        Collections.sort(list, (o1, o2) -> collator.compare(o1.getName(), o2.getName()));
    }

    /**
     * 显示
     *
     * @param manager {@link #show(FragmentManager, String)}
     */
    public void show(FragmentManager manager) {
        super.show(manager, "AddressSelector");
    }

    private enum Type {
        PROVINCE,
        TOWN
    }

}
