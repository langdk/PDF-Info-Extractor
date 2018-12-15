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
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

import java.io.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Predicate;

public class MainController implements Initializable {


    @FXML
    JFXTextField pdfPath;

    @FXML
    JFXTreeTableView keyInfoTable;

    @FXML
    JFXTextField input;

    private final String phonePatternTest = "\\s*(?:\\+?(\\d{1,3}))?[-|− (]*(\\d{3})[-|−. )]*(\\d{3})[-|−. ]*(\\d{4})(?: *x(\\d+))?\\s*";
    public final String emailPatternTest = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    private final String webPattern = "(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[A-Za-z0-9]+([\\-|−\\.]{1}[A-Za-z0-9]+)*\\.(com|net|org|gov|io|us)";
    private final String webPatternTest = "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
            + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)";
    File selectedFile;
    private final int TABLE_COLUMN_SIZE = 230;

    ExtractIdentifiers extractIdentifiers = new ExtractIdentifiers();
    //MainInformationItems items = new MainInformationItems("test", "test", "test");
    StageHelper helper = new StageHelper();
    private List<String> emailAddress = new ArrayList<>();
    private List<String> phoneNumbers = new ArrayList<>();
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
        if(selectedFile!=null) {
            helper.slidingAlert("Success", "PDF loaded successfully, click extract to view information");
        }
        else {
            helper.slidingAlert("Failure","Invalid/no file was selected");
        }
    }

    public void test(List<String> test){

            //List<String> lines = IOUtils.readLines(new FileReader(selectedFile));
            LinkExtractor linkExtractor = LinkExtractor.builder()
                    .linkTypes(EnumSet.of(LinkType.URL, LinkType.WWW))
                    .build();
            test.forEach((String line) -> {
                Iterable<LinkSpan> links = linkExtractor.extractLinks(line);
                for (LinkSpan link : links) {
                    System.out.println(MessageFormat.format("{0} : {1}", link.getType(), line.substring(link.getBeginIndex(), link.getEndIndex())));
                }
            });
    }
    public void test2(List<String> test){
        LinkExtractor linkExtractor = LinkExtractor.builder()
                .linkTypes(EnumSet.of(LinkType.EMAIL))
                .build();
        test.forEach((String line) -> {
            Iterable<LinkSpan> links = linkExtractor.extractLinks(line);
            for (LinkSpan link : links) {
                System.out.println(MessageFormat.format("{0} : {1}", link.getType(), line.substring(link.getBeginIndex(), link.getEndIndex())));
            }
        });
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
            results = pages.split("\\s+");
            List<String> listInfo = Arrays.asList(results);
            //test(listInfo);
            test2(listInfo);

//            extractIdentifiers.test(pages);
//            for (String x :extractIdentifiers.extractPhoneTest(pages, phoneNumbers, webPatternTest)){
//                System.out.println(x);
//            }
//            for (String x:extractIdentifiers.extractPhoneTest(pages, phoneNumbers, webPattern)){
//                System.out.println(x);
//            }


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
