package com.shenyy.yuan.form;

public class BaseForm {
    /**
     * 当前页
     */
    private Integer pageIndex = 1;
    /**
     * 分页大小
     */
    private Integer pageSize = 20;
    /**
     * 排序字段
     */
    private String orderName;
    /**
     * 排序方式
     */
    private String orderType;

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
