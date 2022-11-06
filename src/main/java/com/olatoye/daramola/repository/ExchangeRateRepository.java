package com.olatoye.daramola.repository;

import com.olatoye.daramola.model.entity.ExchangeRate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends MongoRepository<ExchangeRate, String> {
    Optional<ExchangeRate> findByDateCreated(LocalDateTime dateCreated);
//    ExchangeRate findTopByOrderByDateCreatedDesc();
//    List<ExchangeRate> findByDateCreatedBetween(LocalDateTime from, LocalDateTime to);
}
