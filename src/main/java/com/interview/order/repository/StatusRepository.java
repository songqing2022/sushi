package com.interview.order.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.interview.order.model.Status;

@Repository
public class StatusRepository {
    private List<Status> allStatus;
    private static final Logger log = LoggerFactory.getLogger(StatusRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    static class StatusRowMapper implements RowMapper<Status> {
        @Override
        public Status mapRow(ResultSet rs, int rowNum) throws SQLException {
            Status status = new Status();
            status.setId(rs.getInt("id"));
            status.setName(rs.getString("name"));
            return status;
        }

    }

    public int save(Status status) {
        String sql = "insert into status (name) values (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int ok = jdbcTemplate.update(connection -> {
            PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, status.getName());
            return pst;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            int id = key.intValue();
            status.setId(id);
        }
        return ok;
    }

    public Status findByName(String name) {
        Status status = null;
        if (allStatus == null)
            allStatus = findAll();
        if (allStatus != null && allStatus.size() > 0) {
            status = allStatus.stream().filter((s) -> {
                if (s.getName().equalsIgnoreCase(name))
                    return true;
                else
                    return false;
            }).findAny().get();

        }
        // Status status = jdbcTemplate.queryForObject("select * from status where name=
        // ?",
        // new StatusRowMapper(), new Object[] { name });
        return status;

    }

    public Status findById(int id) {
        Status status = null;
        if (allStatus == null)
            allStatus = findAll();
        if (allStatus != null && allStatus.size() > 0) {
            status = allStatus.stream().filter((s) -> {
                if (s.getId() == id)
                    return true;
                else
                    return false;
            }).findAny().get();

        }
        // Status status = jdbcTemplate.queryForObject("select * from status where id=
        // ?",
        // new StatusRowMapper(), new Object[] { id });
        return status;

    }

    public List<Status> findAll() {

        if (allStatus == null) {
            allStatus = jdbcTemplate.query("select * from status",
                    new StatusRowMapper(), new Object[] {});
        }
        return allStatus;
    }
}
