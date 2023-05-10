package com.dishan.aof.handler;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import com.dishan.aof.bean.CoinBean;
import com.dishan.aof.bean.YahooResponse;
import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.dishan.aof.utils.HttpClientPool;
import com.dishan.aof.utils.LogUtil;

public class YahooCoinHandler extends CoinRefreshHandler {
    private final String URL = "https://query1.finance.yahoo.com/v7/finance/quote?&symbols=";
    private final String KEYS = "&fields=regularMarketChange,regularMarketChangePercent,regularMarketPrice,regularMarketTime,regularMarketDayHigh,regularMarketDayLow";
    private final JLabel refreshTimeLabel;

    private final Gson gson = new Gson();

    public YahooCoinHandler(JTable table, JLabel label) {
        super(table);
        this.refreshTimeLabel = label;
    }

    @Override
    public void handle(List<String> code) {
        if (code.isEmpty()) {
            return;
        }

        pollStock(code);
    }

    private void pollStock(List<String> code) {
        if (code.isEmpty()){
            return;
        }
        String params = Joiner.on(",").join(code);
        try {
            String res = HttpClientPool.getHttpClient().get(URL + params + KEYS);
//            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS"));
//            System.out.printf("%s,%s%n", time, res);
            handleResponse(res);
        } catch (Exception e) {
            LogUtil.info(e.getMessage());
        }
    }

    public void handleResponse(String response) {
//        System.out.println("解析虚拟币："+response);
        List<String> refreshTimeList = new ArrayList<>();
        try{
            YahooResponse yahooResponse = gson.fromJson(response, YahooResponse.class);
            for (CoinBean coinBean : yahooResponse.getQuoteResponse().getResult()) {
                updateData(coinBean);
                refreshTimeList.add(coinBean.getValueByColumn("更新时间",false));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        String text = refreshTimeList.stream().sorted().findFirst().orElse("");
        SwingUtilities.invokeLater(() -> refreshTimeLabel.setText(text));
    }

    @Override
    public void stopHandle() {
        LogUtil.info("leeks stock 自动刷新关闭!");
    }
}
