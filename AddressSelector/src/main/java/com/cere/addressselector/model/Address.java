package com.cere.addressselector.model;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * Created by CheRevir on 2020/11/4
 */
public class Address {
    private String code;
    private String name;
    private String province;
    private String city;
    private String area;
    private String town;
    private List<Address> children;
    private boolean hasChildren = true;

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getArea() {
        return area;
    }

    public String getTown() {
        return town;
    }

    public List<Address> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return hasChildren;
    }

    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    private int getSize(){
        if (children != null) return children.size();
        else return 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "Province{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", town='" + town + '\'' +
                ", children=" + getSize() +
                '}';
    }
}
