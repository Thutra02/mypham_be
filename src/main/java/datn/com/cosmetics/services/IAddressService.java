package datn.com.cosmetics.services;

import java.util.List;

import datn.com.cosmetics.entity.Address;
import datn.com.cosmetics.entity.User;

public interface IAddressService {
    Address createAddress(Address address);
    Address updateAddress(Long id, Address address);
    void deleteAddress(Long id);
    Address getAddressById(Long id);
    List<Address> getAllAddresses();
    List<Address> getAllAddressesByUser(User user);
    void toggleDefaultAddress(Long addressId, User user);

    Address getAddressDefault(User user);
}
