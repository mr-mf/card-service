package com.mishas.stuff.ms.web.exceptionmapper;

import com.mishas.stuff.ms.utils.exceptions.HttpClientException;
import com.mishas.stuff.ms.web.dto.ResponseDto;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class HttpClientExceptionsMapper implements ExceptionMapper<HttpClientException> {

    @Override
    public Response toResponse(HttpClientException e) {
        return Response
                .status(HttpStatus.INTERNAL_SERVER_ERROR_500)
                .entity(new ResponseDto(HttpStatus.INTERNAL_SERVER_ERROR_500, "INTERNAL SERVER ERROR", e.getMessage()))
                .build();
    }
}
