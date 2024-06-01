package view;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ViewUtil {
    public static void setTextLabel(Label lbl, String text) {
        Platform.runLater(() -> lbl.setText(text));
    }

    public static void handleInvalidInput(Label lbl, TextField tf, String message) {
        setTextLabel(lbl, message);
        Platform.runLater(tf::clear);
    }

    public static void addItemToList(ListView<String> lv, String item) {
        Platform.runLater(() -> lv.getItems().add(item));
    }

    public static void removeItemFromList(ListView<String> lv, String item) {
        Platform.runLater(() -> lv.getItems().remove(item));
    }
}
