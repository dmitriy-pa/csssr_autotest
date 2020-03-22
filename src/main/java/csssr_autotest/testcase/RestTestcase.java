package csssr_autotest.testcase;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import csssr_autotest.rest_api.ApiException;
import csssr_autotest.rest_api.api.SuperheroControllerApi;
import csssr_autotest.rest_api.model.CreateUpdateSuperhero;
import csssr_autotest.rest_api.model.CreateUpdateSuperhero.GenderEnum;
import csssr_autotest.rest_api.model.Superhero;

public class RestTestcase {
	
	private SuperheroControllerApi api = new SuperheroControllerApi();
	
	public static CreateUpdateSuperhero newHero() {
		return new CreateUpdateSuperhero()
				.birthDate(org.threeten.bp.LocalDate.now().minusYears(20))
				.city("Нижний Новгород")
				.fullName("Петров Петр")
				.gender(GenderEnum.M)
				.mainSkill("Аэросейсмическая сверхчувствительность")
				.phone("1112223344");
	}
	
	public static void assertEquals(CreateUpdateSuperhero expected, Superhero actual) {
		Assertions.assertEquals(expected.getBirthDate(), actual.getBirthDate(), "Не совпала дата рождения");
		Assertions.assertEquals(expected.getCity(), actual.getCity(), "Не совпал город");
		Assertions.assertEquals(expected.getFullName(), actual.getFullName(), "Не совпало имя");
		Assertions.assertEquals(expected.getGender().name(), actual.getGender().name(), "Не совпал пол");
		Assertions.assertEquals(expected.getMainSkill(), actual.getMainSkill(), "Не совпал навык");
		Assertions.assertEquals(expected.getPhone(), actual.getPhone(), "Не совпал телефон");
	}
	
	public static void assertEquals(Superhero expected, Superhero actual) {
		Assertions.assertEquals(expected.getBirthDate(), actual.getBirthDate(), "Не совпала дата рождения");
		Assertions.assertEquals(expected.getCity(), actual.getCity(), "Не совпал город");
		Assertions.assertEquals(expected.getFullName(), actual.getFullName(), "Не совпало имя");
		Assertions.assertEquals(expected.getGender().name(), actual.getGender().name(), "Не совпал пол");
		Assertions.assertEquals(expected.getMainSkill(), actual.getMainSkill(), "Не совпал навык");
		Assertions.assertEquals(expected.getPhone(), actual.getPhone(), "Не совпал телефон");
		Assertions.assertEquals(expected.getId(), actual.getId(), "Не совпал идентификатор");
	}
	
	@Test
	@DisplayName("Создание нового героя")
	public void createSuperHero() throws ApiException, InterruptedException {
		
		CreateUpdateSuperhero newHero = newHero();
		Superhero createdHero = api.createUsingPOST(newHero);
		assertEquals(newHero, createdHero);
		
		List<Superhero> allHeroes = api.getAllUsingGET();
		Optional<Superhero> findedHero = allHeroes.stream().filter(h->createdHero.getId().equals(h.getId())).findAny();
		
		Assertions.assertTrue(findedHero.isPresent(), "Созданный герой не найден с id "+createdHero.getId().toString()+" в списке всех героев");
		assertEquals(createdHero, findedHero.get());
	}
	
	@Test
	@DisplayName("Получение списка всех героев")
	public void getAllSuperHero() throws ApiException {
		
		List<Superhero> allHeroes = api.getAllUsingGET();
		Assertions.assertFalse(allHeroes.isEmpty(), "Список героев пуст");
		
		Set<Long> ids = new HashSet<>();
		allHeroes.stream().map(h->h.getId()).forEach(id->{
			Assertions.assertFalse(ids.contains(id), "Найден дублированный идентификатор " + id.toString());
			ids.add(id);
		});
	}
	
	@Test
	@DisplayName("Получение героя по идентификатору")
	public void getSuperHeroById() throws ApiException {
		Superhero createdHero = api.createUsingPOST(newHero());
		
		Superhero hero = api.getByIdUsingGET(createdHero.getId());
		assertEquals(createdHero, hero);
	}
	
	@Test
	@DisplayName("Изменение данных героя")
	public void updateSuperHero() throws ApiException {
		CreateUpdateSuperhero newHero = newHero();
		Superhero createdHero = api.createUsingPOST(newHero);
		
		newHero.setBirthDate(newHero.getBirthDate().minusYears(1).minusMonths(1).minusDays(1));
		newHero.setCity("Владивосток");
		newHero.setFullName("Иванов Иван");
		newHero.setGender(GenderEnum.F);
		newHero.setMainSkill("йцукен");
		newHero.setPhone("5556667788");
		api.updateUsingPUT(newHero, createdHero.getId());
		
		createdHero = api.getByIdUsingGET(createdHero.getId());
		assertEquals(newHero, createdHero);
	}
	
	@Test
	@DisplayName("Удаление героя")
	public void removeSuperHero() throws ApiException {
		Superhero createdHero = api.createUsingPOST(newHero());
		
		api.removeUsingDELETE(createdHero.getId());
		
		try {
			api.getByIdUsingGET(createdHero.getId());
			Assertions.fail("Герой не удален, id: " + createdHero.getId().toString());
		} catch (ApiException e) {
			Assertions.assertEquals(404, e.getCode(), "Не совпал код ошибки");
		}
	}
}
