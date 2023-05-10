package com.dishan.aof.setting;


import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.dishan.aof.bean.OperateTraceBean;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 操作记录 config item
 */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperateTraceConfigItem {

    // 时间,code,操作(B/S),单价，数量，手续费（option），关联交易日期|单价（option）

    /**
     * 操作类型
     */
    public enum OperateType {
        B, S
    }

    /**
     * 操作时间 ：day
     * pattern  yyyyMMdd yyyy-MM-dd
     */
    String date;
    /**
     * code
     */
    String code;
    /**
     * 操作类型 B|S
     */
    OperateType type;
    /**
     * 单价
     */
    String unitPrice;
    /**
     * 操作数量
     */
    String num;
    /**
     * 手续费（option）
     */
    String fee;
    /**
     * 关联交易，pattern 交易日期|单价（option）
     */
    String ref;

    /**
     * @param text
     * @return
     */
    public static List<OperateTraceConfigItem> parseText(String text) {
        if (StringUtils.isBlank(text)) {
            return Collections.emptyList();
        }
        String[] split = text.split("\n");
        return Arrays.stream(split).map(line -> {
                    line = line.trim();
                    if (StringUtils.isBlank(line)) {
                        return null;
                    }
                    try {
                        String[] cols = line.split(",");
                        if (cols.length >= 5 && cols.length <= 7) {
                            OperateTraceConfigItem item = OperateTraceConfigItem.builder()
                                    .date(cols[0])
                                    .code(cols[1])
                                    .type(OperateType.valueOf(cols[2]))
                                    .unitPrice(cols[3])
                                    .num(cols[4]).build();
                            if (cols.length >= 6) {
                                item.setFee(cols[5]);
                            }
                            if (cols.length >= 7) {
                                item.setRef(cols[6]);
                            }
                            return item;
                        }
                    } catch (Exception e) {
                        return null;
                    }
                    return null;
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    /**
     * 查找最近操作  0 buy  1 sell
     *
     * @param code
     * @return
     */
    public static String[] findLastOperate(String code) {
        Map<String, List<OperateTraceBean>> operateTraceConfig = SettingHolder.OPERATE_TRACE_CONFIG;
        if (MapUtils.isEmpty(operateTraceConfig)) {
            return new String[]{"--", "--"};
        }
        List<OperateTraceBean> operateTraceBeans = operateTraceConfig.get(code);
        if (CollectionUtils.isEmpty(operateTraceBeans)) {
            return new String[]{"--", "--"};
        }
        Optional<OperateTraceBean> buy = operateTraceBeans.stream()
                .filter(item -> item.getOperateType() == OperateType.B)
                .findFirst();

        Optional<OperateTraceBean> sell = operateTraceBeans.stream()
                .filter(item -> item.getOperateType() == OperateType.S)
                .findFirst();
        String buyStr = buy.isPresent() ? buy.get().digest() : "--";
        String sellStr = sell.isPresent() ? sell.get().digest() : "--";
        return new String[]{buyStr, sellStr};
    }

    /**
     * check config valid
     */
    public void check() {
        Preconditions.checkArgument(StringUtils.isNotBlank(date), "date is blank");
        Preconditions.checkArgument(StringUtils.isNotBlank(code), "code is blank");
        Preconditions.checkArgument(StringUtils.isNotBlank(unitPrice), "unitPrice is blank");
        Preconditions.checkArgument(StringUtils.isNotBlank(num), "num is blank");
        Preconditions.checkArgument(Objects.nonNull(type), "type is blank");
        try {
            DateUtils.parseDate(date, new String[]{"yyyyMMdd", "yyyy-MM-dd", "yyyy/MM/dd"});
        } catch (ParseException e) {
            throw new IllegalArgumentException("data pattern error", e);
        }
        Preconditions.checkArgument(NumberUtils.isDigits(num), "num must digits");
        Preconditions.checkArgument(NumberUtils.isCreatable(unitPrice), "unitPrice must number");

        if (StringUtils.isNotBlank(fee)) {
            Preconditions.checkArgument(NumberUtils.isCreatable(fee), "fee must number");
        }
    }
}
