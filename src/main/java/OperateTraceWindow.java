import java.awt.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;

import com.dishan.aof.bean.OperateTraceBean;
import com.dishan.aof.handler.OperateTraceHandler;
import com.dishan.aof.setting.OperateTraceConfigItem;
import com.dishan.aof.setting.SettingHolder;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ui.table.JBTable;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import static com.dishan.aof.constants.AofConstants.KEY_GRID_CONFIG;
import static com.dishan.aof.constants.AofConstants.KEY_OPT_TRACE;

public class OperateTraceWindow {
    public static final String NAME = "optTrace";
    private JPanel mPanel;
    static JBTable table;
    static JComboBox codeOptions;
    static OperateTraceHandler handler;


    public JPanel getmPanel() {
        return mPanel;
    }

    static {
        table = new JBTable();
    }

    public OperateTraceWindow() {
        //切换接口
        handler = new OperateTraceHandler(table);

        codeOptions = new JComboBox<>();
        codeOptions.addActionListener(
                e -> handler.setupTable((String) codeOptions.getSelectedItem())
        );
        JPanel jPanel = new JPanel();
        jPanel.setVisible(true);
        jPanel.add(codeOptions, BorderLayout.EAST);
        mPanel.add(jPanel, BorderLayout.NORTH);
        mPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        apply();
    }


    public static void apply() {
        if (handler != null) {
            codeOptions.removeAllItems();
            String text = PropertiesComponent.getInstance().getValue(KEY_OPT_TRACE);
            List<OperateTraceConfigItem> configItems = OperateTraceConfigItem.parseText(text);
            Map<String, List<OperateTraceBean>> operateTraceBeans = OperateTraceBean.of(configItems);
            SettingHolder.OPERATE_TRACE_CONFIG = operateTraceBeans;
            if(MapUtils.isNotEmpty(SettingHolder.OPERATE_TRACE_CONFIG)){
                // 配置的code 作为 select option
                SettingHolder.OPERATE_TRACE_CONFIG.keySet().stream()
                        .forEach(item -> {
                            codeOptions.addItem(item);
                        });
            }
            PropertiesComponent instance = PropertiesComponent.getInstance();
            handler.setStriped(instance.getBoolean("key_table_striped"));
            handler.setupTable((String) codeOptions.getSelectedItem());

            boolean colorful = instance.getBoolean("key_colorful");
            handler.refreshColorful(colorful);
        }
    }
}
