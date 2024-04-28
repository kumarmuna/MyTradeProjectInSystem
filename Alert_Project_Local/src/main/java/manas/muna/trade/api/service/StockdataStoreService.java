package manas.muna.trade.api.service;

import manas.muna.trade.api.model.Stockdata;
import manas.muna.trade.api.repository.StockClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("stockdataStoreService")
public class StockdataStoreService {

    @Autowired
    StockClient client;

    public String storeStockData(Stockdata stockdata) {
        String res = "";
        try{
            res = client.addStockData(stockdata);
        }catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}
