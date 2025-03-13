package datn.com.cosmetics.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import datn.com.cosmetics.entity.Address;
import datn.com.cosmetics.entity.User;
import datn.com.cosmetics.exceptions.DuplicateResourceException;
import datn.com.cosmetics.exceptions.ResourceNotFoundException;
import datn.com.cosmetics.exceptions.ValidationException;
import datn.com.cosmetics.repository.AddressRepository;
import datn.com.cosmetics.services.IAddressService;

@Service
public class AddressServiceImpl implements IAddressService {

    @Autowired
    private AddressRepository addressRepository;
    

    @Override
    public Address createAddress(Address address) {
        if (addressRepository.existsByStreetAddressAndUser(address.getStreetAddress(), address.getUser())) {
            throw new DuplicateResourceException("Address already exists for this user");
        }
        Address defaultAddress = addressRepository.findByUserAndDefaultAddressTrue(address.getUser().getId()).orElse(null);
        if (defaultAddress == null) {
            address.setDefault(true);
        }
        validateAddress(address);
        
        return addressRepository.save(address);
    }

    @Override
    public Address updateAddress(Long id, Address address) {
        if (!addressRepository.existsById(id)) {
            throw new ResourceNotFoundException("Address not found");
        }
        validateAddress(address);
        address.setId(id);
        return addressRepository.save(address);
    }

    private void validateAddress(Address address) {
        if (address.getFirstName().isEmpty() || address.getLastName().isEmpty() ||
            address.getStreetAddress().isEmpty() || address.getCity().isEmpty() ||
            address.getState().isEmpty() || address.getZipCode().isEmpty()) {
            throw new ValidationException("All fields are required");
        }
        // Add more validation logic as needed
    }

    @Override
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }

    @Override
    public Address getAddressById(Long id) {
        return addressRepository.findById(id).orElse(null);
    }

    @Override
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    @Override
    public List<Address> getAllAddressesByUser(User user) {
        return addressRepository.findByUser(user);
    }

    @Override
    public void toggleDefaultAddress(Long addressId, User user) {
        List<Address> addresses = addressRepository.findByUser(user);
        for (Address address : addresses) {
            if (address.getId().equals(addressId)) {
                address.setDefault(true);
            } else {
                address.setDefault(false);
            }
            addressRepository.save(address);
        }
    }
    @Override
    public Address getAddressDefault(User user) {
        return addressRepository.findByUserAndDefaultAddressTrue(user.getId()).orElse(null);
    }
}
