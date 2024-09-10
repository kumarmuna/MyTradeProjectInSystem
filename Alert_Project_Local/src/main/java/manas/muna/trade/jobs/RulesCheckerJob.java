package manas.muna.trade.jobs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import manas.muna.trade.constants.CandleTypes;
import manas.muna.trade.util.StockUtil;
import manas.muna.trade.vo.Rules;

import java.util.HashMap;
import java.util.Map;

public class RulesCheckerJob {
//    public static objectMapper = new ObjectMapper();
    public static void storeRuleToFile() throws JsonProcessingException {
        String jsonString = StockUtil.readFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\rule\\rule.txt");
        Map<String, Rules> rule = new ObjectMapper().readValue(jsonString, new TypeReference<>() {});

//        Map<String, Rules> rule = new HashMap<>();
        Rules r = Rules.builder()
                .name("LONGLEGGEDDOJI").checkPoint("If All time High then don't carry order")
                .mrkDirection("Not UP")
                .tradeCondition("Wait to cross low-1")
                .rsiCondition("")
                .build();
        rule.put(CandleTypes.DojiTypes.LONGLEGGEDDOJI, r);
        r = Rules.builder().name("MyFirstCandle").checkPoint("If All time High then don't carry order")
                .mrkDirection("Not UP")
                .tradeCondition("Entry at low, if open lower wait to came in ur entry point")
                .rsiCondition("")
                .build();
        rule.put("MyFirstCandle", r);
        r = Rules.builder().name("MySecondCandle").checkPoint("If All time High then don't carry order")
                .mrkDirection("Not UP")
                .tradeCondition("Entry at low, if open lower wait to came in ur entry point")
                .rsiCondition("")
                .build();
        rule.put("MySecondCandle",r);
        String jsonObject = new ObjectMapper().writeValueAsString(rule);
        System.out.println(jsonObject);
        StockUtil.storeFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\rule\\rule.txt", jsonObject);

        System.out.println();
    }

    public static void main(String[] args) throws JsonProcessingException {
        storeRuleToFile();
    }
}
