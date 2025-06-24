package com.broker.price.opinion.service;

import com.broker.price.opinion.dto.BrokerPriceOpinionFile;
import com.broker.price.opinion.repository.BrokerPriceOpinionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class BrokerPriceOpinionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BrokerPriceOpinionRepository brokerPriceOpinionRepository;

    @Autowired
    private FillBrokerPriceOpinionPDFService fillBrokerPriceOpinionPDFService;

    public Optional<BrokerPriceOpinionFile> findBrokerPriceOpinion(Long id) {
        return brokerPriceOpinionRepository.findById(id);
    }

    public BrokerPriceOpinionFile generateBrokerPriceOpinionPDFRequest(String reportName, String metro, String mlsID) {

        BrokerPriceOpinionFile file = new BrokerPriceOpinionFile();

        String inputTimestamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());

        Integer id = trackBrokerPriceOpinionPDFGeneration(inputTimestamp, "demo-users", reportName, metro, mlsID);

        file.setId(Long.valueOf(id));
        file.setInput_timestamp(inputTimestamp);
        file.setUser_group("demo-users");
        file.setReport_name(reportName);
        file.setMetro(metro);
        file.setMls_id(mlsID);

        brokerPriceOpinionRepository.save(file);

        CompletableFuture.runAsync(() -> {
            try {
                this.fillBrokerPriceOpinionPDFService.fillPlaltabPDF(id, reportName, metro, mlsID);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return file;
    }

    public Integer trackBrokerPriceOpinionPDFGeneration(String inputTimestamp,
                                                        String userGroup,
                                                        String reportName,
                                                        String metro,
                                                        String mlsID) {

        String INSERT_SQL =
                "INSERT INTO broker_price_opinion_files (" +
                        "input_timestamp, user_group, report_name, metro, mls_id, output_timestamp, output_file_url, status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, inputTimestamp);
                ps.setString(2, userGroup);
                ps.setString(3, reportName);
                ps.setString(4, metro);
                ps.setString(5, mlsID);
                ps.setString(6, null);
                ps.setString(7, null);
                ps.setString(8, null);
                return ps;
            }, keyHolder);
            return Objects.requireNonNull(keyHolder.getKey()).intValue();
        } catch (DataAccessException e) {
            return null;
        }
    }
}