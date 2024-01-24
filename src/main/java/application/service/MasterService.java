package application.service;

import application.dto.master.MasterRequestDto;
import application.dto.master.MasterResponseDto;
import application.dto.order.OrderResponseDto;
import java.math.BigDecimal;
import java.util.List;

public interface MasterService {
    MasterResponseDto createMaster(MasterRequestDto masterRequestDto);

    MasterResponseDto updateById(Long id, MasterRequestDto masterRequestDto);

    List<OrderResponseDto> getAllSuccessfulOrdersByMasterId(Long id);

    BigDecimal getSalaryByMasterId(Long id);
}
