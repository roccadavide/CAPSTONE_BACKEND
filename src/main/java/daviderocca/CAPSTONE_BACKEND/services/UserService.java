package daviderocca.CAPSTONE_BACKEND.services;

import daviderocca.CAPSTONE_BACKEND.entities.User;
import daviderocca.CAPSTONE_BACKEND.exceptions.NotFoundException;
import daviderocca.CAPSTONE_BACKEND.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findUserById(UUID userId) {
        return this.userRepository.findById(userId).orElseThrow(()-> new NotFoundException(userId));
    }

}
