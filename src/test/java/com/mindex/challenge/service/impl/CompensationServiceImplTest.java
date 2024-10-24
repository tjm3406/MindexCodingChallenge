package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationUrl;



    @Autowired
    private CompensationRepository compensationRepository;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:" + port + "/compensation";
    }

    public Compensation createEmployee() {
        // Create an employee
        Employee john = new Employee();
        john.setEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        john.setFirstName("John");
        john.setLastName("Lennon");
        john.setPosition("Development Manager");
        john.setDepartment("Engineering");

        // Create compensation for the employee
        Compensation compensation = new Compensation();
        compensation.setEmployee(john);
        compensation.setSalary(100000);
        compensation.setEffectiveDate(LocalDate.of(2023, 1, 1));

        return compensation;
    }

    @Test
    public void testCreateCompensation() {

        Compensation compensation = createEmployee();

        // Call the create endpoint
        Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, compensation, Compensation.class).getBody();

        // Assert that the compensation was created successfully
        assertNotNull(createdCompensation);
        assertEquals(compensation.getSalary(), createdCompensation.getSalary());
        assertEquals(compensation.getEffectiveDate(), createdCompensation.getEffectiveDate());
    }

    @Test
    public void testReadCompensation() {

        // Fetch the compensation by employeeId (assuming compensation has been created)
        Compensation fetchedCompensation = restTemplate.getForEntity(compensationUrl + "/16a596ae-edd3-4847-99fe-c4518e82c86f", Compensation.class).getBody();

        // Assert that the fetched data is correct
        assertNotNull(fetchedCompensation);
        assertEquals("16a596ae-edd3-4847-99fe-c4518e82c86f", fetchedCompensation.getEmployee().getEmployeeId());
        assertEquals(100000, fetchedCompensation.getSalary());
        assertEquals(LocalDate.of(2023, 1, 1), fetchedCompensation.getEffectiveDate());
    }


}
