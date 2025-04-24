package org.almir.wallets.mapper;

import org.almir.wallets.dto.UserResponseDTO;
import org.almir.wallets.entity.User;
import org.almir.wallets.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserMapperTest {
    private final UserMapper mapper = UserMapper.INSTANCE;

    @Test
    void toResponseDto_success() {
        User user = User.builder()
                .id(1L)
                .name("Alice")
                .email("alice@example.com")
                .role(Role.USER)
                .build();

        UserResponseDTO dto = mapper.toResponseDto(user);

        assertNotNull(dto);
        assertEquals(user.getId(), dto.id());
        assertEquals(user.getName(), dto.name());
        assertEquals(user.getEmail(), dto.email());
        assertEquals(user.getRole(), dto.role());
    }
}
