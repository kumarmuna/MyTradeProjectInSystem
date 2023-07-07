package manas.muna.trade.jobs;

import java.util.TimerTask;

public class StockEmaRedNotificationForBuyStockJobScheduler extends TimerTask {

    @Override
    public void run() {
        StockEmaRedNotificationForBuyStockJob.execute();
    }
}
