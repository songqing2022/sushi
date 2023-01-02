package com.interview.order.model;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int id;
    private int status_id;
    private int sushi_id;
    private long createdAt;
    private List<OrderAction> actions = new ArrayList<OrderAction>();

    public List<OrderAction> getActions() {
        return this.actions;
    }

    public void setActions(List<OrderAction> actions) {
        this.actions = actions;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus_id() {
        return this.status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public int getSushi_id() {
        return this.sushi_id;
    }

    public void setSushi_id(int sushi_id) {
        this.sushi_id = sushi_id;
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

}
