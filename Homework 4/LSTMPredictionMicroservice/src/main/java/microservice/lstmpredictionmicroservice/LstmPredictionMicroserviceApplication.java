package microservice.lstmpredictionmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LstmPredictionMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LstmPredictionMicroserviceApplication.class, args);
        System.out.println("LSTM MICROSERVICE: STATUS 200 OK");
    }

}
