package com.interview.order.service;

import java.util.List;
import java.util.Map;
import com.interview.order.model.Order;
import com.interview.order.vo.OrderSpentTimeVO;

public interface OrderManager {
    public Order createOrder(String name);

    public int cancelOrder(int orderId);

    public Map<String, List<OrderSpentTimeVO>> findAll();

    public int pauseOrder(int orderId);

    public int resumeOrder(int orderId);

    public int startProcess(int orderId);

    public int completeOrder(int orderId);
}
