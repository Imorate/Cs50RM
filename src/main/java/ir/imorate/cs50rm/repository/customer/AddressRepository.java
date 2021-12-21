package ir.imorate.cs50rm.repository.customer;

import ir.imorate.cs50rm.entity.customer.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

}