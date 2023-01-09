package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Model;
import viewModel.ViewModelController;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Model myModel = new Model();
        myModel.writeToXML(myModel.properties);
        ViewModelController myViewModelController = new ViewModelController(myModel);

        String url = "MainView.fxml";
        System.out.println(getClass().getResource(url).getPath());
        FXMLLoader fxml= new FXMLLoader(getClass().getResource(url));
        Parent root = fxml.load();
        primaryStage.setTitle("TIOA Flight simulator v0.666");
        ControllerView controllerView = fxml.getController();
        controllerView.init(myViewModelController);
        primaryStage.setScene(new Scene(root, 1100, 650));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
