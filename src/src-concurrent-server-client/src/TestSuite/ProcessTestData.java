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
    public static final String asymmetricRetoEncryptionIterativeTestFolder = "IterativeClientServerTests/AsymmetricRetoEncryption";

    public static final String symmetricRetoEncryptionIterativeTestFolder = "IterativeClientServerTests/SymmetricRetoEncryption";

    public static final String asymmetricRetoEncryptionConcurrentTestFolder = "ConcurrentClientServerTests/AsymmetricRetoEncryption";

    public static final String symmetricRetoEncryptionConcurrentTestFolder = "ConcurrentClientServerTests/SymmetricRetoEncryption";

    public static String currentAccumulatedReport = "";
    //----------------------------------------------------------------------
    // METHODS
    //----------------------------------------------------------------------


    //TODO: Documentar esto
    public static void writeToIndividualTestCsv(List<Long> timeList,boolean retoSymmetric) throws IOException {
        FileWriter csvWriter = generateFileName(retoSymmetric, symmetricRetoEncryptionIterativeTestFolder, asymmetricRetoEncryptionIterativeTestFolder);
        csvWriter.append(String.valueOf(timeList.get(timeList.size()-1))).append("\n");

        csvWriter.flush();
        csvWriter.close();
    }

    //TODO: Documentar esto
    public static void writeToAccumulatedReportCsvResult(Long timeElapsedRetoCypher,boolean retoSymmetric) throws IOException {
        FileWriter csvWriter = generateFileName(retoSymmetric, symmetricRetoEncryptionConcurrentTestFolder, asymmetricRetoEncryptionConcurrentTestFolder);
        csvWriter.append(String.valueOf(timeElapsedRetoCypher)).append("\n");

        csvWriter.flush();
        csvWriter.close();
    }

    private static FileWriter generateFileName(boolean retoSymmetric, String symmetricRetoEncryptionConcurrentTestFolder, String asymmetricRetoEncryptionConcurrentTestFolder) throws IOException {
        String directory = (retoSymmetric) ? symmetricRetoEncryptionConcurrentTestFolder : asymmetricRetoEncryptionConcurrentTestFolder;

        if(currentAccumulatedReport.equals("")){
            LocalDateTime currentTime = LocalDateTime.now();
            currentAccumulatedReport = String.format("%s/%s-%s%s%s%s%s.csv",directory,"AccumulatedReport",currentTime.getNano(),currentTime.getSecond(),currentTime.getMinute(), currentTime.getHour(),currentTime.getDayOfMonth());
        }

        File file = new File(currentAccumulatedReport);
        FileWriter csvWriter = new FileWriter(file,true);
        return csvWriter;
    }


}
