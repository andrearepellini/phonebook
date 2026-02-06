package andrearepellini.phonebook.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import andrearepellini.phonebook.entity.Contact;
import andrearepellini.phonebook.entity.User;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query("SELECT c FROM Contact c WHERE c.user = ?1 AND (c.firstName LIKE %?2% OR c.lastName LIKE %?3%) AND c.deleted = ?4")
    Page<Contact> findByUserFilteredAndDeleted(
            User user,
            String firstName,
            String lastName,
            Boolean deleted,
            Pageable pageable);

    @Query("SELECT c FROM Contact c WHERE c.user = ?1 AND (c.firstName LIKE %?2% OR c.lastName LIKE %?3%)")
    Page<Contact> findByUserFiltered(
            User user,
            String firstName,
            String lastName,
            Pageable pageable);

    Page<Contact> findByUserAndDeleted(User user, Boolean deleted, Pageable pageable);

    Page<Contact> findByUser(User user, Pageable pageable);

    Optional<Contact> findByIdAndUser(Long id, User user);
}
