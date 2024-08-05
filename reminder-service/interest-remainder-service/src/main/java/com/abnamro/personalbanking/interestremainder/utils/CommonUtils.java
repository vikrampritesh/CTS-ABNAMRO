package com.abnamro.personalbanking.interestremainder.utils;

import com.abnamro.personalbanking.interestremainder.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

public class CommonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);

    /**
     * This method is used to invoke the rest api.
     * @param request Object
     * @param path String
     * @param restClient RestClient
     * @param headers HttpHeaders
     * @param httpMethod HttpMethod
     * @param isEmptyResponseBody boolean
     * @return Object
     * @throws BusinessException GenericException
     */
    public static <T> T invokeRestApi(Object request, String path, RestClient restClient,
                                      HttpHeaders headers, HttpMethod httpMethod, boolean isEmptyResponseBody,
                                      ParameterizedTypeReference<T> responseType) throws BusinessException {
        LOGGER.debug("Invoking Rest API :: " + path+ " Request :: " + writeToString(request));
        try {
            RestClient.ResponseSpec responseSpec = callUri(request, headers, httpMethod,
                    mapHttpMethod(restClient, httpMethod), UriComponentsBuilder.fromHttpUrl(path));
            if(isEmptyResponseBody) {
                return handleEmptyResponse(responseSpec, responseType);
            } else {
                return handleResponse(responseSpec, responseType);
            }
        }catch (HttpServerErrorException | HttpClientErrorException exception) {
            LOGGER.error("API Response :: " + exception.getStatusText());
            throw BusinessException.builder()
                    .status(HttpStatus.valueOf(exception.getStatusCode().value()))
                    .message(exception.getMessage())
                    .exception(exception).build();
        } catch (RestClientException exception) {
            LOGGER.error("RestClientError :: " + HttpStatus.SERVICE_UNAVAILABLE.name(), exception.getMessage());
            throw BusinessException.builder()
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .message(exception.getMessage())
                    .exception(exception).build();
        } catch (Exception exception) {
            LOGGER.error("Exception :: " + HttpStatus.INTERNAL_SERVER_ERROR.name(), exception.getMessage());
            throw BusinessException.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(exception.getMessage())
                    .exception(exception).build();
        }
    }

    /**
     * This method is used to write the object to string.
     * @param data Object
     * @return String
     * @throws BusinessException GenericException
     */
    public static String writeToString(Object data) throws BusinessException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(data);
        } catch (JsonProcessingException exception) {
            throw BusinessException.builder()
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .message(exception.getMessage())
                    .exception(exception).build();
        }
    }

    /**
     * This method is used to call the endpoint uri.
     * @param request Object
     * @param headers HttpHeaders
     * @param httpMethod HttpMethod
     * @param postReqBodyUriSpec RestClient.RequestBodyUriSpec
     * @param builder UriComponentsBuilder
     * @return ResponseSpec
     */
    public static RestClient.ResponseSpec
    callUri(Object request, HttpHeaders headers, HttpMethod httpMethod, RestClient.RequestBodyUriSpec postReqBodyUriSpec, UriComponentsBuilder builder) {
        RestClient.ResponseSpec responseSpec;
        if(HttpMethod.GET.equals(httpMethod) || HttpMethod.DELETE.equals(httpMethod)) {
            responseSpec = postReqBodyUriSpec.uri(builder.build().toUri())
                    .headers(h -> h.addAll(headers))
                    .body("")
                    .retrieve();
        } else {
            responseSpec = postReqBodyUriSpec.uri(builder.build().toUri())
                    .headers(h -> h.addAll(headers))
                    .body(request)
                    .retrieve();
        }
        return responseSpec;
    }

    /**
     * This method is used to map the http method to the rest client.
     * @param restClient RestClient
     * @param httpMethod HttpMethod
     * @return RestClient.RequestBodyUriSpec
     */
    private static RestClient.RequestBodyUriSpec mapHttpMethod(RestClient restClient, HttpMethod httpMethod) {
        RestClient.RequestBodyUriSpec postReqBodyUriSpec = null;

        if (httpMethod.equals(HttpMethod.GET)) {
            postReqBodyUriSpec = (RestClient.RequestBodyUriSpec) restClient.get();
        } else if (httpMethod.equals(HttpMethod.POST)) {
            postReqBodyUriSpec = restClient.post();
        } else if (httpMethod.equals(HttpMethod.PUT)) {
            postReqBodyUriSpec = restClient.put();
        } else if (httpMethod.equals(HttpMethod.DELETE)) {
            postReqBodyUriSpec = (RestClient.RequestBodyUriSpec) restClient.delete();
        }

        return postReqBodyUriSpec;
    }

    /**
     * This method is used to handle the empty response.
     * @param responseSpec RestClient.ResponseSpec
     * @return Object of T type
     */
    private static <T> T handleEmptyResponse(RestClient.ResponseSpec responseSpec,
                                             ParameterizedTypeReference<T> responseType) {
        if(null != responseSpec) {
            ResponseEntity<T> responseEntity = responseSpec.toEntity(responseType);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("API call is successful.");
                return responseEntity.getBody();
            } else {
                LOGGER.error("API_CALL_FAILED Http status: " + responseEntity.getStatusCode());
            }
        } else {
            LOGGER.error("API_CALL_FAILED Http status: 404 ");
        }
        return null;
    }

    /**
     * This method is used to handle the response.
     * @param responseSpec RestClient.ResponseSpec
     * @return Object
     */
    private static <T> T handleResponse(RestClient.ResponseSpec responseSpec,
                                        ParameterizedTypeReference<T> responseType) {
        if(null != responseSpec) {
            ResponseEntity<T> responseEntity = responseSpec.toEntity(responseType);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                LOGGER.info(" API call is successful.");
                return responseEntity.getBody();
            } else {
                LOGGER.info("API_CALL_FAILED Http status: " +
                        responseEntity.getStatusCode() + " Error: " + responseEntity.getBody());
            }
        } else {
            LOGGER.info("API_CALL_FAILED Http status: 404");
        }
        return null;
    }
}
