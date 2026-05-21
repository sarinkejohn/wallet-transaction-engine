package com.sarinkejohn.minipaymentengine.mapper;

import com.sarinkejohn.minipaymentengine.dto.ChargeRuleRequest;
import com.sarinkejohn.minipaymentengine.dto.ChargeRuleResponse;
import com.sarinkejohn.minipaymentengine.entity.ChargeRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChargeRuleMapper {

    @Mapping(target = "id", ignore = true)
    ChargeRule toEntity(ChargeRuleRequest request);

    ChargeRuleResponse toResponse(ChargeRule rule);
}
