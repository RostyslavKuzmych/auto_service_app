package application.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import application.dto.order.OrderRequestDto;
import application.dto.order.OrderRequestStatusDto;
import application.dto.order.OrderResponseDto;
import application.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {
    protected static MockMvc mockMvc;
    private static final String PATH_MASTERS_ORDERS = "classpath:database/masters_orders/";
    private static final Integer ZERO = 0;
    private static final Long FIRST_LONG_ORDER_ID = 1L;
    private static final Long SECOND_LONG_ORDER_ID = 2L;
    private static final String PATH = "classpath:database/orders/";
    private static final String PATH_OWNERS_ORDERS = "classpath:database/owners_orders/";
    private static final String PATH_JOBS = "classpath:database/jobs/";
    private static final String PATH_ORDERS_GOODS = "classpath:database/orders_goods/";
    private static final String STATUS = "/status";
    private static final BigDecimal EXPECTED_ORDER_PRICE = BigDecimal.valueOf(1200);
    private static final String PAYMENT = "/payment";
    private static final String DATE_OF_ACCEPTANCE = "dateOfAcceptance";
    private static final String CUSTOM_DESCRIPTION = "Oil was leaking...";
    private static final String FIRST_ORDER_ID = "/1";
    private static final String GOODS = "/goods";
    private static final String FIRST_GOOD_ID = "/1";
    private static final String CARS = "/cars";
    private static final String PROCESSED = "PROCESSED";
    private static final String AUDI_STRING_ID = "/1";
    private static final String API = "/api/orders";
    private static OrderRequestDto firstOrderRequestDto;
    private static OrderRequestDto secondOrderRequestDto;
    private static String jsonRequest;
    private static String jsonRequestSecond;
    private static String jsonRequestStatus;
    private static final Long AUDI_LONG_ID = 1L;
    private static final String PAID = "PAID";
    private static final String DESCRIPTION_FIRST_ORDER = "My car broke down...";
    private static final BigDecimal BIG_AMOUNT = BigDecimal.valueOf(1200);
    private static final String DESCRIPTION_SECOND_ORDER = "Something broke down...";
    private static final BigDecimal SMALL_AMOUNT = BigDecimal.valueOf(500);
    private static final Long VACUUM_CLEANER_ID = 1L;
    private static OrderRequestStatusDto orderRequestStatusDto;
    private static OrderResponseDto secondOrderResponseDto;
    private static OrderResponseDto firstOrderResponseDto;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .build();
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        orderRequestStatusDto = new OrderRequestStatusDto()
                .setStatus(Order.Status.PROCESSED);
        secondOrderRequestDto = new OrderRequestDto()
                .setProblemDescription(CUSTOM_DESCRIPTION);
        firstOrderRequestDto = new OrderRequestDto()
                .setProblemDescription(DESCRIPTION_FIRST_ORDER);
        firstOrderResponseDto = new OrderResponseDto().setId(FIRST_LONG_ORDER_ID)
                .setStatus(PAID)
                .setCarId(AUDI_LONG_ID)
                .setProblemDescription(DESCRIPTION_FIRST_ORDER)
                .setFinalAmount(BIG_AMOUNT);
        secondOrderResponseDto = new OrderResponseDto().setId(SECOND_LONG_ORDER_ID)
                .setStatus(PAID)
                .setFinalAmount(SMALL_AMOUNT)
                .setProblemDescription(DESCRIPTION_SECOND_ORDER)
                .setCarId(AUDI_LONG_ID);
        jsonRequest = objectMapper.writeValueAsString(firstOrderRequestDto);
        jsonRequestSecond = objectMapper.writeValueAsString(secondOrderRequestDto);
        jsonRequestStatus = objectMapper.writeValueAsString(orderRequestStatusDto);
    }

    @Test
    @Sql(scripts = {
            PATH_JOBS + "remove_all_jobs_from_jobs_table.sql",
            PATH_OWNERS_ORDERS + "remove_all_owners_orders.sql",
            PATH + "remove_all_orders_from_orders_table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify placeOrder() method
            """)
    void placeOrder_ValidRequestDto_ReturnResponseDto() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(post(API + CARS + AUDI_STRING_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)).andExpect(status().isCreated()).andReturn();

        // then
        OrderResponseDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), OrderResponseDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertNotNull(actual.getDateOfAcceptance());
        Assertions.assertEquals(actual.getProblemDescription(),
                firstOrderRequestDto.getProblemDescription());
    }

    @Test
    @DisplayName("""
            Verify addGoodToOrder() method
            """)
    @Sql(scripts = PATH + "add_two_orders_to_orders_table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            PATH_ORDERS_GOODS + "remove_all_orders_goods.sql",
            PATH + "remove_two_orders_from_orders_table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addGoodToOrder_ValidOrderId_ReturnResponseDto() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(post(API
                + FIRST_ORDER_ID + GOODS + FIRST_GOOD_ID))
                .andExpect(status().isCreated()).andReturn();

        // then
        OrderResponseDto expected = firstOrderResponseDto
                .setGoodsId(Set.of(VACUUM_CLEANER_ID));
        OrderResponseDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), OrderResponseDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, DATE_OF_ACCEPTANCE);
    }

    @Test
    @DisplayName("""
            Verify updateOrder() method
            """)
    @Sql(scripts = PATH + "add_two_orders_to_orders_table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = PATH + "remove_two_orders_from_orders_table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateOrder_ValidRequestDto_ReturnResponseDto() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(put(API + FIRST_ORDER_ID)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonRequestSecond))
                .andExpect(status().isOk()).andReturn();

        // then
        OrderResponseDto expected = firstOrderResponseDto
                .setProblemDescription(CUSTOM_DESCRIPTION);
        OrderResponseDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), OrderResponseDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, DATE_OF_ACCEPTANCE);
    }

    @Test
    @Sql(scripts = PATH + "add_two_orders_to_orders_table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = PATH + "remove_two_orders_from_orders_table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify updateOrderStatus() method
            """)
    void updateOrderStatus_ValidRequestDto_ReturnResponseDto() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(put(API + FIRST_ORDER_ID + STATUS)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonRequestStatus))
                .andExpect(status().isOk()).andReturn();

        // then
        OrderResponseDto expected = firstOrderResponseDto.setStatus(PROCESSED);
        OrderResponseDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), OrderResponseDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(expected, actual, DATE_OF_ACCEPTANCE);
    }

    @Test
    @Sql(scripts = {
            PATH + "add_two_unpaid_orders_to_orders_table.sql",
            PATH_JOBS + "add_two_jobs_to_jobs_table.sql",
            PATH_OWNERS_ORDERS + "add_two_orders_to_owner.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            PATH_OWNERS_ORDERS + "remove_all_owners_orders.sql",
            PATH_MASTERS_ORDERS + "remove_all_masters_orders.sql",
            PATH_JOBS + "remove_two_jobs_from_jobs_table.sql",
            PATH + "remove_two_orders_from_orders_table.sql",
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify payForOrder() method
            """)
    void payForOrder_ValidInputs_ReturnPrice() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(get(API
                + FIRST_ORDER_ID + PAYMENT + AUDI_STRING_ID)).andExpect(status().isOk())
                .andReturn();

        // then
        BigDecimal actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), BigDecimal.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(ZERO, EXPECTED_ORDER_PRICE.compareTo(actual));
    }
}
