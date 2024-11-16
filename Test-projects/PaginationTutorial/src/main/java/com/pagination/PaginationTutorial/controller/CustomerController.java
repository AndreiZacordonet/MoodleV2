package com.pagination.PaginationTutorial.controller;

import com.pagination.PaginationTutorial.models.Customer;
import com.pagination.PaginationTutorial.models.CustomerModel;
import com.pagination.PaginationTutorial.models.CustomerModelAssembler;
import com.pagination.PaginationTutorial.repository.CustomerRepository;
import com.pagination.PaginationTutorial.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerModelAssembler customerModelAssembler;

    @Autowired
    private CustomerModelAssembler pagedResourcesAssembler;


    /**
     * @param firstNameFilter Filter for the first Name if required
     * @param lastNameFilter  Filter for the last Name if required
     * @param page            number of the page returned
     * @param size            number of entries in each page
     * @param sortList        list of columns to sort on
     * @param sortOrder       sort order. Can be ASC or DESC
     * @return PagedModel object in Hateoas with customers after filtering and sorting
     */
    @GetMapping("/api/v4/customers")
    public PagedModel<CustomerModel> fetchCustomersWithPagination(
            @RequestParam(defaultValue = "") String firstNameFilter,
            @RequestParam(defaultValue = "") String lastNameFilter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "") List<String> sortList,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortOrder) {
        Page<Customer> customerPage = customerService.fetchCustomerDataAsPageWithFilteringAndSorting(firstNameFilter, lastNameFilter, page, size, sortList, sortOrder.toString());
        // Use the pagedResourcesAssembler and customerModelAssembler to convert data to PagedModel format
        return pagedResourcesAssembler.toModel(customerPage, customerModelAssembler);
    }




}
