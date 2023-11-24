package com.example.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
    @Bean
    DiscoveryClientRouteDefinitionLocator dynamicRoutes(ReactiveDiscoveryClient rdc, DiscoveryLocatorProperties dlp ) {
        return new DiscoveryClientRouteDefinitionLocator(rdc,dlp);
    }

                            /*****************Route Filter*****************/
    @Bean
    RouteLocator gatewayRoutes(RouteLocatorBuilder builder){
        return builder.routes()
                .route("id",r->r.path("/restcountries/**")
                        .filters(f->f
                                .addRequestHeader("X-RapidAPI-Host","rest-countries10.p.rapidapi.com")
                                .addRequestHeader("X-RapidAPI-Key", "d99492ca7fmshf3c19799f1f0c2dp10a1dejsna4c1f6cad1f4")
                                .rewritePath("/restcountries/(?<segment>.*)","/${segment}"))
                        .uri("https://rest-countries10.p.rapidapi.com/countries"))

                .route(r->r.path("/muslimsalat/**")
                .filters(f->f
                        .addRequestHeader("x-rapidapi-host","muslimsalat.p.rapidapi.com")
                        .addRequestHeader("x-rapidapi-key", "6b06808925msh5c3d3694ac98647p1a1741jsnf273ee2671bf")
                                .rewritePath("/muslimsalat/(?<segment>.*)","/${segment}"))
                        .uri("https://muslimsalat.p.rapidapi.com"))
                .build();
    }

    /*****************Circuit Breaker avec Resilience4j*****************/
//    @Bean
//    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route("rest-countries", r -> r.path("/restcountries/**")
//                        .filters(f -> f
//                                .addRequestHeader("x-rapidapi-host", "restcountries-v1.p.rapidapi.com")
//                                .addRequestHeader("x-rapidapi-key", "fe5e774996msh4eb6e863d457420p1d2ffbjsnee0617ac5078")
//                                .rewritePath("/restcountries/(?<segment>.*)", "/${segment}")
//                                .circuitBreaker(c -> c.setName("rest-countries")
//                                        .setFallbackUri("forward:/restCountriesFallback"))
//                        )
//                        .uri("https://restcountries-v1.p.rapidapi.com")
//                )
//                .route("muslimsalat", r -> r.path("/muslimsalat/**")
//                        .filters(f -> f
//                                .addRequestHeader("x-rapidapi-host", "muslimsalat.p.rapidapi.com")
//                                .addRequestHeader("x-rapidapi-key", "fe5e774996msh4eb6e863d457420p1d2ffbjsnee0617ac5078")
//                                .rewritePath("/muslimsalat/(?<segment>.*)", "/${segment}")
//                                .circuitBreaker(c -> c.setName("muslimsalat")
//                                        .setFallbackUri("forward:/muslimsalatFallback"))
//                        )
//                        .uri("https://muslimsalat.p.rapidapi.com")
//                )
//                .build();
//    }

    /*****************Circuit Breaker avec Resilience4j*****************/
    @RestController
    class FallBackRestController {

        @GetMapping("/restCountriesFallback")
        public Map<String, String> restCountriesFallback() {
            Map<String, String> map = new HashMap<>();
            map.put("message", "Default Rest Countries Fallback service");
            map.put("countries", "Algeria, Tunisia");
            return map;
        }

        @GetMapping("/muslimsalatFallback")
        public Map<String, String> muslimsalatFallback() {
            Map<String, String> map = new HashMap<>();
            map.put("message", "Default Muslim Fallback service");
            map.put("Fajr", "07:00");
            map.put("Dhuhr", "14:00");
            return map;
        }
    }
}
