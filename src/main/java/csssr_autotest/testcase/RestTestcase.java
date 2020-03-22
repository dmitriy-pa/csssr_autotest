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
				.city("������ ��������")
				.fullName("������ ����")
				.gender(GenderEnum.M)
				.mainSkill("���������������� ���������������������")
				.phone("1112223344");
	}
	
	public static void assertEquals(CreateUpdateSuperhero expected, Superhero actual) {
		Assertions.assertEquals(expected.getBirthDate(), actual.getBirthDate(), "�� ������� ���� ��������");
		Assertions.assertEquals(expected.getCity(), actual.getCity(), "�� ������ �����");
		Assertions.assertEquals(expected.getFullName(), actual.getFullName(), "�� ������� ���");
		Assertions.assertEquals(expected.getGender().name(), actual.getGender().name(), "�� ������ ���");
		Assertions.assertEquals(expected.getMainSkill(), actual.getMainSkill(), "�� ������ �����");
		Assertions.assertEquals(expected.getPhone(), actual.getPhone(), "�� ������ �������");
	}
	
	public static void assertEquals(Superhero expected, Superhero actual) {
		Assertions.assertEquals(expected.getBirthDate(), actual.getBirthDate(), "�� ������� ���� ��������");
		Assertions.assertEquals(expected.getCity(), actual.getCity(), "�� ������ �����");
		Assertions.assertEquals(expected.getFullName(), actual.getFullName(), "�� ������� ���");
		Assertions.assertEquals(expected.getGender().name(), actual.getGender().name(), "�� ������ ���");
		Assertions.assertEquals(expected.getMainSkill(), actual.getMainSkill(), "�� ������ �����");
		Assertions.assertEquals(expected.getPhone(), actual.getPhone(), "�� ������ �������");
		Assertions.assertEquals(expected.getId(), actual.getId(), "�� ������ �������������");
	}
	
	@Test
	@DisplayName("�������� ������ �����")
	public void createSuperHero() throws ApiException, InterruptedException {
		
		CreateUpdateSuperhero newHero = newHero();
		Superhero createdHero = api.createUsingPOST(newHero);
		assertEquals(newHero, createdHero);
		
		List<Superhero> allHeroes = api.getAllUsingGET();
		Optional<Superhero> findedHero = allHeroes.stream().filter(h->createdHero.getId().equals(h.getId())).findAny();
		
		Assertions.assertTrue(findedHero.isPresent(), "��������� ����� �� ������ � id "+createdHero.getId().toString()+" � ������ ���� ������");
		assertEquals(createdHero, findedHero.get());
	}
	
	@Test
	@DisplayName("��������� ������ ���� ������")
	public void getAllSuperHero() throws ApiException {
		
		List<Superhero> allHeroes = api.getAllUsingGET();
		Assertions.assertFalse(allHeroes.isEmpty(), "������ ������ ����");
		
		Set<Long> ids = new HashSet<>();
		allHeroes.stream().map(h->h.getId()).forEach(id->{
			Assertions.assertFalse(ids.contains(id), "������ ������������� ������������� " + id.toString());
			ids.add(id);
		});
	}
	
	@Test
	@DisplayName("��������� ����� �� ��������������")
	public void getSuperHeroById() throws ApiException {
		Superhero createdHero = api.createUsingPOST(newHero());
		
		Superhero hero = api.getByIdUsingGET(createdHero.getId());
		assertEquals(createdHero, hero);
	}
	
	@Test
	@DisplayName("��������� ������ �����")
	public void updateSuperHero() throws ApiException {
		CreateUpdateSuperhero newHero = newHero();
		Superhero createdHero = api.createUsingPOST(newHero);
		
		newHero.setBirthDate(newHero.getBirthDate().minusYears(1).minusMonths(1).minusDays(1));
		newHero.setCity("�����������");
		newHero.setFullName("������ ����");
		newHero.setGender(GenderEnum.F);
		newHero.setMainSkill("������");
		newHero.setPhone("5556667788");
		api.updateUsingPUT(newHero, createdHero.getId());
		
		createdHero = api.getByIdUsingGET(createdHero.getId());
		assertEquals(newHero, createdHero);
	}
	
	@Test
	@DisplayName("�������� �����")
	public void removeSuperHero() throws ApiException {
		Superhero createdHero = api.createUsingPOST(newHero());
		
		api.removeUsingDELETE(createdHero.getId());
		
		try {
			api.getByIdUsingGET(createdHero.getId());
			Assertions.fail("����� �� ������, id: " + createdHero.getId().toString());
		} catch (ApiException e) {
			Assertions.assertEquals(404, e.getCode(), "�� ������ ��� ������");
		}
	}
}
