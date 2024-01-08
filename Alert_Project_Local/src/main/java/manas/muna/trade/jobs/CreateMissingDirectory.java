package manas.muna.trade.jobs;

import manas.muna.trade.constants.CandleTypes;

import java.io.File;
import java.util.List;

public class CreateMissingDirectory {

    public static void createDirectory(List<String> directoryNames, String path){

        for (String name: directoryNames){
            String directory = path+"\\"+name;
            File file = new File(directory);
            if (!file.exists()){
                file.mkdir();
            }
        }
    }

    public static void main(String[] args) {
        String path = "D:\\share-market\\GIT-PUSH\\Alert_Project_Local\\src\\main\\resources\\candle_stocks";
        createDirectory(CandleTypes.getAllConstantNames(), path);
    }
}
