package manas.muna.trade.constants;

import java.util.Arrays;
import java.util.List;

public class CandleTypes {
    public static final String BULLISHHARAMI = "BullishHarami";
    public static final String HAMMER = "Hammer";
    public static final String INVERTEDHAMMER = "InvertedHammer";
    public static final String BULLISHENGULFINGOCCURS = "BullishEngulfingOccurs";
    public static final String MORINGSTAR = "Moringstar";
    public static final String PIERCINGLINE = "PiercingLine";
    public static final String THREEWHITESOLDIERS = "ThreeWhiteSoldiers";
    public static final String TWEEZERBOTTOMS = "TweezerBottoms";
    public static final String DOJIS = "Dojis";
    public static final String BULISHRAILWAYTRACKS = "BulishRailwayTracks";
    public static final String MYFIRSTCANDLE = "MyFirstCandle";

    public static final String HARAMIBEARISH = "HaramiBearish";
    public static final String BEARISHABANDONEDBABY = "BearishAbandonedBaby";
    public static final String ENGULFINGBEARISH = "EngulfingBearish";
    public static final String DARKCLOUDCOVER = "DarkCloudCover";
    public static final String SHOOTINGSTAR = "ShootingStar";
    public static final String EVENINGSTAR = "EveningStar";
    public static final String BEARISHRAILWAYTRACKS = "BearishRailwayTracks";
    public static final String BEARISHREVERSAL = "BearishReversal";

    public class DojiTypes {
        public static final String NEUTRALDOJI = "NeutralDoji";
        public static final String LONGLEGGEDDOJI = "LongLeggedDoji";
        public static final String GRAVESTONEDOJI = "GravestoneDoji";
        public static final String DRAGONFLYDOJI = "DragonflyDoji";
        public static final String PRICEDOJI = "PriceDoji";
    }

    public static List<String> getAllConstantNames() {
        return Arrays.asList(
                CandleTypes.BEARISHREVERSAL,
                CandleTypes.BEARISHABANDONEDBABY,
                CandleTypes.BEARISHRAILWAYTRACKS,
                CandleTypes.BULISHRAILWAYTRACKS,
                CandleTypes.BULLISHENGULFINGOCCURS,
                CandleTypes.BULLISHHARAMI,
                CandleTypes.DARKCLOUDCOVER,
                CandleTypes.ENGULFINGBEARISH,
                CandleTypes.EVENINGSTAR,
                CandleTypes.HAMMER,
                CandleTypes.HARAMIBEARISH,
                CandleTypes.INVERTEDHAMMER,
                CandleTypes.MORINGSTAR,
                CandleTypes.MYFIRSTCANDLE,
                CandleTypes.PIERCINGLINE,
                CandleTypes.SHOOTINGSTAR,
                CandleTypes.THREEWHITESOLDIERS,
                CandleTypes.TWEEZERBOTTOMS,
                CandleTypes.DOJIS,
                DojiTypes.DRAGONFLYDOJI,
                DojiTypes.GRAVESTONEDOJI,
                DojiTypes.LONGLEGGEDDOJI,
                DojiTypes.NEUTRALDOJI,
                DojiTypes.PRICEDOJI
        );
    }
}
