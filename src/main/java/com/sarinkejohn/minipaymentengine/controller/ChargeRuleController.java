package com.sarinkejohn.minipaymentengine.controller;

import com.sarinkejohn.minipaymentengine.dto.ChargeRuleRequest;
import com.sarinkejohn.minipaymentengine.dto.ChargeRuleResponse;
import com.sarinkejohn.minipaymentengine.service.ChargeRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charge-rules")
@RequiredArgsConstructor
@Tag(name = "Charge Rule Management", description = "APIs for configuring transaction charges")
public class ChargeRuleController {

    private final ChargeRuleService chargeRuleService;

    @PostMapping
    @Operation(summary = "Create a new charge rule")
    public ResponseEntity<ChargeRuleResponse> createChargeRule(@Valid @RequestBody ChargeRuleRequest request) {
        return new ResponseEntity<>(chargeRuleService.createChargeRule(request), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all charge rules")
    public ResponseEntity<List<ChargeRuleResponse>> getAllChargeRules() {
        return ResponseEntity.ok(chargeRuleService.getAllChargeRules());
    }

    @GetMapping("/channel/{channel}")
    @Operation(summary = "Get charge rules by channel")
    public ResponseEntity<List<ChargeRuleResponse>> getChargeRulesByChannel(@PathVariable String channel) {
        return ResponseEntity.ok(chargeRuleService.getChargeRulesByChannel(channel));
    }
}
