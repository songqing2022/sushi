package com.interview.order.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.interview.order.model.Order;
import com.interview.order.service.OrderManager;
import com.interview.order.vo.OrderSpentTimeVO;
import com.interview.order.vo.ResponseMessageVO;
import com.interview.order.vo.ResponseOrderVO;

@RestController
public class OrderController {

    @Autowired
    private OrderManager orderManager;

    @PostMapping("/api/orders")
    public ResponseEntity<ResponseOrderVO> createOrder(@RequestBody Map<String, String> req) {
        String sushiName = req.get("sushi_name");
        Order order = orderManager.createOrder(sushiName);
        ResponseOrderVO res = new ResponseOrderVO();
        if (order != null && order.getId() > 0) {
            res.setCode(0);
            res.setMsg("Order Created");
            res.setOrder(order);
        } else {
            res.setCode(-1);
            res.setMsg("Failed to create order");
        }
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<ResponseOrderVO> entity = new ResponseEntity<>(res, headers, HttpStatus.CREATED);
        return entity;

    }

    @DeleteMapping("/api/orders/{order_id}")
    public ResponseEntity<ResponseMessageVO> cancelOrder(@PathVariable int order_id) {
        int ok = orderManager.cancelOrder(order_id);
        ResponseMessageVO res = new ResponseMessageVO();
        if (ok > 0) {
            res.setCode(0);
            res.setMsg("Order Cancelled");
        } else {
            res.setCode(-1);
            res.setMsg("Failed to cancel order");
        }
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<ResponseMessageVO> entity = new ResponseEntity<ResponseMessageVO>(res, headers, HttpStatus.OK);
        return entity;
    }

    @GetMapping("/api/orders/status")
    public ResponseEntity<Map<String, List<OrderSpentTimeVO>>> list() {
        Map<String, List<OrderSpentTimeVO>> orders = orderManager.findAll();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<Map<String, List<OrderSpentTimeVO>>> entity = new ResponseEntity<>(orders, headers,
                HttpStatus.OK);
        return entity;
    }

    @PutMapping("/api/orders/{order_id}/pause")
    public ResponseEntity<ResponseMessageVO> pauseOrder(@PathVariable int order_id) {

        int ok = orderManager.pauseOrder(order_id);
        ResponseMessageVO res = new ResponseMessageVO();
        if (ok > 0) {
            res.setCode(0);
            res.setMsg("Order paused");
        } else {
            res.setCode(-1);
            res.setMsg("Failed to pause order");
        }
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<ResponseMessageVO> entity = new ResponseEntity<>(res, headers, HttpStatus.OK);
        return entity;

    }

    @PutMapping("/api/orders/{order_id}/resume")
    public ResponseEntity<ResponseMessageVO> resumeOrder(@PathVariable int order_id) {

        int ok = orderManager.resumeOrder(order_id);
        ResponseMessageVO res = new ResponseMessageVO();
        if (ok > 0) {
            res.setCode(0);
            res.setMsg("Order resumed");
        } else {
            res.setCode(-1);
            res.setMsg("Failed to resume order");
        }
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<ResponseMessageVO> entity = new ResponseEntity<>(res, headers, HttpStatus.OK);
        return entity;

    }

    @GetMapping("/api/orders/{order_id}/start")
    public ResponseEntity<ResponseMessageVO> startProcess(@PathVariable int order_id) {

        int ok = orderManager.startProcess(order_id);
        ResponseMessageVO res = new ResponseMessageVO();
        if (ok > 0) {
            res.setCode(0);
            res.setMsg("Order In Progress");
        } else {
            res.setCode(-1);
            res.setMsg("Failed to Start Process");
        }
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<ResponseMessageVO> entity = new ResponseEntity<>(res, headers, HttpStatus.OK);
        return entity;

    }

    @GetMapping("/api/orders/{order_id}/complete")
    public ResponseEntity<ResponseMessageVO> completeProcess(@PathVariable int order_id) {

        int ok = orderManager.completeOrder(order_id);
        ResponseMessageVO res = new ResponseMessageVO();
        if (ok > 0) {
            res.setCode(0);
            res.setMsg("Order Finished");
        } else {
            res.setCode(-1);
            res.setMsg("Failed to Complete Process");
        }
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<ResponseMessageVO> entity = new ResponseEntity<>(res, headers, HttpStatus.OK);
        return entity;

    }
}