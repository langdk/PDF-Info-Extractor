import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
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

    @FXML
    JFXHamburger hamburger;

    @FXML
    JFXDrawer drawer;





    File selectedFile;
    private final int TABLE_COLUMN_SIZE = 330;

    private ExtractIdentifiers extractIdentifiers = new ExtractIdentifiers();

    Helper helper = new Helper();
    private List<String> emailAddress = new ArrayList<>();
    private List<String> websites = new ArrayList<>();
    private List<String> phoneNumbers = new ArrayList<>();
    public ObservableList<MainInformationItems> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {


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
        initDrawer();



    }

    private void initDrawer() {
        try {
            VBox toolbar = FXMLLoader.load(getClass().getResource("ButtonToolbar.fxml"));
            drawer.setSidePane(toolbar);
            drawer.setDefaultDrawerSize(150);
        }catch (IOException e){
            e.printStackTrace();
        }
        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
        task.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, (Event event) -> {

            task.setRate(task.getRate() * -1);
            task.play();
            if (drawer.isClosed()) {
                drawer.open();
            } else {
                drawer.close();
            }
        });

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
        extractHelper();
    }



    private void extractHelper(){
        String pages;
        String [] results;
        try{
            PDDocument document = PDDocument.load(selectedFile);
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
            pages = pdfTextStripper.getText(document);
            results = pages.split("\\s+");
            List<String> listInfo = Arrays.asList(results);
            extractEmail(listInfo);
            extractWebsite(listInfo);
            extractIdentifiers.extractPhoneNumbers(pages, phoneNumbers);
            emailAddress = extractIdentifiers.eliminateDuplicates(emailAddress);
            websites = extractIdentifiers.eliminateDuplicates(websites);
            phoneNumbers = extractIdentifiers.eliminateDuplicates(phoneNumbers);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void extract() {
        populateTable();
    }

    public void populateTable(){

        for (int i =0; i<filler(); i++){

            data.add(new MainInformationItems(emailAddress.get(i), phoneNumbers.get(i), websites.get(i)));
        }
        keyInfoTable.refresh();
    }

    private int filler(){
        int largest = getLargest();
        for(int i = emailAddress.size(); i<largest; i++){
            emailAddress.add("");
        }
        for(int i = websites.size(); i<largest; i++){
            websites.add("");
        }
        for(int i = phoneNumbers.size(); i<largest; i++){
            phoneNumbers.add("");
        }
        return largest;
    }

    private int getLargest(){
        List <Integer> largest = new ArrayList<>();
        largest.add(emailAddress.size());
        largest.add(websites.size());
        largest.add(phoneNumbers.size());

        return Collections.max(largest);
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

    private void printLists(){
        helper.printList(emailAddress);
        helper.printList(websites);
        helper.printList(phoneNumbers);
    }

    private List<String> extractWebsite(List<String> info){
        LinkExtractor linkExtractor = LinkExtractor.builder()
                .linkTypes(EnumSet.of(LinkType.URL, LinkType.WWW))
                .build();
        info.forEach((String line) -> {
            Iterable<LinkSpan> links = linkExtractor.extractLinks(line);
            for (LinkSpan link : links) {
                websites.add(MessageFormat.format("{1}", link.getType(), line.substring(link.getBeginIndex(), link.getEndIndex())));
            }
        });
        return websites;
    }
    private List<String> extractEmail(List<String> info){

        LinkExtractor linkExtractor = LinkExtractor.builder()
                .linkTypes(EnumSet.of(LinkType.EMAIL))
                .build();
        info.forEach((String line) -> {
            Iterable<LinkSpan> links = linkExtractor.extractLinks(line);
            for (LinkSpan link : links) {
                emailAddress.add(MessageFormat.format("{1}", link.getType(), line.substring(link.getBeginIndex(), link.getEndIndex())));
            }
        });
        return emailAddress;
    }

//    public void extractSinglePageEachTime(){
//        HashMap<Integer, String> pageInfo = new HashMap<>();
//        String pages;
//        String info;
//        int occurrences = 0;
//        String [] results = {};
//        try {
//            PDDocument document = PDDocument.load(selectedFile);
//            PDFTextStripper pdfTextStripper = new PDFTextStripper();
//            for (int i = 0; i<document.getNumberOfPages(); i++){
//                pdfTextStripper.setStartPage(i);
//                pdfTextStripper.setEndPage(i);
//                pages = pdfTextStripper.getText(document);
//                results = pages.split("\\s+");
//                for (int j =0; j<results.length; j++){
//                    extractIdentifiers.extractManufacture(results);
//                }
//            }
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//    }


}
