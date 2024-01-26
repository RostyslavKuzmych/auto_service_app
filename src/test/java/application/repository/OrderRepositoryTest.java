package application.repository;

import application.model.Car;
import application.model.Good;
import application.model.Job;
import application.model.Master;
import application.model.Order;
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
        "classpath:database/jobs/add_two_jobs_to_jobs_table.sql",
        "classpath:database/orders_goods/add_good_to_order.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = {
        "classpath:database/orders_goods/remove_good_from_order.sql",
        "classpath:database/jobs/remove_two_jobs_from_jobs_table.sql",
        "classpath:database/orders/remove_two_orders_from_orders_table.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@DataJpaTest
class OrderRepositoryTest {
    private static final Long AUDI_ID = 1L;
    private static final Long FIRST_JOB_ID = 1L;
    private static final Long SECOND_JOB_ID = 2L;
    private static final Long FIRST_GOOD_ID = 1L;
    private static final Long FIRST_ORDER_ID = 1L;
    private static final String VACUUM_CLEANER = "vacuum cleaner";
    private static final BigDecimal GOOD_PRICE = BigDecimal.valueOf(1200.0);
    private static final BigDecimal FIRST_JOB_PRICE = BigDecimal.valueOf(500);
    private static final BigDecimal SECOND_JOB_PRICE = BigDecimal.valueOf(1200);
    private static final Long STEPAN_ID = 1L;
    private static final String DESCRIPTION_FIRST_ORDER = "My car broke down...";
    private static final BigDecimal BIG_AMOUNT = BigDecimal.valueOf(1200);
    private static Job firstJob;
    private static Job secondJob;
    private static Good good;
    private static Order order;
    @Autowired
    private OrderRepository orderRepository;

    @BeforeAll
    static void beforeAll() {
        order = new Order().setId(FIRST_ORDER_ID)
                .setStatus(Order.Status.PAID)
                .setCar(new Car().setId(AUDI_ID))
                .setProblemDescription(DESCRIPTION_FIRST_ORDER)
                .setFinalAmount(BIG_AMOUNT);
        firstJob = new Job().setId(FIRST_JOB_ID)
                .setStatus(Job.Status.UNPAID)
                .setMaster(new Master().setId(STEPAN_ID))
                .setOrder(new Order().setId(order.getId()))
                .setPrice(FIRST_JOB_PRICE);
        secondJob = new Job().setId(SECOND_JOB_ID)
                .setStatus(Job.Status.PAID)
                .setMaster(new Master().setId(STEPAN_ID))
                .setOrder(new Order().setId(order.getId()))
                .setPrice(SECOND_JOB_PRICE);
        good = new Good().setId(FIRST_GOOD_ID)
                .setName(VACUUM_CLEANER)
                .setPrice(GOOD_PRICE);
        order.setJobs(Set.of(firstJob, secondJob)).setGoods(Set.of(good));
    }

    @Test
    @DisplayName("""
            Verify findByIdWithGoodsAndJobs() method
            """)
    void findByIdWithGoodsAndJobs_ValidOrderId_ReturnOrder() {
        Order expected = order;
        Order actual = orderRepository.findByIdWithGoodsAndJobs(FIRST_ORDER_ID)
                .orElse(null);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual);
    }
}
