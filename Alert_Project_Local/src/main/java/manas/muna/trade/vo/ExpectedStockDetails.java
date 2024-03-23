package manas.muna.trade.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class ExpectedStockDetails {
    String name;
    double highDiff;
    double lowDiff;
    String candleType;
    String mrkDirection;
    String todayMove;
    String selectCategory;
    int volumePos;

    public static ExpectedStockDetails prepareExpectedStockDetails(String[] data) {
        return ExpectedStockDetails.builder()
                .name(data[0].split("StockName=")[1].trim())
                .highDiff(Double.parseDouble(data[1].split("HighDiff=")[1].trim()))
                .lowDiff(Double.parseDouble(data[2].split("LowDiff=")[1].trim()))
                .candleType(data[3].split("candleType=").length>1?data[3].split("candleType=")[1].trim():"")
                .mrkDirection(data[4].split("mrkDirection=")[1].trim())
                .todayMove(data[5].split("isRedGreen=")[1].trim())
                .selectCategory(data[6].split("slctCategry=")[1].trim())
                .volumePos(Integer.parseInt(data[7].split("volumePos=")[1].trim()))
                .build();
    }
}
