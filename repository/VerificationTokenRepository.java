package ir.ac.loging.repository;

import ir.ac.loging.model.VerificationToken;
import org.aspectj.apache.bcel.util.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Integer> {
    List<VerificationToken> findByEmailAndCode(String email, String code);
    void deleteByEmail(String email);
}
