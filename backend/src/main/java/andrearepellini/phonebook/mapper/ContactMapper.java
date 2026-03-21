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
        return new ContactResponse(
                contact.getId(),
                contact.getFirstName(),
                contact.getLastName(),
                contact.getAddress(),
                contact.getPhoneNumber(),
                contact.getAge(),
                contact.getDeleted());
    }

    public Contact toEntity(CreateContactRequest request) {
        if (request == null) {
            return null;
        }
        Contact contact = new Contact();
        contact.setFirstName(request.firstName());
        contact.setLastName(request.lastName());
        contact.setAddress(request.address());
        contact.setPhoneNumber(request.phoneNumber());
        contact.setAge(request.age());
        contact.setDeleted(false);
        return contact;
    }

    public Contact toEntity(PatchContactRequest request) {
        if (request == null) {
            return null;
        }
        Contact contact = new Contact();
        contact.setFirstName(request.firstName());
        contact.setLastName(request.lastName());
        contact.setAddress(request.address());
        contact.setPhoneNumber(request.phoneNumber());
        contact.setAge(request.age());
        contact.setDeleted(request.deleted());
        return contact;
    }
}
