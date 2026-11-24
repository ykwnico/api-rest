package br.com.cshub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Classe principal: inicia o servidor embutido (Tomcat) na porta configurada
@SpringBootApplication
public class CsHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(CsHubApplication.class, args);
    }
}
