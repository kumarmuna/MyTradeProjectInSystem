package manas.muna.bestautotrade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.internal.StringUtil;
import manas.muna.bestautotrade.config.InitializeStockName;
import manas.muna.bestautotrade.model.StockHighLowData;
import manas.muna.bestautotrade.model.StockHighLowVo;
import manas.muna.bestautotrade.model.SupportAndResistance;
import manas.muna.bestautotrade.service.StockdataService;
import manas.muna.bestautotrade.util.ApiCallToGetHistData;
import manas.muna.bestautotrade.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stockhighlow")
public class StockHighLowController {

    @Autowired
    StockdataService service;
    @Autowired
    InitializeStockName initializeStockName;

    @PostMapping("/update_month_data")
    public String updateHighLowData() {
        System.out.println(initializeStockName.getAllAZStockNames());
        return "";
    }

    @PostMapping("/update_month_data/{stockname}")
    public String updateHighLowDataForStock() {
        return "";
    }

    @PostMapping("/update_year_data")
    public String updateYearData(@RequestParam(required = false) Integer year) throws InterruptedException {
        if (year==null || year==0)
            year = 2024;
        List<String> names = initializeStockName.getAllAZStockNames();
        for (String name:names){
//            List<StockHighLowVo> rs = getStockHighLowData(name, year);
            List<StockHighLowVo> rs = new ArrayList<>();
            if (true || rs.size()==0 || StringUtil.isNullOrEmpty(rs.get(0).getAug_mon())) {
                System.out.println(name);
                Thread.sleep(10000);
                List<StockHighLowData> results = ApiCallToGetHistData.getHistData(name, "2y", "1mo");
                if (results.size()!=0) {
                    StockHighLowVo data = Converter.preapreApiReqDataForYear(results, name, year);
                    service.storeHighLowData(data);
                }
            }
        }
        return "stored data";
    }

    @PostMapping("/deleted_year_data")
    public String deleteUnListedStocks() {
        List<StockHighLowVo> result =  service.getAllStockHighLowData();
        List<String> names = result.stream().map(a->a.getStockName()).sorted().collect(Collectors.toList());
        System.out.println(names);
        for (StockHighLowVo vo: result){
            if (StringUtil.isNullOrEmpty(vo.getNov_mon())){
                service.deleteByKey(vo.getStockName(), vo.getYearNumber());
                System.out.println("Deleted ...."+vo.getStockName());
            }
        }
        return "deleted....";
    }

    @PostMapping("/update_year_data/{stockname}")
    public String updateYearDataForStock() {
        return "";
    }

    @GetMapping("/getData/{stockname}/{year}")
    public List<StockHighLowVo> getStockHighLowData(@PathVariable String stockname, @PathVariable int year) {
        List<StockHighLowVo> list = new ArrayList<>();
        try{
            list = service.getHighLowStock(stockname,year);
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    @GetMapping("/getSupportResistance/{stockname}")
    public Map<String, Map<String,Double>> getStock2HighLowData(@PathVariable String stockname) {
        List<StockHighLowVo> list = new ArrayList<>();
        List<StockHighLowVo> listPrevYr = new ArrayList<>();
        List<SupportAndResistance> supportAndResistance = new ArrayList<>();
        List<SupportAndResistance> supportAndResistance3Months = new ArrayList<>();
        List<SupportAndResistance> supportAndResistance6Months = new ArrayList<>();
        List<SupportAndResistance> supportAndResistance9Months = new ArrayList<>();

        try{
            LocalDate todayDate = LocalDate.now();
            int year = todayDate.getYear();
            list = service.getHighLowStock(stockname,year);
            if (list.size()==0 || list.get(0).getDec_mon().isEmpty())
                listPrevYr = service.getHighLowStock(stockname, year-1);
            StockHighLowVo data = list.size()==0? listPrevYr.get(0) : list.get(0);
            StockHighLowVo prevYrdata = listPrevYr.get(0);
            List<String > monthData = List.of(data.getJan_mon()==null?prevYrdata.getJan_mon():data.getJan_mon(),data.getFeb_mon()==null?prevYrdata.getFeb_mon():data.getFeb_mon(),data.getMar_mon()==null?prevYrdata.getMar_mon():data.getMar_mon(),
                    data.getApr_mon()==null?prevYrdata.getApr_mon():data.getApr_mon(),data.getMay_mon()==null?prevYrdata.getMay_mon():data.getMay_mon(),data.getJun_mon()==null?prevYrdata.getJun_mon():data.getJun_mon(),
                    data.getJul_mon()==null?prevYrdata.getJul_mon():data.getJul_mon(),data.getAug_mon()==null?prevYrdata.getAug_mon():data.getAug_mon(),data.getSep_mon()==null?prevYrdata.getSep_mon():data.getSep_mon(),
                    data.getOct_mon()==null?prevYrdata.getOct_mon():data.getOct_mon(),data.getNov_mon()==null?prevYrdata.getNov_mon():data.getNov_mon(),data.getDec_mon()==null?prevYrdata.getDec_mon():data.getDec_mon());
            for (String dt : monthData){
                if (!dt.isEmpty()) {
                    Map<String, Object> stockData = new ObjectMapper().readValue(dt, HashMap.class);
                    SupportAndResistance sr = SupportAndResistance.builder().high(Double.parseDouble(stockData.get("high").toString())).low(Double.parseDouble(stockData.get("low").toString()))
                        .open(Double.parseDouble(stockData.get("open").toString())).close(Double.parseDouble(stockData.get("close").toString())).volume(Long.parseLong(stockData.get("volume").toString()))
                        .date(stockData.get("date").toString()).build();
                    supportAndResistance.add(sr);
                    Date convertedDate = convertStrToDate(stockData.get("date").toString(),"yyyy-MM-dd");
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.setTime(convertedDate);
                    int startDateDayOfMonth = startCalendar.get(Calendar.DAY_OF_MONTH);
                    int startDateTotalMonths = 12 * startCalendar.get(Calendar.YEAR)+ startCalendar.get(Calendar.MONTH);
//                    int dataMonValue = convertedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue();
                    int curMonthVal = todayDate.getMonthValue();
                    Calendar endCalendar = Calendar.getInstance();
                    endCalendar.setTime(new Date());
                    int endDateDayOfMonth = endCalendar.get(Calendar.DAY_OF_MONTH);
                    int endDateTotalMonths = 12 * endCalendar.get(Calendar.YEAR)+ endCalendar.get(Calendar.MONTH);
//                    int monthValue = (curMonthVal - dataMonValue) < 0 ? -1*(curMonthVal - dataMonValue) : (curMonthVal - dataMonValue);
                    int monthValue = (startDateDayOfMonth > endDateDayOfMonth)? (endDateTotalMonths - startDateTotalMonths) - 1 : (endDateTotalMonths - startDateTotalMonths);
                    if (monthValue==0 || monthValue==1 || monthValue==2 || monthValue==3){
                        supportAndResistance3Months.add(sr);
                    }if (monthValue==0 || monthValue==1 || monthValue==2 || monthValue==3 || monthValue==4 || monthValue==5 || monthValue==6){
                        supportAndResistance6Months.add(sr);
                    }if (monthValue==0 || monthValue==1 || monthValue==2 || monthValue==3 || monthValue==4 || monthValue==5 || monthValue==6 || monthValue==7 || monthValue==8 || monthValue==9){
                        supportAndResistance9Months.add(sr);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        supportAndResistance.sort(Comparator.comparingDouble(SupportAndResistance::getHigh));
//        Collections.reverse(supportAndResistance);

//        supportAndResistance.sort(Comparator.comparingDouble(SupportAndResistance::getLow));
//        Map<String, List<Double>> suppRegData = new HashMap<>();
        Map<String, Map<String,Double>> suppRegData = new HashMap<>();
        suppRegData.put("lowData3Months", sortValue(supportAndResistance3Months.stream().sorted(Comparator.comparingDouble(SupportAndResistance::getLow)).limit(3).collect(Collectors.toMap(SupportAndResistance::getDate, SupportAndResistance::getLow))));
        suppRegData.put("lowData6Months", sortValue(supportAndResistance6Months.stream().sorted(Comparator.comparingDouble(SupportAndResistance::getLow)).limit(3).collect(Collectors.toMap(SupportAndResistance::getDate, SupportAndResistance::getLow))));
        suppRegData.put("lowData9Months", sortValue(supportAndResistance9Months.stream().sorted(Comparator.comparingDouble(SupportAndResistance::getLow)).limit(3).collect(Collectors.toMap(SupportAndResistance::getDate, SupportAndResistance::getLow))));
        suppRegData.put("lowData", sortValue(supportAndResistance.stream().sorted(Comparator.comparingDouble(SupportAndResistance::getLow)).collect(Collectors.toMap(SupportAndResistance::getDate, SupportAndResistance::getLow))));

//        suppRegData.put("lowOpenData3Months", sortValue(supportAndResistance3Months.stream().sorted(Comparator.comparingDouble(SupportAndResistance::getOpen)).limit(3).collect(Collectors.toMap(SupportAndResistance::getDate, SupportAndResistance::getOpen))));
//        suppRegData.put("lowOpenData6Months", sortValue(supportAndResistance6Months.stream().sorted(Comparator.comparingDouble(SupportAndResistance::getOpen)).limit(3).collect(Collectors.toMap(SupportAndResistance::getDate, SupportAndResistance::getOpen))));
//        suppRegData.put("lowOpenData9Months", sortValue(supportAndResistance9Months.stream().sorted(Comparator.comparingDouble(SupportAndResistance::getOpen)).limit(3).collect(Collectors.toMap(SupportAndResistance::getDate, SupportAndResistance::getOpen))));
//        suppRegData.put("lowOpenData", sortValue(supportAndResistance.stream().sorted(Comparator.comparingDouble(SupportAndResistance::getOpen)).collect(Collectors.toMap(SupportAndResistance::getDate, SupportAndResistance::getOpen))));

        suppRegData.put("highData3Months", sortValue(supportAndResistance3Months.stream().sorted(Comparator.comparingDouble(SupportAndResistance::getHigh).reversed()).limit(3).collect(Collectors.toMap(SupportAndResistance::getDate, SupportAndResistance::getHigh))));
        suppRegData.put("highData6Months", sortValue(supportAndResistance6Months.stream().sorted(Comparator.comparingDouble(SupportAndResistance::getHigh).reversed()).limit(3).collect(Collectors.toMap(SupportAndResistance::getDate, SupportAndResistance::getHigh))));
        suppRegData.put("highData9Months", sortValue(supportAndResistance9Months.stream().sorted(Comparator.comparingDouble(SupportAndResistance::getHigh).reversed()).limit(3).collect(Collectors.toMap(SupportAndResistance::getDate, SupportAndResistance::getHigh))));
        suppRegData.put("highData", sortValue(supportAndResistance.stream().sorted(Comparator.comparingDouble(SupportAndResistance::getHigh).reversed()).collect(Collectors.toMap(SupportAndResistance::getDate, SupportAndResistance::getHigh))));

//        suppRegData.put("highCloseData3Months", sortValue(supportAndResistance3Months.stream().sorted(Comparator.comparingDouble(SupportAndResistance::getClose).reversed()).limit(3).collect(Collectors.toMap(SupportAndResistance::getDate, SupportAndResistance::getClose))));
//        suppRegData.put("highCloseData6Months", sortValue(supportAndResistance6Months.stream().sorted(Comparator.comparingDouble(SupportAndResistance::getClose).reversed()).limit(3).collect(Collectors.toMap(SupportAndResistance::getDate, SupportAndResistance::getClose))));
//        suppRegData.put("highCloseData9Months", sortValue(supportAndResistance9Months.stream().sorted(Comparator.comparingDouble(SupportAndResistance::getClose).reversed()).limit(3).collect(Collectors.toMap(SupportAndResistance::getDate, SupportAndResistance::getClose))));
//        suppRegData.put("highCloseData", sortValue(supportAndResistance.stream().sorted(Comparator.comparingDouble(SupportAndResistance::getClose).reversed()).collect(Collectors.toMap(SupportAndResistance::getDate, SupportAndResistance::getClose))));

        return suppRegData;
    }

    private List<Double> sortValue(List<Double> values) {
        Collections.sort(values);
        return values;
    }

    private Map<String,Double> sortValue(Map<String,Double> values) {
        return values.entrySet().stream().
                sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e2, e1) -> e2, LinkedHashMap::new));
    }

    public static Date convertStrToDate(String date, String format){
        Date dt = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            dt = dateFormat.parse(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return dt;
    }
}
