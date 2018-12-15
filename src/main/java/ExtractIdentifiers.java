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



    private final String phonePattern = "\\s*(?:\\+?(\\d{1,3}))?[-|− (]*(\\d{3})[-|−. )]*(\\d{3})[-|−. ]*(\\d{4})(?: *x(\\d+))?\\s*";


    private List<String> phoneNumbers = new ArrayList<>();

    List<String> extractPhoneNumbers(String info, List<String> infoList){
        Pattern pattern = Pattern.compile(phonePattern);
        Matcher matcher = pattern.matcher(info);
        while (matcher.find()){
            infoList.add(matcher.group().replaceAll("\\s",""));
        }
        return eliminateDuplicates(infoList);
    }

    List<String> eliminateDuplicates(List<String> infoList){
        return infoList.stream().distinct().collect(Collectors.toList());
    }





}
