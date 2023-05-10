package com.dishan.aof.bean;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.dishan.aof.setting.GridStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GridStrategyBean {

    static NumberFormat unitValueNF = new DecimalFormat("0.###");
    static NumberFormat handsNF = new DecimalFormat("0.##");
    static NumberFormat profitNF = new DecimalFormat("0.#");
    static NumberFormat amtNF = new DecimalFormat("0");

    public String toString() {
        return String.format(GridStrategy.PATTERN,
                index,
                getUnitValue(),
                getHands(),
                getBuyAmt(),
                getBuyNum(),
                getSellNum(),
                getSellUnitValue(),
                getSellAmt(),
                getProfit()
        );
    }


    int index;
    /**
     * 估值
     */
    public BigDecimal unitValue;
    /**
     * 操作多少手
     */
    BigDecimal hands;
    /**
     * 买入金额
     */
    Integer buyAmt;
    /**
     * 买入数量
     */
    Integer buyNum;

    /**
     * 卖出单价
     */
    BigDecimal sellUnitValue;

    /**
     * 卖出数量
     */
    Integer sellNum;

    /**
     * 卖出金额
     */
    Integer sellAmt;

    /**
     * 利润
     */
    BigDecimal profit;

    public String getUnitValue() {
        return unitValue == null ? "" : unitValueNF.format(unitValue);
    }

    public String getHands() {
        return hands == null ? "" : handsNF.format(hands);
    }

    public String getBuyAmt() {
        return buyAmt == null ? "" : amtNF.format(buyAmt);
    }

    public String getBuyNum() {
        return buyNum == null ? "" : buyNum + "";
    }

    public String getSellUnitValue() {
        return sellUnitValue == null ? "" : unitValueNF.format(sellUnitValue);
    }

    public String getSellNum() {
        return sellNum == null ? "" : sellNum + "";
    }

    public String getSellAmt() {
        return sellAmt == null ? "" : amtNF.format(sellAmt);
    }

    public String getProfit() {
        return profit == null ? "" : profitNF.format(profit);
    }


}
