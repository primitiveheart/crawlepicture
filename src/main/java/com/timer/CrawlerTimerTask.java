package com.timer;

import com.algorithm.BaiduPictureCrawler;
import com.algorithm.BaiduSearchResultCrawler;
import com.algorithm.WebCrawler;
import com.entity.Keyword;
import com.entity.Url;
import com.mapper.KeywordMapper;
import com.mapper.UrlMapper;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by admin on 2018/5/6.
 */
public class CrawlerTimerTask extends TimerTask {

    private ServletContextEvent servletContextEvent;

    public CrawlerTimerTask(ServletContextEvent servletContextEvent){
        this.servletContextEvent = servletContextEvent;
    }

    @Override
    public void run() {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        //该方法中需要开辟多个线程
        //核心内容
        final WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContextEvent.getServletContext());
        KeywordMapper keywordMapper = webApplicationContext.getBean(KeywordMapper.class);
        final List<Keyword> keywords = keywordMapper.getKeywordByIsNew("1");
        //第一部分:百度搜索和百度搜索
        //第一步:百度图片
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                BaiduPictureCrawler baiduPictureCrawler = webApplicationContext.getBean(BaiduPictureCrawler.class);
                for(int i=0; i < keywords.size(); i++){
                    //爬取百度图片数据
                    baiduPictureCrawler.insertBaiduPicResult(keywords.get(i).getKeyword(), keywords.get(i).getId());
                }
            }
        });

        //第二步: 百度搜索
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                BaiduSearchResultCrawler baiduSearchResultCrawler = webApplicationContext.getBean(BaiduSearchResultCrawler.class);
                //开始爬取数据
                for(int i=0; i < keywords.size(); i++){
                    //爬取百度搜索数据
                    baiduSearchResultCrawler.insertBaiduSearchResult(keywords.get(i).getKeyword(), keywords.get(i).getId());
                }
            }
        });
        //第二部分:对某个网站进行搜索
       executorService.execute(new Runnable() {
           @Override
           public void run() {
               UrlMapper urlMapper = webApplicationContext.getBean(UrlMapper.class);
               List<Url> urls = urlMapper.getUrlByIsNew("1");

               WebCrawler webCrawler = webApplicationContext.getBean(WebCrawler.class);
               for(int i=0 ;i < urls.size(); i++){
                   webCrawler.insertWebSearchResult(urls.get(i).getUrlSite(), urls.get(i).getKeyword(), urls.get(i).getId());
               }
           }
       });

        //关闭线程
        executorService.shutdown();
        //TODO
        //第三部分，需要对新爬虫完的关键字，在url和keyword表进行更新isNew状态为0
    }
}
