package ir.imorate.cs50rm.service.customer.Impl;

import ir.imorate.cs50rm.entity.customer.Customer;
import ir.imorate.cs50rm.exception.customer.CustomerNotFoundException;
import ir.imorate.cs50rm.repository.customer.CustomerRepository;
import ir.imorate.cs50rm.service.customer.AddressService;
import ir.imorate.cs50rm.service.customer.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AddressService addressService;

    @Override
    public Customer findCustomer(String email) throws CustomerNotFoundException {
        return customerRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new CustomerNotFoundException(String.format("Customer with email %s not found", email)));
    }

    @Override
    public void removeCustomer(Customer customer) {
        customerRepository.delete(customer);
    }

    @Override
    public boolean isCustomerExist(String email) {
        return customerRepository.findByEmailIgnoreCase(email).isPresent();
    }

    @Override
    public boolean isCustomerExistWithPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    @Override
    @Transactional
    public void createCustomer(Customer customer) {
        customerRepository.save(customer);
        addressService.save(customer.getAddress());
    }

    @Override
    public List<Customer> listAll() {
        return customerRepository.findAll();
    }

    @Override
    public void save(Customer customer) {
        customerRepository.save(customer);
    }

}
