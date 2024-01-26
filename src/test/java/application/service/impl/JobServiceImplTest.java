package application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import application.dto.job.JobRequestDto;
import application.dto.job.JobRequestStatusDto;
import application.dto.job.JobResponseDto;
import application.exception.EntityNotFoundException;
import application.mapper.JobMapper;
import application.model.Job;
import application.model.Master;
import application.model.Order;
import application.repository.JobRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JobServiceImplTest {
    private static final String PAID = "PAID";
    private static final String EXCEPTION_FINDING = "Can't find job by id ";
    private static final Long INCORRECT_JOB_ID = 100L;
    private static final Integer ONE_TIME = 1;
    private static final Long STEPAN_ID = 1L;
    private static final Long DIAGNOSTICS_ID = 2L;
    private static final Long LUBRICANT_CHANGE_ID = 1L;
    private static final BigDecimal LUBRICANT_CHANGE_PRICE = BigDecimal.valueOf(300);
    private static final BigDecimal DIAGNOSTICS_PRICE = BigDecimal.valueOf(500);
    private static Job lubricantChange;
    private static JobRequestStatusDto jobRequestStatusDto;
    private static JobRequestDto diagnosticsRequestDto;
    private static Job diagnostics;
    private static JobResponseDto diagnosticsResponseDto;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private JobMapper jobMapper;
    @InjectMocks
    private JobServiceImpl jobServiceImpl;

    @BeforeEach
    void beforeEach() {
        jobRequestStatusDto = new JobRequestStatusDto()
                .setStatus(Job.Status.PAID);
        diagnosticsRequestDto = new JobRequestDto()
                .setOrderId(1L)
                .setMasterId(STEPAN_ID)
                .setPrice(DIAGNOSTICS_PRICE);
        diagnostics = new Job().setId(DIAGNOSTICS_ID)
                .setMaster(new Master().setId(STEPAN_ID))
                .setOrder(new Order().setId(1L))
                .setPrice(DIAGNOSTICS_PRICE);
        diagnosticsResponseDto = new JobResponseDto().setId(diagnostics.getId())
                .setOrderId(diagnostics.getOrder().getId())
                .setStatus(diagnostics.getStatus().toString())
                .setPrice(diagnostics.getPrice())
                .setMasterId(diagnostics.getMaster().getId());
        lubricantChange = new Job().setId(LUBRICANT_CHANGE_ID)
                .setPrice(LUBRICANT_CHANGE_PRICE)
                .setMaster(new Master().setId(STEPAN_ID))
                .setOrder(new Order().setId(1L))
                .setStatus(Job.Status.UNPAID);
    }

    @Test
    @DisplayName("""
            Verify createJob() method
            """)
    void createJob_ValidRequestDto_ReturnResponseDto() {
        // when
        when(jobMapper.toEntity(diagnosticsRequestDto)).thenReturn(diagnostics);
        when(jobRepository.save(diagnostics)).thenReturn(diagnostics);
        when(jobMapper.toDto(diagnostics)).thenReturn(diagnosticsResponseDto);

        // then
        JobResponseDto actual = jobServiceImpl.createJob(diagnosticsRequestDto);
        assertNotNull(actual);
        assertEquals(diagnosticsResponseDto, actual);
        verify(jobRepository, times(ONE_TIME)).save(diagnostics);
    }

    @Test
    @DisplayName("""
            Verify updateJob() method
            """)
    void updateJob_ValidRequestDto_ReturnResponseDto() {
        // when
        when(jobRepository.findById(LUBRICANT_CHANGE_ID))
                .thenReturn(Optional.ofNullable(lubricantChange));
        when(jobMapper.toEntity(diagnosticsRequestDto)).thenReturn(diagnostics);
        when(jobRepository.save(diagnostics.setId(LUBRICANT_CHANGE_ID)))
                .thenReturn(diagnostics);
        when(jobMapper.toDto(diagnostics))
                .thenReturn(diagnosticsResponseDto.setId(LUBRICANT_CHANGE_ID));

        // then
        JobResponseDto actual = jobServiceImpl
                .updateJob(LUBRICANT_CHANGE_ID, diagnosticsRequestDto);
        assertNotNull(actual);
        assertEquals(diagnosticsResponseDto, actual);
        verify(jobRepository, times(ONE_TIME)).findById(LUBRICANT_CHANGE_ID);
        verify(jobRepository, times(ONE_TIME)).save(diagnostics);
    }

    @Test
    @DisplayName("""
            Verify updateJob() method with incorrect jobId
            """)
    void updateJob_InvalidJobId_ThrowException() {
        // when
        when(jobRepository.findById(INCORRECT_JOB_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> jobServiceImpl.updateJob(INCORRECT_JOB_ID, diagnosticsRequestDto));

        // then
        String expected = EXCEPTION_FINDING + INCORRECT_JOB_ID;
        String actual = exception.getMessage();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify updateJobStatus() method
            """)
    void updateJobStatus_ValidJobStatus_ReturnResponseDto() {
        // when
        when(jobRepository.findById(DIAGNOSTICS_ID))
                .thenReturn(Optional.ofNullable(diagnostics));
        when(jobRepository.save(diagnostics.setStatus(Job.Status.PAID)))
                .thenReturn(diagnostics);
        when(jobMapper.toDto(diagnostics))
                .thenReturn(diagnosticsResponseDto.setStatus(PAID));

        // then
        JobResponseDto actual
                = jobServiceImpl.updateJobStatus(DIAGNOSTICS_ID, jobRequestStatusDto);
        assertNotNull(actual);
        assertEquals(diagnosticsResponseDto, actual);
    }
}
