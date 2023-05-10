package com.dishan.aof.setting;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dishan.aof.bean.OperateTraceBean;
import org.apache.commons.collections.CollectionUtils;

public class SettingHolder {

    /**
     * 操作记录配置
     * code ->  List<OperateTraceBean>
     * list 操作时间倒序
     */
    public static Map<String, List<OperateTraceBean>> OPERATE_TRACE_CONFIG;
    /**
     * 网格配置策略
     */
    public static List<GridStrategyConfig> GRID_STRATEGY_CONFIGS;

    /**
     * code -> strategy
     *
     * @return
     */
    public static Map<String, GridStrategyConfig> getGridStrategyConfig() {
        if (CollectionUtils.isEmpty(GRID_STRATEGY_CONFIGS)) {
            return Collections.emptyMap();
        }
        return GRID_STRATEGY_CONFIGS.stream().collect(Collectors.toMap(x -> x.getCode(), x -> x, (x, y) -> x));

    }
}
