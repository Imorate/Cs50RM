package ir.imorate.cs50rm.repository.document;

import ir.imorate.cs50rm.entity.document.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    List<Contract> findByClosedIsFalse();

}