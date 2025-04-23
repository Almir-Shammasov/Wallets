package org.almir.wallets.mapper;

import org.almir.wallets.dto.UserRequestDTO;
import org.almir.wallets.dto.UserResponseDTO;
import org.almir.wallets.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponseDTO toResponseDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "cards", ignore = true)
    User toEntity(UserRequestDTO requestDto);
}
