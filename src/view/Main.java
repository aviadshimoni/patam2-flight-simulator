package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        String url = "MainView.fxml";
        System.out.println(getClass().getResource(url).getPath());
        FXMLLoader fxml= new FXMLLoader(getClass().getResource(url));
        Parent root = fxml.load();
        primaryStage.setTitle("Flight Gear Simulator");
        ControllerView controllerView = fxml.getController();
        primaryStage.setScene(new Scene(root, 970, 630));
        primaryStage.show();


    }


    public static void main(String[] args) {
        launch(args);
    }
}
