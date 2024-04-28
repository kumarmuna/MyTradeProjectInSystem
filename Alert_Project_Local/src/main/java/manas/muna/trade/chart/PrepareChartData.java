package manas.muna.trade.chart;

import manas.muna.trade.util.DateUtil;
import manas.muna.trade.util.StockUtil;

import java.util.*;
import java.util.stream.Collectors;

public class PrepareChartData {

    public static Map<Integer, List<String[]>> monDataSet = new HashMap<>();
    public static Map<Integer, List<String[]>> wkDataSet = new HashMap<>();

    public static void loadDataSet(String name) {
        List<String[]> hData = StockUtil.loadReportData(name);
        hData = hData.subList(1, hData.size()-1);
        for (String[] data:hData){
            Date dt = DateUtil.convertStrToDate(data[1], "yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dt);
            int month = calendar.get(Calendar.MONTH)+1;
            int wk = calendar.get(Calendar.WEEK_OF_MONTH);
            if (monDataSet.get(month) == null)
                monDataSet.put(month, new ArrayList<String[]>(){{add(data);}});
            else {
                List<String[]> val = monDataSet.get(month);
                val.add(data);
                monDataSet.put(month, val);
            }
            if (wkDataSet.get(wk) == null)
                wkDataSet.put(wk, new ArrayList<String[]>(){{add(data);}});
            else {
                List<String[]> val = wkDataSet.get(wk);
                val.add(data);
                wkDataSet.put(month, val);
            }
        }
    }

    public static Map<Integer, List<String[]>> getMonthDataSet(String name) {
        if (monDataSet.isEmpty())
            loadDataSet(name);
        return monDataSet;
    }

    public static Map<Integer, List<String[]>> getWeekDataSet(String name) {
        if (wkDataSet.isEmpty())
            loadDataSet(name);
        return wkDataSet;
    }

    public static void main(String[] args) {
        Map<Integer, List<String[]>> mData = getMonthDataSet("3IINFOLTD.NS");
        Map<Integer, List<String[]>> wData = getWeekDataSet("3IINFOLTD.NS");
        System.out.println("");
    }
}
