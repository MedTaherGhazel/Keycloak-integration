package com.example.inventoryservice;

import com.example.inventoryservice.model.Product;
import com.example.inventoryservice.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Projection(name = "fullProduct",types = Product.class)
interface ProductProjection extends Projection {
    public Long getId();
    public String getName ();
    public Double getPrice ();
}
@EnableWebSecurity
@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
    @Bean
    CommandLineRunner start(ProductRepository productRepository, RepositoryRestConfiguration restConfig){
    return args->{
        restConfig.exposeIdsFor(Product.class);
        productRepository.save(new Product(null,"Computer Desk Top HP",900,5));
        productRepository.save(new Product(null,"Printer Epson",80,6));
        productRepository.save(new Product(null,"MacBook Pro Lap Top",1800,0));
        productRepository.findAll().forEach(System.out::println);};
}

}
