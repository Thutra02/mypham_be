package datn.com.cosmetics.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import datn.com.cosmetics.bean.request.AddressRequest;
import datn.com.cosmetics.bean.response.ApiResponse;
import datn.com.cosmetics.entity.Address;
import datn.com.cosmetics.entity.User;
import datn.com.cosmetics.exceptions.DuplicateResourceException;
import datn.com.cosmetics.services.IAddressService;
import datn.com.cosmetics.services.IUserService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

@RestController
@RequestMapping("/api/address")
@Tag(name = "Address", description = "API for address management")
@Validated
@OpenAPIDefinition(info = @Info(title = "User API", version = "1.0", description = "User management API"))
public class AddressController {

    @Autowired
    private IAddressService addressService;

    @Autowired
    private IUserService userService;

    @PostMapping("")
    @Operation(summary = "Create a new address", description = "Create a new address for a user with the provided details")
    public ResponseEntity<ApiResponse<Address>> createAddress(
            @RequestHeader(name = "Authorization", required = false) String jwt,
            @Valid @RequestBody AddressRequest addressRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not logged in"));
        }

        String username = authentication.getName();

        User user = userService.getUserInfo(username);
        Address address = new Address();
        // Set properties from addressRequest to address
        address.setFirstName(addressRequest.getFirstName());
        address.setLastName(addressRequest.getLastName());
        address.setStreetAddress(addressRequest.getStreetAddress());
        address.setCity(addressRequest.getCity());
        address.setState(addressRequest.getState());
        address.setZipCode(addressRequest.getZipCode());
        address.setDefault(false);
        address.setPhone(addressRequest.getPhone());
        address.setEmail(addressRequest.getEmail());
        address.setUser(user);
        try {
            Address createdAddress = addressService.createAddress(address);
            return ResponseEntity.ok(ApiResponse.success(createdAddress, "Address created successfully"));
        } catch (ValidationException | DuplicateResourceException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an address", description = "Update the details of an existing address by its ID")
    public ResponseEntity<ApiResponse<Address>> updateAddress(
            @Parameter(description = "Address ID", required = true) @PathVariable Long id,
            @Parameter(description = "Address request body", required = true) @Valid @RequestBody AddressRequest addressRequest) {
        Address address = addressService.getAddressById(id);
        if (address == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Address not found"));
        }
        // Set properties from addressRequest to address
        address.setFirstName(addressRequest.getFirstName());
        address.setLastName(addressRequest.getLastName());
        address.setStreetAddress(addressRequest.getStreetAddress());
        address.setCity(addressRequest.getCity());
        address.setState(addressRequest.getState());
        address.setZipCode(addressRequest.getZipCode());
        address.setPhone(addressRequest.getPhone());
        address.setEmail(addressRequest.getEmail());
        try {
            Address updatedAddress = addressService.updateAddress(id, address);
            return ResponseEntity.ok(ApiResponse.success(updatedAddress, "Address updated successfully"));
        } catch (ValidationException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PutMapping("/{id}/default")
    @Operation(summary = "Toggle default address", description = "Set the specified address as default and unset others")
    public ResponseEntity<ApiResponse<Void>> toggleDefaultAddress(
            @Parameter(description = "Address ID", required = true) @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not logged in"));
        }

        String username = authentication.getName();
        User user = userService.getUserInfo(username);

        addressService.toggleDefaultAddress(id, user);
        return ResponseEntity.ok(ApiResponse.success(null, "Default address toggled successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address", description = "Delete an existing address by its ID")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @Parameter(description = "Address ID", required = true) @PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Address deleted successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an address by ID", description = "Retrieve an address by its unique ID")
    public ResponseEntity<ApiResponse<Address>> getAddressById(
            @Parameter(description = "Address ID", required = true) @PathVariable Long id) {
        Address address = addressService.getAddressById(id);
        return ResponseEntity.ok(ApiResponse.success(address, "Address retrieved successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all addresses", description = "Retrieve all addresses for the authenticated user")
    public ResponseEntity<ApiResponse<List<Address>>> getAllAddresses(
            @RequestHeader(name = "Authorization", required = false) String jwt) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not logged in"));
        }

        String username = authentication.getName();
        System.out.println("username: " + username);
        User user = userService.getUserInfo(username);
        List<Address> addresses = addressService.getAllAddressesByUser(user);
        return ResponseEntity.ok(ApiResponse.success(addresses, "Addresses retrieved successfully"));
    }

    @GetMapping("/default")
    @Operation(summary = "Get default address", description = "Retrieve the default address for the authenticated user")
    public ResponseEntity<ApiResponse<Address>> getDefaultAddress(
            @RequestHeader(name = "Authorization", required = false) String jwt) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not logged in"));
        }

        String username = authentication.getName();
        System.out.println("username: " + username);
        User user = userService.getUserInfo(username);
        Address address = addressService.getAddressDefault(user);
        if (address == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("No default address found"));
        }
        return ResponseEntity.ok(ApiResponse.success(address, "Default address retrieved successfully"));
    }

}
