package application.service.impl;

import application.dto.good.GoodRequestDto;
import application.dto.good.GoodResponseDto;
import application.exception.EntityNotFoundException;
import application.mapper.GoodMapper;
import application.model.Good;
import application.repository.GoodRepository;
import application.service.GoodService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GoodServiceImpl implements GoodService {
    private static final String EXCEPTION = "Can't find good by id ";
    private final GoodMapper goodMapper;
    private final GoodRepository goodRepository;

    @Override
    public GoodResponseDto createGood(GoodRequestDto goodRequestDto) {
        return goodMapper.toDto(goodRepository.save(goodMapper.toEntity(goodRequestDto)));
    }

    @Override
    @Transactional
    public GoodResponseDto updateGood(Long id, GoodRequestDto goodRequestDto) {
        if (goodRepository.findById(id).isPresent()) {
            Good newGood = goodMapper.toEntity(goodRequestDto).setId(id);
            return goodMapper.toDto(goodRepository.save(newGood));
        }
        throw new EntityNotFoundException(EXCEPTION + id);
    }
}
