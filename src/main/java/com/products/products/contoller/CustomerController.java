package com.products.products.contoller;



import com.products.products.models.Customer;
import com.products.products.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    // Get all customers
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Add new customer
    @PostMapping
    public Customer addCustomer(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Customer not found with id " + id));
    }
    
    // Optional: Get customer by phone number
    @GetMapping("/phone/{phoneNumber}")
    public Customer getCustomerByPhone(@PathVariable String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber);
    }

    // Optional: Delete customer
    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerRepository.deleteById(id);
    }

    // Optional: Update customer
    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer updatedCustomer) {
        return customerRepository.findById(id).map(customer -> {
            customer.setName(updatedCustomer.getName());
            customer.setPhoneNumber(updatedCustomer.getPhoneNumber());
            customer.setGstin(updatedCustomer.getGstin());
            customer.setType(updatedCustomer.getType());
            return customerRepository.save(customer);
        }).orElseThrow(() -> new RuntimeException("Customer not found with id " + id));
    }
}
