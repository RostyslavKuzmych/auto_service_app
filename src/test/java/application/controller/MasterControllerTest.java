package application.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import application.dto.master.MasterRequestDto;
import application.dto.master.MasterResponseDto;
import application.dto.order.OrderResponseDto;
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
class MasterControllerTest {
    protected static MockMvc mockMvc;
    private static final String ID = "id";
    private static final Long FIRST_ORDER_ID = 1L;
    private static final Long SECOND_ORDER_ID = 2L;
    private static final String PATH = "classpath:database/masters/";
    private static final String PATH_JOBS = "classpath:database/jobs/";
    private static final String PATH_MASTERS_ORDERS = "classpath:database/masters_orders/";
    private static final String PATH_ORDERS = "classpath:database/orders/";
    private static final BigDecimal EXPECTED_SALARY = BigDecimal.valueOf(200);
    private static final String SALARY = "/salary";
    private static final Long AUDI_ID = 1L;
    private static final String PAID = "PAID";
    private static final String DESCRIPTION_FIRST_ORDER = "My car broke down...";
    private static final String DESCRIPTION_SECOND_ORDER = "Something broke down...";
    private static final BigDecimal SMALL_AMOUNT = BigDecimal.valueOf(500);
    private static final BigDecimal BIG_AMOUNT = BigDecimal.valueOf(1200);
    private static final String API = "/api/masters";
    private static final Integer ZERO = 0;
    private static final String ORDERS = "/orders";
    private static final String ANDRIY_ID = "/4";
    private static final Long ANDRIY_LONG_ID = 4L;
    private static final String SASHA = "Sasha";
    private static final String MARKOV = "Markov";
    private static OrderResponseDto firstOrderResponseDto;
    private static OrderResponseDto secondOrderResponseDto;
    private static String jsonRequest;
    private MasterRequestDto sashaRequestDto;
    private MasterResponseDto sashaResponseDto;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .build();
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        sashaRequestDto = new MasterRequestDto()
                .setFirstName(SASHA)
                .setLastName(MARKOV);
        sashaResponseDto = new MasterResponseDto()
                .setFirstName(SASHA)
                .setLastName(MARKOV);
        firstOrderResponseDto = new OrderResponseDto().setId(FIRST_ORDER_ID)
                .setStatus(PAID)
                .setCarId(AUDI_ID)
                .setProblemDescription(DESCRIPTION_FIRST_ORDER)
                .setFinalAmount(BIG_AMOUNT);
        secondOrderResponseDto = new OrderResponseDto().setId(SECOND_ORDER_ID)
                .setStatus(PAID)
                .setFinalAmount(SMALL_AMOUNT)
                .setProblemDescription(DESCRIPTION_SECOND_ORDER)
                .setCarId(AUDI_ID);
        jsonRequest = objectMapper.writeValueAsString(sashaRequestDto);
    }

    @Test
    @DisplayName("""
            Verify createMaster() method
            """)
    @Sql(scripts = PATH + "remove_sasha_master_from_masters_table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createMaster_ValidRequestDto_ReturnResponseDto() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(post(API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)).andExpect(status().isCreated()).andReturn();

        // then
        MasterResponseDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), MasterResponseDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(sashaResponseDto, actual, ID);
    }

    @Test
    @Sql(scripts = PATH + "add_andriy_master_to_masters_table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = PATH + "remove_andriy_master_from_masters_table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify updateMaster() method
            """)
    void updateMaster_ValidRequestDto_ReturnResponseDto() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(put(API + ANDRIY_ID).content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andReturn();

        // then
        MasterResponseDto expected = sashaResponseDto.setId(ANDRIY_LONG_ID);
        MasterResponseDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), MasterResponseDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Sql(scripts = {
            PATH + "add_andriy_master_to_masters_table.sql",
            PATH_ORDERS + "add_two_orders_to_orders_table.sql",
            PATH_MASTERS_ORDERS + "add_orders_to_andriy.sql",
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            PATH_MASTERS_ORDERS + "remove_andriy_orders.sql",
            PATH_ORDERS + "remove_two_orders_from_orders_table.sql",
            PATH + "remove_andriy_master_from_masters_table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify getAllSuccessfulOrdersByMasterId() method
            """)
    void getAllSuccessfulOrdersByMasterId_ValidMasterId_ReturnList() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(get(API + ANDRIY_ID + ORDERS))
                .andReturn();

        // then
        List<OrderResponseDto> expected = List.of(firstOrderResponseDto,
                secondOrderResponseDto);
        List<OrderResponseDto> actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), new TypeReference<List<OrderResponseDto>>() {});
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify getSalaryByMasterId() method
            """)
    @Sql(scripts = {
            PATH + "add_andriy_master_to_masters_table.sql",
            PATH_ORDERS + "add_two_orders_to_orders_table.sql",
            PATH_MASTERS_ORDERS + "add_orders_to_andriy.sql",
            PATH_JOBS + "add_three_jobs_to_jobs_table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
            PATH_MASTERS_ORDERS + "remove_andriy_orders.sql",
            PATH_JOBS + "remove_all_jobs_from_jobs_table.sql",
            PATH_ORDERS + "remove_two_orders_from_orders_table.sql",
            PATH + "remove_andriy_master_from_masters_table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getSalaryByMasterId_ValidMasterId_ReturnSalary() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(get(API + ANDRIY_ID + SALARY))
                .andReturn();

        // then
        BigDecimal actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), BigDecimal.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(ZERO, EXPECTED_SALARY.compareTo(actual));
    }
}
