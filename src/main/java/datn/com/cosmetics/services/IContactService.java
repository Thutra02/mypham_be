package datn.com.cosmetics.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import datn.com.cosmetics.entity.Contact;

public interface IContactService {
    Contact createContact(Contact contact);
    Contact updateContact(Long id, Contact contact);
    void deleteContact(Long id);
    Contact getContactById(Long id);
    
    Page<Contact> getAllContacts(String keyword, Pageable pageable);
}