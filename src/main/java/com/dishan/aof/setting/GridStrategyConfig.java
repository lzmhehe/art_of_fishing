package com.dishan.aof.setting;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.dishan.aof.bean.GridStrategyBean;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GridStrategyConfig {

    public enum Type {
        STOCK, FUND
    }

    /**
     * 编码
     */
    String code;

    /**
     * 类型
     */
    Type type;

    /**
     * 网格策略
     */
    GridStrategy gridStrategy;

    public void check() {
        Preconditions.checkArgument(StringUtils.isNotBlank(code), "code is blank");
        Preconditions.checkArgument(Objects.nonNull(type), "type is null");
        Preconditions.checkArgument(Objects.nonNull(gridStrategy), "gridStrategy is null");
        gridStrategy.check();
    }


    /**
     * 寻找上区间
     *
     * @return [0] 上区间，[1] 区间
     * pattern: 区间值[比例]
     */
    public static String[] findLastInterval(String currentStr, String code) {
        try {
            if (StringUtils.isBlank(currentStr)) {
                return new String[]{"--", "--"};
            }
            Map<String, GridStrategyConfig> gridStrategyConfig = SettingHolder.getGridStrategyConfig();
            GridStrategyConfig strategy = gridStrategyConfig.get(code.trim());
            if (strategy == null) {
                return new String[]{"--", "--"};
            }
            List<GridStrategyBean> generate = strategy.getGridStrategy().generate();
            if (CollectionUtils.isEmpty(generate)) {
                return new String[]{"--", "--"};
            }
            BigDecimal current = new BigDecimal(currentStr);

            // 区间内
            for (int i = 0; i < generate.size() - 1; i++) {
                GridStrategyBean up = generate.get(i);
                GridStrategyBean down = generate.get(i + 1);
                if (up.unitValue.doubleValue() >= current.doubleValue()
                        && down.unitValue.doubleValue() <= current.doubleValue()) {
                    BigDecimal upRadio = new BigDecimal((up.unitValue.doubleValue() - current.doubleValue()) * 100)
                            .divide(current, 2, RoundingMode.HALF_UP);
                    BigDecimal downRadio = new BigDecimal((down.unitValue.doubleValue() - current.doubleValue()) * -100)
                            .divide(current, 2, RoundingMode.HALF_UP);
                    return new String[]{String.format("%s[%s]", up.getUnitValue(), upRadio.toString()),
                            String.format("%s[%s]", down.getUnitValue(), downRadio.toString())};
                }
            }
            // 超过上线
            GridStrategyBean down = generate.get(0);
            if (down.unitValue.doubleValue() > current.doubleValue()) {
                BigDecimal downRadio = new BigDecimal((down.unitValue.doubleValue() - current.doubleValue()) * -100)
                        .divide(current, 2, RoundingMode.HALF_UP);
                return new String[]{"--", String.format("%s[%s]", down.getUnitValue(), downRadio.toString())};
            }
            // 超过下线
            GridStrategyBean up = generate.get(generate.size() - 1);
            if (up.unitValue.doubleValue() < current.doubleValue()) {
                BigDecimal upRadio = new BigDecimal((up.unitValue.doubleValue() - current.doubleValue()) * 100)
                        .divide(current, 2, RoundingMode.HALF_UP);
                return new String[]{String.format("%s[%s]", up.getUnitValue(), upRadio.toString()), "--"};
            }
            return new String[]{"--", "--"};
        } catch (Exception e) {
            return new String[]{"Na", "Na"};
        }


    }

    /**
     * 查找策略
     *
     * @return 0:open，1 upstep，2 downstep
     */
    public static String[] findGridStrategyStr(String code) {
        try {
            Map<String, GridStrategyConfig> gridStrategyConfig = SettingHolder.getGridStrategyConfig();
            GridStrategyConfig strategy = gridStrategyConfig.get(code.trim());
            if (strategy == null) {
                return new String[]{"--", "--", "--"};
            }
            GridStrategy gridStrategy = strategy.getGridStrategy();
            return new String[]{gridStrategy.getOpenUnitValue() + "", gridStrategy.getUpStep() * 100 + "%", gridStrategy.getDownStep() * 100 + "%"};
        } catch (Exception e) {
            return new String[]{"Na", "Na", "Na"};
        }
    }
}
