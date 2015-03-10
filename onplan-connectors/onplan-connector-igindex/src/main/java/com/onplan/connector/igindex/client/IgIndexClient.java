package com.onplan.connector.igindex.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.onplan.domain.transitory.InstrumentInfo;
import com.onplan.util.http.HttpClientResponse;
import com.onplan.util.http.HttpMethod;
import com.onplan.util.http.HttpStatus;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.onplan.connector.igindex.client.IgIndexConstant.API_VERSION_2;
import static com.onplan.connector.igindex.client.IgIndexResponseParser.createConnectionCredentials;
import static com.onplan.connector.igindex.client.IgIndexResponseParser.createInstrumentInfo;
import static com.onplan.connector.igindex.client.IgIndexResponseParser.createInstrumentInfoList;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class IgIndexClient {
  private static final String FIELD_IDENTIFIER = "identifier";
  private static final String FIELD_PASSWORD = "password";

  private final String serverUrl;

  private volatile IgIndexConnectionCredentials connectionCredentials;

  public IgIndexClient(String serverUrl) {
    this.serverUrl = checkNotNullOrEmpty(serverUrl);
  }

  public IgIndexConnectionCredentials login(String apiKey, String username, String password)
      throws IOException {
    Map<String, String> header = ImmutableMap.of(
        IgIndexConstant.HEADER_PARAMETER_CONTENT_TYPE, IgIndexConstant.HEADER_PARAMETER_VALUE_JSON_UTF8,
        IgIndexConstant.HEADER_PARAMETER_ACCEPT, IgIndexConstant.HEADER_PARAMETER_VALUE_JSON_UTF8,
        IgIndexConstant.HEADER_PARAMETER_API_KEY, apiKey);
    String body = createCredentialsRequestBody(username, password);
    IgIndexClientRequest request = IgIndexClientRequest.newBuilder()
        .setCustomHeader(header)
        .setBody(body)
        .setHttpMethod(HttpMethod.POST)
        .setUrl(getRequestUrl(IgIndexConstant.TAG_SESSION))
        .build();
    HttpClientResponse response = request.executeRequest();
    connectionCredentials = createConnectionCredentials(apiKey, checkResponseCode(response));
    return connectionCredentials;
  }

  public List<InstrumentInfo> findInstrumentsBySearchTerm(String searchTerm) throws IOException {
    checkNotNullOrEmpty(searchTerm);
    IgIndexClientRequest request = IgIndexClientRequest.newBuilder()
        .setConnectionCredentials(connectionCredentials)
        .setHttpMethod(HttpMethod.GET)
        .setUrlParameters(ImmutableMap.of("searchTerm", searchTerm))
        .setUrl(getRequestUrl(IgIndexConstant.TAG_MARKETS))
        .build();
    HttpClientResponse response = request.executeRequest();
    return createInstrumentInfoList(checkResponseCode(response).getBody());
  }

  public InstrumentInfo getInstrumentInfo(String epic) throws IOException {
    checkNotNullOrEmpty(epic);
    IgIndexClientRequest request = IgIndexClientRequest.newBuilder()
        .setConnectionCredentials(connectionCredentials)
        .setHttpMethod(HttpMethod.GET)
        .setUrl(getRequestUrl(IgIndexConstant.TAG_MARKETS, epic))
        .setApiVersion(API_VERSION_2)
        .build();
    HttpClientResponse response = request.executeRequest();
    return createInstrumentInfo(checkResponseCode(response).getBody());
  }

  private String createCredentialsRequestBody(String username, String password) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
    Map<String, String> requestData = Maps.newHashMap();
    requestData.put(FIELD_IDENTIFIER, checkNotNullOrEmpty(username));
    requestData.put(FIELD_PASSWORD, checkNotNullOrEmpty(password));
    return mapper.writeValueAsString(requestData);
  }

  private String getRequestUrl(String urlSuffix) {
    return new StringBuilder()
        .append(serverUrl)
        .append("/")
        .append(urlSuffix)
        .toString();
  }

  private String getRequestUrl(String urlSuffix, String elementId) {
    return new StringBuilder()
        .append(serverUrl)
        .append("/")
        .append(urlSuffix)
        .append("/")
        .append(elementId)
        .toString();
  }

  private static HttpClientResponse checkResponseCode(HttpClientResponse httpClientResponse) {
    checkArgument(HttpStatus.OK.value() == httpClientResponse.getStatusCode(),
        String.format(
            "Expected response code [%d] but was [%d].",
            HttpStatus.OK.value(),
            httpClientResponse.getStatusCode()));
    return httpClientResponse;
  }
}
