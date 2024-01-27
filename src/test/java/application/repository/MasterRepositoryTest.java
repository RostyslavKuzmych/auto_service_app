package application.repository;

import application.model.Car;
import application.model.Master;
import application.model.Order;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@Sql(scripts = {
        "classpath:database/orders/add_two_orders_to_orders_table.sql",
        "classpath:database/masters_orders/add_orders_to_stepan.sql",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {
        "classpath:database/masters_orders/remove_stepan_orders.sql",
        "classpath:database/orders/remove_two_orders_from_orders_table.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class MasterRepositoryTest {
    private static final Long AUDI_ID = 1L;
    private static final Long FIRST_ORDER_ID = 1L;
    private static final Long SECOND_ORDER_ID = 2L;
    private static final Long STEPAN_ID = 1L;
    private static final String DESCRIPTION_FIRST_ORDER = "My car broke down...";
    private static final String DESCRIPTION_SECOND_ORDER = "Something broke down...";
    private static final BigDecimal SMALL_AMOUNT = BigDecimal.valueOf(500);
    private static final BigDecimal BIG_AMOUNT = BigDecimal.valueOf(1200);
    private static final String STEPAN = "Stepan";
    private static final String KOS = "Kos";
    private static Order firstOrder;
    private static Order secondOrder;
    private static Master stepan;
    @Autowired
    private MasterRepository masterRepository;

    @BeforeAll
    static void beforeAll() {
        stepan = new Master().setId(STEPAN_ID)
                .setFirstName(STEPAN)
                .setLastName(KOS);
        firstOrder = new Order().setId(FIRST_ORDER_ID)
                .setStatus(Order.Status.PAID)
                .setCar(new Car().setId(AUDI_ID))
                .setProblemDescription(DESCRIPTION_FIRST_ORDER)
                .setFinalAmount(BIG_AMOUNT);
        secondOrder = new Order().setId(SECOND_ORDER_ID)
                .setStatus(Order.Status.PAID)
                .setFinalAmount(SMALL_AMOUNT)
                .setProblemDescription(DESCRIPTION_SECOND_ORDER)
                .setCar(new Car().setId(AUDI_ID));
        stepan.setOrders(Set.of(firstOrder, secondOrder));
    }

    @Test
    @DisplayName("""
            Verify findByIdWithAllOrders() method
            """)
    void findByIdWithAllOrders_ValidMasterId_ReturnList() {
        Master expected = stepan;
        Master actual = masterRepository.findByIdWithAllOrders(STEPAN_ID).orElse(null);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }
}
