package application.mapper;

import application.config.MapperConfig;
import application.dto.master.MasterRequestDto;
import application.dto.master.MasterResponseDto;
import application.model.Master;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface MasterMapper {
    Master toEntity(MasterRequestDto masterRequestDto);

    MasterResponseDto toDto(Master master);
}
