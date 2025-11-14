package com.orion.platform.analytics.repository;

import com.orion.platform.analytics.document.OrderAnalytics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderAnalyticsRepository extends MongoRepository<OrderAnalytics, String> {
    List<OrderAnalytics> findByTimestampBetween(Long startDate, Long endDate);
}
