package com.brokage.controller;

import com.brokage.constant.ChallengeConstant;
import com.brokage.constant.Currency;
import com.brokage.dto.DepositDto;
import com.brokage.dto.WithdrawDto;
import com.brokage.resource.AmountResource;
import com.brokage.service.BankOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = ChallengeConstant.BASE_URL)
@RequiredArgsConstructor
public class BankOperationController {

    private final BankOperationService bankOperationService;

    @PostMapping("/customers/{customerId}/deposit")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<AmountResource> depositAmount(@PathVariable String customerId, @RequestBody @Valid DepositDto depositDto) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return ResponseEntity.ok(AmountResource.builder()
                .amount(bankOperationService.depositAmount(depositDto.getAmount(), userName, customerId))
                .currency(Currency.TRY)
                .build());
    }

    @PostMapping("/customers/{customerId}/withdraw")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_ADMIN')")
    public ResponseEntity<AmountResource> withdrawAmount(@PathVariable String customerId, @RequestBody @Valid WithdrawDto withdrawDto) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        //I ignored iban for mocking
        return ResponseEntity.ok(AmountResource.builder()
                .amount(bankOperationService.withdrawAmount(withdrawDto.getAmount(), userName, customerId))
                .currency(Currency.TRY)
                .build());
    }
}
