package application.repository;

import application.model.Car;
import application.model.Order;
import application.model.Owner;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@Sql(scripts = {
        "classpath:database/orders/add_two_orders_to_orders_table.sql",
        "classpath:database/owners_orders/add_two_orders_to_owner.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {
        "classpath:database/owners_orders/remove_owner_orders.sql",
        "classpath:database/orders/remove_two_orders_from_orders_table.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@DataJpaTest
class OwnerRepositoryTest {
    private static final String IVAN_PHONE_NUMBER = "+380984354356";
    private static final String IVAN = "Ivan";
    private static final String PETROVYCH = "Petrovych";
    private static final Long AUDI_ID = 1L;
    private static final Long FIRST_ORDER_ID = 1L;
    private static final Long SECOND_ORDER_ID = 2L;
    private static final String DESCRIPTION_FIRST_ORDER = "My car broke down...";
    private static final String DESCRIPTION_SECOND_ORDER = "Something broke down...";
    private static final BigDecimal SMALL_AMOUNT = BigDecimal.valueOf(500);
    private static final BigDecimal BIG_AMOUNT = BigDecimal.valueOf(1200);
    private static final Long FIRST_OWNER_ID = 1L;
    private static Owner owner;
    private static Order firstOrder;
    private static Order secondOrder;
    @Autowired
    private OwnerRepository ownerRepository;

    @BeforeAll
    static void beforeAll() {
        owner = new Owner().setId(FIRST_OWNER_ID).setPhoneNumber(IVAN_PHONE_NUMBER)
                .setFirstName(IVAN).setLastName(PETROVYCH);
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
        owner.setOrders(Set.of(firstOrder, secondOrder));
    }

    @Test
    @DisplayName("""
            Verify findByIdWithOrders() method
            """)
    void findByIdWithOrders_ValidOwnerId_ReturnOwner() {
        Owner expected = owner;
        Owner actual = ownerRepository.findByIdWithOrders(FIRST_OWNER_ID)
                .orElse(null);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify findByCarId() method
            """)
    void findByCarId_ValidCarId_ReturnOwner() {
        Owner expected = owner;
        Owner actual = ownerRepository.findByCarId(AUDI_ID).orElse(null);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }
}
