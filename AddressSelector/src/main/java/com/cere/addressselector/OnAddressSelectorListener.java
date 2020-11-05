package com.cere.addressselector;

/**
 * Created by CheRevir on 2020/11/5
 */
public interface OnAddressSelectorListener {
    /**
     * 地址选择回调
     * @param address 0  - 3 {"省", "市", "区", "镇"}
     * @return 是否关闭
     */
    boolean onAddressSelected(String[] address);
}
