package andrearepellini.phonebook.mapper;

import org.springframework.stereotype.Component;

import andrearepellini.phonebook.dto.ContactDTO;
import andrearepellini.phonebook.dto.CreateContactRequest;
import andrearepellini.phonebook.dto.PatchContactRequest;
import andrearepellini.phonebook.entity.Contact;

@Component
public class ContactMapper {

    public ContactDTO toDTO(Contact contact) {
        if (contact == null) {
            return null;
        }
        ContactDTO dto = new ContactDTO();
        dto.setId(contact.getId());
        dto.setFirstName(contact.getFirstName());
        dto.setLastName(contact.getLastName());
        dto.setAddress(contact.getAddress());
        dto.setPhoneNumber(contact.getPhoneNumber());
        dto.setAge(contact.getAge());
        dto.setDeleted(contact.getDeleted());
        return dto;
    }

    public Contact toEntity(CreateContactRequest request) {
        if (request == null) {
            return null;
        }
        Contact contact = new Contact();
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setAddress(request.getAddress());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setAge(request.getAge());
        contact.setDeleted(false);
        return contact;
    }

    public Contact toEntity(PatchContactRequest request) {
        if (request == null) {
            return null;
        }
        Contact contact = new Contact();
        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setAddress(request.getAddress());
        contact.setPhoneNumber(request.getPhoneNumber());
        contact.setAge(request.getAge());
        contact.setDeleted(request.getDeleted());
        return contact;
    }
}
