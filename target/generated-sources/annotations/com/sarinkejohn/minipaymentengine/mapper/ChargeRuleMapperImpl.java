package com.sarinkejohn.minipaymentengine.mapper;

import com.sarinkejohn.minipaymentengine.dto.ChargeRuleRequest;
import com.sarinkejohn.minipaymentengine.dto.ChargeRuleResponse;
import com.sarinkejohn.minipaymentengine.entity.ChargeRule;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-21T13:03:12+0300",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260407-0427, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class ChargeRuleMapperImpl implements ChargeRuleMapper {

    @Override
    public ChargeRule toEntity(ChargeRuleRequest request) {
        if ( request == null ) {
            return null;
        }

        ChargeRule.ChargeRuleBuilder chargeRule = ChargeRule.builder();

        chargeRule.channel( request.getChannel() );
        chargeRule.chargeType( request.getChargeType() );
        chargeRule.chargeValue( request.getChargeValue() );
        chargeRule.maxAmount( request.getMaxAmount() );
        chargeRule.minAmount( request.getMinAmount() );

        return chargeRule.build();
    }

    @Override
    public ChargeRuleResponse toResponse(ChargeRule rule) {
        if ( rule == null ) {
            return null;
        }

        ChargeRuleResponse.ChargeRuleResponseBuilder chargeRuleResponse = ChargeRuleResponse.builder();

        chargeRuleResponse.channel( rule.getChannel() );
        chargeRuleResponse.chargeType( rule.getChargeType() );
        chargeRuleResponse.chargeValue( rule.getChargeValue() );
        chargeRuleResponse.id( rule.getId() );
        chargeRuleResponse.maxAmount( rule.getMaxAmount() );
        chargeRuleResponse.minAmount( rule.getMinAmount() );

        return chargeRuleResponse.build();
    }
}
