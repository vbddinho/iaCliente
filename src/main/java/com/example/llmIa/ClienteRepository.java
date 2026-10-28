package com.example.llmIa;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>{

    
    Optional<Cliente> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);
}
