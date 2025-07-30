package com.broker.price.opinion.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ImagesService {

    private final JdbcTemplate trinoJdbcTemplate;

    @Autowired
    public ImagesService(
            @Qualifier("trinoJdbcTemplate") JdbcTemplate trinoJdbcTemplate) {
        this.trinoJdbcTemplate = trinoJdbcTemplate;
    }

    public List<String> imageURLsPlatlabTrinoServer(String mls_id, String display_mls_number) {

        String sql = "SELECT source_url FROM iceberg.platlab.mls_image_updates " +
                "WHERE mls_id = ? AND mls_listing_id = ? " +
                "ORDER BY sequence_number";

        return trinoJdbcTemplate.queryForList(sql, String.class, mls_id, display_mls_number);
    }
}