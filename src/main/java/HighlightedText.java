import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class HighlightedText  extends RecursiveTreeObject<HighlightedText> {

    public SimpleStringProperty text, page;


    public HighlightedText(String pageC, String textC) {
        this.page = new SimpleStringProperty(pageC);
        this.text = new SimpleStringProperty(textC);
    }


}
