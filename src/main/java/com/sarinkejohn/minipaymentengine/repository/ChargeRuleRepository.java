package com.sarinkejohn.minipaymentengine.repository;

import com.sarinkejohn.minipaymentengine.entity.ChargeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChargeRuleRepository extends JpaRepository<ChargeRule, Long> {
    List<ChargeRule> findByChannel(String channel);

    Optional<ChargeRule> findByChannelIgnoreCaseAndMinAmountLessThanEqualAndMaxAmountGreaterThanEqual(
            String channel, BigDecimal amountMin, BigDecimal amountMax
    );
}
