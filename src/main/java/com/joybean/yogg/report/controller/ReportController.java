package com.joybean.yogg.report.controller;

import com.joybean.yogg.report.TaskReport;
import com.joybean.yogg.report.record.RecordStatus;
import com.joybean.yogg.report.record.SMSSendingRecord;
import com.joybean.yogg.report.record.SMSSendingRecordProxy;
import com.joybean.yogg.report.service.ReportService;
import com.joybean.yogg.task.Task;
import com.joybean.yogg.view.ViewManager;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * @author jobean
 */
@Controller
@Lazy
public class ReportController extends SplitPane implements Initializable {
    private final static String RECORD_STATUS_ITEM_ALL = "ALL";
    @Autowired
    private ViewManager viewManager;
    @FXML
    private PieChart reportPieChart;
    @FXML
    private TextField websiteTextField;
    @FXML
    private ComboBox statusComboBox;
    @FXML
    private TableView<SMSSendingRecordProxy> reportRecordTableView;
    @FXML
    private Pagination reportRecordPagination;
    @Autowired
    private ReportService reportService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initStatusComboBox();
        initReport();
    }

    /**
     * Search sending records
     */
    public void search() {
        try {
            refreshTableItems(0);
            setPageCount();
            reportRecordPagination.setCurrentPageIndex(0);
        } catch (Exception e) {
            viewManager.showException(e, "Failed to search sending record due to exception");
        }
    }

    /**
     * Reset search area
     */
    public void reset() {
        statusComboBox.getSelectionModel().select(0);
        websiteTextField.clear();
    }

    /**
     * Clear task report and refresh report display
     */
    public void clear() {
        try {
            reportService.clearTaskReport(Task.TASK_ID_MAIN);
            reportRecordTableView.getItems().clear();
            reportPieChart.getData().clear();
        } catch (Exception e) {
            viewManager.showException(e, "Failed to clear report due to exception");
        }
    }

    /**
     * Refresh task report
     */
    public void refresh() {
        try {
            refreshReportChart();
            refreshTableItems(reportRecordPagination.getCurrentPageIndex());
            setPageCount();
        } catch (Exception e) {
            viewManager.showException(e, "Failed to refresh report due to exception");
        }
    }

    private Node search(int pageIndex) {
        refreshTableItems(pageIndex);
        return new BorderPane(reportRecordTableView);
    }

    /**
     * Refresh sending record table by page index and current search conditions
     * @param pageIndex the records page index to display
     */
    private void refreshTableItems(int pageIndex) {
        if (!hasStatistics()) {
            reportRecordTableView.getItems().clear();
            return;
        }
        int pageSize = com.joybean.yogg.support.Pagination.DEFAULT_PAGE_SIZE;
        int fromIndex = pageIndex * pageSize;
        String website = websiteTextField.getText();
        RecordStatus status = getSelectedStatus();
        List<SMSSendingRecord> records = reportService.fetchSMSSendingRecord(Task.TASK_ID_MAIN, website, new com.joybean.yogg.support.Pagination(fromIndex, pageSize), status);
        if (CollectionUtils.isEmpty(records)) {
            reportRecordTableView.getItems().clear();
            return;
        }
        reportRecordTableView.setItems(FXCollections.observableArrayList(records.stream().map(r -> new SMSSendingRecordProxy(r)).collect(Collectors.toList())));

    }

    /**
     * Initialize items of combo box for record status
     */
    private void initStatusComboBox() {
        ObservableList items = FXCollections.observableArrayList(RecordStatus.values());
        //Display "ALL" item in the combo box so as to represent all status
        items.add(0, RECORD_STATUS_ITEM_ALL);
        statusComboBox.setItems(items);
    }
    /**
     * Initialize report,display report chart and report table
     */
    private void initReport() {
        initReportChart();
        initReportRecordTable();
        initPagination();
    }

    /**
     * Initialize pagination of sending record table
     */
    private void initPagination() {
        reportRecordPagination.setPageFactory(this::search);
        setPageCount();
    }

    /**
     * Initialize sending record table
     */
    private void initReportRecordTable() {
        ObservableList<TableColumn<SMSSendingRecordProxy, ?>> tableColumns = reportRecordTableView.getColumns();
        tableColumns.forEach(c -> {
            String columnName = c.getText();
            //Work out related sending record field name by record table column name
            String fieldName = StringUtils.uncapitalize(StringUtils.deleteWhitespace(WordUtils.capitalizeFully(columnName)));
            if (ReflectionUtils.findField(SMSSendingRecord.class, fieldName) != null) {
                c.setCellValueFactory(new PropertyValueFactory(fieldName));
            }
        });
        initTableContextMenu();
    }


    private void refreshReportChart() {
        initReportChart();
    }

    /**
     * Initialize report pie chart
     */
    private void initReportChart() {
        if (!hasStatistics()) {
            reportPieChart.getData().clear();
            return;
        }
        Map<RecordStatus, Long> counterMap = reportService.getStatusCounterMap(Task.TASK_ID_MAIN);
        if (counterMap == null) {
            return;
        }
        ObservableList<PieChart.Data> data = generateReportChartData(counterMap);
        data.forEach(d ->
                d.nameProperty().bind(
                        Bindings.format("%s(%s)",
                                d.getName(), d.pieValueProperty().intValue()
                        )
                )
        );
        reportPieChart.setData(data);
    }

    /**
     * Add copy context menu for record table
     */
    private void initTableContextMenu() {
        reportRecordTableView.getSelectionModel().setCellSelectionEnabled(true);
        reportRecordTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        MenuItem item = new MenuItem("Copy");
        item.setOnAction(event -> {
            ObservableList<TablePosition> posList = reportRecordTableView.getSelectionModel().getSelectedCells();
            int old_r = -1;
            StringBuilder clipboardString = new StringBuilder();
            for (TablePosition p : posList) {
                int r = p.getRow();
                int c = p.getColumn();
                Object cell = reportRecordTableView.getColumns().get(c).getCellData(r);
                if (cell == null)
                    cell = StringUtils.EMPTY;
                if (old_r == r)
                    clipboardString.append('\t');
                else if (old_r != -1)
                    clipboardString.append(StringUtils.LF);
                Object value = cell;
                if (cell instanceof Labeled) {
                    value = ((Labeled) cell).getText();
                }
                clipboardString.append(value);
                old_r = r;
            }
            final ClipboardContent content = new ClipboardContent();
            content.putString(clipboardString.toString());
            Clipboard.getSystemClipboard().setContent(content);
        });
        ContextMenu menu = new ContextMenu();
        menu.getItems().add(item);
        reportRecordTableView.setContextMenu(menu);
    }

    /**
     * Generate report pie chart by statistics counter
     *
     * @param counterMap statistics counter
     * @return Data of pie chart
     */
    private ObservableList<PieChart.Data> generateReportChartData(Map<RecordStatus, Long> counterMap) {
        if (counterMap == null) {
            return null;
        }
        PieChart.Data[] dataArr = counterMap.entrySet().stream().map(e -> new PieChart.Data(e.getKey().name(), e.getValue().doubleValue())).toArray(PieChart.Data[]::new);
        return FXCollections.observableArrayList(dataArr);
    }

    /**
     * Whether statistics file exists and have valid value
     *
     * @return
     */
    private boolean hasStatistics() {
        TaskReport taskReport = reportService.loadTaskReport(Task.TASK_ID_MAIN);
        return taskReport != null && taskReport.getStatistics().getTotal().intValue() > 0;
    }

    /**
     * Set pagination total page number
     */
    private void setPageCount() {
        if (!hasStatistics()) {
            reportRecordPagination.setPageCount(0);
            return;
        }
        String website = websiteTextField.getText();
        RecordStatus status = getSelectedStatus();
        int recordCount = reportService.countSMSSendingRecord(Task.TASK_ID_MAIN, website, status);
        reportRecordPagination.setPageCount((int) Math.ceil(((double) recordCount) / com.joybean.yogg.support.Pagination.DEFAULT_PAGE_SIZE));
    }

    private RecordStatus getSelectedStatus() {
        Object selected = statusComboBox.getSelectionModel().getSelectedItem();
        RecordStatus status = null;
        if (selected instanceof RecordStatus) {
            status = (RecordStatus) selected;
        }
        return status;
    }

}
