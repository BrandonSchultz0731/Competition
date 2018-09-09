package sample;

import java.awt.ActiveEvent;
import java.awt.Button.*;
import java.beans.EventHandler;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class AlertBox {

  public static void display(String title, String message) {
    Stage window = new Stage();

    //Block user interaction with other windows until this is taken care of
    window.initModality(Modality.APPLICATION_MODAL);
    window.setTitle(title);
    window.setMinWidth(250);

    Label label = new Label();
    label.setText(message);

    Button closeButton = new Button("Ok");
    closeButton.setOnAction(e -> window.close());

    VBox layout = new VBox(10);
    layout.getChildren().addAll(label,closeButton);
    layout.setAlignment(Pos.CENTER);

    Scene scene = new Scene(layout);
    window.setScene(scene);
    window.showAndWait();
  }


}
