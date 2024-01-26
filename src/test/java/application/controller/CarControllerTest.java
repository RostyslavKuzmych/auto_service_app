package application.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import application.dto.car.CarRequestDto;
import application.dto.car.CarResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class CarControllerTest {
    protected static MockMvc mockMvc;
    private static final String PATH = "classpath:database/cars/";
    private static final Long ACURA_LONG_ID = 5L;
    private static final String ACURA_ID = "/5";
    private static final String ID = "id";
    private static final String API = "/api/cars";
    private static final String UNIQUE_NUMBER = "123GSD";
    private static final String MERCEDES = "Mercedes";
    private static final String BENZ = "Benz";
    private static final Integer RANDOM_YEAR = 2020;
    private static final Long STEPAN_ID = 1L;
    private static CarRequestDto mercedesRequestDto;
    private static String jsonRequest;
    private static CarResponseDto mercedesResponseDto;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .build();
    }

    @BeforeEach
    void setUp() throws JsonProcessingException {
        mercedesRequestDto = new CarRequestDto()
                .setOwnerId(STEPAN_ID)
                .setBrand(MERCEDES)
                .setModel(BENZ)
                .setManufactureYear(RANDOM_YEAR)
                .setNumber(UNIQUE_NUMBER);
        mercedesResponseDto = new CarResponseDto()
                .setBrand(MERCEDES)
                .setModel(BENZ)
                .setOwnerId(STEPAN_ID)
                .setManufactureYear(RANDOM_YEAR)
                .setNumber(UNIQUE_NUMBER);
        jsonRequest = objectMapper.writeValueAsString(mercedesRequestDto);
    }

    @Test
    @Sql(scripts = PATH + "remove_mercedes_car.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify createCar() method
            """)
    void createCar_ValidRequestDto_ReturnResponseDto() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(post(API).contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequest)).andExpect(status().isCreated()).andReturn();

        // then
        CarResponseDto actual = objectMapper.readValue(mvcResult.getResponse()
                        .getContentAsString(), CarResponseDto.class);
        Assertions.assertNotNull(actual);
        EqualsBuilder.reflectionEquals(mercedesResponseDto, actual, ID);
    }

    @Test
    @Sql(scripts = PATH + "add_acure_car_to_cars_table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = PATH + "remove_acura_car_from_cars_table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify updateCarById() method
            """)
    void updateCarById_ValidCarId_ReturnResponseDto() throws Exception {
        // when
        MvcResult mvcResult = mockMvc.perform(put(API + ACURA_ID)
                        .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
                .andExpect(status().isOk()).andReturn();
        // then
        CarResponseDto expected = mercedesResponseDto.setId(ACURA_LONG_ID);
        CarResponseDto actual = objectMapper.readValue(mvcResult.getResponse()
                        .getContentAsString(),
                CarResponseDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(expected, actual);
    }
}
