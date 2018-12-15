import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class MainInformationItems extends RecursiveTreeObject<MainInformationItems> {


    public SimpleStringProperty email, phone, website;

    public MainInformationItems(String emailC, String phoneC, String websiteC) {
        this.email = new SimpleStringProperty(emailC);
        this.phone = new SimpleStringProperty(phoneC);
        this.website = new SimpleStringProperty(websiteC);
    }


}
