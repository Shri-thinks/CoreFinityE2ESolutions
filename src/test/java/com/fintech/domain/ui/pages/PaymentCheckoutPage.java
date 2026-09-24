package com.fintech.domain.ui.pages;

import com.fintech.framework.ui.BasePage;
import org.openqa.selenium.By;

public class PaymentCheckoutPage extends BasePage {

    private final By cardNumberInput = By.id("cardNumber");
    private final By cardExpiryInput = By.id("cardExpiry");
    private final By cardCvvInput = By.id("cardCvv");
    private final By payAmountInput = By.id("amount");
    private final By submitPaymentBtn = By.id("btn-pay-now");
    private final By authConfirmationBanner = By.id("auth-confirmation");

    public PaymentCheckoutPage loadCheckout() {
        logger.info("Loading Payment Checkout Page");
        navigateTo("data:text/html;charset=utf-8," +
                "<html><head><title>Checkout Gateway</title></head><body>" +
                "<h2>Payment Checkout</h2>" +
                "<input id='cardNumber' placeholder='Card Number'/>" +
                "<input id='cardExpiry' placeholder='MM/YY'/>" +
                "<input id='cardCvv' placeholder='CVV'/>" +
                "<input id='amount' placeholder='Amount'/>" +
                "<button id='btn-pay-now' onclick=\"document.getElementById('auth-confirmation').innerText='Payment Approved: AUTH-994422'\">Pay Now</button>" +
                "<div id='auth-confirmation'></div>" +
                "</body></html>");
        return this;
    }

    public PaymentCheckoutPage enterPaymentDetails(String pan, String expiry, String cvv, String amount) {
        enterText(cardNumberInput, pan);
        enterText(cardExpiryInput, expiry);
        enterText(cardCvvInput, cvv);
        enterText(payAmountInput, amount);
        return this;
    }

    public PaymentCheckoutPage submitPayment() {
        click(submitPaymentBtn);
        return this;
    }

    public String getConfirmationMessage() {
        return getElementText(authConfirmationBanner);
    }
}
