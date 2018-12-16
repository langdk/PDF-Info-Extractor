import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
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
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
    JFXTreeTableView keyInfoTable, highlightTable;

    @FXML
    JFXTextField input;

    @FXML
    JFXButton extractButton;

    @FXML
    MenuItem exportMainButton, exportHighlightButton, customSearchButton;

    private File selectedFile;
    private final int TABLE_COLUMN_SIZE = 330;

    private ExtractIdentifiers extractIdentifiers = new ExtractIdentifiers();
    private Helper helper = new Helper();
    private List<String> emailAddress = new ArrayList<>();
    private List<String> websites = new ArrayList<>();
    private List<String> phoneNumbers = new ArrayList<>();
    private List<String> pageNum = new ArrayList<>();
    private List<String> content = new ArrayList<>();
    private ObservableList<MainInformationItems> mainData = FXCollections.observableArrayList();
    private ObservableList<HighlightedText> highlightData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        JFXTreeTableColumn<HighlightedText, String> page = new JFXTreeTableColumn<>("Page");
        page.setPrefWidth(150);
        page.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HighlightedText, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HighlightedText, String> param) {
                return param.getValue().getValue().page;
            }
        });
        JFXTreeTableColumn<HighlightedText, String> text = new JFXTreeTableColumn<>("Text");
        text.setPrefWidth(850);
        text.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<HighlightedText, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<HighlightedText, String> param) {
                return param.getValue().getValue().text;
            }
        });

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
        final TreeItem<HighlightedText> root1 = new RecursiveTreeItem<>(highlightData, RecursiveTreeObject::getChildren);
        final TreeItem<MainInformationItems> root = new RecursiveTreeItem<MainInformationItems>(mainData, RecursiveTreeObject::getChildren);
        highlightTable.getColumns().setAll(page, text);
        highlightTable.setRoot(root1);
        highlightTable.setShowRoot(false);
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

        input.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                highlightTable.setPredicate(new Predicate<TreeItem<HighlightedText>>() {
                    @Override
                    public boolean test(TreeItem<HighlightedText> treeItem) {
                        Boolean flag = treeItem.getValue().page.getValue().contains(newValue)
                                || treeItem.getValue().text.getValue().contains(newValue);
                        return flag;
                    }
                });
            }
        });
    }

    /**
     * Clears all entres
     */
    private void clearAll(){
        emailAddress.clear();
        websites.clear();
        phoneNumbers.clear();
        pageNum.clear();
        content.clear();
        mainData.clear();
        highlightData.clear();
    }

    /**
     * Allows user to pick a file
     */
    public void fileChooser(){
        clearAll();
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
            return;
        }
        extractHelper();
        extractButton.setDisable(false);

    }

    /**
     * Helper method to set button access after a file is loaded
     */
    private void setButtonsHelper(){
        exportHighlightButton.setDisable(false);
        exportMainButton.setDisable(false);
        customSearchButton.setDisable(false);
    }


    /**
     * Helper method to extract phone, web, and email.
     */
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

    /**
     * Extracts information and populates table
     */
    public void extract() {
        extractHighlighted();
        populateMainTable();
        setButtonsHelper();
    }


    /**
     * Populates the highlight information table
     */
    private void populateHighlightTable(){
        try {
            getHighlightedText();
        } catch (IOException e){
            e.printStackTrace();
        }
        for (int i = 0; i<pageNum.size(); i++){

            highlightData.add(new HighlightedText(pageNum.get(i), content.get(i)));
        }
        highlightTable.refresh();
    }

    /**
     * Extracts highlighted text from doc
     */
    private void extractHighlighted(){
        populateHighlightTable();
    }

    /**
     * Extracts highlighted text from document
     * @throws IOException
     */
    private void getHighlightedText() throws IOException {
        String[] test = {};
        List<String> lines = new ArrayList<>();
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
                    if(str!=null) {
                        test = str.split("\r?\n");
                        lines.addAll(Arrays.asList(test));
                        for (int m=0; m<lines.size(); m++){
                            pageNum.add(String.valueOf(l+1));
                            content.add(lines.get(m));
                        }
                    }
                    lines.clear();
                }
            }
        }
        document.close();
    }

    /**
     * Populates the main information table
     */
    private void populateMainTable(){
        for (int i =0; i<filler(); i++){
            mainData.add(new MainInformationItems(emailAddress.get(i), phoneNumbers.get(i), websites.get(i)));
        }
        keyInfoTable.refresh();
    }

    /**
     * Finds largest size of arraylist, and populates other arraylists will null value so tableView populates correctly.
     * @return Size of largest arraylist
     */
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

    /**
     * Compares three arraylists and returns largest.
     * @return Largest arraylist size
     */
    private int getLargest(){
        List <Integer> largest = new ArrayList<>();
        largest.add(emailAddress.size());
        largest.add(websites.size());
        largest.add(phoneNumbers.size());

        return Collections.max(largest);
    }

    /**
     * Closes stage
     */
    public void close(){
        Platform.exit();
    }

    /**
     * Saves to Excel sheet
     */
    public void saveAs() {
        String[] columns = {"Phone Number", "E-mail Address", "Website"}; //Number of columns in tableview
        int rowNum = 1;
        Workbook workbook = new XSSFWorkbook();

        CreationHelper createHelper = workbook.getCreationHelper();

        Sheet sheet = workbook.createSheet("Main Information");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        for (int i =0; i<columns.length; i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        for (MainInformationItems items : mainData){
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(items.phone.getValue());
            row.createCell(1).setCellValue(items.email.getValue());
            row.createCell(2).setCellValue(items.website.get());
        }
        for (int i=0; i<columns.length; i++){
            sheet.autoSizeColumn(i);
        }
        try {
            FileOutputStream fileOut = new FileOutputStream("main-highlights.xlsx");
            workbook.write(fileOut);
            fileOut.close();

            // Closing the workbook
            workbook.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        helper.slidingAlert("Success", "File created successfully!");
    }

    /**
     * Saves as excel sheet
     */
    public void saveHighlighted(){
        String[] columns = {"Pages", "Highlighted Text"}; //Number of columns in tableview
        int rowNum = 1;
        Workbook workbook = new XSSFWorkbook();

        CreationHelper createHelper = workbook.getCreationHelper();

        Sheet sheet = workbook.createSheet("Highlighted Information");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        Row headerRow = sheet.createRow(0);
        for (int i =0; i<columns.length; i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        for (HighlightedText items : highlightData){
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(String.valueOf(items.page.getValue()));
            row.createCell(1).setCellValue(items.text.getValue());
        }
        for (int i=0; i<columns.length; i++){
            sheet.autoSizeColumn(i);
        }
        try {
            FileOutputStream fileOut = new FileOutputStream("text-highlights.xlsx");
            workbook.write(fileOut);
            fileOut.close();

            // Closing the workbook
            workbook.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        helper.slidingAlert("Success", "File created successfully!");

    }

    /**
     * Resets all the fields
     */
    public void reset(){
        clearAll();
        keyInfoTable.refresh();
        highlightTable.refresh();
    }


    public void customSearch(){

    }

    /**
     * Extracts website information
     * @param info The list passed in to search from
     * @return A list of websites
     */
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

    /**
     * Extracts email information
     * @param info The list passed in to search from
     * @return A list of emails
     */
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
}
