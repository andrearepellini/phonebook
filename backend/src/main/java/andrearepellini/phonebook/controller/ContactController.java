package andrearepellini.phonebook.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import andrearepellini.phonebook.dto.ContactDTO;
import andrearepellini.phonebook.dto.CreateContactRequest;
import andrearepellini.phonebook.dto.PatchContactRequest;
import andrearepellini.phonebook.entity.Contact;
import andrearepellini.phonebook.mapper.ContactMapper;
import andrearepellini.phonebook.service.ContactService;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;
    private final ContactMapper contactMapper;

    public ContactController(ContactService contactService, ContactMapper contactMapper) {
        this.contactService = contactService;
        this.contactMapper = contactMapper;
    }

    @GetMapping
    public Page<ContactDTO> getAllContacts(
            @RequestParam(required = false) String filter,
            Pageable pageable) {
        return contactService.getAllContacts(filter, pageable)
                .map(contactMapper::toDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDTO> getContactById(@PathVariable Long id) {
        return contactService.getContactById(id)
                .map(contactMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ContactDTO createContact(@RequestBody CreateContactRequest request) {
        Contact contact = contactMapper.toEntity(request);
        Contact createdContact = contactService.createContact(contact);
        return contactMapper.toDTO(createdContact);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ContactDTO> patchContact(@PathVariable Long id, @RequestBody PatchContactRequest request) {
        try {
            Contact contactDetails = contactMapper.toEntity(request);
            Contact patchedContact = contactService.patchContact(id, contactDetails);
            return ResponseEntity.ok(contactMapper.toDTO(patchedContact));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
