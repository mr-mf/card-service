package com.mishas.stuff.cas.web.controller;

import com.mishas.stuff.cas.service.AccountService;
import com.mishas.stuff.cas.service.IAccountService;
import com.mishas.stuff.cas.web.dto.AccountDto;
import com.mishas.stuff.cas.web.dto.ResponseDto;
import com.mishas.stuff.cas.web.dto.TransactionDto;
import com.mishas.stuff.cas.web.dto.TransactionStatusDto;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class AccountController {

    private IAccountService accountService;
    private static final Logger logger = Logger.getLogger(AccountController.class);

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @POST
    @Path("account")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createAccount(AccountDto accountDto) {
        Long id = accountService.createAccount(accountDto);
        return Response.status(HttpStatus.CREATED_201).build();
    }

    @GET
    @Path("account/{id}")
    public Response getAccount(@PathParam("id") @Size(max=1) final Long id) {
       AccountDto accountDto =  accountService.getAccount(id);
        return Response.status(HttpStatus.OK_200).entity(
                new ResponseDto(HttpStatus.OK_200, "OK", Map.of("account", accountDto))
        ).build();
    }

    @PUT
    @Path("account")
    public Response updateAccount(TransactionDto transactionDto) {
        logger.info("started transaction approval process: " + transactionDto.getCorrelationId());
        TransactionStatusDto transactionStatusDto = accountService.updateAccount(transactionDto);
        if (logger.isDebugEnabled()) {
            logger.debug("transaction status: " + transactionStatusDto.getTransactionStaus().toString());
        }
        return Response.status(HttpStatus.OK_200).entity(
                new ResponseDto(HttpStatus.OK_200, "OK", Map.of("transactionStatus", transactionStatusDto))
        ).build();
    }
}

