package application.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import application.dto.order.OrderResponseDto;
import application.dto.owner.OwnerRequestDto;
import application.dto.owner.OwnerResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
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
class OwnerControllerTest {
    protected static MockMvc mockMvc;
    private static final Long MARIA_ID = 5L;
    private static final String MARIA_STRING_ID = "/5";
    private static final String ID = "id";
    private static final String ANNA_PHONE_NUMBER = "+380965676431";
    private static final String ANNA = "Anna";
    private static final String SHEVCHENKO = "Shevchenko";
    private static final String PATH = "classpath:database/owners/";
    private static final String PATH_OWNERS_ORDERS = "classpath:database/owners_orders/";
    private static final String PATH_ORDERS = "classpath:database/orders/";
    private static final String DATE_OF_ACCEPTANCE = "dateOfAcceptance";
    private static final Integer EXPECTED_SIZE = 2;
    private static final String ORDERS = "/orders";
    private static final String FIRST_OWNER_ID = "/1";
    private static final String API = "/api/owners";
    private static final String PAID = "PAID";
    private static final Long AUDI_LONG_ID = 1L;
    private static final Long FIRST_LONG_ORDER_ID = 1L;
    private static final Long SECOND_LONG_ORDER_ID = 2L;
    private static final String DESCRIPTION_FIRST_ORDER = "My car broke down...";
    private static final BigDecimal BIG_AMOUNT = BigDecimal.valueOf(1200);
    private static final String DESCRIPTION_SECOND_ORDER = "Something broke down...";
    private static final BigDecimal SMALL_AMOUNT = BigDecimal.valueOf(500);
    private static OwnerRequestDto annaRequestDto;
    private static OwnerResponseDto annaResponseDto;
    private static OrderResponseDto firstOrderResponseDto;
    private static OrderResponseDto secondOrderResponseDto;
    private static String jsonRequest;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .build();
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        annaRequestDto = new OwnerRequestDto().setPhoneNumber(ANNA_PHONE_NUMBER)
                .setFirstName(ANNA).setLastName(SHEVCHENKO);
        annaResponseDto = new OwnerResponseDto().setPhoneNumber(ANNA_PHONE_NUMBER)
                .setFirstName(ANNA).setLastName(SHEVCHENKO);
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
        jsonRequest = objectMapper.writeValueAsString(annaRequestDto);
    }

    @Test
    @Sql(scripts = PATH + "remove_anna_from_owners_table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify createOwner() method
            """)
    void createOwner_ValidRequest_ReturnResponseDto() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(post(API).contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated()).andReturn();

        // then
        OwnerResponseDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), OwnerResponseDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(annaResponseDto, actual, ID);
    }

    @Test
    @DisplayName("""
            Verify updateMaster() method
            """)
    @Sql(scripts = PATH + "add_maria_to_owners_table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = PATH + "remove_maria_from_owners_table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateMaster_ValidRequestDto_ReturnResponseDto() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(put(API + MARIA_STRING_ID)
                .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        // then
        OwnerResponseDto expected = annaResponseDto.setId(MARIA_ID);
        OwnerResponseDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), OwnerResponseDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(annaResponseDto, actual);
    }

    @Test
    @DisplayName("""
            Verify updateMaster() method
            """)
    void updateMaster_InvalidRequestDto_ReturnExpectedStatus() throws Exception {
        // when
        mockMvc.perform(put(API + MARIA_STRING_ID)
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql(scripts = {
            PATH_ORDERS + "add_two_orders_to_orders_table.sql",
            PATH_OWNERS_ORDERS + "add_two_orders_to_owner.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            PATH_OWNERS_ORDERS + "remove_all_owners_orders.sql",
            PATH_ORDERS + "remove_two_orders_from_orders_table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify getAllOrdersByOwnerId() method
            """)
    void getAllOrdersByOwnerId_ValidOwnerId_ReturnList() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(get(API + FIRST_OWNER_ID + ORDERS))
                .andReturn();

        // then
        List<OrderResponseDto> expected = List.of(firstOrderResponseDto, secondOrderResponseDto);
        List<OrderResponseDto> actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), new TypeReference<List<OrderResponseDto>>() {});
        Assertions.assertEquals(EXPECTED_SIZE, actual.size());
        EqualsBuilder.reflectionEquals(expected, actual, DATE_OF_ACCEPTANCE);
    }
}
