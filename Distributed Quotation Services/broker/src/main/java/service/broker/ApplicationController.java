package service.broker;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import service.core.Application;
import service.core.ClientInfo;
import service.core.Quotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
public class ApplicationController{

    private Map<Integer, Application> applications = new TreeMap<>();   //treemap used to maintain the order of client callouts
    private RestTemplate restTemplate = new RestTemplate();
    private List<String> registeredServices = new ArrayList<>();
    @Value("${server.port}")
    private int port;

    @PostMapping(value = "/applications", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Application> createApplication(@RequestBody ClientInfo info) {
        Application application = new Application(info);
        //String[] QUOTATION_SERVICES = new String[3];
        //String brokerEnv = System.getenv("BROKER_ENV");

        /*if (brokerEnv == null){
            QUOTATION_SERVICES = new String[]{
                    "http://localhost:8080/quotations",
                    "http://localhost:8081/quotations",
                    "http://localhost:8082/quotations"
            };
        }else{
            QUOTATION_SERVICES = new String[]{
                    "http://auldfellas:8080/quotations",
                    "http://dodgygeezers:8081/quotations",
                    "http://girlsallowed:8082/quotations"
            };
        }*/
        // Contact each quotation service and gather quotations
        for (String url : registeredServices) {
            try {
                ResponseEntity<Quotation> response = restTemplate.postForEntity(url, info, Quotation.class);
                if (response.getStatusCode() == HttpStatus.CREATED) {
                    Quotation quotation = response.getBody();
                    application.quotations.add(quotation);
                }
            } catch (ResourceAccessException e) {
                System.err.println("Failed to connect to service at " + url + ": " + e.getMessage());
                // Optionally, log the error or add some placeholder to indicate the service is unreachable
            }
        }

        applications.put(application.id, application);
        String location = "http://localhost:" + port + "/applications/" + application.id;
        return ResponseEntity.status(HttpStatus.CREATED).header("Location", location).body(application);
    }


    // Endpoint to retrieve all registered services
    @GetMapping("/services")
    public ResponseEntity<List<String>> getServices() {
        return ResponseEntity.ok(registeredServices);
    }

    // Endpoint to add a new service URL to the list
    @PostMapping("/services")
    public ResponseEntity<Void> registerService(@RequestBody String serviceUrl) {
        if (!registeredServices.contains(serviceUrl)) {
            registeredServices.add(serviceUrl);
            System.out.println("Service registered: " + serviceUrl);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/applications/{id}", produces = "application/json")
    public ResponseEntity<Application> getApplication(@PathVariable int id) {
        Application application = applications.get(id);
        if (application == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(application);
    }

    @GetMapping(value = "/applications", produces = "application/json")
    public ResponseEntity<ArrayList<Application>> getAllApplications() {
        return ResponseEntity.ok(new ArrayList<>(applications.values()));
    }
}

