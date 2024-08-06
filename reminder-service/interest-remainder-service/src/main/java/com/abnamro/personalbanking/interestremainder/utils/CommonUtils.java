package com.abnamro.personalbanking.interestremainder.utils;

import com.abnamro.personalbanking.basedomains.domain.CustomerRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommonUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);


    /**
     * This method converts Json to POJO Response from exchange server.
     * @param response of type Object
     * @return List
     * @throws JsonProcessingException
     */
    public static List<CustomerRequest> convertResponse(Object response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String responseJson = objectMapper.writeValueAsString(response);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(responseJson, new TypeReference<List<CustomerRequest>>(){});
    }
}
