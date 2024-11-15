package Elements.Form;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Utils.UIUtils;

public abstract class BaseForm {

    protected abstract String getTitle();
    protected abstract void configureForm(VBox vbox, Stage stage);

    public void show(Stage stage) {
        Label titleLabel = UIUtils.createLabel(getTitle(), 24);
        VBox vbox = UIUtils.createVBox(30, Pos.CENTER, titleLabel);
        configureForm(vbox, stage);

        Scene scene = new Scene(vbox, 800, 500);
        scene.getStylesheets().add("/style.css");
        stage.setScene(scene);
        stage.show();
    }
}