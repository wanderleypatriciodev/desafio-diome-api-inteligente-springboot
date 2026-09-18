package dio.budgeting.application;

import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalculateTotalBalanceUseCaseTest {

    @Test
    void should_returnZero_when_thereAreNoTransactions() {
        var useCase = new CalculateTotalBalanceUseCase(new InMemoryTransactionRepository());

        assertThat(useCase.execute()).isZero();
    }

    @Test
    void should_sumAllTransactionAmounts() {
        var repository = new InMemoryTransactionRepository();
        repository.save(new Transaction("Mercado", 1500, Category.GROCERIES));
        repository.save(new Transaction("Farmácia", 2500, Category.PHARMA));
        var useCase = new CalculateTotalBalanceUseCase(repository);

        assertThat(useCase.execute()).isEqualTo(4000);
    }

    private static class InMemoryTransactionRepository implements TransactionRepository {
        private final List<Transaction> transactions = new ArrayList<>();

        @Override
        public Transaction save(Transaction transaction) {
            transactions.add(transaction);
            return transaction;
        }

        @Override
        public List<Transaction> findAllByCategory(Category category) {
            return transactions.stream()
                    .filter(transaction -> transaction.getCategory() == category)
                    .toList();
        }

        @Override
        public List<Transaction> findAll() {
            return List.copyOf(transactions);
        }
    }
}
