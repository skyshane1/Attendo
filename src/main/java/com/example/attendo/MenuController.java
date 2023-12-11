package com.example.attendo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Button Swipe;

    @FXML
    public void switchToSwipe(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("swipes.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1080, 720);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchToFile(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("file.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1080, 720);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void switchToView(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1080, 720);
        stage.setScene(scene);
        stage.show();
    }
}