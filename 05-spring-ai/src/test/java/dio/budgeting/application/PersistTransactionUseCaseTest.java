package dio.budgeting.application;

import dio.budgeting.application.input.PersistTransactionInput;
import dio.budgeting.domain.Category;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersistTransactionUseCaseTest {

    @Test
    void should_persistValidTransaction() {
        var repository = new InMemoryTransactionRepository();
        var useCase = new PersistTransactionUseCase(repository);

        var output = useCase.execute(new PersistTransactionInput("Mercado", 1999, Category.GROCERIES));

        assertThat(output.description()).isEqualTo("Mercado");
        assertThat(repository.findAll()).hasSize(1);
    }

    @Test
    void should_rejectBlankDescription() {
        var useCase = new PersistTransactionUseCase(new InMemoryTransactionRepository());

        assertThatThrownBy(() -> useCase.execute(new PersistTransactionInput("  ", 100, Category.AUTO)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("descrição");
    }

    @Test
    void should_rejectNonPositiveAmount() {
        var useCase = new PersistTransactionUseCase(new InMemoryTransactionRepository());

        assertThatThrownBy(() -> useCase.execute(new PersistTransactionInput("Uber", 0, Category.AUTO)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("valor");
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
