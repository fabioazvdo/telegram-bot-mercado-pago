package com.flexa.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class PagBankService {

    @Value("${pagbank.public_key}")
    private String publicKey;

    public String criarPagamento(String userId) {
        String url = "https://sandbox.api.pagseguro.com/orders";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(publicKey);

        Map<String, Object> body = new HashMap<>();
        body.put("reference_id", userId);

        Map<String, Object> customer = new HashMap<>();
        customer.put("name", "Cliente Telegram");
        customer.put("email", "cliente@email.com");
        body.put("customer", customer);

        Map<String, Object> item = new HashMap<>();
        item.put("name", "Acesso Grupo");
        item.put("quantity", 1);
        item.put("unit_amount", 1000);
        body.put("items", List.of(item));

        Map<String, Object> paymentMethod = new HashMap<>();
        paymentMethod.put("type", "PIX");
        Map<String, Object> charge = new HashMap<>();
        charge.put("payment_method", paymentMethod);
        body.put("charges", List.of(charge));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            Map<String, Object> charges = ((Map<String, Object>) ((List<?>) response.getBody().get("charges")).get(0));
            Map<String, Object> pix = (Map<String, Object>) charges.get("payment_method");
            Map<String, Object> qrCode = (Map<String, Object>) pix.get("qr_code");
            return "PIX gerado. Copie o código abaixo:\n\n" + qrCode.get("emv");
        } else {
            return "Erro ao gerar pagamento.";
        }
    }
}