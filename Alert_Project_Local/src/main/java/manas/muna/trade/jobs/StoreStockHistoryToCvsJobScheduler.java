package manas.muna.trade.jobs;

import java.util.TimerTask;

public class StoreStockHistoryToCvsJobScheduler extends TimerTask {

    @Override
    public void run(){
        try {
            StoreStockHistoryToCvsJob.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
