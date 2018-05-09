package com.timer;

import javax.servlet.ServletContextEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

/**
 * Created by admin on 2018/5/6.
 */
public class CrawlerTimer {
    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;

    public CrawlerTimer(ServletContextEvent servletContextEvent){
        Calendar calendar = Calendar.getInstance();
        //定制每日12:00执行
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 56);
        calendar.set(Calendar.SECOND, 10);

        //第一次执行任务的时间
        Date date = calendar.getTime();
        //如果第一次执行时间小于当前时间，此时要在第一次执行定时任务的时间上加一天，以便此任务在下一个时间点执行。
        if(date.before(new Date())){
            date = this.addDay(date, 1);
        }

        Timer timer = new Timer();

        CrawlerTimerTask task = new CrawlerTimerTask(servletContextEvent);

        //每天固定时间执行任务
        timer.schedule(task, date, PERIOD_DAY);
    }

    public Date addDay(Date date, int num){
        Calendar startC = Calendar.getInstance();
        startC.setTime(date);
        startC.add(Calendar.DAY_OF_MONTH, num);
        return startC.getTime();
    }
}