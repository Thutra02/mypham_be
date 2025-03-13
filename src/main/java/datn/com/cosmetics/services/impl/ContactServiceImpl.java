package datn.com.cosmetics.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import datn.com.cosmetics.entity.Contact;
import datn.com.cosmetics.repository.ContactRepository;
import datn.com.cosmetics.services.IContactService;

@Service
public class ContactServiceImpl implements IContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Override
    public Contact createContact(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    public Contact updateContact(Long id, Contact contact) {
        Optional<Contact> existingContact = contactRepository.findById(id);
        if (existingContact.isPresent()) {
            Contact updatedContact = existingContact.get();
            updatedContact.setName(contact.getName());
            updatedContact.setEmail(contact.getEmail());
            updatedContact.setMessage(contact.getMessage());
            return contactRepository.save(updatedContact);
        } else {
            throw new RuntimeException("Contact not found with id " + id);
        }
    }

    @Override
    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }

    @Override
    public Contact getContactById(Long id) {
        return contactRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contact not found with id " + id));
    }

    @Override
    public Page<Contact> getAllContacts(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isEmpty()) {
            return contactRepository.findAll(pageable);
        } else {
            return contactRepository.findByNameContainingOrEmailContaining(keyword, pageable);
        }
    }
}
