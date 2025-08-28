package daviderocca.CAPSTONE_BACKEND.services;

import daviderocca.CAPSTONE_BACKEND.entities.ServiceItem;
import daviderocca.CAPSTONE_BACKEND.exceptions.ResourceNotFoundException;
import daviderocca.CAPSTONE_BACKEND.repositories.ServiceItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ServiceItemService {

    @Autowired
    private ServiceItemRepository serviceItemRepository;

    public ServiceItem findServiceItemById(UUID serviceItemId) {
        return this.serviceItemRepository.findById(serviceItemId).orElseThrow(()-> new ResourceNotFoundException(serviceItemId));
    }

}
