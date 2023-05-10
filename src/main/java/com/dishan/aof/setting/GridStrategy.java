package com.dishan.aof.setting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import com.dishan.aof.bean.GridStrategyBean;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GridStrategy {
    public static final String PATTERN = "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s";
    /**
     * 历史最高 市值
     */
    double historyMax;
    /**
     * 历史最低 市值
     */
    double historyMin;
    /**
     * 建仓 市值
     */
    double openUnitValue;
    /**
     * 建仓值向上 止盈 step；5% =0.05
     */
    float upStep;
    /**
     * 建仓值向下 补仓 step；3% =0.03
     */
    float downStep;
    /**
     * 一手多少钱（注意不是一手多少股）
     * 因为我们建策略的时候，更关心需要花多少钱，压测时，钱的极限
     */
    int oneHandAmt;
    /**
     * 建仓时 一次买入多少手
     */
    int openHands;
    /**
     * 向下建仓，可能有增加系数，1表示 保持不变，1.1 表示 每个step相对前一个增加1.1倍
     */
    float downStepHandsRadio;
    /**
     * 购买多少手，小数位
     */
    int handsScale;
    /**
     * 价格，小数位
     */
    int unitValueScale;
    /**
     * 利润小数位
     */
    int profitScale;
    /**
     * 总金额小数位
     */
    int amtScale;


    public void check() {
    }

    public List<GridStrategyBean> generate() {
        int buyAmt = openHands * oneHandAmt;
        int buyNum = (int) (buyAmt / openUnitValue);
        BigDecimal unitValue = new BigDecimal(openUnitValue + "").setScale(unitValueScale, RoundingMode.HALF_UP);
        GridStrategyBean openRow = GridStrategyBean.builder()
                .index(0)
                .unitValue(unitValue)
                .hands(new BigDecimal(openHands))
                .buyNum(buyNum)
                .buyAmt(buyAmt)
                .build();

        // 向上 部分
        TreeMap<Integer, GridStrategyBean> upMap = Maps.newTreeMap((Comparator<Integer>) Comparator.reverseOrder());
        int index = 0;
        unitValue = new BigDecimal(openUnitValue + "").setScale(unitValueScale, RoundingMode.HALF_UP);
        while (true) {
            index++;
            unitValue = unitValue.multiply(new BigDecimal(1 + upStep)).setScale(unitValueScale, RoundingMode.HALF_UP);
            if (index <= openHands) {
                // 能够卖出
                int sellNum = (int) (oneHandAmt / openUnitValue);
                GridStrategyBean gridRow = GridStrategyBean.builder()
                        .index(index)
                        .unitValue(unitValue)
                        .hands(new BigDecimal(1.0))
                        .sellUnitValue(unitValue)
                        .sellNum(sellNum)
                        .sellAmt(unitValue.multiply(new BigDecimal(sellNum)).intValue())
                        // .profit((sellNum * (unitValue - openUnitValue)))
                        .profit((new BigDecimal(sellNum).multiply(new BigDecimal(unitValue.doubleValue() - openUnitValue)).setScale(profitScale, RoundingMode.HALF_UP)))
                        .build();
                upMap.put(index, gridRow);
            } else {
                // 不能够卖出
                GridStrategyBean gridRow = GridStrategyBean.builder()
                        .index(index)
                        .unitValue(unitValue)
                        .build();
                upMap.put(index, gridRow);
            }
            if (unitValue.doubleValue() >= historyMax) {
                // 超过 = 历史峰值，截断
                break;
            }
        }

        // 向下部分
        TreeMap<Integer, GridStrategyBean> downMap = Maps.newTreeMap();
        index = 0;
        unitValue = new BigDecimal(openUnitValue + "");
        BigDecimal hands = new BigDecimal(1 + "").setScale(handsScale);
        while (true) {
            index++;
            // 上一个网格单价
            BigDecimal upStepUnitValue = unitValue;
            unitValue = unitValue.multiply(new BigDecimal(1 - downStep)).setScale(unitValueScale, RoundingMode.HALF_UP);
            hands = hands.multiply(new BigDecimal(1 + downStepHandsRadio)).setScale(handsScale, RoundingMode.HALF_UP);
            buyAmt = hands.multiply(new BigDecimal(oneHandAmt)).intValue();
            buyNum = new BigDecimal(buyAmt).divide(unitValue, BigDecimal.ROUND_HALF_UP).intValue();
            GridStrategyBean gridRow = GridStrategyBean.builder()
                    .index(index)
                    .unitValue(unitValue)
                    .hands(hands)
                    .buyNum(buyNum)
                    .buyAmt(buyAmt)
                    .sellUnitValue(upStepUnitValue)
                    .sellNum(buyNum)
                    .sellAmt(upStepUnitValue.multiply(new BigDecimal(buyNum)).setScale(amtScale, RoundingMode.HALF_UP).intValue())
                    .profit(new BigDecimal((upStepUnitValue.doubleValue() - unitValue.doubleValue()) * buyNum).setScale(profitScale, RoundingMode.HALF_UP))
                    .build();
            downMap.put(index, gridRow);

            if (unitValue.doubleValue() <= historyMin) {
                // 低于 = 历史最小值，截断
                break;
            }
        }

        List<GridStrategyBean> ret = Lists.newArrayList();
        // 2	0.437	1.21		4840	11076	11076	0.451	4840	155.1
        // System.out.println(String.format(PATTERN, "序号", "估值", "手", "买金额", "买股数", "卖股数", "卖估值", "卖金额", "利润"));

        // 输出
        upMap.forEach((k, v) -> {
            // System.out.println(v.toString());
            ret.add(v);
        });
        ret.add(openRow);
        // System.out.println(openRow.toString());
        downMap.forEach((k, v) -> {
            // System.out.println(v.toString());
            ret.add(v);
        });
        return ret;

    }


}
