import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.transitions.hamburger.HamburgerSlideCloseTransition;
import javafx.application.Platform;
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
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;
import org.nibor.autolink.LinkType;

import java.awt.geom.Rectangle2D;
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
    List<String> emailAddress = new ArrayList<>();
    List<String> websites = new ArrayList<>();
    List<String> phoneNumbers = new ArrayList<>();
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
            document.close();
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




    public void close(){
        Platform.exit();
    }

    public void saveAs(){

    }

    public void reset(){
        data.clear();
        keyInfoTable.refresh();
    }

    public void extractHighlighted(){
        List<String> highlightedText = new ArrayList<>();

        try {
            //for(int i =0; i<getHighlightedText().size(); i++){
                System.out.println(getHighlightedText().entries());
            //}
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public Multimap<Integer, String> getHighlightedText() throws IOException {
        ArrayList<String> highlightedTexts = new ArrayList<>();
        //MultiMap<Integer, String> highlights = new HashMap<>();
        Multimap<Integer, String> highlights = ArrayListMultimap.create();


        // this is the in-memory representation of the PDF document.
        // this will load a document from a file.
        PDDocument document = PDDocument.load(selectedFile);
        // this represents all pages in a PDF document.
        List<PDPage> allPages = new ArrayList<>();
        for(int i =0; i<document.getNumberOfPages();i++){
            allPages.add(document.getPage(i));
        }
        // this represents a single page in a PDF document.
        List<PDAnnotation> annotations;
        for(int l =0; l<document.getNumberOfPages();l++) {
            PDPage page = allPages.get(l);
            // get  annotation dictionaries
            annotations = page.getAnnotations();
            for (int i = 0; i < annotations.size(); i++) {
                // check subType
                if (annotations.get(i).getSubtype().equals("Highlight")) {
                    // extract highlighted text
                    PDFTextStripperByArea stripperByArea = new PDFTextStripperByArea();

                    COSArray quadsArray = (COSArray) annotations.get(i).getCOSObject().getDictionaryObject(COSName.getPDFName("QuadPoints"));
                    String str = null;

                    for (int j = 1, k = 0; j <= (quadsArray.size() / 8); j++) {

                        COSFloat ULX = (COSFloat) quadsArray.get(0 + k);
                        COSFloat ULY = (COSFloat) quadsArray.get(1 + k);
                        COSFloat URX = (COSFloat) quadsArray.get(2 + k);
                        COSFloat URY = (COSFloat) quadsArray.get(3 + k);
                        COSFloat LLX = (COSFloat) quadsArray.get(4 + k);
                        COSFloat LLY = (COSFloat) quadsArray.get(5 + k);
                        COSFloat LRX = (COSFloat) quadsArray.get(6 + k);
                        COSFloat LRY = (COSFloat) quadsArray.get(7 + k);

                        k += 8;

                        float ulx = ULX.floatValue() - 1;                           // upper left x.
                        float uly = ULY.floatValue();                               // upper left y.
                        float width = URX.floatValue() - LLX.floatValue();          // calculated by upperRightX - lowerLeftX.
                        float height = URY.floatValue() - LLY.floatValue();         // calculated by upperRightY - lowerLeftY.

                        PDRectangle pageSize = page.getMediaBox();
                        uly = pageSize.getHeight() - uly;

                        Rectangle2D.Float rectangle_2 = new Rectangle2D.Float(ulx, uly, width, height);
                        stripperByArea.addRegion("highlightedRegion", rectangle_2);
                        stripperByArea.extractRegions(page);
                        String highlightedText = stripperByArea.getTextForRegion("highlightedRegion");

                        if (j > 1) {
                            str = str.concat(highlightedText);
                        } else {
                            str = highlightedText;
                        }
                    }
                    //highlightedTexts.add(str);
                    highlights.put(l,str);
                }
            }
        }
        document.close();

        return highlights;
    }

    public void customSearch(){

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
