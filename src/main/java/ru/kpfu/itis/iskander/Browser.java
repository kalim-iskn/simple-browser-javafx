package ru.kpfu.itis.iskander;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Browser extends Application implements Initializable {

    public ArrayList<String> historyList = new ArrayList<>();
    public int currentPageNumber = 0;
    private WebEngine webEngine;

    @FXML
    public WebView browser;

    @FXML
    public Button searchBtn;

    @FXML
    public Label statusLabel;

    @FXML
    public TextField searchField;

    @FXML
    public Button backBtn;

    @FXML
    public Button forwardBtn;

    @FXML
    public Button historyBtn;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/browser.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Simple Browser");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        webEngine = browser.getEngine();

        Worker<Void> worker = webEngine.getLoadWorker();

        worker.stateProperty().addListener((observable, oldValue, newValue) -> {
            statusLabel.setStyle("-fx-text-fill: gray");
            statusLabel.setText("Status: " + newValue.toString());
            if (newValue == Worker.State.SUCCEEDED) {
                statusLabel.setStyle("-fx-text-fill: #4bb34b");
            } else if (newValue == Worker.State.FAILED) {
                statusLabel.setStyle("-fx-text-fill: red");
            }
        });

        String defaultUrl = "https://google.com";
        webEngine.load(defaultUrl);

        searchBtn.setOnAction(event -> {
            String url = searchField.getText();
            loadUrl(url);
        });

        backBtn.setOnAction(actionEvent -> {
            if (currentPageNumber > 0) {
                currentPageNumber--;
                String url = historyList.get(currentPageNumber);
                searchField.setText(url);
                webEngine.load(url);
            }
        });

        forwardBtn.setOnAction(actionEvent -> {
            if (currentPageNumber < historyList.size() - 1) {
                currentPageNumber++;
                String url = historyList.get(currentPageNumber);
                searchField.setText(url);
                webEngine.load(url);
            }
        });

        historyBtn.setOnAction(actionEvent -> showHistory());

    }

    private void showHistory() {
        StringBuilder historyTable = new StringBuilder();
        int i = 0;
        for (String site : historyList) {
            i++;
            historyTable.append(i).append(") ").append(site).append("\r\n");
        }

        Label label = new Label(historyTable.toString());

        GridPane layout = new GridPane();
        layout.getChildren().add(label);
        Scene historyScene = new Scene(layout, 450, 300);

        Stage newWindow = new Stage();
        newWindow.setTitle("History");
        newWindow.setScene(historyScene);

        newWindow.show();
    }

    private void loadUrl(String url) {
        if (!isHasProtocol(url)) {
            url = getGoogleSearchUrl(url);
        }
        historyList.add(url);
        currentPageNumber = historyList.size() - 1;
        webEngine.load(url);
    }

    private String getGoogleSearchUrl(String url) {
        return "https://google.com/search?q=" + url;
    }

    private boolean isHasProtocol(String url) {
        return url.startsWith("https://") || url.startsWith("http://");
    }

}
