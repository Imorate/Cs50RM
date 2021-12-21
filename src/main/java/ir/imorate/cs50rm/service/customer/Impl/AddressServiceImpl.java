package ir.imorate.cs50rm.service.customer.Impl;

import ir.imorate.cs50rm.entity.customer.Address;
import ir.imorate.cs50rm.repository.customer.AddressRepository;
import ir.imorate.cs50rm.service.customer.AddressService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

    @Override
    public void save(Address address) {
        addressRepository.save(address);
    }

    @Override
    public List<Address> listAll() {
        return addressRepository.findAll();
    }

}
