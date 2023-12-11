package com.example.attendo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static com.example.attendo.DBUtil.*;

public class ViewController implements Initializable {
    private Stage stage;
    private Scene scene;
    private Parent root;
    @FXML
    private TableView<Person> tableview;
    @FXML
    private TableColumn<Person, String> name;
    @FXML
    private TableColumn<Person, String> banner_id;
    @FXML
    private TableColumn<Person, Integer> swipes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        name.setCellValueFactory(new PropertyValueFactory<Person, String>("name"));
        banner_id.setCellValueFactory(new PropertyValueFactory<Person, String>("banner_id"));
        swipes.setCellValueFactory(new PropertyValueFactory<Person, Integer>("swipes"));

        try {
            ResultSet data = dbGetAll();
            ObservableList dbData = FXCollections.observableArrayList(dataBaseArrayList(data));
            tableview.setItems(dbData);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void switchToMenu(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1080, 720);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void clearDatabase(ActionEvent event) throws SQLException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Clear Database? This action cannot be undone.", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            dbDestroy();
            try {
                ResultSet data = dbGetAll();
                ObservableList dbData = FXCollections.observableArrayList(dataBaseArrayList(data));
                tableview.setItems(dbData);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ArrayList dataBaseArrayList(ResultSet resultSet) throws SQLException {
        ArrayList<Person> data =  new ArrayList<>();
        while (resultSet.next()) {
            Person person = new Person(resultSet.getString("name"), resultSet.getString("banner_id"), resultSet.getInt("swipes"));
            data.add(person);
        }
        return data;
    }
}