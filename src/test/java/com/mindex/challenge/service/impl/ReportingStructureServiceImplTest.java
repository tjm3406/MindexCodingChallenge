package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureIdUrl;


    @Autowired
    private ReportingStructureService reportingStructureService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureIdUrl = "http://localhost:" + port + "/reportingstructure/{id}";
    }

    @Test
    public void testSingleEmployee() {
        Employee singleEmployee = new Employee();
        singleEmployee.setFirstName("Single");
        singleEmployee.setLastName("Employee");

        // create employee id
        singleEmployee = restTemplate.postForEntity(employeeUrl, singleEmployee, Employee.class).getBody();

        // test read reporting structure
        ReportingStructure singleEmployeeReportingStructure = restTemplate.getForEntity(reportingStructureIdUrl, ReportingStructure.class, singleEmployee.getEmployeeId()).getBody();

        assertNotNull(singleEmployeeReportingStructure);
        assertEquals(0, singleEmployeeReportingStructure.getNumberOfReports());
    }

    @Test
    public void testOneDirectReport() {
        Employee manager = new Employee();
        manager.setFirstName("Manager");
        manager.setLastName("Employee");

        // create employee id
        manager = restTemplate.postForEntity(employeeUrl, manager, Employee.class).getBody();

        Employee report = new Employee();
        report.setFirstName("Report");
        report.setLastName("Employee");

        // create employee id
        report = restTemplate.postForEntity(employeeUrl, report, Employee.class).getBody();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // set direct report
        ArrayList<Employee> managerReports = new ArrayList<>();
        managerReports.add(report);
        manager.setDirectReports(managerReports);
        manager = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, new HttpEntity<Employee>(manager, headers), Employee.class, manager).getBody();

        // test read reporting structure
        ReportingStructure managerReportingStructure = restTemplate.getForEntity(reportingStructureIdUrl, ReportingStructure.class, manager.getEmployeeId()).getBody();

        assertNotNull(managerReportingStructure);
        assertEquals(1, managerReportingStructure.getNumberOfReports());
    }

    @Test
    public void testTopEmployee() {
        // create employees to test
        Employee john = new Employee();
        john.setFirstName("John");
        john.setLastName("Lennon");

        Employee paul = new Employee();
        paul.setFirstName("Paul");
        paul.setLastName("Mccartney");

        Employee ringo = new Employee();
        ringo.setFirstName("Ringo");
        ringo.setLastName("Starr");

        Employee pete = new Employee();
        pete.setFirstName("Pete");
        pete.setLastName("Best");

        Employee george = new Employee();
        george.setFirstName("George");
        george.setLastName("Harrison");

        // create employee ids
        john = restTemplate.postForEntity(employeeUrl, john, Employee.class).getBody();
        ringo = restTemplate.postForEntity(employeeUrl, ringo, Employee.class).getBody();
        paul = restTemplate.postForEntity(employeeUrl, paul, Employee.class).getBody();
        pete = restTemplate.postForEntity(employeeUrl, pete, Employee.class).getBody();
        george = restTemplate.postForEntity(employeeUrl, george, Employee.class).getBody();

        // create employee report lists
        paul.setDirectReports(new ArrayList<>());
        george.setDirectReports(new ArrayList<>());
        pete.setDirectReports(new ArrayList<>());
        ArrayList<Employee> johnReports = new ArrayList<>();
        johnReports.add(paul);
        johnReports.add(ringo);
        ArrayList<Employee> ringoReports = new ArrayList<>();
        ringoReports.add(pete);
        ringoReports.add(george);

        john.setDirectReports(johnReports);
        ringo.setDirectReports(ringoReports);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        john = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, new HttpEntity<Employee>(john, headers), Employee.class, john).getBody();
        paul = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, new HttpEntity<Employee>(paul, headers), Employee.class, paul).getBody();
        ringo = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, new HttpEntity<Employee>(ringo, headers), Employee.class, ringo).getBody();
        pete = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, new HttpEntity<Employee>(pete, headers), Employee.class, pete).getBody();
        george = restTemplate.exchange(employeeIdUrl, HttpMethod.PUT, new HttpEntity<Employee>(george, headers), Employee.class, george).getBody();

        // test read reporting structure
        ReportingStructure johnReportingStructure = restTemplate.getForEntity(reportingStructureIdUrl, ReportingStructure.class, john.getEmployeeId()).getBody();
        ReportingStructure ringoReportingStructure = restTemplate.getForEntity(reportingStructureIdUrl, ReportingStructure.class, ringo.getEmployeeId()).getBody();
        ReportingStructure paulReportingStructure = restTemplate.getForEntity(reportingStructureIdUrl, ReportingStructure.class, paul.getEmployeeId()).getBody();

        assertNotNull(johnReportingStructure);
        assertEquals(4, johnReportingStructure.getNumberOfReports());
        assertNotNull(ringoReportingStructure);
        assertEquals(2, ringoReportingStructure.getNumberOfReports());
        assertNotNull(paulReportingStructure);
        assertEquals(0, paulReportingStructure.getNumberOfReports());

    }
}
