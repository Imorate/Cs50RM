package ir.imorate.cs50rm.service.customer;

import ir.imorate.cs50rm.entity.customer.Customer;
import ir.imorate.cs50rm.exception.customer.CustomerNotFoundException;

import java.util.List;

public interface CustomerService {

    Customer findCustomer(String email) throws CustomerNotFoundException;

    void removeCustomer(Customer customer);

    boolean isCustomerExist(String email);

    boolean isCustomerExistWithPhoneNumber(String phoneNumber);

    void createCustomer(Customer customer);

    List<Customer> listAll();

    void save(Customer customer);

}
