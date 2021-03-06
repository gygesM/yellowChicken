package org.igeek.pojo;

import java.util.Date;

public class Rank {
    private Integer id;

    private String title;

    private String desc;

    private String status;

    private Date created;

    private Date modified;

    public Rank(Integer id, String title, String desc, String status, Date created, Date modified) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.status = status;
        this.created = created;
        this.modified = modified;
    }

    public Rank() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc == null ? null : desc.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}