package com.mishas.stuff.cas.web.controller;

import com.mishas.stuff.cas.service.AccountService;
import com.mishas.stuff.cas.service.IAccountService;
import com.mishas.stuff.cas.web.dto.AccountDto;
import com.mishas.stuff.cas.web.dto.ResponseDto;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class AccountController {

    private IAccountService accountService;

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
    public Response getAccount(@PathParam("id") final Long id) {
       AccountDto accountDto =  accountService.getAccount(id);
        return Response.status(HttpStatus.OK_200).entity(
                new ResponseDto(HttpStatus.OK_200, "OK", Map.of("account", accountDto))
        ).build();
    }
}

