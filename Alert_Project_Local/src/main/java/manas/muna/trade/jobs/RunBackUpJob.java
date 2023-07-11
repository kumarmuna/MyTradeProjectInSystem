package manas.muna.trade.jobs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class RunBackUpJob {
    public static void main(String[] args) {
        backupEmaData();
    }

    public static void backupEmaData() {
        String readFileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\history_ema_data";
        String storeFileLocation = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\back-up-data\\calculated-system-data";
        String storeFileSubLocation = "";
        try {
            List<String> files = Files.list(Paths.get(readFileLocation))
                    .map(path -> path.getFileName().toFile().getName()).collect(Collectors.toList());
            for (String file : files){
                storeFileSubLocation = getSubLocation(file);
                String destinationStoreFileLocation = storeFileLocation+"\\"+storeFileSubLocation;
                String fileDataInString = "";
                FileReader fr = new FileReader(readFileLocation+"\\"+file);
                BufferedReader br = new BufferedReader(fr);
                int data;
                while((data = br.read())!=-1){
                    fileDataInString = fileDataInString+ data;
                }
                FileWriter fw = new FileWriter(destinationStoreFileLocation+"\\"+file);
                BufferedWriter bw = new BufferedWriter(fw);
                for(String str: fileDataInString.split(" ")){
                    bw.write(str);
                    bw.write(" ");
                }
                bw.flush();
                fr.close();
                fw.close();
                br.close();
                bw.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String getSubLocation(String file) {
        switch (file.charAt(0)) {
            case 'a':
            case 'A':
                return "a-stocks";
            case 'b':
            case 'B':
                return "b-stocks";
            default:
                return "";
        }
    }
}
