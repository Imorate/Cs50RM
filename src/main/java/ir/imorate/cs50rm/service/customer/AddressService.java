package ir.imorate.cs50rm.service.customer;

import ir.imorate.cs50rm.entity.customer.Address;

import java.util.List;

public interface AddressService {

    void save(Address address);

    List<Address> listAll();

}
