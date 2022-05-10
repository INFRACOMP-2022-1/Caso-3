package IterativeTestSuite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

//TODO: Documentar esto
public class ProcessTestData {

    //----------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------

    //TODO: Documentar esto
    public static final String asymmetricRetoEncryptionIndividualTestFolder = "IterativeClientServerTests/AsymmetricRetoEncryption";

    public static final String symmetricRetoEncryptionIndividualTestFolder = "IterativeClientServerTests/SymmetricRetoEncryption";

    public static final String asymmetricRetoEncryptionAccumulatedTestsFolder = "IterativeClientServerTests/AsymmetricRetoEncryption/AccumulatedReport";

    public static final String symmetricRetoEncryptionAccumulatedTestsFolder = "IterativeClientServerTests/SymmetricRetoEncryption/AccumulatedReport";


    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------


    //TODO: Documentar esto
    public void writeToIndividualTestCsv(List<Long> timeList,boolean retoSymmetric) throws IOException {
        String directory = (retoSymmetric)? symmetricRetoEncryptionIndividualTestFolder : asymmetricRetoEncryptionIndividualTestFolder;

        LocalDateTime currentTime = LocalDateTime.now();
        File file = new File(String.format("%s/%s-%d%d%d%d%d.csv",(retoSymmetric)?"symmetric":"asymmetric",currentTime.getSecond(),currentTime.getMinute(), currentTime.getHour(),currentTime.getDayOfMonth()));

        FileWriter csvWriter = new FileWriter(file);
        for(Long time : timeList){
            csvWriter.append(String.valueOf(time)).append("\n");
        }

        csvWriter.flush();
        csvWriter.close();
    }

    //TODO: Documentar esto
    public void writeToAccumulatedTestCsv(List<Long> timeList,boolean retoSymmetric) throws IOException {
        String file = (retoSymmetric)?symmetricRetoEncryptionAccumulatedTestsFolder:asymmetricRetoEncryptionAccumulatedTestsFolder;

        FileWriter csvWriter = new FileWriter(file,true);

        for(Long time : timeList){
            csvWriter.append(String.valueOf(time)).append("\n");
        }

        csvWriter.flush();
        csvWriter.close();
    }

}
