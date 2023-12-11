package com.example.attendo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AttendoApplication extends Application {

    static Stage pstage;
    @Override
    public void start(Stage stage) throws IOException, SQLException, ClassNotFoundException {
        DBUtil.dbConnect();
        FXMLLoader fxmlLoader = new FXMLLoader(AttendoApplication.class.getResource("menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1080, 720);
        stage.setTitle("Attendo");
        stage.setScene(scene);
        pstage = stage;
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}