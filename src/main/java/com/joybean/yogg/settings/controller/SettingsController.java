package com.joybean.yogg.settings.controller;

import com.google.common.net.InetAddresses;
import com.joybean.yogg.config.IDataSource;
import com.joybean.yogg.config.Proxy;
import com.joybean.yogg.config.YoggConfig;
import com.joybean.yogg.crawler.AbstractPageProcessor;
import com.joybean.yogg.datasource.*;
import com.joybean.yogg.report.controller.ReportController;
import com.joybean.yogg.settings.service.SettingService;
import com.joybean.yogg.statemachine.StateMachinePool;
import com.joybean.yogg.support.ContextHolder;
import com.joybean.yogg.support.DatabaseUtils;
import com.joybean.yogg.support.JFXUtils;
import com.joybean.yogg.support.YoggException;
import com.joybean.yogg.task.executor.AbstractTaskExecutor;
import com.joybean.yogg.view.ViewManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

import static com.joybean.yogg.support.UrlUtils.isValid;

/**
 * @author joybean
 */
@Controller
@Lazy
public class SettingsController extends SplitPane implements Initializable {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(SettingsController.class);
    private final static String MYSQL = "MySQL";
    //private final static String ORACLE = "Oracle";
    private final static String DEFAULT_HOST = "127.0.0.1";
    private final static String DEFAULT_PORT = "8080";
    private static final String START_URL_SEPARATOR = ",";
    private YoggConfig config;
    private StateMachinePool stateMachinePool;
    private ViewManager viewManager;
    private SettingService settingService;
    private ReportController reportController;
    @FXML
    private TextField threadsTextField;
    @FXML
    private TextField timeoutTextField;
    @FXML
    private ToggleGroup proxyTypeGroup;
    @FXML
    private RadioButton directTypeRadio;
    @FXML
    private RadioButton httpTypeRadio;
    @FXML
    private RadioButton socksTypeRadio;
    @FXML
    private TextField proxyHostTextField;
    @FXML
    private TextField proxyPortTextField;
    @FXML
    private TabPane dataSourceTabPane;
    @FXML
    private TextField crawlerStartUrlTextField;
    @FXML
    private TextField filePathTextField;
    @FXML
    private ChoiceBox<String> databaseTypeChoiceBox;
    @FXML
    private TextField databaseUrlTextField;
    @FXML
    private TextField databaseUsernameTextField;
    @FXML
    private TextField databasePasswordTextField;
    /**
     * Map to save all available database information:key is database type;value is a object that contains driver class name and  url template
     */
    private final Map<String, Pair<String, String>> databaseMapping = new LinkedHashMap<>();
    /**
     * Map of proxy type and corresponding radio button on page
     */
    private final Map<Proxy.Type, RadioButton> proxyTypeRadioMap = new HashMap<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initBasic();
        initProxy();
        initDataSource();
    }

    /**
     * Save settings to file and update config in memory
     *
     * @param actionEvent actionEvent
     */
    public void saveSettings(ActionEvent actionEvent) {
        List<String> invalidItems = validateInputs(threadsTextField.getText(), timeoutTextField.getText(), proxyHostTextField.getText(), proxyPortTextField.getText());
        if (CollectionUtils.isNotEmpty(invalidItems)) {
            viewManager.showAlert(Alert.AlertType.ERROR, "Invalid input:" + invalidItems);
            return;
        }
        IDataSource newDataSource = rebuildDataSource();
        if (newDataSource == null) {
            return;
        }
        //Clear old report and records if data source changed
        if (dataSourceChanged(newDataSource)) {
            clearOldReport();
        }
        //update thread pool configuration
        refreshThreadPool();
        Proxy proxy = rebuildProxy();
        config.setTimeout(Integer.parseInt(timeoutTextField.getText()));
        config.setProxy(proxy);
        config.setDataSource(newDataSource);
        settingService.saveSettings(config);
        viewManager.showAlert(Alert.AlertType.INFORMATION, "Save successfully");
    }

    private void clearOldReport() {
        reportController.clear();
    }


    private boolean dataSourceChanged(IDataSource newDataSource) {
        IDataSource oldDataSource = config.getDataSource();
        return !newDataSource.equals(oldDataSource);
    }

    private void refreshThreadPool() {
        int threadNum = Integer.parseInt(threadsTextField.getText());
        config.setThreads(threadNum);
        //refresh state machine pool size
        stateMachinePool.setMaxTotal(threadNum);
        //refresh thread pool size
        ContextHolder.getSpringContext().getBeansOfType(AbstractTaskExecutor.class).values().forEach(e -> {
            ThreadPoolExecutor threadPool = e.getThreadPool();
            if (threadPool != null) {
                threadPool.setCorePoolSize(threadNum);
                threadPool.setMaximumPoolSize(threadNum);
            }
        });
    }

    private Proxy rebuildProxy() {
        String proxyHost = proxyHostTextField.getText();
        String proxyPort = proxyPortTextField.getText();
        return new Proxy(getSelectedProxyType(), proxyHost, proxyPort);
    }

    /**
     * Build a new data source by user input
     *
     * @return the data source represents user input
     */
    private IDataSource rebuildDataSource() {
        String dataSourceType = getSelectedDataSourceType();
        IDataSource dataSource = DataSourceFactory.getDataSource(dataSourceType);
        switch (dataSource.getDataSourceType()) {
            case CRAWLER:
                String startUrl = crawlerStartUrlTextField.getText();
                if (!isValid(startUrl)) {
                    viewManager.showAlert(Alert.AlertType.ERROR, "Malformed start url");
                    return null;
                } else {
                    ((CrawlerDataSource) dataSource).setStartUrls(Arrays.asList(startUrl.split(START_URL_SEPARATOR)));
                }
                break;
            case FILE:
                String filePath = filePathTextField.getText();
                if (!validateFilePath(filePath)) {
                    viewManager.showAlert(Alert.AlertType.ERROR, "Invalid file path");
                    return null;
                } else {
                    ((FileDataSource) dataSource).setFilePath(filePath);
                }
                break;
            case DATABASE:
                DatabaseDataSource databaseDataSource = (DatabaseDataSource) dataSource;
                String databaseUrl = databaseUrlTextField.getText();
                String databaseUsername = databaseUsernameTextField.getText();
                String databasePassword = databasePasswordTextField.getText();
                if (DatabaseUtils.testConnection(databaseDataSource.getDriverClassName(), databaseUrl, databaseUsername, databasePassword)) {
                    databaseDataSource.setUrl(databaseUrl);
                    databaseDataSource.setUsername(databaseUsername);
                    databaseDataSource.setPassword(databasePassword);
                } else {
                    viewManager.showAlert(Alert.AlertType.ERROR, "Cannot connect to database");
                    return null;
                }
                break;
        }
        return dataSource;
    }


    private boolean validateFilePath(String filePath) {
        Path path = Paths.get(filePath);
        try {
            return Files.exists(path) || Files.lines(path).findFirst().isPresent();
        } catch (IOException e) {
            LOGGER.error("Invalid file path {}", filePath, e);
            return false;
        }
    }


    private List<String> validateInputs(String threads, String timeout, String proxyHost, String proxyPort) {
        List<String> invalidItems = new ArrayList<>();
        if (StringUtils.isBlank(threads) || Integer.parseInt(threads) < 1) {
            invalidItems.add("threads");
        }
        if (StringUtils.isBlank(timeout) || Integer.parseInt(timeout) < 1) {
            invalidItems.add("timeout");
        }
        if (!directTypeRadio.isSelected()) {
            if (!InetAddresses.isInetAddress(proxyHost)) {
                invalidItems.add("host");
            }
        }
        if (proxyPortTextField.editableProperty().get() && (StringUtils.isBlank(proxyPort) || Integer.parseInt(proxyPort) < 1)) {
            invalidItems.add("port");
        }
        return invalidItems;
    }

    private String getSelectedDataSourceType() {
        return dataSourceTabPane.getSelectionModel().getSelectedItem().getText();
    }

    /**
     * Test database connection
     *
     * @param actionEvent actionEvent
     */
    public void testDatabaseConnection(ActionEvent actionEvent) {
        String dataSourceType = databaseTypeChoiceBox.getSelectionModel().getSelectedItem();
        String driverClassName = databaseMapping.get(dataSourceType).getKey();
        String databaseUrl = databaseUrlTextField.getText();
        String databaseUsername = databaseUsernameTextField.getText();
        String databasePassword = databasePasswordTextField.getText();
        if (DatabaseUtils.testConnection(driverClassName, databaseUrl, databaseUsername, databasePassword)) {
            viewManager.showAlert(Alert.AlertType.INFORMATION, "Connection successful");
        } else {
            viewManager.showAlert(Alert.AlertType.ERROR, "Failed to connect to database");
        }

    }

    /**
     * Open a choose file dialog
     *
     * @param actionEvent actionEvent
     * @throws IOException If I/O error occurs
     */
    public void browseFile(ActionEvent actionEvent) throws IOException {
        final FileChooser fileChooser = new FileChooser();
        configureFileChooser(fileChooser);
        File websiteFile = fileChooser.showOpenDialog(((Node) actionEvent.getTarget()).getScene().getWindow());
        if (websiteFile != null) {
            filePathTextField.setText(websiteFile.getCanonicalPath());
        }
    }

    /**
     * Initialize basic settings display
     */
    private void initBasic() {
        threadsTextField.setText(String.valueOf(config.getThreads()));
        timeoutTextField.setText(String.valueOf(config.getTimeout()));
        JFXUtils.bindNumericTextFieldChangeListener(threadsTextField);
        JFXUtils.bindNumericTextFieldChangeListener(timeoutTextField);
    }

    /**
     * Initialize proxy display
     */
    private void initProxy() {
        proxyTypeRadioMap.put(Proxy.Type.DIRECT, directTypeRadio);
        proxyTypeRadioMap.put(Proxy.Type.HTTP, httpTypeRadio);
        proxyTypeRadioMap.put(Proxy.Type.SOCKS, socksTypeRadio);
        JFXUtils.bindNumericTextFieldChangeListener(proxyPortTextField);
        directTypeRadio.setUserData(new ProxyInputStyle());
        httpTypeRadio.setUserData(new ProxyInputStyle(true));
        socksTypeRadio.setUserData(new ProxyInputStyle(true));
        Proxy proxy = config.getProxy();
        if (proxy != null) {
            Proxy.Type proxyType = proxy.getType();
            if (proxyType != null) {
                setProxyDisplayValue(proxy.getHost(), proxy.getPort());
                proxyTypeRadioMap.get(proxyType).setSelected(true);
            }
        }
        if (proxy == null || proxy.getType() == Proxy.Type.DIRECT) {
            setProxyInputEditable(false);
        }
        //Input is not allowed while DIRECT radio button is selected
        proxyTypeGroup.selectedToggleProperty().addListener((ov, old, _new) -> {
            if (proxyTypeGroup.getSelectedToggle() != null) {
                ProxyInputStyle proxyInputStyle = (ProxyInputStyle) proxyTypeGroup.getSelectedToggle().getUserData();
                setProxyInputEditable(proxyInputStyle.isEditable());
                if (directTypeRadio.isSelected()) {
                    proxyHostTextField.clear();
                    proxyPortTextField.clear();
                } else {
                    if (StringUtils.isBlank(proxyHostTextField.getText()) && StringUtils.isBlank(proxyPortTextField.getText())) {
                        setProxyDisplayValue(DEFAULT_HOST, DEFAULT_PORT);
                    }
                }
            }
        });
    }

    private void setProxyInputEditable(boolean value) {
        proxyHostTextField.setEditable(value);
        proxyPortTextField.setEditable(value);
    }

    private void setProxyDisplayValue(String defaultHost, String defaultPort) {
        proxyHostTextField.setText(defaultHost);
        proxyPortTextField.setText(defaultPort);
    }

    /**
     * Initialize data source display
     */
    private void initDataSource() {
        buildDatabaseTypeItems();
        IDataSource dataSource = config.getDataSource();
        DataSourceType dataSourceType = dataSource.getDataSourceType();
        //switch data source tab pane
        dataSourceTabPane.getSelectionModel().select(dataSourceType.getValue());
        switch (dataSourceType) {
            case CRAWLER:
                List<String> startUrls = ((CrawlerDataSource) dataSource).getStartUrls();
                if (CollectionUtils.isNotEmpty(startUrls)) {
                    crawlerStartUrlTextField.setText(StringUtils.join(startUrls, START_URL_SEPARATOR));
                }
                break;
            case FILE:
                String filePath = ((FileDataSource) dataSource).getFilePath();
                filePathTextField.setText(filePath);
                break;
            case DATABASE:
                DatabaseDataSource dbDataSource = (DatabaseDataSource) dataSource;
                String driverClassName = dbDataSource.getDriverClassName();

                String url = dbDataSource.getUrl();
                String username = dbDataSource.getUsername();
                String password = dbDataSource.getPassword();
                databaseUrlTextField.setText(url);
                databaseUsernameTextField.setText(username);
                databasePasswordTextField.setText(password);
                databaseTypeChoiceBox.getSelectionModel().select(getSelectedDbType(driverClassName));
                break;
        }
        //change url template while changing data source type
        databaseTypeChoiceBox.getSelectionModel().selectedIndexProperty().addListener(
                (o, oldVal, newVal) -> setUrlTemplate(newVal));
    }

    /**
     * Build choice box of database type
     */
    private void buildDatabaseTypeItems() {
        databaseMapping.put(MYSQL, new Pair<>("com.mysql.jdbc.Driver", "jdbc:mysql://<host>:<port>/<database>"));
        //databaseMapping.put(ORACLE, new Pair("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@<host>:<port>:<sid> "));
        for (String type : databaseMapping.keySet()) {
            databaseTypeChoiceBox.getItems().add(type);
        }
    }

    private void setUrlTemplate(Number newVal) {
        DatabaseDataSource dataSource = DataSourceFactory.getDataSource(DatabaseDataSource.class);
        Pair<String, String> pair = databaseMapping.get(databaseTypeChoiceBox.getItems().get(newVal.intValue()));
        String driverClassName = pair.getKey();
        String urlTemplate = pair.getValue();
        dataSource.setDriverClassName(driverClassName);
        databaseUrlTextField.setText(urlTemplate);
    }


    private void configureFileChooser(FileChooser fileChooser) {
        fileChooser.setTitle("Choose Website File");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    }

    private String getSelectedProxyType() {
        Optional<RadioButton> selectedRadioButton = proxyTypeRadioMap.values().stream().filter(RadioButton::isSelected).findAny();
        if (selectedRadioButton.isPresent()) {
            return selectedRadioButton.get().getText();
        }
        throw new YoggException("No proxy type selected");
    }

    private String getSelectedDbType(String driverClassName) {
        Optional<String> optional = databaseMapping.entrySet().stream().filter(e -> e.getValue().getKey().equals(driverClassName)).map(Map.Entry::getKey).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new YoggException("Unsupported driver:%s", driverClassName);
    }

    static class ProxyInputStyle {
        private boolean editable;

        ProxyInputStyle() {
        }

        ProxyInputStyle(boolean editable) {
            this.editable = editable;
        }

        boolean isEditable() {
            return editable;
        }
    }

    @Autowired
    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    @Autowired
    public void setSettingService(SettingService settingService) {
        this.settingService = settingService;
    }

    @Autowired
    public void setReportController(ReportController reportController) {
        this.reportController = reportController;
    }

    @Autowired
    public void setConfig(YoggConfig config) {
        this.config = config;
    }

    @Autowired
    public void setStateMachinePool(StateMachinePool stateMachinePool) {
        this.stateMachinePool = stateMachinePool;
    }
}
