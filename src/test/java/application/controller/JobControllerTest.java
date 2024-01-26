package application.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import application.dto.job.JobRequestDto;
import application.dto.job.JobRequestStatusDto;
import application.dto.job.JobResponseDto;
import application.model.Job;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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

@Sql(scripts = "classpath:database/orders/add_two_orders_to_orders_table.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "classpath:database/orders/remove_two_orders_from_orders_table.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JobControllerTest {
    protected static MockMvc mockMvc;
    private static final Long FIRST_ORDER_ID = 1L;
    private static final String PATH = "classpath:database/jobs/";
    private static final String STATUS = "/status";
    private static final String PAID = "PAID";
    private static final String ID = "id";
    private static final String API = "/api/jobs";
    private static final String WHEEL_CHANGE_ID = "/3";
    private static final Long WHEEL_CHANGE_LONG_ID = 3L;
    private static final String UNPAID = "UNPAID";
    private static final Long STEPAN_ID = 1L;
    private static final BigDecimal LUBRICANT_CHANGE_PRICE = BigDecimal.valueOf(300);
    private static final BigDecimal WHEEL_CHANGE_PRICE = BigDecimal.valueOf(300);
    private static JobRequestDto lubricantChangeRequestDto;
    private static JobResponseDto lubricantChangeResponseDto;
    private static JobRequestStatusDto statusDto;
    private static String jsonRequest;
    private static String jsonRequestStatus;
    private static JobResponseDto wheelChangeResponseDto;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .build();
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        statusDto = new JobRequestStatusDto()
                .setStatus(Job.Status.PAID);
        lubricantChangeRequestDto = new JobRequestDto()
                .setOrderId(FIRST_ORDER_ID)
                .setPrice(LUBRICANT_CHANGE_PRICE)
                .setMasterId(STEPAN_ID);
        lubricantChangeResponseDto = new JobResponseDto()
                .setStatus(UNPAID)
                .setPrice(LUBRICANT_CHANGE_PRICE)
                .setMasterId(STEPAN_ID)
                .setOrderId(FIRST_ORDER_ID);
        wheelChangeResponseDto = new JobResponseDto()
                .setId(WHEEL_CHANGE_LONG_ID)
                .setMasterId(STEPAN_ID)
                .setStatus(PAID)
                .setPrice(WHEEL_CHANGE_PRICE)
                .setOrderId(FIRST_ORDER_ID);
        jsonRequest = objectMapper.writeValueAsString(lubricantChangeRequestDto);
        jsonRequestStatus = objectMapper.writeValueAsString(statusDto);
    }

    @Test
    @Sql(scripts = PATH + "remove_all_jobs_from_jobs_table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify createJob() method
            """)
    void createJob_ValidRequestDto_ReturnResponseDto() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(post(API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)).andExpect(status().isCreated()).andReturn();

        // then
        JobResponseDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), JobResponseDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(lubricantChangeResponseDto, actual, ID);
    }

    @Test
    @Sql(scripts = PATH + "add_wheel_change_job_to_jobs_table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = PATH + "remove_wheel_change_job_from_jobs_table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify updateJob() method
            """)
    void updateJob_ValidRequestDto_ReturnResponseDto() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(put(API + WHEEL_CHANGE_ID)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                .andExpect(status().isOk()).andReturn();

        // then
        JobResponseDto expected = lubricantChangeResponseDto.setId(WHEEL_CHANGE_LONG_ID);
        JobResponseDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), JobResponseDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @Sql(scripts = PATH + "add_wheel_change_job_to_jobs_table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = PATH + "remove_wheel_change_job_from_jobs_table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify updateJobStatus() method
            """)
    void updateJobStatus_ValidRequestDto_ReturnResponseDto() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(
                put(API + WHEEL_CHANGE_ID + STATUS)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonRequestStatus))
                .andExpect(status().isOk()).andReturn();

        // then
        JobResponseDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), JobResponseDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(wheelChangeResponseDto, actual);
    }
}
