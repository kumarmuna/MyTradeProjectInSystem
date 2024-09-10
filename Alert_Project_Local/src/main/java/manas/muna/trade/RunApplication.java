package manas.muna.trade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RunApplication {
    public static void main(String[] args){
        SpringApplication.run(RunApplication.class, args);}
}
