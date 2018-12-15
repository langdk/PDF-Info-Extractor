import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class MainController implements Initializable {


    @FXML
    JFXTextField pdfPath;

    @FXML
    JFXTreeTableView keyInfoTable;

    @FXML
    JFXTextField input;

    File selectedFile;
    private final int TABLE_COLUMN_SIZE = 230;

    ExtractIdentifiers extractIdentifiers = new ExtractIdentifiers();
    //MainInformationItems items = new MainInformationItems("test", "test", "test");
    StageHelper helper = new StageHelper();
    private List<String> emailAddress = new ArrayList<>();
    public ObservableList<MainInformationItems> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        data.add(new MainInformationItems("test", "test", "test"));
        data.add(new MainInformationItems("tet", "test", "test"));
        data.add(new MainInformationItems("teadsfst", "test", "test"));
        data.add(new MainInformationItems("testasdf", "test", "test"));
        data.add(new MainInformationItems("tesaghagt", "test", "test"));
        data.add(new MainInformationItems("testadg", "test", "test"));
        data.add(new MainInformationItems("test", "test", "test"));
        data.add(new MainInformationItems("test", "test", "test"));
        data.add(new MainInformationItems("test", "test", "test"));
        data.add(new MainInformationItems("test", "test", "test"));
        data.add(new MainInformationItems("test", "test", "test"));
        data.add(new MainInformationItems("test", "test", "test"));
        data.add(new MainInformationItems("test", "test", "test"));
        JFXTreeTableColumn<MainInformationItems, String> email = new JFXTreeTableColumn<>("Emails");
        email.setPrefWidth(TABLE_COLUMN_SIZE);
        email.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<MainInformationItems, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<MainInformationItems, String> param) {
                return param.getValue().getValue().email;
            }
        });

        JFXTreeTableColumn<MainInformationItems, String> phoneNumbers = new JFXTreeTableColumn<>("Phone Numbers");
        phoneNumbers.setPrefWidth(TABLE_COLUMN_SIZE);
        phoneNumbers.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<MainInformationItems, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<MainInformationItems, String> param) {
                return param.getValue().getValue().phone;
            }
        });

        JFXTreeTableColumn<MainInformationItems, String> website = new JFXTreeTableColumn<>("Websites");
        website.setPrefWidth(TABLE_COLUMN_SIZE);
        website.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<MainInformationItems, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<MainInformationItems, String> param) {
                return param.getValue().getValue().website;
            }
        });

        final TreeItem<MainInformationItems> root = new RecursiveTreeItem<MainInformationItems>(data, RecursiveTreeObject::getChildren);
        keyInfoTable.getColumns().setAll(email, phoneNumbers, website);
        keyInfoTable.setRoot(root);
        keyInfoTable.setShowRoot(false);

        input.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                keyInfoTable.setPredicate(new Predicate<TreeItem<MainInformationItems>>() {
                    @Override
                    public boolean test(TreeItem<MainInformationItems> treeItem) {
                        Boolean flag = treeItem.getValue().email.getValue().contains(newValue)
                                || treeItem.getValue().website.getValue().contains(newValue)
                                || treeItem.getValue().phone.getValue().contains(newValue);
                        return flag;
                    }
                });
            }
        });



    }

    void setTableCells(){

    }

    public void fileChooser(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open PDF");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("pdf", "*.pdf")
        );
        selectedFile = fileChooser.showOpenDialog(null);
        helper.slidingAlert("Success", "PDF Loaded Successfully, click extract to view information");
    }

    public void extractSinglePageEachTime(){
        HashMap<Integer, String> pageInfo = new HashMap<>();


        String pages;
        String info;
        int occurrences = 0;
        String [] results = {};
        try {
            PDDocument document = PDDocument.load(selectedFile);
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            for (int i = 0; i<document.getNumberOfPages(); i++){
                pdfTextStripper.setStartPage(i);
                pdfTextStripper.setEndPage(i);
                pages = pdfTextStripper.getText(document);
                results = pages.split("\\s+");
                for (int j =0; j<results.length; j++){
                    extractIdentifiers.extractManufacture(results);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void extract() {
        HashMap<Integer, String> pageInfo = new HashMap<>();


        String pages;
        String info;
        int occurrences = 0;
        String [] results = {};

        try{
            PDDocument document = PDDocument.load(selectedFile);
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            pages = pdfTextStripper.getText(document);

            //data = FXCollections.observableArrayList(extractIdentifiers.extractTest(pages, extractIdentifiers.emailPatternTest, emailAddress));
//            extractIdentifiers.extractPhoneTest(pages);
////            extractIdentifiers.printPhone();
//            results = pages.split("\\s+");
//            extractIdentifiers.extractWebsite(results);
            //extractManufacture(results);
            //extractPrefer(results);
            //extractIdentifiers.extractPhoneInformation(results);
            //extractIdentifiers.extractEmail(results);

            //System.out.println(results.length);

            //System.out.println(pages);
            //write(results);

            //String text = pdfTextStripper.getText(document);
//            for(int i =0; i<document.getNumberOfPages();i++){
//                pdfTextStripper.setStartPage(i);
//                pdfTextStripper.setEndPage(i);
                //pages = pdfTextStripper.getText(document);
                //results = pages.split("\\s+");

//                for (int j =0; j<results.length;j++) {
//                    if (results[j].matches(pattern)) {
//                        System.out.println(results[j]);
//                    }
//                    //System.out.println(results[j]);
//
//                }
                //document.close();
//                }
//                System.out.println(pages);
//                m = p.matcher(pages);
//                if(m.find()){
//                    System.out.println(m.group());
//                    //pageInfo.put(i, m.group());
//
//                }
//                if (pages.matches(pattern)){
//                    occurrences++;
//                    System.out.println("Page number: " +i + " Occurences: " + occurrences);
//                }


            //System.out.println(pageInfo.size());
//            for (int i = 0; i<results.length;i++){
//                System.out.println(results[i]);
//            }




        //System.out.println(pageInfo);

    }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void populateTable(){
        keyInfoTable.getColumns().add(createColumn(0, "Phone Number"));
        keyInfoTable.getColumns().add(createColumn(1, "Email"));
        keyInfoTable.getColumns().add(createColumn(2, "Website"));

        for (int i = 0; i < data.size(); i++) {
            for (int columnIndex = keyInfoTable.getColumns().size(); columnIndex < data.size(); columnIndex++) {
                keyInfoTable.getColumns().add(createColumn(columnIndex, ""));
            }
            ObservableList<StringProperty> data = FXCollections.observableArrayList();
            data.add(new SimpleStringProperty(data.get(i).toString()));

            //keyInfoTable.getItems().add(data);
        }


    }


    private TableColumn<ObservableList<StringProperty>, String> createColumn(
            final int columnIndex, String columnTitle) {
        TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
        column.setPrefWidth(150);
        String title;
        if (columnTitle == null || columnTitle.trim().length() == 0) {
            title = "Column " + (columnIndex + 1);  // DELETE??
        } else {
            title = columnTitle;
        }
        column.setText(title);
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<StringProperty>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(
                    TableColumn.CellDataFeatures<ObservableList<StringProperty>, String> cellDataFeatures) {
                ObservableList<StringProperty> values = cellDataFeatures.getValue();
                if (columnIndex >= values.size()) {
                    return new SimpleStringProperty("");
                } else {
                    return cellDataFeatures.getValue().get(columnIndex);
                }
            }
        });
        // width of column set to width of table / number of columns
        column.setPrefWidth(800 / 6);
        return column;
    }



    private void write(String[] itemToWrite) {
        try {
            PrintWriter printWriter = new PrintWriter("test3.txt");
            for (int i =0; i<itemToWrite.length;i++){
                printWriter.println(itemToWrite[i]);
            }
            printWriter.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }



}
