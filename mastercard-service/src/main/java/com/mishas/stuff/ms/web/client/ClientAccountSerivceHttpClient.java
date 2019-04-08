package com.mishas.stuff.ms.web.client;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ClientAccountSerivceHttpClient {

    private static Logger logger = Logger.getLogger(ClientAccountSerivceHttpClient.class);
    private final URIBuilder builder = new URIBuilder().setScheme("http").setHost("client-account-service:8080");

    /**
     * Create a new resource or update an existing resource
     *
     * @param path
     * @param resource
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public String updateResource(String path, StringEntity resource) throws IOException, URISyntaxException {

        URI uri = builder.setPath(path).build();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPut requestPut = new HttpPut(uri);
            // prepare the payload
            requestPut.addHeader("Content-Type", MediaType.APPLICATION_JSON);
            requestPut.setHeader("Accept", MediaType.APPLICATION_JSON);
            // set payload
            requestPut.setEntity(resource);
            // execute the request
            CloseableHttpResponse response = httpClient.execute(requestPut);

            try {
                return handleResponse(response);
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
    }

    private String handleResponse(CloseableHttpResponse response) throws IOException {
        // get the status code and response body if any
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        String stringEntity = entity != null ? EntityUtils.toString(entity) : null;
        if (statusCode >= 200 && statusCode < 300) {
            return stringEntity;
        } else {
            logger.error("Request: failed. Status Code: " + statusCode + " message: " + stringEntity);
            throw new ClientProtocolException("Unexpected response status: " + statusCode + " message: " + stringEntity);

        }
    }
}