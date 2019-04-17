package com.mishas.stuff.ms;

import com.mishas.stuff.ms.repository.dao.TransactionRepository;
import com.mishas.stuff.ms.repository.dao.TransactionStatusRepository;
import com.mishas.stuff.ms.service.RecordKeepingService;
import com.mishas.stuff.ms.service.TransactionApprovalService;
import com.mishas.stuff.ms.web.client.ClientAccountSerivceHttpClient;
import com.mishas.stuff.ms.web.controller.CardController;
import com.mishas.stuff.ms.web.exceptionmapper.DatabaseExcpetionMapper;
import com.mishas.stuff.ms.web.exceptionmapper.HttpClientExceptionsMapper;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class MastercardApplication extends Application {



    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet();
        return resources;
    }

    @Override
    public Set<Object> getSingletons() {
        // create service layer
        RecordKeepingService  recordKeepingService = new RecordKeepingService(
                new TransactionRepository(),
                new TransactionApprovalService(new ClientAccountSerivceHttpClient()),
                new TransactionStatusRepository()
        );
        // add resources
        final Set<Object> singletons = new HashSet<>();
        singletons.add(new CardController(recordKeepingService));
        singletons.add(new DatabaseExcpetionMapper());
        singletons.add(new HttpClientExceptionsMapper());
        return singletons;
    }

}
