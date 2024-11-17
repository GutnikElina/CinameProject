package Elements.Menu;

import Utils.UIUtils;
import javafx.stage.Stage;

public class MainMenu {
    public static void show(Stage stage) {
        UIUtils.loadScene(stage, "/SceneBuilder/MainMenu.fxml", "Главное меню");
    }
}