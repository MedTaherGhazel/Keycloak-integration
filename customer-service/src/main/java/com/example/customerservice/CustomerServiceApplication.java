package com.example.customerservice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.config.Projection;

@Entity @Data @NoArgsConstructor @AllArgsConstructor @ToString
class Customer{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
}
@RepositoryRestResource
interface CustomerRepository extends JpaRepository<Customer,Long> { }

@Projection(name = "fullCustomer",types = Customer.class)
interface CustomerProjection extends Projection {
    public Long getId();
    public String getName ();
    public String getEmail ();
}
@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {SpringApplication.run(CustomerServiceApplication.class, args ) ;}
    @Bean
    CommandLineRunner start(CustomerRepository customerRepository){
        return args ->{
            customerRepository.save(new Customer(null,"eni","contact@eni.tn"));
            customerRepository.save(new Customer(null,"FST","contact@fst.tn"));
            customerRepository.save(new Customer(null,"ENSI","contact@ensi.tn"));
            customerRepository.findAll().forEach(System.out::println);
        };
    }
}


