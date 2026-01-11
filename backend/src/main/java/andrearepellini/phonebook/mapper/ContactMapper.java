package andrearepellini.phonebook.mapper;

import org.springframework.stereotype.Component;

import andrearepellini.phonebook.dto.request.CreateContactRequest;
import andrearepellini.phonebook.dto.request.PatchContactRequest;
import andrearepellini.phonebook.dto.response.ContactResponse;
import andrearepellini.phonebook.entity.Contact;

@Component
public class ContactMapper {

    public ContactResponse toDTO(Contact contact) {
        if (contact == null) {
            return null;
        }
        ContactResponse dto = new ContactResponse();
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
