package kz.kaznu.antiplagiarism;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AntiPlagiarismApplication {

    public static void main(String[] args) {
        SpringApplication.run(AntiPlagiarismApplication.class, args);
    }

    // TODO:
//    use msa patterns
//    executorService
//    ascending-map-urls-resultDto [percentage, url]
}
