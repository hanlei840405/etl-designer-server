package com.nxin.framework.etl.designer.vo;

import java.io.Serializable;
import java.util.List;

public class PageVo<T> implements Serializable {

    private long total;

    private List<T> items;

    public PageVo() {

    }

    public PageVo(long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
