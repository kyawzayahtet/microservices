package com.example.paymentgateway.service;

import com.example.paymentgateway.dao.AccountDao;
import com.example.paymentgateway.entity.Account;
import com.example.paymentgateway.exception.InsufficientAmountException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountDao accountDao;

    public double getBalance(String email){
        return findByEmail(email).getAmount();
    }

    @Transactional
    public void transfer(String fromEmail, String toEmail, double amount){
        withdraw(fromEmail, amount);
        deposit(toEmail, amount);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public double deposit(String email, double amount){
        double existing = getBalance(email);
        double updateAmount = amount + existing;
        Account account = findByEmail(email);
        account.setAmount(updateAmount);
        return updateAmount;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public double withdraw(String email, double amount){
        double existing = getBalance(email);
        if(existing < amount){
            throw new InsufficientAmountException();
        }
        double updateAmount = existing - amount;
        Account account = findByEmail(email);
        account.setAmount(updateAmount);
        return updateAmount;
    }

    public Account findByEmail(String email){
        return accountDao.findByEmail(email).orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND)));
    }
}
