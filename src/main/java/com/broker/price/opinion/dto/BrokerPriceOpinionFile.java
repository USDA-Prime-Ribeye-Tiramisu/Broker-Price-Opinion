package com.broker.price.opinion.dto;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "broker_price_opinion_files")
public class BrokerPriceOpinionFile {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name = "input_timestamp")
    private String input_timestamp;

    @Column(name = "user_group")
    private String user_group;

    @Column(name = "report_name")
    private String report_name;

    @Column(name = "metro")
    private String metro;

    @Column(name = "mls_id")
    private String mls_id;

    @Column(name = "output_timestamp")
    private String output_timestamp;

    @Column(name = "output_file_url")
    private String output_file_url;

    @Column(name = "status")
    private String status;
}