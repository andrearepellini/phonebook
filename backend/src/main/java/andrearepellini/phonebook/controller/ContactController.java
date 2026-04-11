package andrearepellini.phonebook.controller;

import java.util.Optional;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import andrearepellini.phonebook.dto.request.CreateContactRequest;
import andrearepellini.phonebook.dto.request.PatchContactRequest;
import andrearepellini.phonebook.dto.response.ContactResponse;
import andrearepellini.phonebook.mapper.ContactMapper;
import andrearepellini.phonebook.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/contacts")
@Tag(name = "Contacts", description = "Contact management APIs")
public class ContactController {
    private final ContactService contactService;
    private final ContactMapper contactMapper;

    public ContactController(ContactService contactService, ContactMapper contactMapper) {
        this.contactService = contactService;
        this.contactMapper = contactMapper;
    }

    @Operation(summary = "Get all contacts", description = "Retrieve a paginated list of contacts with optional filtering")
    @GetMapping
    public Page<ContactResponse> getAllContacts(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Boolean deleted,
            @ParameterObject Pageable pageable) {
        return contactService.getAllContacts(filter, deleted, pageable)
                .map(contactMapper::toDTO);
    }

    @Operation(summary = "Get contact by ID", description = "Retrieve a single contact by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact found"),
            @ApiResponse(responseCode = "404", description = "Contact not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> getContactById(@PathVariable Long id) {
        return contactService.getContactById(id)
                .map(contactMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new contact", description = "Create a new contact with the provided details")
    @ApiResponse(responseCode = "201", description = "Contact created successfully")
    @PostMapping
    public ResponseEntity<ContactResponse> createContact(@RequestBody CreateContactRequest request) {
        return Optional.of(request)
                .map(contactMapper::toEntity)
                .map(contactService::createContact)
                .map(contactMapper::toDTO)
                .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                .get();
    }

    @Operation(summary = "Update a contact", description = "Update an existing contact's details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact updated successfully"),
            @ApiResponse(responseCode = "404", description = "Contact not found", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<ContactResponse> patchContact(
            @PathVariable Long id,
            @RequestBody PatchContactRequest request) {
        return contactService.patchContact(id, contactMapper.toEntity(request))
                .map(contactMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
