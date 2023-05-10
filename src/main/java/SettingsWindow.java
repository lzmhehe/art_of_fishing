import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.swing.*;

import com.dishan.aof.bean.OperateTraceBean;
import com.dishan.aof.quartz.QuartzManager;
import com.dishan.aof.setting.GridStrategyConfig;
import com.dishan.aof.setting.OperateTraceConfigItem;
import com.dishan.aof.setting.SettingHolder;
import com.dishan.aof.utils.HttpClientPool;
import com.dishan.aof.utils.JacksonUtils;
import com.dishan.aof.utils.LogUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import static com.dishan.aof.constants.AofConstants.KEY_GRID_CONFIG;
import static com.dishan.aof.constants.AofConstants.KEY_OPT_TRACE;

public class SettingsWindow implements Configurable {

    private JPanel panel1;
    private JTextArea textAreaFund;
    private JTextArea textAreaStock;
    private JCheckBox checkbox;
    /**
     * 使用tab界面，方便不同的设置分开进行控制
     */
    private JTabbedPane tabbedPane1;
    private JCheckBox checkBoxTableStriped;
    private JTextField cronExpressionFund;
    private JTextField cronExpressionStock;
    private JTextField cronExpressionCoin;
    private JCheckBox checkboxSina;
    private JCheckBox checkboxLog;
    private JTextArea textAreaCoin;
    private JLabel proxyLabel;
    private JTextField inputProxy;
    private JButton proxyTestButton;
    private JTextArea gridConfigTextArea;
    private JPanel operatorTraceTbl;
    private JTextArea operatorTraceTextArea;

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) String getDisplayName() {
        return "art of fishing";
    }

    @Override
    public @Nullable JComponent createComponent() {
        PropertiesComponent instance = PropertiesComponent.getInstance();
        String value = instance.getValue("key_funds");
        String value_stock = instance.getValue("key_stocks");
        String value_coin = instance.getValue("key_coins");
        boolean value_color = instance.getBoolean("key_colorful");

        textAreaFund.setText(value);
        textAreaStock.setText(value_stock);
        textAreaCoin.setText(value_coin);
        checkbox.setSelected(!value_color);
        checkBoxTableStriped.setSelected(instance.getBoolean("key_table_striped"));
        checkboxSina.setSelected(instance.getBoolean("key_stocks_sina"));
        checkboxLog.setSelected(instance.getBoolean("key_close_log"));
        cronExpressionFund.setText(instance.getValue("key_cron_expression_fund", "0 * * * * ?")); //默认每分钟执行
        cronExpressionStock.setText(instance.getValue("key_cron_expression_stock", "*/10 * * * * ?")); //默认每10秒执行
        cronExpressionCoin.setText(instance.getValue("key_cron_expression_coin", "*/10 * * * * ?")); //默认每10秒执行

        // 网格策略设置
        gridConfigTextArea.setText(instance.getValue(KEY_GRID_CONFIG));

        //操作trace 记录
        operatorTraceTextArea.setText(instance.getValue(KEY_OPT_TRACE));

        //代理设置
        inputProxy.setText(instance.getValue("key_proxy"));
        proxyTestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String proxy = inputProxy.getText().trim();
                testProxy(proxy);
            }
        });
        return panel1;
    }

    @Override
    public boolean isModified() {
        return true;
    }

    @Override
    public void apply() throws ConfigurationException {
        String errorMsg = checkConfig();
        if (StringUtils.isNotEmpty(errorMsg)) {
            throw new ConfigurationException(errorMsg);
        }
        PropertiesComponent instance = PropertiesComponent.getInstance();
        instance.setValue("key_funds", textAreaFund.getText());
        instance.setValue("key_stocks", textAreaStock.getText());
        instance.setValue("key_coins", textAreaCoin.getText());
        instance.setValue("key_colorful", !checkbox.isSelected());
        instance.setValue("key_cron_expression_fund", cronExpressionFund.getText());
        instance.setValue("key_cron_expression_stock", cronExpressionStock.getText());
        instance.setValue("key_cron_expression_coin", cronExpressionCoin.getText());
        instance.setValue("key_table_striped", checkBoxTableStriped.isSelected());
        instance.setValue("key_stocks_sina", checkboxSina.isSelected());
        instance.setValue("key_close_log", checkboxLog.isSelected());
        // 网格策略设置
        instance.setValue(KEY_GRID_CONFIG, gridConfigTextArea.getText());
        // 操作trace设置
        instance.setValue(KEY_OPT_TRACE, operatorTraceTextArea.getText());
        List<OperateTraceConfigItem> configItems = OperateTraceConfigItem.parseText(operatorTraceTextArea.getText());
        Map<String, List<OperateTraceBean>> operateTraceBeans = OperateTraceBean.of(configItems);
        SettingHolder.OPERATE_TRACE_CONFIG = operateTraceBeans;

        String proxy = inputProxy.getText().trim();
        instance.setValue("key_proxy", proxy);

        HttpClientPool.getHttpClient().buildHttpClient(proxy);
        StockWindow.apply();
        FundWindow.apply();
        CoinWindow.apply();
        GridStrategyWindow.apply();
        OperateTraceWindow.apply();
    }


    private void testProxy(String proxy) {
        if (proxy.indexOf('：') > 0) {
            LogUtil.notify("别用中文分割符啊!", false);
            return;
        }
        HttpClientPool httpClientPool = HttpClientPool.getHttpClient();
        httpClientPool.buildHttpClient(proxy);
        try {
            httpClientPool.get("https://www.baidu.com");
            LogUtil.notify("代理测试成功!请保存", true);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.notify("测试代理异常!", false);
        }
    }

    public static List<String> getConfigList(String key, String split) {
        String value = PropertiesComponent.getInstance().getValue(key);
        if (StringUtils.isEmpty(value)) {
            return new ArrayList<>();
        }
        Set<String> set = new LinkedHashSet<>();
        String[] codes = value.split(split);
        for (String code : codes) {
            if (!code.isEmpty()) {
                set.add(code.trim());
            }
        }
        return new ArrayList<>(set);
    }

    public static List<String> getConfigList(String key) {
        String value = PropertiesComponent.getInstance().getValue(key);
        if (StringUtils.isEmpty(value)) {
            return new ArrayList<>();
        }
        Set<String> set = new LinkedHashSet<>();
        String[] codes = null;
        if (value.contains(";")) {//包含分号
            codes = value.split("[;]");
        } else {
            codes = value.split("[,，]");
        }
        for (String code : codes) {
            if (!code.isEmpty()) {
                set.add(code.trim());
            }
        }
        return new ArrayList<>(set);
    }

    /**
     * 检查配置项
     *
     * @return 返回提示的错误信息
     */
    private String checkConfig() {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append(getConfigList(cronExpressionFund.getText(), ";").stream().map(s -> {
            if (!QuartzManager.checkCronExpression(s)) {
                return "Fund请配置正确的cron表达式[" + s + "]、";
            } else {
                return "";
            }
        }).collect(Collectors.joining()));
        errorMsg.append(getConfigList(cronExpressionStock.getText(), ";").stream().map(s -> {
            if (!QuartzManager.checkCronExpression(s)) {
                return "Stock请配置正确的cron表达式[" + s + "]、";
            } else {
                return "";
            }
        }).collect(Collectors.joining()));
        errorMsg.append(getConfigList(cronExpressionCoin.getText(), ";").stream().map(s -> {
            if (!QuartzManager.checkCronExpression(s)) {
                return "Coin请配置正确的cron表达式[" + s + "]、";
            } else {
                return "";
            }
        }).collect(Collectors.joining()));

        String gridConfigText = gridConfigTextArea.getText();
        if (StringUtils.isNotBlank(gridConfigText)) {
            try {
                List<GridStrategyConfig> gridStrategyConfigs = JacksonUtils.parseArray(gridConfigText, GridStrategyConfig.class);
                for (GridStrategyConfig gridStrategyConfig : gridStrategyConfigs) {
                    gridStrategyConfig.check();
                }
            } catch (Exception e) {
                errorMsg.append("grid config error, msg:" + e.getMessage());
            }
        }

        String operatorTraceTextAreaText = operatorTraceTextArea.getText();
        List<OperateTraceConfigItem> operateTraceConfigItems = OperateTraceConfigItem.parseText(operatorTraceTextAreaText);

        if (CollectionUtils.isNotEmpty(operateTraceConfigItems)) {
            for (OperateTraceConfigItem operateTraceConfigItem : operateTraceConfigItems) {
                try {
                    operateTraceConfigItem.check();
                } catch (Exception e) {
                    errorMsg.append("operate trace config error, msg:" + e.getMessage());
                }
            }
        }


        return errorMsg.toString();
    }
}
