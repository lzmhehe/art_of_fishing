package com.dishan.aof.handler;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.dishan.aof.bean.GridStrategyBean;
import com.dishan.aof.setting.GridStrategyConfig;
import com.dishan.aof.utils.LogUtil;
import com.dishan.aof.utils.PinYinUtils;
import com.dishan.aof.constants.TableConstants;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ui.JBColor;
import com.intellij.ui.table.JBTable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import static com.dishan.aof.setting.SettingHolder.GRID_STRATEGY_CONFIGS;

public class GridStrategyHandler extends DefaultTableModel {
    private static String[] columnNames;
    // /**
    //  * 存放【编码】的位置，更新数据时用到
    //  */
    // public int codeColumnIndex;

    private JTable table;
    private boolean colorful = true;

    public GridStrategyHandler(JBTable table) {
        this.table = table;
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        // Fix tree row height
        FontMetrics metrics = table.getFontMetrics(table.getFont());
        table.setRowHeight(Math.max(table.getRowHeight(), metrics.getHeight()));
        table.setModel(this);
        refreshColorful(!colorful);
    }


    static {
        PropertiesComponent instance = PropertiesComponent.getInstance();
        String tableHeader = instance.getValue(TableConstants.GRID_TABLE_HEADER_KEY);
        if (StringUtils.isBlank(tableHeader)) {
            instance.setValue(TableConstants.GRID_TABLE_HEADER_KEY, TableConstants.GRID_TABLE_HEADER_VALUE);
            tableHeader = TableConstants.GRID_TABLE_HEADER_VALUE;
        }
        String[] configStr = tableHeader.split(",");
        columnNames = new String[configStr.length];
        for (int i = 0; i < configStr.length; i++) {
            columnNames[i] = TableConstants.remapPinYin(configStr[i]);
        }
    }

    // {
    //     for (int i = 0; i < columnNames.length; i++) {
    //         if ("编码".equals(columnNames[i])) {
    //             codeColumnIndex = i;
    //         }
    //     }
    // }


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
        // TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(this);
        // Comparator<Object> doubleComparator = (o1, o2) -> {
        //     Double v1 = NumberUtils.toDouble(StringUtils.remove((String) o1, '%'));
        //     Double v2 = NumberUtils.toDouble(StringUtils.remove((String) o2, '%'));
        //     return v1.compareTo(v2);
        // };
        // Arrays.stream("估算净值,估算涨跌".split(","))
        //         .map(name -> WindowUtils.getColumnIndexByName(columnNames, name))
        //         .filter(index -> index >= 0)
        //         .forEach(index -> rowSorter.setComparator(index, doubleComparator));
        // table.setRowSorter(rowSorter);
        // columnColors(colorful);
    }


    /**
     * 按照编码顺序初始化，for 每次刷新都乱序，没办法控制显示顺序
     *
     * @param selectedItem
     */
    public void setupTable( String selectedItem) {
        try {
            if (CollectionUtils.isEmpty(GRID_STRATEGY_CONFIGS) || StringUtils.isEmpty(selectedItem)) {
                return;
            }
            Map<String, GridStrategyConfig> configMap = GRID_STRATEGY_CONFIGS.stream()
                    .collect(Collectors.toMap(x -> x.getCode(), x -> x, (x, y) -> x));
            GridStrategyConfig config = configMap.get(selectedItem);
            if (config != null && config.getGridStrategy() != null) {
                List<GridStrategyBean> beans = config.getGridStrategy().generate();
                // for (GridStrategyBean bean : beans) {
                //     System.out.println(bean);
                // }
                clearRow();
                updateData(beans);
            }
        } catch (Exception e) {
            LogUtil.info("parse GridStrategy config error");
        }
    }

    private void columnColors(boolean colorful) {
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                double temp = NumberUtils.toDouble(StringUtils.remove(Objects.toString(value), "%"));
                if (temp > 0) {
                    if (colorful) {
                        setForeground(JBColor.RED);
                    } else {
                        setForeground(JBColor.DARK_GRAY);
                    }
                } else if (temp < 0) {
                    if (colorful) {
                        setForeground(JBColor.GREEN);
                    } else {
                        setForeground(JBColor.GRAY);
                    }
                } else if (temp == 0) {
                    Color orgin = getForeground();
                    setForeground(orgin);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
//        table.getColumn(getColumnName(2)).setCellRenderer(cellRenderer);
        int columnIndex = TableConstants.getColumnIndexByName(columnNames, "估算涨跌");

        int columnIndex3 = TableConstants.getColumnIndexByName(columnNames, "收益率");
        int columnIndex4 = TableConstants.getColumnIndexByName(columnNames, "收益");

        table.getColumn(getColumnName(columnIndex)).setCellRenderer(cellRenderer);

        table.getColumn(getColumnName(columnIndex3)).setCellRenderer(cellRenderer);
        table.getColumn(getColumnName(columnIndex4)).setCellRenderer(cellRenderer);
    }

    protected void updateData(List<GridStrategyBean> beans) {
        if (CollectionUtils.isNotEmpty(beans)) {
            for (GridStrategyBean bean : beans) {
                Vector<Object> convertData = convertData(bean);
                if (convertData == null) {
                    continue;
                }
                // System.out.println(convertData);
                addRow(convertData);
            }
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
    private Vector<Object> convertData(GridStrategyBean fundBean) {
        if (fundBean == null) {
            return null;
        }
        String str = fundBean.toString();
        String[] split = str.split("\t");
        // 与columnNames中的元素保持一致
        Vector<Object> v = new Vector<Object>(columnNames.length);
        for (String s : split) {
            v.addElement(s);
        }
        return v;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
