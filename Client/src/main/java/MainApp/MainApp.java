package MainApp;

import Elements.Menu.MainMenu;
import Utils.AppUtils;
import Utils.ResponseProcessor;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.Getter;

public class MainApp extends Application {

    @Getter private static MainApp instance;
    @Getter private ServerConnection serverConnection;

    @Override
    public void start(Stage primaryStage) {
        instance = this;
        ResponseProcessor responseProcessor = new ResponseProcessor();
        serverConnection = new ServerConnection(responseProcessor);
        showMainMenu(primaryStage);
    }

    public void showMainMenu(Stage primaryStage) {
        MainMenu.show(primaryStage);
    }

    @Override
    public void stop() {
        if (serverConnection != null) {
            serverConnection.closeConnection();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
