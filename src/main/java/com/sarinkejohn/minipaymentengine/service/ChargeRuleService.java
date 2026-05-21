package com.sarinkejohn.minipaymentengine.service;

import com.sarinkejohn.minipaymentengine.dto.ChargeRuleRequest;
import com.sarinkejohn.minipaymentengine.dto.ChargeRuleResponse;
import com.sarinkejohn.minipaymentengine.entity.ChargeRule;
import com.sarinkejohn.minipaymentengine.mapper.ChargeRuleMapper;
import com.sarinkejohn.minipaymentengine.repository.ChargeRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChargeRuleService {

    private final ChargeRuleRepository chargeRuleRepository;
    private final ChargeRuleMapper chargeRuleMapper;

    @Transactional
    public ChargeRuleResponse createChargeRule(ChargeRuleRequest request) {
        log.info("Creating charge rule for channel: {}, amount range: {} - {}", 
                request.getChannel(), request.getMinAmount(), request.getMaxAmount());
        
        ChargeRule rule = chargeRuleMapper.toEntity(request);
        ChargeRule saved = chargeRuleRepository.save(rule);
        return chargeRuleMapper.toResponse(saved);
    }

    public List<ChargeRuleResponse> getAllChargeRules() {
        log.info("Retrieving all charge rules");
        return chargeRuleRepository.findAll().stream()
                .map(chargeRuleMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ChargeRuleResponse> getChargeRulesByChannel(String channel) {
        log.info("Retrieving charge rules for channel: {}", channel);
        return chargeRuleRepository.findByChannel(channel).stream()
                .map(chargeRuleMapper::toResponse)
                .collect(Collectors.toList());
    }
}
