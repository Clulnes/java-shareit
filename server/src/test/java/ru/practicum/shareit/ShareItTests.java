package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class ShareItTests {

	@Autowired
	private ItemService itemService;

	@Autowired
	private UserRepository userRepository;

	@Test
	void getAllUserItems_shouldReturnItemsWithCorrectOwner() {
		User user = new User(null, "Тест пользователь", "test@mail.ru");
		User savedUser = userRepository.save(user);

		ItemDto itemDto1 = new ItemDto(null, "Вещь 1", "Описание 1", true, null, null, null, null);
		ItemDto itemDto2 = new ItemDto(null, "Вещь 2", "Описание 2", true, null, null, null, null);

		itemService.createItem(savedUser.getId(), itemDto1);
		itemService.createItem(savedUser.getId(), itemDto2);

		List<ItemDto> items = itemService.getItemsByUserId(savedUser.getId());

		assertThat(items).hasSize(2);
		assertThat(items.get(0).getName()).isEqualTo("Вещь 1");
		assertThat(items.get(1).getName()).isEqualTo("Вещь 2");
	}

}
