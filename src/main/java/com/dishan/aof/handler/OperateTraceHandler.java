package com.dishan.aof.handler;

import java.awt.*;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.dishan.aof.bean.OperateTraceBean;
import com.dishan.aof.setting.SettingHolder;
import com.dishan.aof.utils.LogUtil;
import com.dishan.aof.utils.PinYinUtils;
import com.dishan.aof.constants.TableConstants;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ui.table.JBTable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class OperateTraceHandler extends DefaultTableModel {
    private static String[] columnNames;
    // /**
    //  * 存放【编码】的位置，更新数据时用到
    //  */
    // public int codeColumnIndex;

    private JTable table;
    private boolean colorful = true;

    static {
        PropertiesComponent instance = PropertiesComponent.getInstance();
        String tableHeader = instance.getValue(TableConstants.OPT_TRACE_TABLE_HEADER_KEY);
        if (StringUtils.isBlank(tableHeader)) {
            instance.setValue(TableConstants.OPT_TRACE_TABLE_HEADER_KEY, TableConstants.OPT_TRACE__TABLE_HEADER_VALUE);
            tableHeader = TableConstants.OPT_TRACE__TABLE_HEADER_VALUE;
        }
        String[] configStr = tableHeader.split(",");
        columnNames = new String[configStr.length];
        for (int i = 0; i < configStr.length; i++) {
            columnNames[i] = TableConstants.remapPinYin(configStr[i]);
        }
    }

    public OperateTraceHandler(JBTable table) {
        this.table = table;
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        // Fix tree row height
        FontMetrics metrics = table.getFontMetrics(table.getFont());
        table.setRowHeight(Math.max(table.getRowHeight(), metrics.getHeight()));
        table.setModel(this);
        refreshColorful(!colorful);
    }

    public void refreshColorful(boolean colorful) {
        if (this.colorful == colorful) {
            return;
        }
        this.colorful = colorful;
        // 刷新表头
        if (colorful) {
            setColumnIdentifiers(columnNames);
        } else {
            setColumnIdentifiers(PinYinUtils.toPinYin(columnNames));
        }
    }

    protected void updateData(List<OperateTraceBean> beans) {
        if (CollectionUtils.isNotEmpty(beans)) {
            for (OperateTraceBean bean : beans) {
                String[] split = bean.toString().split(",");
                Vector<Object> convertData = new Vector<>();
                for (String col : split) {
                    convertData.addElement(col);
                }
                addRow(convertData);
            }
        }
    }

    public void setupTable(String selectedItem) {
        try {
            if (StringUtils.isEmpty(selectedItem)) {
                return;
            }
            List<OperateTraceBean> beans = SettingHolder.OPERATE_TRACE_CONFIG.get(selectedItem.trim());
            if (CollectionUtils.isNotEmpty(beans)) {
                clearRow();
                updateData(beans);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.info("parse GridStrategy config error");
        }
    }

    /**
     * 参考源码{@link DefaultTableModel#removeRow(int)}，此为直接清除全部行，提高点效率
     */
    public void clearRow() {
        int size = dataVector.size();
        if (0 < size) {
            dataVector.clear();
            // 通知listeners刷新ui
            fireTableRowsDeleted(0, size - 1);
        }
    }

    /**
     * 设置表格条纹（斑马线）
     *
     * @param striped true设置条纹
     * @throws RuntimeException 如果table不是{@link JBTable}类型，请自行实现setStriped
     */
    public void setStriped(boolean striped) {
        if (table instanceof JBTable) {
            ((JBTable) table).setStriped(striped);
        } else {
            throw new RuntimeException("table不是JBTable类型，请自行实现setStriped");
        }
    }


    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
