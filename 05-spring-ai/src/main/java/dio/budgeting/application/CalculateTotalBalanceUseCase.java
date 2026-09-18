package dio.budgeting.application;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CalculateTotalBalanceUseCase {
    private final TransactionRepository transactionRepository;

    public CalculateTotalBalanceUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "calculate-total-balance", description = "Calcula o saldo total de todas as transações financeiras (receitas menos despesas)")
    public long execute() {
        List<Transaction> transactions = transactionRepository.findAll();
        long totalBalance = 0;
        
        for (Transaction transaction : transactions) {
            totalBalance += transaction.getAmount();
        }
        
        return totalBalance;
    }
}
