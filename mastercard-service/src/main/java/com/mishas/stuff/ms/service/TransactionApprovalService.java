package com.mishas.stuff.ms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishas.stuff.ms.utils.exceptions.HttpClientException;
import com.mishas.stuff.ms.web.client.ClientAccountSerivceHttpClient;
import com.mishas.stuff.ms.web.dto.IDto;
import com.mishas.stuff.ms.web.dto.TransactionDto;
import com.mishas.stuff.ms.web.dto.TransactionStatusDto;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;


public class TransactionApprovalService implements ITransactionApprovalService {

    private static final Logger logger = Logger.getLogger(TransactionApprovalService.class);

    private ClientAccountSerivceHttpClient clientAccountSerivceHttpClient;

    public TransactionApprovalService(ClientAccountSerivceHttpClient clientAccountSerivceHttpClient) {
        this.clientAccountSerivceHttpClient = clientAccountSerivceHttpClient;
    }

    @Override
    public TransactionStatusDto sendTransactionForApproval(TransactionDto transactionDto) {
        TransactionStatusDto transactionStatusDto = null;
        try {
            StringEntity transactionDtoResource = convertObjectToPayload(transactionDto);
            logger.info("Transaction Stirng Entity " + transactionDtoResource.toString());
            String transactionStatusStringResponse =  clientAccountSerivceHttpClient.updateResource("/api/v1/account", transactionDtoResource);
            transactionStatusDto = convertStringToTransactionStatusDto(transactionStatusStringResponse);
        } catch (IOException | URISyntaxException ioe) {
            logger.error("Could not confirm the transaction " + ioe.getMessage());
            throw new HttpClientException("Could not confirm the transaction " + transactionDto.getCorrelationId(), ioe);
        }
        return transactionStatusDto;
    }

    private StringEntity convertObjectToPayload(IDto resource) throws JsonProcessingException, UnsupportedEncodingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return new StringEntity(objectMapper.writeValueAsString(resource));
    }

    private TransactionStatusDto convertStringToTransactionStatusDto(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);
        return objectMapper.readValue(jsonNode.path("data").path("transactionStatus").toString(), TransactionStatusDto.class);
    }
}
