public class ExtractIdentifiers {

    private final String webPattern = "^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[A-Za-z0-9]+([\\-|−\\.]{1}[A-Za-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?(.|,)?$$";
    private final String manufacturePattern = "\\W*((?i)manufacture[r]?[s]?(?-i))\\W*";
    private final String preferPattern = "\\W*((?i)preferred|prefer[r]?[s]?(?-i))\\W*";
    private final String phonePattern = "\\([0-9]{3}\\)";
    private final String singlePhoneLine = "^\\s*(\\()?(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})(?: *x(\\d+))?(\\W+)?\\s*$";
    private final String emailPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}(\\W*)?$";


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
