package com.dishan.aof.constants;

import java.util.HashMap;

import com.dishan.aof.utils.PinYinUtils;

/**
 * @Created by DAIE
 * @Date 2021/3/8 20:26
 * @Description leek面板TABLE工具类
 */
public class TableConstants {
    //基金表头
    public static final String FUND_TABLE_HEADER_KEY = "fund_table_header_key5"; //移动表头时存储的key
    public static final String FUND_TABLE_HEADER_VALUE = "编码,基金名称,估算涨跌,当日净值,估算净值,上区间,下区间,上次买,上次卖,建仓,上梯度,下梯度,更新时间,持仓成本价,持有份额,收益率,收益";
    //股票表头
    public static final String STOCK_TABLE_HEADER_KEY = "stock_table_header_key5"; //移动表头时存储的key
    public static final String STOCK_TABLE_HEADER_VALUE = "编码,股票名称,涨跌,涨跌幅,最高价,最低价,当前价,上区间,下区间,上次买,上次卖,建仓,上梯度,下梯度,更新时间,成本价,持仓,收益率,收益";
    //货币表头
    public static final String COIN_TABLE_HEADER_KEY = "coin_table_header_key5"; //移动表头时存储的key
    public static final String COIN_TABLE_HEADER_VALUE = "编码,当前价,涨跌,涨跌幅,最高价,最低价,更新时间";
    //网格策略
    public static final String GRID_TABLE_HEADER_KEY = "GRID_TABLE_HEADER_KEY5"; //移动表头时存储的key
    public static final String GRID_TABLE_HEADER_VALUE = "序号,估值,手,买金额,买股数,卖股数,卖估值,卖金额,收益";
    //操作记录
    public static final String OPT_TRACE_TABLE_HEADER_KEY = "OPT_TRACE_TABLE_HEADER_KEY5"; //移动表头时存储的key
    public static final String OPT_TRACE__TABLE_HEADER_VALUE = "日期,编码,操作,单价,数量,总额,费用,日期ref,操作ref,单价ref,数量ref,总额ref,费用ref,收益";


    private static HashMap<String, String> remapPinYinMap = new HashMap<>();

    static {
        remapPinYinMap.put(PinYinUtils.toPinYin("编码"), "编码");
        remapPinYinMap.put(PinYinUtils.toPinYin("基金名称"), "基金名称");
        remapPinYinMap.put(PinYinUtils.toPinYin("估算净值"), "估算净值");
        remapPinYinMap.put(PinYinUtils.toPinYin("估算涨跌"), "估算涨跌");
        remapPinYinMap.put(PinYinUtils.toPinYin("更新时间"), "更新时间");
        remapPinYinMap.put(PinYinUtils.toPinYin("当日净值"), "当日净值");
        remapPinYinMap.put(PinYinUtils.toPinYin("股票名称"), "股票名称");
        remapPinYinMap.put(PinYinUtils.toPinYin("当前价"), "当前价");
        remapPinYinMap.put(PinYinUtils.toPinYin("涨跌"), "涨跌");
        remapPinYinMap.put(PinYinUtils.toPinYin("涨跌幅"), "涨跌幅");
        remapPinYinMap.put(PinYinUtils.toPinYin("最高价"), "最高价");
        remapPinYinMap.put(PinYinUtils.toPinYin("最低价"), "最低价");
        remapPinYinMap.put(PinYinUtils.toPinYin("名称"), "名称");

        remapPinYinMap.put(PinYinUtils.toPinYin("成本价"), "成本价");
        remapPinYinMap.put(PinYinUtils.toPinYin("持仓"), "持仓");
        remapPinYinMap.put(PinYinUtils.toPinYin("收益率"), "收益率");
        remapPinYinMap.put(PinYinUtils.toPinYin("收益"), "收益");

        remapPinYinMap.put(PinYinUtils.toPinYin("持仓成本价"), "持仓成本价");
        remapPinYinMap.put(PinYinUtils.toPinYin("持有份额"), "持有份额");

        remapPinYinMap.put(PinYinUtils.toPinYin("序号"), "序号");
        remapPinYinMap.put(PinYinUtils.toPinYin("估值"), "估值");
        remapPinYinMap.put(PinYinUtils.toPinYin("手"), "手");
        remapPinYinMap.put(PinYinUtils.toPinYin("买金额"), "买金额");
        remapPinYinMap.put(PinYinUtils.toPinYin("买股数"), "买股数");
        remapPinYinMap.put(PinYinUtils.toPinYin("卖股数"), "卖股数");
        remapPinYinMap.put(PinYinUtils.toPinYin("卖估值"), "卖估值");
        remapPinYinMap.put(PinYinUtils.toPinYin("卖金额"), "卖金额");

        remapPinYinMap.put(PinYinUtils.toPinYin("上区间"), "上区间");
        remapPinYinMap.put(PinYinUtils.toPinYin("下区间"), "下区间");
        remapPinYinMap.put(PinYinUtils.toPinYin("建仓"), "建仓");
        remapPinYinMap.put(PinYinUtils.toPinYin("上梯度"), "上梯度");
        remapPinYinMap.put(PinYinUtils.toPinYin("下梯度"), "下梯度");
        remapPinYinMap.put(PinYinUtils.toPinYin("上次买"), "上次买");
        remapPinYinMap.put(PinYinUtils.toPinYin("上次卖"), "上次卖");

        // 日期,编码,操作,单价,数量,总额,费用,日期ref,操作ref,单价ref,数量ref,总额ref,费用ref,收益
        remapPinYinMap.put(PinYinUtils.toPinYin("日期"), "日期");
        remapPinYinMap.put(PinYinUtils.toPinYin("操作"), "操作");
        remapPinYinMap.put(PinYinUtils.toPinYin("单价"), "单价");
        remapPinYinMap.put(PinYinUtils.toPinYin("数量"), "数量");
        remapPinYinMap.put(PinYinUtils.toPinYin("总额"), "总额");
        remapPinYinMap.put(PinYinUtils.toPinYin("费用"), "费用");

        remapPinYinMap.put(PinYinUtils.toPinYin("日期ref"), "日期ref");
        remapPinYinMap.put(PinYinUtils.toPinYin("操作ref"), "操作ref");
        remapPinYinMap.put(PinYinUtils.toPinYin("单价ref"), "单价ref");
        remapPinYinMap.put(PinYinUtils.toPinYin("数量ref"), "数量ref");
        remapPinYinMap.put(PinYinUtils.toPinYin("总额ref"), "总额ref");
        remapPinYinMap.put(PinYinUtils.toPinYin("费用ref"), "费用ref");
    }


    /**
     * 通过列名 获取该TABLE的列的数组下标
     *
     * @param columnNames 列名数组
     * @param columnName  要获取的列名
     * @return 返回给出列名的数组下标 匹配失败返回-1
     */
    public static int getColumnIndexByName(String[] columnNames, String columnName) {
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equals(columnName)) {
                return i;
            }
        }
        //考虑拼音编码

        return -1;
    }

    public static String remapPinYin(String pinyin) {
        return remapPinYinMap.getOrDefault(pinyin, pinyin);
    }


}
