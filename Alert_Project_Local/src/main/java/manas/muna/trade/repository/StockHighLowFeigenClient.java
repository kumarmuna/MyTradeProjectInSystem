package manas.muna.trade.repository;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public class StockHighLowFeigenClient {

    static ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).registerModule(new JavaTimeModule());

    public static Map<String, Map<String, Double>> getHighLowData(String stockName) {
        HttpRequest request = getRequest("GET", "", "/getSupportResistance/"+stockName);
        HttpResponse<String> res = getResponse(request);
        Map<String, Map<String,Double>> data = null;
        try{
            data = mapper.readValue(res.body(),Map.class);
        }catch (Exception e){
            System.out.println("Error during conversion");
        }
        return data;
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

    private static HttpRequest getRequest(String methodType, String requestBody, String path) {
        HttpRequest request = null;
        if (methodType.equals("POST")) {
            request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .uri(URI.create("http://localhost:8080/stockhighlow" + path))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .build();
        }else if (methodType.equals("GET")){
            request = HttpRequest.newBuilder().GET().uri(URI.create("http://localhost:8080/stockhighlow" + path))
                    .header("Content-Type", "application/json")
                    .build();
        }
        return request;
    }
    public static void main(String[] args) {
        System.out.println("hih");
        getHighLowData("DCM.NS");
    }
}
