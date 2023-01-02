package com.interview.order.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.interview.order.model.Order;
import com.interview.order.model.OrderAction;

@Repository
public class OrderRepository {

    private static final Logger log = LoggerFactory.getLogger(OrderRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    static class OrderRowMapper implements RowMapper<Order> {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setId(rs.getInt("id"));
            order.setSushi_id(rs.getInt("sushi_id"));
            order.setStatus_id(rs.getInt("status_id"));
            order.setCreatedAt(rs.getTimestamp("createdat").getTime());
            return order;
        }

    }

    static class OrderActionRowMapper implements RowMapper<OrderAction> {
        @Override
        public OrderAction mapRow(ResultSet rs, int rowNum) throws SQLException {
            OrderAction action = new OrderAction();
            action.setCode(rs.getInt("code"));
            action.setOrder_id(rs.getInt("order_id"));
            action.setTimeAt(rs.getTimestamp("timeat").getTime());
            return action;
        }

    }

    public int save(Order order) {
        String sql = "insert into sushi_order (status_id, sushi_id) values (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int status = jdbcTemplate.update(connection -> {
            PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, order.getStatus_id());
            pst.setInt(2, order.getSushi_id());
            return pst;
        }, keyHolder);
        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null)
            for (String key : keys.keySet()) {
                if (key.equalsIgnoreCase("id"))
                    order.setId(Integer.parseInt(keys.get("id") + ""));
                if (key.equalsIgnoreCase("createdat")) {
                    Timestamp d = (Timestamp) keys.get("createdat");
                    order.setCreatedAt(d.getTime());
                }
            }
        return status;
    }

    public Order findById(int id) {
        Order order = jdbcTemplate.queryForObject("select * from sushi_order where id= ?",
                new OrderRowMapper(), new Object[] { id });
        return order;

    }

    public List<Order> findAll() {
        List<Order> orders = jdbcTemplate.query("select * from sushi_order",
                new OrderRowMapper(), new Object[] {});
        return orders;
    }

    public List<Order> findByStatus(int status_id) {
        List<Order> orders = jdbcTemplate.query("select * from sushi_order where status_id= ?",
                new OrderRowMapper(), new Object[] { status_id });
        return orders;
    }

    public List<Order> findAllOrderWithActions() {
        OrderRowMapper orderMapper = new OrderRowMapper();
        OrderActionRowMapper actionMapper = new OrderActionRowMapper();
        return jdbcTemplate.query("select * from sushi_order, order_action "
                + " where sushi_order.id = order_action.order_id order by sushi_order.id",
                new ResultSetExtractor<List<Order>>() {
                    public List<Order> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        List<Order> orders = new ArrayList<Order>();
                        Order currentOrder = null;
                        int orderIdx = 0;
                        int actionIdx = 0;
                        while (rs.next()) {
                            if (currentOrder == null || currentOrder.getId() != rs.getInt("id")) {
                                currentOrder = orderMapper.mapRow(rs, orderIdx++);
                                actionIdx = 0;
                                orders.add(currentOrder);
                            }
                            currentOrder.getActions().add(actionMapper.mapRow(rs, actionIdx++));
                        }
                        return orders;
                    }

                });
    }

    public int updateStatus(int orderId, int status) {
        int ok = jdbcTemplate.update("UPDATE sushi_order SET status_id = ? WHERE id = ?",
                new Object[] { status, orderId });
        return ok;
    }

    public int countByStatus(int status) {
        String sql = "SELECT COUNT(*) FROM sushi_order where status_id=?";
        return jdbcTemplate.queryForObject(sql, Integer.class, new Object[] { status });
    }

}
