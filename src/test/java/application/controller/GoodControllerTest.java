package application.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import application.dto.good.GoodRequestDto;
import application.dto.good.GoodResponseDto;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GoodControllerTest {
    protected static MockMvc mockMvc;
    private static final String PATH = "classpath:database/goods/";
    private static final String ID = "id";
    private static final String API = "/api/goods";
    private static final Long ENGINE_LONG_ID = 5L;
    private static final String ENGINE_ID = "/5";
    private static final String LUBRICANT = "lubricant";
    private static final BigDecimal LUBRICANT_PRICE = BigDecimal.valueOf(400);
    private static GoodRequestDto lubricantRequestDto;
    private static String jsonRequest;
    private static GoodResponseDto lubricantResponseDto;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .build();
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        lubricantRequestDto = new GoodRequestDto()
                .setName(LUBRICANT)
                .setPrice(LUBRICANT_PRICE);
        lubricantResponseDto = new GoodResponseDto()
                .setName(LUBRICANT)
                .setPrice(LUBRICANT_PRICE);
        jsonRequest = objectMapper.writeValueAsString(lubricantRequestDto);
    }

    @Test
    @Sql(scripts = PATH + "remove_lubricant_good.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify createGood() method
            """)
    void createGood_ValidRequestDto_ReturnResponseDto() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(post(API).contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)).andExpect(status().isCreated()).andReturn();

        // then
        GoodResponseDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), GoodResponseDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(lubricantResponseDto, actual, ID);
    }

    @Test
    @Sql(scripts = PATH + "add_engine_good.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = PATH + "remove_engine_good.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify updateGood() method
            """)
    void updateGood_ValidRequestDto_ReturnResponseDto() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(put(API + ENGINE_ID)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                .andExpect(status().isOk()).andReturn();

        // then
        GoodResponseDto expected = lubricantResponseDto.setId(ENGINE_LONG_ID);
        GoodResponseDto actual = objectMapper.readValue(mvcResult.getResponse()
                .getContentAsString(), GoodResponseDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }
}
