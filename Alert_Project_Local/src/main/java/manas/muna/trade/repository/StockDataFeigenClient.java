package manas.muna.trade.repository;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.lang.invoke.MethodType;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StockDataFeigenClient {
    static ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).registerModule(new JavaTimeModule());
    public static HttpResponse<String> getStockdataByName(String name) {
        HttpRequest request = getRequest("GET", "", "/name/"+name);
        return getResponse(request);
    }

    public static List<String> getStockNamesBydate(Map<String, String> dates){
        List<String> res = new ArrayList<>();
        try {
            Request requestbody = Request.builder().dates(dates).build();
            HttpRequest request = getRequest("POST", mapper.writeValueAsString(requestbody), "/getStocksByDates");
            HttpResponse response = getResponse(request);
            res = mapper.readValue(response.body().toString(), List.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }

    public static List<String> getStockNameAndPositionBydate(String date){
        List<String> res = new ArrayList<>();
        try {
            HttpRequest request = getRequest("GET", "", "/nameAndPosition/bydate/"+date);
            HttpResponse response = getResponse(request);
            res = mapper.readValue(response.body().toString(), List.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
    private static HttpRequest getRequest(String methodType, String requestBody, String path) {
        HttpRequest request = null;
        if (methodType.equals("POST")) {
            request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .uri(URI.create("http://localhost:8080/stockdata" + path))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();
        }else if (methodType.equals("GET")){
            request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/stockdata" + path))
                    .header("Content-Type", "application/json")
                    .build();
        }
        return request;
    }
    private static HttpResponse<String> getResponse(HttpRequest request) {
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }
}
