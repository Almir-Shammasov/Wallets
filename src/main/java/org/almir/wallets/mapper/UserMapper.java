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
}
