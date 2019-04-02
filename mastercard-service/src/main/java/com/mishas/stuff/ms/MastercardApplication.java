package com.mishas.stuff.ms;

import com.mishas.stuff.ms.repository.dao.TransactionDao;
import com.mishas.stuff.ms.service.RecordKeepingService;
import com.mishas.stuff.ms.web.controller.CardController;
import com.mishas.stuff.ms.web.exceptionmapper.DatabaseExcpetionMapper;
import io.swagger.jaxrs.config.BeanConfig;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class MastercardApplication extends Application {

    public MastercardApplication() {
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.2");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/api/v1");
        beanConfig.setPrettyPrint(true);
        beanConfig.setResourcePackage("com.mishas.stuff.ms.web.controller");
        //beanConfig.setScan(true);
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet();
        resources.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        resources.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);
        return resources;
    }

    @Override
    public Set<Object> getSingletons() {
        // create DAO layer
        TransactionDao transactionDao = new TransactionDao();
        // create service layer
        RecordKeepingService  recordKeepingService= new RecordKeepingService(transactionDao);
        // add resources
        final Set<Object> singletons = new HashSet<>();

        singletons.add(new CardController(recordKeepingService));
        singletons.add(new DatabaseExcpetionMapper());
        return singletons;
    }

}
