package com.interview.order.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.interview.order.model.OrderAction;

@Repository
public class OrderActionRepository {

    private static final Logger log = LoggerFactory.getLogger(OrderActionRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    public int save(OrderAction orderAction) {
        String sql = "insert into order_action (order_id,code) values (?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int status = jdbcTemplate.update(connection -> {
            PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, orderAction.getOrder_id());
            pst.setInt(2, orderAction.getCode());
            return pst;
        }, keyHolder);
        Map<String, Object> keys = keyHolder.getKeys();
        if (keys != null)
            for (String key : keys.keySet()) {
                if (key.equalsIgnoreCase("timeat")) {
                    Timestamp d = (Timestamp) keys.get("timeat");
                    orderAction.setTimeAt(d.getTime());
                }
            }
        return status;
    }

}
