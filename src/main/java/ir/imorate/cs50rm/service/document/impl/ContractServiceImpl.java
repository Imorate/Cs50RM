package ir.imorate.cs50rm.service.document.impl;

import ir.imorate.cs50rm.entity.customer.Customer;
import ir.imorate.cs50rm.entity.document.Contract;
import ir.imorate.cs50rm.exception.document.ContractNotFoundException;
import ir.imorate.cs50rm.repository.document.ContractRepository;
import ir.imorate.cs50rm.service.document.ContractService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;

    @Override
    public void createContract(Contract contract, Customer customer) {
        contract.setCustomer(customer);
        contractRepository.save(contract);
    }

    @Override
    public Contract findContract(Long id) {
        return contractRepository.findById(id).orElseThrow(() -> new ContractNotFoundException("Contract not found"));
    }

    @Override
    public void acceptContract(Contract contract) {
        contract.setStatus(true);
        contractRepository.save(contract);
    }

    @Override
    public void closeContract(Contract contract) {
        contract.setClosed(true);
        contractRepository.save(contract);
    }

    @Override
    public void save(Contract contract) {
        contractRepository.save(contract);
    }

    @Override
    public List<Contract> listAll() {
        return contractRepository.findByClosedIsFalse();
    }
}
