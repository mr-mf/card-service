package com.mishas.stuff.cas;

import com.mishas.stuff.cas.repository.dao.AccountRepository;
import com.mishas.stuff.cas.repository.dao.TransactionStatusRepository;
import com.mishas.stuff.cas.service.AccountService;
import com.mishas.stuff.cas.web.controller.AccountController;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class ClientAccountApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final var classes = new HashSet<Class<?>>();
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        final var accountRepository = new AccountRepository();
        final var transactionStatusRepository = new TransactionStatusRepository();
        final var accountService = new AccountService(accountRepository, transactionStatusRepository);
        final var accountController = new AccountController(accountService);
        final var singletons = new HashSet<>();
        singletons.add(accountController);
        return singletons;
    }
}
