package application.service;

import application.dto.good.GoodRequestDto;
import application.dto.good.GoodResponseDto;

public interface GoodService {
    GoodResponseDto createGood(GoodRequestDto goodRequestDto);

    GoodResponseDto updateGood(Long id, GoodRequestDto goodRequestDto);
}
