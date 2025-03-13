package datn.com.cosmetics.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import datn.com.cosmetics.bean.response.ApiResponse;
import datn.com.cosmetics.entity.Contact;
import datn.com.cosmetics.services.IContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/contact")
@Tag(name = "Contact", description = "Contact management APIs")
public class ContactController {

    @Autowired
    private IContactService contactService;

    @PostMapping
    @Operation(summary = "Create a new contact", description = "Create a new contact with the provided details")
    public ResponseEntity<ApiResponse<Contact>> createContact(@RequestBody Contact contact) {
        Contact createdContact = contactService.createContact(contact);
        return ResponseEntity.ok(ApiResponse.success(createdContact, "Contact created successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing contact", description = "Update the details of an existing contact by ID")
    public ResponseEntity<ApiResponse<Contact>> updateContact(@PathVariable Long id, @RequestBody Contact contact) {
        Contact updatedContact = contactService.updateContact(id, contact);
        if (updatedContact != null) {
            return ResponseEntity.ok(ApiResponse.success(updatedContact, "Contact updated successfully"));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("Contact not found"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a contact", description = "Delete a contact by ID")
    public ResponseEntity<ApiResponse<Void>> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Contact deleted successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get contact by ID", description = "Retrieve a contact by its ID")
    public ResponseEntity<ApiResponse<Contact>> getContactById(@PathVariable Long id) {
        Contact contact = contactService.getContactById(id);
        if (contact != null) {
            return ResponseEntity.ok(ApiResponse.success(contact, "Contact retrieved successfully"));
        }
        return ResponseEntity.status(404).body(ApiResponse.error("Contact not found"));
    }

    @GetMapping
    @Operation(summary = "Get all contacts", description = "Retrieve a list of all contacts with optional search by name or email and pagination")
    public ResponseEntity<ApiResponse<List<Contact>>> getAllContacts(
            @Parameter(description = "Search keyword", required = false) @RequestParam(required = false) String search,
            Pageable pageable) {
        Page<Contact> contacts = contactService.getAllContacts(search, pageable);
        ApiResponse.Pagination pagination = new ApiResponse.Pagination(contacts.getNumber() + 1,
                contacts.getTotalPages(), contacts.getTotalElements());
        String message = "Contacts retrieved successfully";
        return ResponseEntity.ok(ApiResponse.success(contacts.getContent(), message, pagination));
    }
}
