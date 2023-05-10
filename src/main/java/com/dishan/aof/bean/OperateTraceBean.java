package com.dishan.aof.bean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dishan.aof.setting.OperateTraceConfigItem;
import com.dishan.aof.utils.LogUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperateTraceBean {
    //日期,code,操作,单价,数量,总额,费用,日期ref,操作ref,单价ref,数量ref,总额ref,费用ref,利润

    /**
     * 日期
     */
    LocalDate date;
    /**
     * code
     */
    String code;
    /**
     * 操作
     */
    OperateTraceConfigItem.OperateType operateType;
    /**
     * 单价
     */
    String unitPrice;
    /**
     * 数量
     */
    int num;
    /**
     * 总额
     */
    BigDecimal totalAmt;
    /**
     * 费用
     */
    String fee;
    /**
     * 关联操作，买->卖
     * 操作日期
     */
    LocalDate refDate;
    /**
     * 关联操作，买->卖
     * 操作日期
     */
    OperateTraceConfigItem.OperateType refOperateType;
    /**
     * 关联操作，买->卖
     * 单价
     */
    String refUnitPrice;
    /**
     * 关联操作，买->卖
     * 数量
     */
    Integer refNum;
    /**
     * 关联操作，买->卖
     * 总额
     */
    BigDecimal refTotalAmt;
    /**
     * 关联操作，买->卖
     * 费用
     */
    String refFee;
    /**
     * 利润
     */
    BigDecimal profit;

    /**
     * 关联 info
     */
    transient String ref;


    @Override
    public String toString() {
        //日期,code,操作,单价,数量,总额,费用,日期ref,操作ref,单价ref,数量ref,总额ref,费用ref,利润
        List<Object> list = new ArrayList<>();
        list.add(date);
        list.add(code);
        list.add(operateType);
        list.add(unitPrice);
        list.add(num);
        list.add(totalAmt);
        list.add(fee != null ? fee : "");
        list.add(refDate != null ? refDate : "");
        list.add(refOperateType != null ? refOperateType : "");
        list.add(refUnitPrice != null ? refUnitPrice : "");
        list.add(refNum != null ? refNum : "");
        list.add(refTotalAmt != null ? refTotalAmt : "");
        list.add(refFee != null ? refFee : "");
        list.add(profit != null ? profit : "");
        return Joiner.on(",").join(list);
    }

    public String digest() {
        return String.format("%s|%s|%s|%s", getDate(), getUnitPrice(), getNum(), getTotalAmt());
    }

    @SneakyThrows
    public static OperateTraceBean of(OperateTraceConfigItem item) {
        // 这里处理有点挫，不想依赖多余jar
        LocalDate localDate = getLocalDate(item.getDate());
        return OperateTraceBean.builder()
                .date(localDate)
                .code(item.getCode())
                .operateType(item.getType())
                .unitPrice(item.getUnitPrice())
                .num(Integer.parseInt(item.getNum()))
                .fee(item.getFee())
                .totalAmt(new BigDecimal(item.getNum()).multiply(new BigDecimal(item.getUnitPrice())).setScale(2, RoundingMode.HALF_UP))
                .ref(item.getRef())
                .build();

    }

    @SneakyThrows
    private static LocalDate getLocalDate(String item) throws ParseException {
        Date date = DateUtils.parseDate(item, new String[]{"yyyyMMdd", "yyyy-MM-dd", "yyyy/MM/dd"});
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate;
    }

    public static Map<String, List<OperateTraceBean>> of(List<OperateTraceConfigItem> itemList) {
        Map<String, List<OperateTraceBean>> ret = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(itemList)) {
            try {
                Multimap<String, OperateTraceBean> multimap = HashMultimap.create();
                for (OperateTraceConfigItem operateTraceConfigItem : itemList) {
                    OperateTraceBean bean = of(operateTraceConfigItem);
                    multimap.put(bean.getCode(), bean);
                }
                for (String code : multimap.keySet()) {
                    final Map<String, OperateTraceBean> map = Maps.newTreeMap();
                    Collection<OperateTraceBean> operateTraceBeans = multimap.get(code);
                    // 根据操作时间 倒序排列
                    List<OperateTraceBean> collect = operateTraceBeans.stream()
                            .sorted(Comparator.comparing(OperateTraceBean::getDate).reversed())
                            .peek(item -> {
                                map.put(buildRefKey(item), item);
                            }).collect(Collectors.toList());

                    // has ref build ref
                    collect.stream().forEach(item -> {
                        String itemRef = item.getRef();
                        if (StringUtils.isNotBlank(itemRef)) {
                            String key = buildRefKey(itemRef);
                            OperateTraceBean refBean = map.get(key);
                            if (refBean == null) {
                                item.setRefUnitPrice("nf");
                            } else {
                                item.setRefBeanCalcProfit(refBean);
                                refBean.setRefBeanCalcProfit(item);
                            }
                        }
                    });
                    ret.put(code, collect);
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.info("OperateTraceConfigItem 2 OperateTraceBean error,msg:" + e.getMessage());
            }
        }
        return ret;
    }

    /**
     * 设置 ref 同时calc profit
     *
     * @param refBean
     */
    private void setRefBeanCalcProfit(OperateTraceBean refBean) {
        this.refDate = refBean.date;
        this.refOperateType = refBean.operateType;
        this.refNum = refBean.num;
        this.refUnitPrice = refBean.unitPrice;
        this.refTotalAmt = refBean.totalAmt;
        this.refFee = refBean.fee;

        BigDecimal profit = this.refTotalAmt.subtract(this.totalAmt);
        // 如果是卖，修正值
        if (operateType == OperateTraceConfigItem.OperateType.S) {
            profit = profit.multiply(new BigDecimal(-1));
        }
        if (StringUtils.isNotBlank(fee)) {
            profit = profit.subtract(new BigDecimal(fee));
        }
        if (StringUtils.isNotBlank(refFee)) {
            profit = profit.subtract(new BigDecimal(refFee));
        }
        profit.setScale(2, RoundingMode.HALF_UP);
        this.profit = profit;
    }

    private static String buildRefKey(OperateTraceBean item) {
        return String.format("%s|%s", item.getDate().toString(), item.unitPrice);
    }

    @SneakyThrows
    private static String buildRefKey(String item) {
        String[] split = item.split("[|]");
        if (split.length == 2) {
            String date = split[0];
            LocalDate localDate1 = getLocalDate(date);
            return String.format("%s|%s", localDate1, split[1]);
        }
        return "";
    }
}
