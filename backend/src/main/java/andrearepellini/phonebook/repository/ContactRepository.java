package andrearepellini.phonebook.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import andrearepellini.phonebook.entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    Page<Contact> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName, Pageable pageable);
}
