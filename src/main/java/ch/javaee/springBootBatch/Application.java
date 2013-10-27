package ch.javaee.springBootBatch;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

public class Application {
    public static void main(String args[]) {
        ApplicationContext ctx = SpringApplication.run(BatchConfiguration.class, args);
    }
}
