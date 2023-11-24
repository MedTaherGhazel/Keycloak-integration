package com.example.billingservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Date;
import java.util.List;
@Data
class Product{
    private Long id;
    private String name;
    private double price;
}
@Data
class Customer {
    private Long id;
    private String name;
    private String email;
}

@Entity @Data @NoArgsConstructor @AllArgsConstructor
class Bill{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date billingDate;
    @Transient
    @OneToMany(mappedBy = "bill")
    private Collection<ProductItem> productItems;
    @Transient
    private Customer customer;
    private long customerID;
}

@Entity @Data @NoArgsConstructor @AllArgsConstructor
class ProductItem{
    @Id @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long id;
    @Transient
    private Product product;
    private long productID;
    private double price;
    private double quantity;
    @ManyToOne
    @JsonProperty(access =JsonProperty.Access.WRITE_ONLY)
    private Bill bill;
}


@RepositoryRestResource
interface ProductItemRepository extends JpaRepository<ProductItem,Long>{
    List<ProductItem> findByBillId(Long billID );
}


@RepositoryRestResource
interface BillRepository extends JpaRepository<Bill,Long> {}

@FeignClient(name="customer-service")
interface CustomerServiceClient{
    @GetMapping("/customers/{id}?projection=fullCustomer")
    Customer findCustomerById(@PathVariable("id") Long id);
}

@FeignClient(name="inventory-service")
interface InventoryServiceClient{
    @GetMapping("/products/{id}?projection=fullProduct")
    Product findProductById(@PathVariable("id") Long id);
    @GetMapping("/products?projection=fullProduct")
    PagedModel<Product> findAll();
}

@RestController
class BillRestController{
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private ProductItemRepository productItemRepository;
    @Autowired
    private CustomerServiceClient customerServiceClient;
    @Autowired
    private InventoryServiceClient inventoryServiceClient;
    @GetMapping("/bills/full/{id}")
    Bill getBill(@PathVariable(name="id") Long id) {
        Bill bill=billRepository.findById(id).get();
        bill.setCustomer(customerServiceClient.findCustomerById(bill.getCustomerID()));
        bill.setProductItems(productItemRepository.findByBillId(id));
        bill.getProductItems().forEach(pi->{
            pi.setProduct(inventoryServiceClient.findProductById(pi.getProductID()));
        });
        return bill; }
}
@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

    public static void main(String[] args) {SpringApplication.run(BillingServiceApplication.class, args);}

        @Bean
        CommandLineRunner start(BillRepository billRepository,
                                ProductItemRepository productItemRepository,
                                InventoryServiceClient inventoryServiceClient,
                                CustomerServiceClient customerServiceClient ){
            return args ->{
                    Bill bill=new Bill();
                    bill.setBillingDate(new Date());
                    Customer customer=customerServiceClient.findCustomerById(1L );
                    bill.setCustomerID(customer.getId());
                    billRepository.save(bill);
                    inventoryServiceClient.findAll().getContent().forEach(p ->{
                        productItemRepository.save(new ProductItem(null,null,p.getId(),p.getPrice(),(int)(1+Math.random()*1000),bill ));});

            };
    }
}