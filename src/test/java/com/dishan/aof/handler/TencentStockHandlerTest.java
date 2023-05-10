package com.dishan.aof.handler;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.dishan.aof.bean.StockBean;
import com.dishan.aof.utils.JacksonUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class TencentStockHandlerTest {
    @Test
    public void parse(){
        String str ="v_sh000001=\"1~上证指数~000001~3357.67~3395.00~3402.39~573013309~286506655~286506655~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~0.00~0~~20230509155902~-37.33~-1.10~3418.95~3356.12~3357.67/573013309/619108477501~573013309~61910848~1.32~13.84~~3418.95~3356.12~1.85~442821.63~593972.90~0.00~-1~-1~1.30~0~3392.82~~~~~~61910847.7501~0.0000~0~ ~ZS~8.69~2.18~~~~3424.84~2885.09~-0.28~1.36~3.88~4242324771183~~-30.33~3.73~4242324771183~~~11.77~0.02~~CNY";
        String str2="v_hk03032=\"100~恒生科技ETF~03032~3.762~3.884~3.884~4898600.0~0~0~3.762~0~0~0~0~0~0~0~0~0~3.762~0~0~0~0~0~0~0~0~0~4898600.0~2023/05/09 16:08:24~-0.122~-3.14~3.884~3.760~3.762~4898600.0~18629136.000~0~0.00~~0~0~3.19~24.2581~24.2581~HSTECH ETF~0.00~5.105~2.708~1.47~86.53~0~0~0~0~0~0.00~0.00~0.76~200~-8.29~-2.79~GP-FUND~~~-4.03~-10.47~-14.58~644818865.67~644818865.67~0.00~0.000~3.803~-12.96~HKD~1\";";

        StockBean stockBean = parseLine(str);
        System.out.println(JacksonUtils.toJsonString(stockBean));

         stockBean = parseLine(str2);
        System.out.println(JacksonUtils.toJsonString(stockBean));
    }

    protected StockBean parseLine(String line) {
        String code = line.substring(line.indexOf("_") + 1, line.indexOf("="));
        String dataStr = line.substring(line.indexOf("=") + 2, line.length() - 2);
        String[] values = dataStr.split("~");
        StockBean bean = new StockBean(code, Maps.newHashMap());
        bean.setName(values[1]);
        bean.setNow(values[3]);
        bean.setChange(values[31]);
        bean.setChangePercent(values[32]);
        bean.setTime(values[30]);
        bean.setMax(values[33]);//33
        bean.setMin(values[34]);//34

        BigDecimal now = new BigDecimal(values[3]);
        String costPriceStr = bean.getCostPrise();
        if (StringUtils.isNotEmpty(costPriceStr)) {
            BigDecimal costPriceDec = new BigDecimal(costPriceStr);
            BigDecimal incomeDiff = now.add(costPriceDec.negate());
            if (costPriceDec.compareTo(BigDecimal.ZERO) <= 0) {
                bean.setIncomePercent("0");
            } else {
                BigDecimal incomePercentDec = incomeDiff.divide(costPriceDec, 5, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.TEN)
                        .multiply(BigDecimal.TEN)
                        .setScale(3, RoundingMode.HALF_UP);
                bean.setIncomePercent(incomePercentDec.toString());
            }

            String bondStr = bean.getBonds();
            if (StringUtils.isNotEmpty(bondStr)) {
                BigDecimal bondDec = new BigDecimal(bondStr);
                BigDecimal incomeDec = incomeDiff.multiply(bondDec)
                        .setScale(2, RoundingMode.HALF_UP);
                bean.setIncome(incomeDec.toString());
            }
        }
        return bean;
    }
}