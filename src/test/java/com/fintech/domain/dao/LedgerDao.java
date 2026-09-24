package com.fintech.domain.dao;

import com.fintech.framework.database.DatabaseUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class LedgerDao {

    private static final Logger LOGGER = LogManager.getLogger(LedgerDao.class);

    public Optional<Map<String, Object>> getAccountBalances(String accountId) {
        String sql = "SELECT available_balance, hold_balance, currency FROM accounts WHERE id = ?";
        LOGGER.info("DB DAO: Fetching balances for Account ID: {}", accountId);
        return DatabaseUtils.executeSingleRowQuery(sql, accountId);
    }

    public int applyAuthorizationHold(String transactionId, String accountId, BigDecimal amount) {
        LOGGER.info("DB DAO: Applying Auth Hold of ${} to Account ID: {}", amount, accountId);

        // 1. Update balances on accounts table
        String updateAccountSql = "UPDATE accounts SET available_balance = available_balance - ?, hold_balance = hold_balance + ? WHERE id = ?";
        DatabaseUtils.executeUpdate(updateAccountSql, amount, amount, accountId);

        // 2. Fetch balance after update for portable ledger insertion
        String selectBalanceSql = "SELECT available_balance FROM accounts WHERE id = ?";
        BigDecimal balanceAfter = DatabaseUtils.executeSingleRowQuery(selectBalanceSql, accountId)
                .map(m -> {
                    Object val = m.get("available_balance");
                    if (val instanceof BigDecimal bd) return bd;
                    if (val instanceof Number num) return BigDecimal.valueOf(num.doubleValue());
                    return BigDecimal.ZERO;
                })
                .orElse(BigDecimal.ZERO);

        // 3. Insert record into double_entry_ledger
        String insertLedgerSql = "INSERT INTO double_entry_ledger (id, transaction_id, account_id, entry_type, amount, balance_after, description) " +
                                 "VALUES (?, ?, ?, 'DEBIT', ?, ?, 'Pending Auth Hold')";

        return DatabaseUtils.executeUpdate(insertLedgerSql,
                "LEDGER-" + System.currentTimeMillis(),
                transactionId,
                accountId,
                amount,
                balanceAfter
        );
    }

    public Optional<Map<String, Object>> getLatestLedgerEntry(String accountId) {
        String sql = "SELECT * FROM double_entry_ledger WHERE account_id = ? ORDER BY created_at DESC LIMIT 1";
        return DatabaseUtils.executeSingleRowQuery(sql, accountId);
    }
}
