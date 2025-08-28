package daviderocca.CAPSTONE_BACKEND.services;

import daviderocca.CAPSTONE_BACKEND.DTO.NewUserDTO;
import daviderocca.CAPSTONE_BACKEND.DTO.UserResponseDTO;
import daviderocca.CAPSTONE_BACKEND.entities.User;
import daviderocca.CAPSTONE_BACKEND.enums.Role;
import daviderocca.CAPSTONE_BACKEND.exceptions.BadRequestException;
import daviderocca.CAPSTONE_BACKEND.exceptions.DuplicateResourceException;
import daviderocca.CAPSTONE_BACKEND.exceptions.ResourceNotFoundException;
import daviderocca.CAPSTONE_BACKEND.exceptions.UnauthorizedOperationException;
import daviderocca.CAPSTONE_BACKEND.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder bcrypt;

    public Page<User> findAll(int pageNumber, int pageSize, String sort) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.valueOf(sort));
        return this.userRepository.findAll(pageable);
    }

    public User findUserById(UUID userId) {
        return this.userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException(userId));
    }

    public User findByUserByEmail(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException(email));
    }

    public UserResponseDTO saveUser(NewUserDTO payload) {
        this.userRepository.findByEmail(payload.email()).ifPresent(user -> {
            throw new DuplicateResourceException("L'email " + user.getEmail() + " appartiene già ad un'altro user");
        });

        this.userRepository.findByPhone(payload.phone()).ifPresent(user -> {
                    throw new DuplicateResourceException("Il numero di telefono " + user.getPhone() + " appartiene già ad un'altro user");
                });

        User newUser =  new User(payload.name(), payload.surname(), payload.email(), bcrypt.encode(payload.password()), payload.phone());
        User savedNewUser = this.userRepository.save(newUser);
        log.info("L'Utente {} con email {} è stato salvato correttamente!", payload.name(), payload.email());
        return new UserResponseDTO(savedNewUser.getUserId(), savedNewUser.getName(), savedNewUser.getSurname(), savedNewUser.getEmail(),
                savedNewUser.getPhone(), savedNewUser.getRole());
    }

    @Transactional
    public UserResponseDTO findUserByIdAndUpdate (UUID idUser, NewUserDTO payload) {
        User found = findUserById(idUser);

        if (!found.getEmail().equals(payload.email()))
            this.userRepository.findByEmail(payload.email()).ifPresent(user -> {
                throw new DuplicateResourceException("L'email " + user.getEmail() + " appartiene già ad un'altro user");
            });

        if (!found.getPhone().equals(payload.phone()))
            this.userRepository.findByPhone(payload.phone()).ifPresent(user -> {
                throw new DuplicateResourceException("Il numero di telefono " + user.getPhone() + " appartiene già ad un'altro user");
            });


        found.setName(payload.name());
        found.setSurname(payload.surname());
        found.setEmail(payload.email());
        found.setPassword(bcrypt.encode(payload.password()));
        found.setPhone(payload.phone());

        User modifiedUser = this.userRepository.save(found);
        log.info("User modificato correttamente");
        return new UserResponseDTO(modifiedUser.getUserId(), modifiedUser.getName(), modifiedUser.getSurname(), modifiedUser.getEmail(),
                modifiedUser.getPhone(), modifiedUser.getRole());;
    }

    @Transactional
    public User findUserByIdAndPatchToAdmin (UUID idUser) {
        User found = findUserById(idUser);

        if(found.getRole().equals(Role.ADMIN)) throw new UnauthorizedOperationException("L'Utente è gia ADMIN!");

        found.setRole(Role.ADMIN);

        return userRepository.save(found);
    }

    @Transactional
    public User findUserByIdAndRemoveFromAdmin (UUID idUser) {
        User found = findUserById(idUser);

        if(found.getRole().equals(Role.COSTUMER)) throw new UnauthorizedOperationException("L'Utente è gia COSTUMER!");

        found.setRole(Role.COSTUMER);

        return userRepository.save(found);
    }

    public void findUserByIdAndDelete(UUID idUser) {
        User found = findUserById(idUser);
        this.userRepository.delete(found);
    }

}
