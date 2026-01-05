package andrearepellini.phonebook.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import andrearepellini.phonebook.entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query("SELECT c FROM Contact c WHERE (c.firstName LIKE %?1% OR c.lastName LIKE %?2%) AND c.deleted = ?3")
    Page<Contact> findByFirstNameContainingOrLastNameContainingAndDeleted(String firstName, String lastName,
            Boolean deleted, Pageable pageable);

    Page<Contact> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName, Pageable pageable);

    Page<Contact> findByDeleted(Boolean deleted, Pageable pageable);
}
