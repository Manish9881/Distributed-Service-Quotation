package service.girlsallowed;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import service.core.ClientInfo;
import service.core.Quotation;


@RestController
public class QuotationController implements CommandLineRunner {
    @Value("${server.port}")
    private int port;

    private static final String BROKER_URL = "http://broker:8083/services";
    private Map<String, Quotation> quotations = new TreeMap<>();
    private GAQService service = new GAQService();

    @Override
    public void run(String... args) {
        System.out.println("Starting Quotation Controller in Girlsallowed");
        registerService();
    }

    private void registerService() {
        RestTemplate restTemplate = new RestTemplate();
        String serviceUrl = "http://girlsallowed:" + port + "/quotations";
        System.out.println("Girlsallowed::Attempting to register service with broker at URL: " + BROKER_URL);
        System.out.println("Girlsallowed::Service URL being registered: " + serviceUrl);

        try {
            // Directly attempt to register service without health check
            ResponseEntity<String> response = restTemplate.postForEntity(BROKER_URL, serviceUrl, String.class);
            System.out.println("Girlsallowed::Successfully registered service URL with broker: " + serviceUrl);
            System.out.println("Girlsallowed::Broker response status: " + response.getStatusCode());
        } catch (Exception e) {
            System.err.println("Failed to register service with broker: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/quotations/{id}", produces = {"application/json"})
    public ResponseEntity<Quotation> getQuotation(@PathVariable String id) {
        Quotation quotation = quotations.get(id);
        if (quotation == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(quotation);
    }

    @PostMapping(value = "/quotations", consumes = "application/json")
    public ResponseEntity<Quotation> createQuotation(@RequestBody ClientInfo info) {
        Quotation quotation = service.generateQuotation(info);
        quotations.put(quotation.reference, quotation);
        String url = "http://" + getHost() + "/quotations/" + quotation.reference;
        return ResponseEntity.status(HttpStatus.CREATED).header("Location", url).header("Content-Location", url).body(quotation);
    }

    private String getHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress() + ":" + port;
        } catch (UnknownHostException e) {
            return "localhost:" + port;
        }
    }
}
