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
import com.interview.order.model.Sushi;

@Repository
public class SushiRepository {
    private static final Logger log = LoggerFactory.getLogger(SushiRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    static class SushiRowMapper implements RowMapper<Sushi> {
        @Override
        public Sushi mapRow(ResultSet rs, int rowNum) throws SQLException {
            Sushi sushi = new Sushi();
            sushi.setId(rs.getInt("id"));
            sushi.setName(rs.getString("name"));
            sushi.setTime_to_make(rs.getInt("time_to_make"));
            return sushi;
        }

    }

    public int save(Sushi sushi) {
        String sql = "insert into sushi (name,time_to_make) values (?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int ok = jdbcTemplate.update(connection -> {
            PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, sushi.getName());
            pst.setInt(2, sushi.getTime_to_make());
            return pst;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key != null) {
            int id = key.intValue();
            sushi.setId(id);
        }
        return ok;
    }

    public Sushi findByName(String name) {
        Sushi sushi = jdbcTemplate.queryForObject("select * from sushi where name=?",
                new SushiRowMapper(), new Object[] { name });
        return sushi;

    }

    public Sushi findById(int id) {
        Sushi sushi = jdbcTemplate.queryForObject("select * from sushi where id=?",
                new SushiRowMapper(), new Object[] { id });
        return sushi;

    }

    public List<Sushi> findAll() {
        List<Sushi> sushies = jdbcTemplate.query("select * from sushi",
                new SushiRowMapper(), new Object[] {});
        return sushies;
    }

}
