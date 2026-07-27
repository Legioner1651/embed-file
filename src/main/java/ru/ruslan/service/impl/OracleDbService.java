package ru.ruslan.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OracleDbService {
    private static final Logger logger = LoggerFactory.getLogger(OracleDbService.class);

    private final JdbcTemplate oracleJdbcTemplate;

    public OracleDbService(JdbcTemplate oracleJdbcTemplate) {
        this.oracleJdbcTemplate = oracleJdbcTemplate;
    }

    public String getExerciseData(String orderId) {
        logger.info("Querying Oracle DB for orderId: {}", orderId);

        String sql = "SELECT * FROM exercises WHERE order_id = ?";

        try {
            Map<String, Object> result = oracleJdbcTemplate.queryForMap(sql, orderId);
            return result.toString();
        } catch (Exception e) {
            logger.error("Error querying Oracle DB", e);
            return "{}";
        }
    }
}