package manas.muna.trade.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CandleTypeNameEnum {

    NEUTRALDOJI("NeutralDoji"),
    LONGLEGGEDDOJI("LongLeggedDoji"),
    GRAVESTONEDOJI("GravestoneDoji"),
    DRAGONFLYDOJI("DragonflyDoji"),
    PRICEDOJI("PriceDoji"),
    ;

    String code;
    CandleTypeNameEnum(String code) {
        this.code = code;
    }

    public static List<String> getBothCandleTypeNames() {
        return Arrays.stream(CandleTypeNameEnum.values()).map(e->e.code).collect(Collectors.toList());
    }

    @Getter
    public enum BullishCandleEnum {
        BULLISHHARAMI("BullishHarami"),
        HAMMER("Hammer"),
        INVERTEDHAMMER("InvertedHammer"),
        BULLISHENGULFINGOCCURS("BullishEngulfingOccurs"),
        MORINGSTAR("Moringstar"),
        PIERCINGLINE("PiercingLine"),
        THREEWHITESOLDIERS("ThreeWhiteSoldiers"),
        TWEEZERBOTTOMS("TweezerBottoms"),
        DOJIS("Dojis"),
        BULISHRAILWAYTRACKS("BulishRailwayTracks"),
        MYFIRSTCANDLE("MyFirstCandle"),
        ;

        String code;
        BullishCandleEnum(String code) {
            this.code = code;
        }

        public static List<String> getBullishCandleNames() {
            return Arrays.stream(BullishCandleEnum.values()).map(e->e.getCode()).collect(Collectors.toList());
        }
    }

    @Getter
    public enum BearishCandleEnum {
        HARAMIBEARISH("HaramiBearish"),
        BEARISHABANDONEDBABY("BearishAbandonedBaby"),
        ENGULFINGBEARISH("EngulfingBearish"),
        DARKCLOUDCOVER("DarkCloudCover"),
        SHOOTINGSTAR("ShootingStar"),
        EVENINGSTAR("EveningStar"),
        BEARISHRAILWAYTRACKS("BearishRailwayTracks"),
        BEARISHREVERSAL("BearishReversal"),
        ;

        String code;
        BearishCandleEnum(String code) {
            this.code = code;
        }

        public static List<String> getBearishCandleNames() {
            return Arrays.stream(BearishCandleEnum.values()).map(e->e.getCode()).collect(Collectors.toList());
        }
    }
}
