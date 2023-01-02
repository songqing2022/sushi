package com.interview.order.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.interview.order.OrderApplication;
import com.interview.order.model.Order;
import com.interview.order.model.OrderAction;
import com.interview.order.model.Status;
import com.interview.order.model.Sushi;
import com.interview.order.repository.OrderActionRepository;
import com.interview.order.repository.OrderRepository;
import com.interview.order.repository.StatusRepository;
import com.interview.order.repository.SushiRepository;
import com.interview.order.service.OrderManager;
import com.interview.order.vo.OrderSpentTimeVO;

@Component
public class OrderManagerImpl implements OrderManager {
    private static final int ACTION_CODE_START = 1;
    private static final int ACTION_CODE_PAUSE = 2;
    private static final int ACTION_CODE_RESUME = 3;
    private static final int ACTION_CODE_COMPLETE = 4;
    private static final int ACTION_CODE_CANCEL = 5;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private SushiRepository sushiRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private OrderActionRepository orderActionRepository;

    private static final Logger log = LoggerFactory.getLogger(OrderApplication.class);

    @Override
    @Transactional
    public Order createOrder(String sushiName) {
        Order order = null;
        try {
            Sushi sushi = sushiRepository.findByName(sushiName);
            Status createdSatus = statusRepository.findByName("created");
            int status_id = createdSatus.getId();
            Status inProgressSatus = statusRepository.findByName("in-progress");
            boolean inProgress = false;
            if (orderRepository.countByStatus(inProgressSatus.getId()) < 3) {
                status_id = inProgressSatus.getId();
                inProgress = true;
            }
            order = new Order();
            order.setSushi_id(sushi.getId());
            order.setStatus_id(status_id);
            orderRepository.save(order);
            if (order.getId() > 0 && inProgress) {
                OrderAction action = new OrderAction(ACTION_CODE_START, order.getId());
                orderActionRepository.save(action);
            }
        } catch (DataAccessException e) {
            log.error("DataBase access error!");
        }
        return order;
    }

    @Override
    @Transactional
    public int cancelOrder(int orderId) {
        int ok = 0;
        try {
            Status status = statusRepository.findByName("cancelled");
            Order currentOrder = orderRepository.findById(orderId);
            Status currentStatus = statusRepository.findById(currentOrder.getStatus_id());
            if (!currentStatus.getName().equalsIgnoreCase("cancelled")
                    && !currentStatus.getName().equalsIgnoreCase("finished")) {
                ok = orderRepository.updateStatus(orderId, status.getId());
                if (ok > 0 && !currentStatus.getName().equalsIgnoreCase("created")) {
                    OrderAction action = new OrderAction(ACTION_CODE_CANCEL, orderId);
                    orderActionRepository.save(action);
                }
            }
        } catch (DataAccessException e) {
            ok = -1;
            log.error("DataBase access error!");
        }
        return ok;
    }

    @Override
    public Map<String, List<OrderSpentTimeVO>> findAll() {
        Map<String, List<OrderSpentTimeVO>> allStatusOrders = new HashMap<>();
        try {
            List<Status> allStatus = statusRepository.findAll();
            List<Order> actedOrders = orderRepository.findAllOrderWithActions();

            for (Status status : allStatus) {
                List<OrderSpentTimeVO> statusOrders = null;
                if (status.getName().equalsIgnoreCase("created")) {
                    List<Order> noActedOrders = orderRepository.findByStatus(status.getId());
                    statusOrders = noActedOrders.stream().map((order) -> {
                        OrderSpentTimeVO m = new OrderSpentTimeVO();
                        m.setOrderId(order.getId());
                        m.setTimeSpent(0);
                        return m;
                    }).collect(Collectors.toList());
                } else {
                    statusOrders = actedOrders.stream()
                            .filter((o) -> o.getStatus_id() == status.getId()).map((order) -> {
                                OrderSpentTimeVO m = new OrderSpentTimeVO();
                                m.setOrderId(order.getId());
                                long timeSpent = 0;
                                for (OrderAction action : order.getActions()) {
                                    if (action.getCode() == ACTION_CODE_START || action.getCode() == ACTION_CODE_RESUME)
                                        timeSpent = timeSpent - action.getTimeAt();
                                    else
                                        timeSpent = timeSpent + action.getTimeAt();
                                }
                                if (status.getName().equalsIgnoreCase("in-progress"))
                                    timeSpent = timeSpent + System.currentTimeMillis();
                                m.setTimeSpent(Math.round(timeSpent / 1000));
                                return m;
                            }).collect(Collectors.toList());
                }
                allStatusOrders.put(status.getName(), statusOrders);

            }
        } catch (DataAccessException e) {
            log.error("DataBase access error!");
        }
        return allStatusOrders;
    }

    @Override
    @Transactional
    public int pauseOrder(int orderId) {
        int ok = 0;
        try {
            Order order = orderRepository.findById(orderId);
            if (order != null) {
                Status currentStatus = statusRepository.findById(order.getStatus_id());
                if (currentStatus.getName().equalsIgnoreCase("in-progress")) {
                    Status pause = statusRepository.findByName("paused");
                    if (pause != null)
                        ok = orderRepository.updateStatus(orderId, pause.getId());
                    if (ok > 0) {
                        OrderAction action = new OrderAction(ACTION_CODE_PAUSE, orderId);
                        orderActionRepository.save(action);
                    }
                }
            }
        } catch (DataAccessException e) {
            ok = -1;
            log.error("DataBase access error!");
        }
        return ok;
    }

    @Override
    @Transactional
    public int resumeOrder(int orderId) {
        int ok = 0;
        try {
            Order order = orderRepository.findById(orderId);
            if (order != null) {
                Status currentStatus = statusRepository.findById(order.getStatus_id());
                if (currentStatus.getName().equalsIgnoreCase("paused")) {
                    Status inProgressStatus = statusRepository.findByName("in-progress");
                    if (orderRepository.countByStatus(inProgressStatus.getId()) < 3) {
                        ok = orderRepository.updateStatus(orderId, inProgressStatus.getId());
                        if (ok > 0) {
                            OrderAction action = new OrderAction(ACTION_CODE_RESUME, orderId);
                            orderActionRepository.save(action);
                        }
                    }
                }
            }
        } catch (DataAccessException e) {
            ok = -1;
            log.error("DataBase access error!");
        }
        return ok;
    }

    @Override
    @Transactional
    public int completeOrder(int orderId) {
        int ok = 0;
        try {
            Order order = orderRepository.findById(orderId);
            if (order != null) {
                Status currentStatus = statusRepository.findById(order.getStatus_id());
                if (currentStatus.getName().equalsIgnoreCase("in-progress")
                        || currentStatus.getName().equalsIgnoreCase("paused")) {
                    Status finishedStatus = statusRepository.findByName("finished");
                    ok = orderRepository.updateStatus(orderId, finishedStatus.getId());
                    if (ok > 0) {
                        OrderAction action = new OrderAction(ACTION_CODE_COMPLETE, orderId);
                        orderActionRepository.save(action);
                    }
                }
            }
        } catch (

        DataAccessException e) {
            ok = -1;
            log.error("DataBase access error!");
        }
        return ok;
    }

    @Override
    @Transactional
    public int startProcess(int orderId) {
        int ok = 0;
        try {
            Order order = orderRepository.findById(orderId);
            if (order != null) {
                Status currentStatus = statusRepository.findById(order.getStatus_id());
                if (currentStatus.getName().equalsIgnoreCase("created")) {
                    Status inProgressStatus = statusRepository.findByName("in-progress");
                    if (orderRepository.countByStatus(inProgressStatus.getId()) < 3) {
                        if (inProgressStatus != null)
                            ok = orderRepository.updateStatus(orderId, inProgressStatus.getId());
                        if (ok > 0) {
                            OrderAction action = new OrderAction(ACTION_CODE_START, orderId);
                            orderActionRepository.save(action);
                        }
                    }
                }
            }
        } catch (DataAccessException e) {
            ok = -1;
            log.error("DataBase access error!");
        }
        return ok;
    }

}
