import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class MainController extends Application {


    @FXML
    JFXTextField pdfPath;

    File selectedFile;

    ExtractIdentifiers extractIdentifiers = new ExtractIdentifiers();

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    public void fileChooser(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open PDF");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("pdf", "*.pdf")
        );
        selectedFile = fileChooser.showOpenDialog(null);
        pdfPath.setText(selectedFile.toString());
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
            extractIdentifiers.extractWebsite(results);
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
