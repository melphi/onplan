package com.onplan.adapter.igindex.client;

import com.google.common.collect.ImmutableList;
import com.onplan.domain.InstrumentInfo;
import com.onplan.domain.InstrumentType;
import com.onplan.util.http.HttpClientResponse;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.adapter.igindex.client.IgIndexConstant.*;
import static com.onplan.adapter.igindex.IgIndexMapper.getInstrumentIdByEpic;
import static com.onplan.adapter.igindex.IgIndexMapper.getInstrumentTypeByText;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public class IgIndexResponseParser {
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
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode node = objectMapper.readTree(json).get(TAG_MARKETS);
    checkNotNull(node, String.format("Tag [%s] not found in JSON text.", TAG_MARKETS));
    for (int i = 0; i < node.size(); i++) {
      result.add(createInstrumentInfo(node.get(i)));
    }
    return result.build();
  }

  private static InstrumentInfo createInstrumentInfo(JsonNode node) {
    checkNotNull(node);
    String instrumentId = getInstrumentIdByEpic(node.get(TAG_EPIC).asText());
    String instrumentName = node.get(TAG_INSTRUMENT_NAME).asText();
    InstrumentType instrumentType = getInstrumentTypeByText(node.get(TAG_INSTRUMENT_TYPE).asText());
    String expiry = checkNotNullOrEmpty(node.get(TAG_EXPIRY).asText());
    int scalingFactor = node.get(TAG_SCALING_FACTOR).asInt();
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
