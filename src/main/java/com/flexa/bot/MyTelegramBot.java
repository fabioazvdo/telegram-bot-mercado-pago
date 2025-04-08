package com.flexa.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    private final PagBankService pagBankService;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(@org.jetbrains.annotations.NotNull org.telegram.telegrambots.meta.api.objects.Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();

            if ("/start".equals(text)) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Olá! Clique abaixo para comprar acesso vitalício ao grupo:");

                InlineKeyboardButton button = new InlineKeyboardButton("Comprar Acesso - R$10");
                button.setCallbackData("buy_access");

                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                markup.setKeyboard(Arrays.asList(Arrays.asList(button)));

                message.setReplyMarkup(markup);

                try {
                    execute(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (update.hasCallbackQuery()) {
            String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
            String userId = update.getCallbackQuery().getFrom().getId().toString();

            if ("buy_access".equals(update.getCallbackQuery().getData())) {
                String paymentText = pagBankService.criarPagamento(userId);
                SendMessage message = new SendMessage(chatId, paymentText);
                try {
                    execute(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}