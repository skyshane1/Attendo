package com.example.attendo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.attendo.DBUtil.*;

public class FileController implements Initializable {
    private List<File> file;
    @FXML
    private Label fileText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Workbook workbook = new XSSFWorkbook();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy MM dd");
        LocalDateTime now = LocalDateTime.now();

        Sheet sheet = workbook.createSheet(dtf.format(now));
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 4000);
        fileText.setText("Click select file to load sheets then click insert data to upload");
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
    private Button importButton;

    @FXML
    protected void onFileButtonClick() throws IOException, InvalidFormatException {
        if(file == null) {
            FileChooser fil_chooser = new FileChooser();
            fil_chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fil_chooser.setTitle("Select Attendance Sheet");
            fil_chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            file = fil_chooser.showOpenMultipleDialog(AttendoApplication.pstage);

            if (file != null) {

                fileText.setText(file.size() + " files selected");
            }
            importButton.setText("Insert Data");
        } else {
            int i = 0;
            for(File excel: file){
                Workbook workbook = new XSSFWorkbook(excel);
                Sheet sheet = workbook.getSheetAt(0);
                readSheet(sheet);
                workbook.close();
                file.stream().close();
                System.out.println("Finished sheet " + i + "!");
                i++;
            }
            fileText.setText("Finished!");
        }
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
                        name = "Bad Scan";
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