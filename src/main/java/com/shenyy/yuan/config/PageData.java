package com.shenyy.yuan.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageData<T> implements Serializable {
    /**
     * 当前页码
     */
    private Integer pageIndex;
    /**
     * 数据总数
     */
    private Long total;
    /**
     * 分页大小
     */
    private Integer pageSize;
    /**
     * 数据
     */
    private List<T> data;
    /**
     * 其他数据
     */
    private Map<String,Object> otherData = new HashMap<>();

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Map<String, Object> getOtherData() {
        return otherData;
    }

    public void setOtherData(Map<String, Object> otherData) {
        this.otherData = otherData;
    }

    @Override
    public String toString() {
        return "PageData{" +
                "pageIndex=" + pageIndex +
                ", total=" + total +
                ", pageSize=" + pageSize +
                ", data=" + data +
                ", otherData=" + otherData +
                '}';
    }
}
