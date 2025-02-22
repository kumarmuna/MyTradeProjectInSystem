package manas.muna.trade.api.repository;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "high-low", url ="http://localhost:8080" ,path = "/stockhighlow/getSupportResistance/DCM.NS")
public class HighLowClient {


}
