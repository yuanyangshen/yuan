package com.shenyy.yuan.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;

public class Content {

    @Field(value = "id")
    private String id;

    @Field(value = "cname")
    private String cname;

    @Field(value = "cdetail")
    private String cdetail;

    @Field(value = "cmark")
    private String cmark;

    @Field(value = "updatetime")
    private Date updatetime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCdetail() {
        return cdetail;
    }

    public void setCdetail(String cdetail) {
        this.cdetail = cdetail;
    }

    public String getCmark() {
        return cmark;
    }

    public void setCmark(String cmark) {
        this.cmark = cmark;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}
