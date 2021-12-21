package ir.imorate.cs50rm.service.document;

import ir.imorate.cs50rm.entity.customer.Customer;
import ir.imorate.cs50rm.entity.document.Contract;

import java.util.List;

public interface ContractService {

    void createContract(Contract contract, Customer customer);

    Contract findContract(Long id);

    void acceptContract(Contract contract);

    void closeContract(Contract contract);

    void save(Contract contract);

    List<Contract> listAll();

}
