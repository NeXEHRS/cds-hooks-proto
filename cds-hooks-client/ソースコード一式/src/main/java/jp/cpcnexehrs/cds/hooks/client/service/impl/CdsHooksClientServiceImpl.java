package jp.cpcnexehrs.cds.hooks.client.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import jp.cpcnexehrs.cds.hooks.client.service.CdsHooksClientService;

@Service
public class CdsHooksClientServiceImpl implements CdsHooksClientService {

    private static final Logger logger = LoggerFactory.getLogger(CdsHooksClientServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    public String sendDiscoveryRequest(String url) {
        logger.debug("Start");
        String responseBody = null;
        try {
            responseBody = restTemplate.getForObject(url, String.class);
        } catch (HttpClientErrorException e) {
            logger.error("", e);
            responseBody = e.getResponseBodyAsString();
        } catch (HttpServerErrorException e) {
            logger.error("", e);
            responseBody = e.getResponseBodyAsString();
        }
        logger.debug("End");
        return responseBody;
    }

    public String sendHooksRequest(String url, String requestBody) {
        logger.debug("Start");
        String responseBody = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            responseBody = restTemplate.postForObject(url, entity, String.class);
        } catch (HttpClientErrorException e) {
            logger.error("", e);
            responseBody = e.getResponseBodyAsString();
        } catch (HttpServerErrorException e) {
            logger.error("", e);
            responseBody = e.getResponseBodyAsString();
        }
        logger.debug("End");
        return responseBody;
    }

}
