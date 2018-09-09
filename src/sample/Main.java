package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        setPrimaryStage(primaryStage);
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        primaryStage.setTitle("LogIn Page");
        primaryStage.setScene(new Scene(root,744,506));

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void setPrimaryStage(Stage stage){
        Main.primaryStage = stage;
    }

    static public Stage getPrimaryStage(){
        return primaryStage;
    }
}
