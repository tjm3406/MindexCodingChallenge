package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ReportingStructure read(String id) {
        LOG.debug("Creating reporting structure with id [{}]", id);
        Set<String> uniqueReports = new HashSet<>();
        uniqueReports.add(id);

        ReportingStructure reportingStructure = new ReportingStructure();
        Employee employee = employeeRepository.findByEmployeeId(id);
        int numberOfReports = calcReports(employee, uniqueReports);

        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(numberOfReports);

        return reportingStructure;
    }

    private int calcReports(Employee employee, Set<String> uniqueReports) {
        int count = 0;

        // increment count only if the id is unique
        if (uniqueReports.add(employee.getEmployeeId())) {
            count = 1;
        }

        if(employee.getDirectReports() == null) {
            return count;
        }

        // recursive call on every employee to count direct reports
        for (Employee directReport : employee.getDirectReports()) {
            count += calcReports(directReport, uniqueReports);
        }
        return count;
    }


}
