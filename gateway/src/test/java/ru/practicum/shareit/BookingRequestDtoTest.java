package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class BookingRequestDtoTest {

    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime start = LocalDateTime.of(2030, 10, 10, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2030, 10, 12, 10, 0, 0);
        BookItemRequestDto dto = new BookItemRequestDto(1L, start, end);

        JsonContent<BookItemRequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2030-10-10T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2030-10-12T10:00:00");
    }
}
