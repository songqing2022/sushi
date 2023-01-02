package com.interview.order.vo;

import com.interview.order.model.Order;

public class ResponseOrderVO extends ResponseMessageVO {

    private Order order;

    public Object getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}
