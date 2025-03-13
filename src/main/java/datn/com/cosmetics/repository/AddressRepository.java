package datn.com.cosmetics.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import datn.com.cosmetics.entity.Address;
import datn.com.cosmetics.entity.User;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    // Custom query methods can be added here
    List<Address> findByUser(User user);

    boolean existsByStreetAddressAndUser(String streetAddress, User user);

    @Query(value = "SELECT * FROM address WHERE user_id = ?1 AND is_default = true", nativeQuery = true)
    Optional<Address> findByUserAndDefaultAddressTrue(Long user);
}
