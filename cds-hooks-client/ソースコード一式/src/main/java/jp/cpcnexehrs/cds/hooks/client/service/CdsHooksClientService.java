package jp.cpcnexehrs.cds.hooks.client.service;

public interface CdsHooksClientService {

    String sendDiscoveryRequest(String url);

    String sendHooksRequest(String url, String requestBody);

}
