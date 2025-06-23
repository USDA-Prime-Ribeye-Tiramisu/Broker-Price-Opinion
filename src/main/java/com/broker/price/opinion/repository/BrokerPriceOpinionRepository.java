package com.broker.price.opinion.repository;

import com.broker.price.opinion.dto.BrokerPriceOpinionFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrokerPriceOpinionRepository extends JpaRepository<BrokerPriceOpinionFile, Long> {

}