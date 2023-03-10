package cl.corona.integrationgroupg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IntegrationGroupGApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntegrationGroupGApplication.class, args);
	}

}
