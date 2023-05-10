import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import com.dishan.aof.handler.GridStrategyHandler;
import com.dishan.aof.setting.GridStrategyConfig;
import com.dishan.aof.setting.SettingHolder;
import com.dishan.aof.utils.JacksonUtils;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ui.table.JBTable;
import org.apache.commons.lang3.StringUtils;

import static com.dishan.aof.constants.AofConstants.KEY_GRID_CONFIG;

public class GridStrategyWindow {
    public static final String NAME = "Grid";
    private JPanel mPanel;
    static JBTable table;
    static JComboBox codeOptions;
    static GridStrategyHandler handler;
    // private JTextField field;


    public JPanel getmPanel() {
        return mPanel;
    }


    static {
        table = new JBTable();
    }

    public GridStrategyWindow() {
        //切换接口
        handler = new GridStrategyHandler(table);

        String[] data = {""};
        codeOptions = new JComboBox<>();
        codeOptions.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handler.setupTable((String) codeOptions.getSelectedItem());
                    }
                }
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
            String config = loadConfig();
            // 配置的code 作为 select option
            if (StringUtils.isNotBlank(config)) {
                SettingHolder.GRID_STRATEGY_CONFIGS = JacksonUtils.parseArray(config, GridStrategyConfig.class);
                SettingHolder.GRID_STRATEGY_CONFIGS.stream().map(item -> item.getCode())
                        .forEach(item -> {
                            codeOptions.addItem(item);
                        });
            }
            PropertiesComponent instance = PropertiesComponent.getInstance();
            boolean colorful = instance.getBoolean("key_colorful");
            handler.refreshColorful(colorful);
            handler.setStriped(instance.getBoolean("key_table_striped"));
            handler.clearRow();
            handler.setupTable((String) codeOptions.getSelectedItem());
        }
    }

    private static String loadConfig() {
        return PropertiesComponent.getInstance().getValue(KEY_GRID_CONFIG);
    }

}
