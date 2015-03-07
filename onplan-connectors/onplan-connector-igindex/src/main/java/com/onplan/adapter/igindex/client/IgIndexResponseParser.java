package com.onplan.adapter.igindex.client;

import com.google.common.collect.ImmutableList;
import com.onplan.domain.transitory.InstrumentInfo;
import com.onplan.domain.InstrumentType;
import com.onplan.util.http.HttpClientResponse;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.adapter.igindex.IgIndexMapper.getInstrumentIdByEpic;
import static com.onplan.adapter.igindex.IgIndexMapper.getInstrumentTypeByText;
import static com.onplan.adapter.igindex.client.IgIndexConstant.*;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class IgIndexResponseParser {
  public static IgIndexConnectionCredentials createConnectionCredentials(
      String apiKey, HttpClientResponse httpClientResponse) throws IOException {
    checkNotNullOrEmpty(apiKey);
    checkNotNull(httpClientResponse);
    String clientSessionToken =
        getHeaderValue(httpClientResponse, IgIndexConstant.HEADER_PARAMETER_CLIENT_SESSION_TOKEN);
    String accountSessionToken =
        getHeaderValue(httpClientResponse, IgIndexConstant.HEADER_PARAMETER_ACCOUNT_SESSION_TOKEN);
    String lightStreamerEndpoint =
        getBodyPropertyValue(httpClientResponse, IgIndexConstant.FIELD_LIGHT_STREAMER_ENDPOINT);
    IgIndexConnectionCredentials credentialsResponse = IgIndexConnectionCredentials.newBuilder()
        .setClientSessionToken(clientSessionToken)
        .setAccountSessionToken(accountSessionToken)
        .setLightStreamerEndpoint(lightStreamerEndpoint)
        .setApiKey(apiKey)
        .build();
    return credentialsResponse;
  }

  public static List<InstrumentInfo> createInstrumentInfoList(String json) throws IOException {
    checkNotNull(json);
    ImmutableList.Builder result = ImmutableList.builder();
    JsonNode node = (new ObjectMapper()).readTree(json).get(TAG_MARKETS);
    checkNotNull(node, String.format("Tag [%s] not found in JSON text.", TAG_MARKETS));
    for (int i = 0; i < node.size(); i++) {
      result.add(createInstrumentInfoForList(node.get(i)));
    }
    return result.build();
  }

  public static InstrumentInfo createInstrumentInfo(String json) throws IOException {
    checkNotNull(json);
    JsonNode jsonNode = checkNotNull((new ObjectMapper()).readTree(json));

    JsonNode instrumentNode = checkNotNull(jsonNode.get(TAG_INSTRUMENT));
    String instrumentId = getInstrumentIdByEpic(instrumentNode.get(TAG_EPIC).asText());
    String instrumentName = instrumentNode.get(TAG_NAME).asText();
    InstrumentType instrumentType = getInstrumentTypeByText(instrumentNode.get(TAG_TYPE).asText());
    String expiry = checkNotNullOrEmpty(instrumentNode.get(TAG_EXPIRY).asText());

    JsonNode snapshotNode = checkNotNull(jsonNode.get(TAG_SNAPSHOT));
    int scalingFactor = snapshotNode.get(TAG_SCALING_FACTOR).asInt();

    return new InstrumentInfo(instrumentId, instrumentName, instrumentType, scalingFactor, expiry);
  }

  private static InstrumentInfo createInstrumentInfoForList(JsonNode jsonNode) {
    checkNotNull(jsonNode);
    String instrumentId = getInstrumentIdByEpic(jsonNode.get(TAG_EPIC).asText());
    String instrumentName = jsonNode.get(TAG_INSTRUMENT_NAME).asText();
    InstrumentType instrumentType =
        getInstrumentTypeByText(jsonNode.get(TAG_INSTRUMENT_TYPE).asText());
    String expiry = checkNotNullOrEmpty(jsonNode.get(TAG_EXPIRY).asText());
    int scalingFactor = jsonNode.get(TAG_SCALING_FACTOR).asInt();
    return new InstrumentInfo(instrumentId, instrumentName, instrumentType, scalingFactor, expiry);
  }

  private static String getHeaderValue(HttpClientResponse httpClientResponse, String key) {
    checkNotNullOrEmpty(key);
    for (Map.Entry<String, String> header: httpClientResponse.getHeaders().entrySet()) {
      if (key.equals(header.getKey())) {
        return header.getValue();
      }
    }
    return null;
  }

  private static String getBodyPropertyValue(HttpClientResponse httpClientResponse, String key)
      throws IOException {
    checkNotNullOrEmpty(key);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode jsonNode = mapper.readTree(httpClientResponse.getBody());
    return jsonNode.findValue(key).asText();
  }
}
