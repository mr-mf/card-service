package com.mishas.stuff.ms.web.controller;

import com.mishas.stuff.ms.service.IRecordKeepingService;
import com.mishas.stuff.ms.web.dto.IDto;
import com.mishas.stuff.ms.web.dto.ResponseDto;
import com.mishas.stuff.ms.web.dto.TransactionDto;
import com.mishas.stuff.ms.web.dto.TransactionStatusDto;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class CardController {

    private IRecordKeepingService recordKeepingService;

    public CardController(IRecordKeepingService recordKeepingService) {
        this.recordKeepingService = recordKeepingService;
    }

    @POST
    @Path("transaction")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response makePayment(TransactionDto transactionDto) {
        TransactionStatusDto transactionStatusDto = recordKeepingService.createTransaction(transactionDto);
        return Response.status(HttpStatus.CREATED_201).entity(
                new ResponseDto(HttpStatus.CREATED_201, "OK", Map.of("transactionStatus", transactionStatusDto))
        ).build();
    }

    @GET
    @Path("transaction/{id}")
    public Response getTransaction(@PathParam("id") final String id) {
        List<IDto> resList = recordKeepingService.getTransaction(id);
        return Response.status(HttpStatus.OK_200).entity(
                new ResponseDto(
                        HttpStatus.OK_200, "OK", Map.of("transactionStatus", resList.get(0), "transaction", resList.get(1)))
        ).build();
    }
}
