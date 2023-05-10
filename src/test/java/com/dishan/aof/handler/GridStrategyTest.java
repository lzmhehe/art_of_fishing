package com.dishan.aof.handler;

import java.util.List;

import com.dishan.aof.bean.GridStrategyBean;
import com.dishan.aof.setting.GridStrategy;
import org.junit.Test;

public class GridStrategyTest {

    @Test
    public void generate() {
        GridStrategy strategy = GridStrategy.builder()
                .historyMax(0.917)
                .historyMin(0.417)
                .openUnitValue(0.465)
                .upStep(0.05f)
                .downStep(0.03f)
                .oneHandAmt(4000)
                .openHands(10)
                .downStepHandsRadio(0.1f)
                .handsScale(2)
                .unitValueScale(3)
                .profitScale(0)
                .amtScale(0)
                .build();
        List<GridStrategyBean> generate = strategy.generate();
        generate.stream().forEach(item -> {
            System.out.println(item);
        });
    }
}