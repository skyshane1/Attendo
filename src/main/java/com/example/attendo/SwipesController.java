package com.example.attendo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import static com.example.attendo.DBUtil.*;

public class SwipesController implements Initializable {
    private Workbook workbook;
    private Sheet sheet;
    private int numEntries = 0;
    @FXML
    private TextField swipeField;
    @FXML
    private Label swipeText;
    @FXML
    private Button exportButton;
    @FXML
    private Button backButton;
    @FXML
    private TableView<Person> tableview;
    @FXML
    private TableColumn<Person, String> name;
    @FXML
    private TableColumn<Person, String> banner_id;
    @FXML
    private TableColumn<Person, Integer> swipes;

    @FXML
    private Label numSwipes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        banner_id.setCellValueFactory(new PropertyValueFactory<>("banner_id"));
        swipes.setCellValueFactory(new PropertyValueFactory<>("swipes"));

        workbook = new XSSFWorkbook();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy MM dd");
        LocalDateTime now = LocalDateTime.now();

        sheet = workbook.createSheet(dtf.format(now));
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);
        swipeText.setText("To use: plug in swiper, click on the text field, then swipe normally. " +
                "When finished click export and an excel sheet with the data will be created in the application directory.");
        numSwipes.setText("Total Entries: 0");
    }

    @FXML
    public void switchToMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1080, 720);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void handleEnterPressed(KeyEvent event) throws SQLException {
        if (event.getCode() == KeyCode.ENTER) {
            Row row = sheet.createRow(numEntries);
            Cell cell = row.createCell(0);
            cell.setCellValue(swipeField.getText());
            String id = swipeField.getText();
            System.out.println(id.substring(0,3));
            if(!id.substring(0,3).equals("000")) {
                Pattern pattern = Pattern.compile(";.*=");
                Matcher matcher = pattern.matcher(id);
                try {
                    matcher.find();
                    id = matcher.group(0);
                    id = id.substring(1, id.length() - 1);

                    int swipes = dbGetSwipes(id);
                    String name = "";
                    try {
                        pattern = Pattern.compile("\\^(.*)\\^");
                        matcher = pattern.matcher(swipeField.getText());
                        matcher.find();
                        name = matcher.group(1);
                        String[] splitt = name.split("/");
                        name = splitt[1].strip() + " " + splitt[0];
                    } catch (Exception e) {
                        name = "Bad Scan";
                    }
                    if (!name.contains("(") && !name.contains(")") && !name.matches(".*\\d.*") && !name.contains(";")) {
                        swipeText.setText(name + " " + id + " swipes: " + swipes);
                        numEntries++;
                        tableview.getItems().add(new Person(name, id, swipes));
                        numSwipes.setText("Total Entries: " + String.valueOf(numEntries));
                    } else {
                        swipeText.setText("Input did not match expected format, try again");
                    }
                } catch (Exception e) {
                    swipeText.setText("Input did not match expected format, try again");
                }
            } else {
                int swipes = dbGetSwipes("600964" + id);
                swipeText.setText("Manual Entry" + " " + "600964" + id + " swipes: " + swipes);
                numEntries++;
                tableview.getItems().add(new Person("Manual Entry", "600964" + id, swipes));
                numSwipes.setText("Total Entries: " + String.valueOf(numEntries));
            }
            swipeField.clear();
        }
    }
    @FXML
    public void onExportButtonClick() throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy MM dd");
        LocalDateTime now = LocalDateTime.now();

        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + dtf.format(now) + ".xlsx";

        readSheet(sheet);

        FileOutputStream outputStream = new FileOutputStream(fileLocation);
        workbook.write(outputStream);
        workbook.close();
        swipeText.setText("Exported!");
        numEntries = 0;
    }

    private void readSheet(Sheet sheet) {
        for (Row row : sheet) {

            //For each row, iterate through all the columns
            Iterator<Cell> cellIterator = row.cellIterator();

            while (cellIterator.hasNext()) {

                Cell cell = cellIterator.next();

                String cellValue;

                try {
                    cellValue = cell.getStringCellValue();
                } catch (Exception e) {
                    continue;
                }

                String name;
                String banner_id;

                try {
                    try {
                        Pattern pattern = Pattern.compile("\\^.*?\\^");
                        Matcher matcher = pattern.matcher(cellValue);
                        matcher.find();
                        name = matcher.group(0);
                        name = name.substring(1, name.length() - 1);
                        name = name.trim();
                    } catch (Exception e) {
                        name = "Name not scanned";
                    }
                    try {
                        Pattern pattern = Pattern.compile(";.*=");
                        Matcher matcher = pattern.matcher(cellValue);
                        matcher.find();
                        banner_id = matcher.group(0);
                        banner_id = banner_id.substring(1, banner_id.length() - 1);
                    } catch (Exception e) {
                        continue;
                    }
                    try {
                        dbInsert(name, banner_id);
                    } catch (Exception e) {
                        dbUpdate(name, banner_id);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}