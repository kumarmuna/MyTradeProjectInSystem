package manas.muna.trade.jobs;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RunBackUpJob {
    public static void main(String[] args) throws Exception{
        backupEmaData();
    }

    public static void backupEmaData() throws Exception{
        writeDateToFile("D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\back-up-data\\backup_run_date.txt");
        String readFileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data";
        String storeFileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\back-up-data\\calculated-system-data";
        String storeFileSubLocation = "";
        try {
            List<String> files = Files.list(Paths.get(readFileLocation))
                    .map(path -> path.getFileName().toFile().getName()).collect(Collectors.toList());
            for (String file : files){
                storeFileSubLocation = getSubLocation(file);
                String destinationStoreFileLocation = storeFileLocation+"\\"+storeFileSubLocation;
                FileReader filereader = new FileReader(readFileLocation+"\\"+file);
                CSVReader csvReader = new CSVReaderBuilder(filereader).build();
                List<String[]> allData = csvReader.readAll();

                File fl = new File(destinationStoreFileLocation+"\\"+file);
                if (!fl.exists()){
                    fl.createNewFile();
                }
                FileWriter outputfile = new FileWriter(destinationStoreFileLocation+"\\"+file, false);
                CSVWriter writer = new CSVWriter(outputfile);
                for (String[] dt : allData){
                    writer.writeNext(dt);
                }
                writer.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void writeDateToFile(String path) throws Exception{
        FileWriter fw = new FileWriter(path);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("Backup Job Run Date :"+new Date());
        bw.flush();
        bw.close();
    }

    private static String getSubLocation(String file) {
        switch (file.charAt(0)) {
            case 'a':
            case 'A':
                return "a-stocks";
            case 'b':
            case 'B':
                return "b-stocks";
            case 'c':
            case 'C':
                return "c-stocks";
            case 'd':
            case 'D':
                return "d-stocks";
            case 'e':
            case 'E':
                return "e-stocks";
            case 'f':
            case 'F':
                return "f-stocks";
            default:
                return "";
        }
    }
}
