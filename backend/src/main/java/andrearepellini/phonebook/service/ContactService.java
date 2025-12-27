package andrearepellini.phonebook.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import andrearepellini.phonebook.entity.Contact;
import andrearepellini.phonebook.repository.ContactRepository;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public Page<Contact> getAllContacts(String filter, Pageable pageable) {
        if (filter != null && !filter.isEmpty()) {
            return contactRepository.findByFirstNameContainingOrLastNameContaining(filter, filter, pageable);
        }
        return contactRepository.findAll(pageable);
    }

    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    public Contact createContact(Contact contact) {
        return contactRepository.save(contact);
    }

    public Contact patchContact(Long id, Contact contactDetails) {
        return contactRepository.findById(id).map(contact -> {
            if (contactDetails.getFirstName() != null) {
                contact.setFirstName(contactDetails.getFirstName());
            }
            if (contactDetails.getLastName() != null) {
                contact.setLastName(contactDetails.getLastName());
            }
            if (contactDetails.getAddress() != null) {
                contact.setAddress(contactDetails.getAddress());
            }
            if (contactDetails.getPhoneNumber() != null) {
                contact.setPhoneNumber(contactDetails.getPhoneNumber());
            }
            if (contactDetails.getAge() != null) {
                contact.setAge(contactDetails.getAge());
            }
            if (contactDetails.getDeleted() != null) {
                contact.setDeleted(contactDetails.getDeleted());
            }
            return contactRepository.save(contact);
        }).orElseThrow(() -> new RuntimeException("Contact with id " + id + " not found"));
    }
}
