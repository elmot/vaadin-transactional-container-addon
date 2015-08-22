package org.vaadin.data.tx.demo.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Ticket implements Serializable {
    private long id;
    private String name;
    private String description;
    private Date createdAt;
    private Ticket parent;
    private List<Ticket> blockedBy;
    private boolean hold;
    private Status status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Ticket getParent() {
        return parent;
    }

    public void setParent(Ticket parent) {
        this.parent = parent;
    }

    public List<Ticket> getBlockedBy() {
        return blockedBy;
    }

    public void setBlockedBy(List<Ticket> blockedBy) {
        this.blockedBy = blockedBy;
    }

    public boolean isHold() {
        return hold;
    }

    public void setHold(boolean hold) {
        this.hold = hold;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Ticket{" + "id=" + id + ", name='" + name + '\''
                + ", description='" + description + '\'' + ", createdAt="
                + createdAt + ", parent=" + parent + ", blockedBy=" + blockedBy
                + ", hold=" + hold + ", status=" + status + '}';
    }
}
