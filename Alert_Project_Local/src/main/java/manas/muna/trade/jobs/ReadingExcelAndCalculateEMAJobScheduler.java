package manas.muna.trade.jobs;

import java.util.TimerTask;

public class ReadingExcelAndCalculateEMAJobScheduler extends TimerTask {

    @Override
    public void run() {
        ReadingExcelAndCalculateEMAJob.execute();
    }
}
