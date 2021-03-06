package TestSuite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

//TODO: Documentar esto
public class ProcessTestData {

    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

    //TODO: Documentar esto
    public static final String asymmetricRetoEncryptionIndividualTestFolder = "IterativeClientServerTests/AsymmetricRetoEncryption";

    public static final String symmetricRetoEncryptionIndividualTestFolder = "IterativeClientServerTests/SymmetricRetoEncryption";


    public static String currentAccumulatedReport = "";
    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------


    //TODO: Documentar esto
    public static void writeToIndividualTestCsv(List<Long> timeList,boolean retoSymmetric) throws IOException {

        String directory = (retoSymmetric)? symmetricRetoEncryptionIndividualTestFolder : asymmetricRetoEncryptionIndividualTestFolder;

        if(currentAccumulatedReport.equals("")){
            LocalDateTime currentTime = LocalDateTime.now();
            currentAccumulatedReport = String.format("%s/%s-%s%s%s%s%s.csv",directory,"AccumulatedReport",currentTime.getNano(),currentTime.getSecond(),currentTime.getMinute(), currentTime.getHour(),currentTime.getDayOfMonth());
        }

        File file = new File(currentAccumulatedReport);

        FileWriter csvWriter = new FileWriter(file,true);
        csvWriter.append(String.valueOf(timeList.get(timeList.size()-1))).append("\n");

        csvWriter.flush();
        csvWriter.close();
    }


}
