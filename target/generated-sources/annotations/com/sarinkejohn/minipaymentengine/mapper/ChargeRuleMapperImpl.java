package com.sarinkejohn.minipaymentengine.mapper;

import com.sarinkejohn.minipaymentengine.dto.ChargeRuleRequest;
import com.sarinkejohn.minipaymentengine.dto.ChargeRuleResponse;
import com.sarinkejohn.minipaymentengine.entity.ChargeRule;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-21T17:43:51+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11-ea (Ubuntu)"
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
        chargeRule.minAmount( request.getMinAmount() );
        chargeRule.maxAmount( request.getMaxAmount() );
        chargeRule.chargeType( request.getChargeType() );
        chargeRule.chargeValue( request.getChargeValue() );

        return chargeRule.build();
    }

    @Override
    public ChargeRuleResponse toResponse(ChargeRule rule) {
        if ( rule == null ) {
            return null;
        }

        ChargeRuleResponse.ChargeRuleResponseBuilder chargeRuleResponse = ChargeRuleResponse.builder();

        chargeRuleResponse.id( rule.getId() );
        chargeRuleResponse.channel( rule.getChannel() );
        chargeRuleResponse.minAmount( rule.getMinAmount() );
        chargeRuleResponse.maxAmount( rule.getMaxAmount() );
        chargeRuleResponse.chargeType( rule.getChargeType() );
        chargeRuleResponse.chargeValue( rule.getChargeValue() );

        return chargeRuleResponse.build();
    }
}
