package ru.ruslan.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PostgreSqlDbService {
    private static final Logger logger = LoggerFactory.getLogger(PostgreSqlDbService.class);

    private final JdbcTemplate postgreSqlJdbcTemplate;

    public PostgreSqlDbService(JdbcTemplate postgreSqlJdbcTemplate) {
        this.postgreSqlJdbcTemplate = postgreSqlJdbcTemplate;
    }

    public String getBillingData(String billingAccount) {
        logger.info("Querying PostgreSQL DB for billingAccount: {}", billingAccount);

        String sql = "SELECT * FROM billing_accounts WHERE account_number = ?";

        try {
            Map<String, Object> result = postgreSqlJdbcTemplate.queryForMap(sql, billingAccount);
            return result.toString();
        } catch (Exception e) {
            logger.error("Error querying PostgreSQL DB", e);
            return "{}";
        }
    }
}