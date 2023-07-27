package com.example.paymentgateway.controller;

import com.example.paymentgateway.service.AccountService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    record RequestAccount(String email, double amount){}
    record ResponseAccount( String email, double amount){}

    record RequestTransferAccount(@JsonProperty("from_email") String fromEmail,
                                  @JsonProperty("to_email") String toEmail,
                                  double amount){}

    record ResponseTransferAccount(@JsonProperty("from_email") String fromEmail,
                                  @JsonProperty("from_amount") double fromAmount,
                                  @JsonProperty("to_email") String toEmail,
                                  @JsonProperty("to_amount") double toAmount){}

    @PostMapping(value = "/withdraw")
    public ResponseAccount withdraw(@RequestBody RequestAccount requestAccount){
        double amount = accountService.withdraw(requestAccount.email, requestAccount.amount);
        return new ResponseAccount(requestAccount.email, amount);
    }

    @PostMapping("/deposit")
    public ResponseAccount deposit(@RequestBody RequestAccount requestAccount){
        double amount = accountService.deposit(requestAccount.email, requestAccount.amount);
        return new ResponseAccount(requestAccount.email, amount);
    }

    @PostMapping("/transfer")
    public ResponseTransferAccount transfer(@RequestBody RequestTransferAccount requestTransferAccount){
        accountService.transfer(requestTransferAccount.fromEmail, requestTransferAccount.toEmail, requestTransferAccount.amount);
        return new ResponseTransferAccount(requestTransferAccount.fromEmail,
                accountService.getBalance(requestTransferAccount.fromEmail),
                requestTransferAccount.toEmail,
                accountService.getBalance(requestTransferAccount.toEmail));
    }


}
