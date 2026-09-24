package com.fintech.domain.dao;

import com.fintech.domain.models.CardDto;
import com.fintech.framework.database.DatabaseUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;

public class CardDao {

    private static final Logger LOGGER = LogManager.getLogger(CardDao.class);

    public int insertCard(CardDto card) {
        String sql = "INSERT INTO cards (id, account_id, card_number_masked, card_token, card_type, status, daily_limit, expiry_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        LOGGER.info("DB DAO: Inserting card record: ID={}", card.getCardId());
        return DatabaseUtils.executeUpdate(sql,
                card.getCardId(),
                card.getAccountId(),
                card.getMaskedPan() != null ? card.getMaskedPan() : "411111******1111",
                card.getCardToken(),
                card.getCardType() != null ? card.getCardType() : "VIRTUAL",
                card.getStatus() != null ? card.getStatus() : "PENDING_ACTIVATION",
                card.getDailyLimit(),
                card.getExpiryDate()
        );
    }

    public Optional<String> getCardStatus(String cardId) {
        String sql = "SELECT status FROM cards WHERE id = ?";
        LOGGER.info("DB DAO: Fetching card status for ID: {}", cardId);
        Optional<Map<String, Object>> row = DatabaseUtils.executeSingleRowQuery(sql, cardId);
        return row.map(r -> (String) r.get("status"));
    }

    public int updateCardStatus(String cardId, String newStatus) {
        String sql = "UPDATE cards SET status = ? WHERE id = ?";
        LOGGER.info("DB DAO: Updating card status: ID={} -> {}", cardId, newStatus);
        return DatabaseUtils.executeUpdate(sql, newStatus, cardId);
    }

    public int updateCardPin(String cardId, String pinHash) {
        String sql = "UPDATE cards SET pin_hash = ? WHERE id = ?";
        LOGGER.info("DB DAO: Updating PIN hash for Card ID={}", cardId);
        return DatabaseUtils.executeUpdate(sql, pinHash, cardId);
    }
}
