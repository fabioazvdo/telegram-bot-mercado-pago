package com.flexa.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@RestController
@RequestMapping("/pagbank/webhook")
public class PaymentWebhookController {

    @Value("${telegram.group.link}")
    private String groupLink;

    private final MyTelegramBot bot;

    public PaymentWebhookController(MyTelegramBot bot) {
        this.bot = bot;
    }

    @PostMapping
    public void receberNotificacao(@RequestBody Map<String, Object> payload) {
        try {
            String status = (String) ((Map<String, Object>) payload.get("status")).get("code");
            String userId = (String) payload.get("reference_id");

            if ("PAID".equalsIgnoreCase(status)) {
                bot.execute(new org.telegram.telegrambots.meta.api.methods.send.SendMessage(userId, "Pagamento confirmado! Aqui está o link do grupo: " + groupLink));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}