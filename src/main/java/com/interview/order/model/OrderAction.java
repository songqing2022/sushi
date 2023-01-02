package com.interview.order.model;

public class OrderAction {

    private int code;
    private int order_id;
    private long timeAt;

    public OrderAction() {

        this.code = 0;
        this.order_id = 0;
        this.timeAt = 0;
    }

    public OrderAction(int code, int order_id) {
        this.code = code;
        this.order_id = order_id;
    }

    public long getTimeAt() {
        return this.timeAt;
    }

    public void setTimeAt(long timeAt) {
        this.timeAt = timeAt;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getOrder_id() {
        return this.order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

}
