package csssr_autotest.testcase;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import csssr_autotest.soap_api.AddCompanyRequest;
import csssr_autotest.soap_api.AddEmployeeRequest;
import csssr_autotest.soap_api.AddEmployeesToCompanyRequest;
import csssr_autotest.soap_api.CompanyResource;
import csssr_autotest.soap_api.CompanyResourceService;
import csssr_autotest.soap_api.CompanyType;
import csssr_autotest.soap_api.EmployeeType;
import csssr_autotest.soap_api.GetCompanyRequest;
import csssr_autotest.soap_api.UpdateEmployeeRequest;

public class SoapTestcase {
	
	private CompanyResource port = new CompanyResourceService().getCompanyResourcePort();
	
	public static AddCompanyRequest addCompanyRequest(String name) {
		AddCompanyRequest request = new AddCompanyRequest();
		request.setName(name);
		return request;
	}
	
	public static GetCompanyRequest getCompanyRequest(String id) {
		GetCompanyRequest request = new GetCompanyRequest();
		request.setCompanyId(id);
		return request;
	}
	
	public static AddEmployeeRequest addEmployeeRequest(String lastName, String firstName, String middleName) {
		AddEmployeeRequest request = new AddEmployeeRequest();
		request.setFirstName(firstName);
		request.setMiddleName(middleName);
		request.setLastName(lastName);
		return request;
	}
	
	public static AddEmployeesToCompanyRequest addEmployeesToCompanyRequest(CompanyType company, EmployeeType... employees) {
		AddEmployeesToCompanyRequest request = new AddEmployeesToCompanyRequest();
		
		request.setCompanyId(company.getId());
		for(EmployeeType employee : employees) { 
			request.getEmployeeId().add(employee.getId());
		}
		
		return request;
	}
	
	public static UpdateEmployeeRequest updateEmployeeRequest(String id, String lastName, String firstName, String middleName) {
		UpdateEmployeeRequest request = new UpdateEmployeeRequest();
		request.setId(id);
		request.setFirstName(firstName);
		request.setMiddleName(middleName);
		request.setLastName(lastName);
		return request;
	}
	
	public static void assertEquals(CompanyType expected, CompanyType actual) {
		Assertions.assertEquals(expected.getCreatedAt(), actual.getCreatedAt(), "Не совпала дата создания");
		Assertions.assertEquals(expected.getId(), actual.getId(), "Не совпал идентификатор");
		Assertions.assertEquals(expected.getName(), actual.getName(), "Не совпало название");
		Assertions.assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt(), "Не совпала дата изменения");
	}
	
	public static void assertEquals(EmployeeType expected, EmployeeType actual) {
		Assertions.assertEquals(expected.getCreatedAt(), actual.getCreatedAt(), "Не совпала дата создания");
		Assertions.assertEquals(expected.getId(), actual.getId(), "Не совпал идентификатор");
		Assertions.assertEquals(expected.getUpdatedAt(), actual.getUpdatedAt(), "Не совпала дата изменения");
		Assertions.assertEquals(expected.getFirstName(), actual.getFirstName(), "Не совпало имя");
		Assertions.assertEquals(expected.getMiddleName(), actual.getMiddleName(), "Не совпало отчество");
		Assertions.assertEquals(expected.getLastName(), actual.getLastName(), "Не совпала фамилия");
		
	}
	
	public static void checkEmployees(CompanyType company, EmployeeType... employees) {
		Assertions.assertEquals(employees.length, company.getEmployees().size(), "Не совпало количество сотрудников компании");
		
		for(EmployeeType emp : employees) {
			List<EmployeeType> filtered = company.getEmployees().stream()
					.filter(e -> emp.getId().equals(e.getId())).collect(Collectors.toList());
			
			if (filtered.size() == 0) {
				Assertions.fail(String.format("У компании id:%s не найден сотрудник id:%s", company.getId(), emp.getId()));
			}
			
			if (filtered.size() > 1) {
				Assertions.fail(String.format("У компании id:%s найдено более одного сотрудника id:%s", company.getId(), emp.getId()));
			}
			
			assertEquals(emp, filtered.get(0));
		}
	}
	
	@Test
	@DisplayName("Добавление компании")
	public void addCompany() {
		String companyName = "test 123";
		CompanyType companyCreated = port.addCompany(addCompanyRequest(companyName)).getCompany();
		Assertions.assertEquals(companyName, companyCreated.getName(), "Не совпало название компании");
		
		Assertions.assertEquals(companyCreated.getCreatedAt(), companyCreated.getUpdatedAt(), "Дата обновления не равна дате создания");
		
		CompanyType companyToCheck = port.getCompany(getCompanyRequest(companyCreated.getId())).getCompany();
		assertEquals(companyCreated, companyToCheck);
	}
	
	@Test
	@DisplayName("Добавление сотрудника")
	public void addEmployee() {
		String lastName = "Иванов";
		String firstName = "Иван";
		String middleName = "Иванович";
		
		EmployeeType employeeCreated = port.addEmployee(addEmployeeRequest(lastName, firstName, middleName)).getEmployee();
		Assertions.assertEquals(firstName, employeeCreated.getFirstName(), "Не совпало имя");
		Assertions.assertEquals(middleName, employeeCreated.getMiddleName(), "Не совпало отчество");
		Assertions.assertEquals(lastName, employeeCreated.getLastName(), "Не совпала фамилия");
	}
	
	@Test
	@DisplayName("Добавление сотрудника к компании и обновление сотрудника")
	public void addEmployeeToCompany() {
		EmployeeType employee1 = port.addEmployee(addEmployeeRequest("Иванов", "Иван", "Иванович")).getEmployee();
		EmployeeType employee2 = port.addEmployee(addEmployeeRequest("Сергеев", "Сергей", "Сергеевич")).getEmployee();
		CompanyType company = port.addCompany(addCompanyRequest("Компания 123")).getCompany();
		
		company = port.addEmployeesToCompany(addEmployeesToCompanyRequest(company, employee1, employee2)).getCompany();
		checkEmployees(company, employee1, employee2);
		
		company = port.getCompany(getCompanyRequest(company.getId())).getCompany();
		checkEmployees(company, employee1, employee2);
		
		employee1 = port.updateEmployee(updateEmployeeRequest(employee1.getId(), "123", "456", "789")).getEmployee();
		employee2 = port.updateEmployee(updateEmployeeRequest(employee2.getId(), "qqq", "www", "eee")).getEmployee();
		
		company = port.getCompany(getCompanyRequest(company.getId())).getCompany();
		checkEmployees(company, employee1, employee2);
	}
}
