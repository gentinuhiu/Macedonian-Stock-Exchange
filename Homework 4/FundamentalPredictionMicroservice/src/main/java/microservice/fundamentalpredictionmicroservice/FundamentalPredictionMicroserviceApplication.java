package microservice.fundamentalpredictionmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FundamentalPredictionMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FundamentalPredictionMicroserviceApplication.class, args);
        System.out.println("FUNDAMENTAL MICROSERVICE: STATUS 200 OK");
    }
}
