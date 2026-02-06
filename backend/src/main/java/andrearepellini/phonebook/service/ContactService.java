package andrearepellini.phonebook.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import andrearepellini.phonebook.entity.Contact;
import andrearepellini.phonebook.entity.User;
import andrearepellini.phonebook.repository.ContactRepository;
import andrearepellini.phonebook.repository.UserRepository;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public ContactService(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User not authenticated");
        }
        Object principal = authentication.getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("User not found: " + email));
    }

    public Page<Contact> getAllContacts(String filter, Boolean deleted, Pageable pageable) {
        User currentUser = getCurrentUser();
        boolean hasFilter = filter != null && !filter.isEmpty();
        boolean hasDeleted = deleted != null;

        if (hasFilter && hasDeleted) {
            return contactRepository.findByUserFilteredAndDeleted(currentUser, filter, filter, deleted, pageable);
        } else if (hasFilter) {
            return contactRepository.findByUserFiltered(currentUser, filter, filter, pageable);
        } else if (hasDeleted) {
            return contactRepository.findByUserAndDeleted(currentUser, deleted, pageable);
        }

        return contactRepository.findByUser(currentUser, pageable);
    }

    public Optional<Contact> getContactById(Long id) {
        User currentUser = getCurrentUser();
        return contactRepository.findByIdAndUser(id, currentUser);
    }

    public Contact createContact(Contact contact) {
        User currentUser = getCurrentUser();
        contact.setUser(currentUser);
        return contactRepository.save(contact);
    }

    public Optional<Contact> patchContact(Long id, Contact contactDetails) {
        User currentUser = getCurrentUser();
        return contactRepository.findByIdAndUser(id, currentUser).map(contact -> {
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
        });
    }
}
