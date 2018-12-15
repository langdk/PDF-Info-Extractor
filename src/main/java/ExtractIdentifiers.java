import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.nibor.autolink.LinkExtractor;
import org.nibor.autolink.LinkSpan;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExtractIdentifiers {

    private final String webPattern = "(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[A-Za-z0-9]+([\\-|−\\.]{1}[A-Za-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?(.|,)?";
    private final String manufacturePattern = "\\W*((?i)manufacture[r]?[s]?(?-i))\\W*";
    private final String preferPattern = "\\W*((?i)preferred|prefer[r]?[s]?(?-i))\\W*";
    private final String phonePattern = "\\([0-9]{3}\\)";
    private final String singlePhoneLine = "^\\s*(\\()?(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?(\\W+)?\\s*$";
    private final String emailPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}(\\W*)?";

    //Test pattern matches
    private final String phonePatternTest = "\\s*(?:\\+?(\\d{1,3}))?[-|− (]*(\\d{3})[-|−. )]*(\\d{3})[-|−. ]*(\\d{4})(?: *x(\\d+))?\\s*";
    public final String emailPatternTest = "[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}(\\W*)";

    private List<String> phoneNumbers = new ArrayList<>();
    private List<String> emailAddress = new ArrayList<>();
    public ObservableList<String> data = FXCollections.observableArrayList();

     void test(String info) {

//         List<String> test = new ArrayList<>();
//         LinkExtractor linkExtractor = LinkExtractor.builder().build();
//         Iterable<LinkSpan> links = linkExtractor.extractLinks(info);
//         LinkSpan link = links.iterator().next();
//         test.add(links.toString());
//         link.getType();
//         link.getBeginIndex();
//         link.getEndIndex();
//         info.substring(link.getBeginIndex(), link.getEndIndex());

//         for (LinkSpan linkTest: links){
//             System.out.println(MessageFormat.format("{0} : {1}", link.getType(), info.substring(link.getBeginIndex(), link.getEndIndex())));
//         }

     }






    List<String> extractPhoneTest(String info, List<String> infoList, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(info);
        while (matcher.find()){
            infoList.add(matcher.group().replaceAll("\\s",""));
        }
        return eliminateDuplicates(infoList);
    }

    List<String> extractTest(String info, String regex, List<String> data){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(info);
        while(matcher.find()){
            data.add(matcher.group());
        }
       return eliminateDuplicates(data);
    }

    void printPhone(){
        for (String x:eliminateDuplicates(eliminateWhiteSpace(phoneNumbers))) {
            System.out.println(x);
        }
    }

    List<String> eliminateWhiteSpace(List<String> infoList){
        List<String> newList = new ArrayList<>();
        for (String x : infoList){
            newList.add(x.replaceAll("\\s",""));
        }
        return newList;
    }

    List<String> eliminateDuplicates(List<String> infoList){
        return infoList.stream().distinct().collect(Collectors.toList());
    }
    void extractEmail(String[] info){
        for (int i=0; i<info.length; i++){
            if (info[i].matches(emailPattern)){
                System.out.println("Result: " +info[i]);
            }
        }
    }
     void extractWebsite(String[] info){
        for(int i=0;i<info.length;i++){
            if (info[i].matches(webPattern)){
                System.out.println("Result: " +info[i]);
            }
        }
    }

     void extractManufacture(String[] info){
        for(int i=0;i<info.length;i++){
            if (info[i].matches(manufacturePattern)){
                System.out.println("Result: " +info[i]);
            }
        }
    }

     void extractPrefer(String[] info){
        for(int i=0;i<info.length;i++){
            if (info[i].matches(preferPattern)){
                System.out.println("Result: " +info[i]);
            }
        }
    }

     private void extractPhone(String[] info){
        for (int i =0; i<info.length; i++){
            if (info[i].matches(phonePattern)){
                System.out.println("Result: " + info[i] + " " + info[i+1]);
            }
        }
    }

     private void extractPhoneSingleLine(String[] info){
        for (int i =0; i<info.length; i++){
            if (info[i].matches(singlePhoneLine)){
                System.out.println("Result: " + info[i]);
            }
        }
    }

    void extractPhoneInformation(String[] info){
         extractPhone(info);
         extractPhoneSingleLine(info);
    }


}
