package manas.muna.trade.jobs;

import java.util.TimerTask;

public class StockEmaGreenStatusNotificationJobScheduler extends TimerTask {

    @Override
    public void run() {
        StockEmaTradeStartStatusNotificationJob.execute();
    }
}
