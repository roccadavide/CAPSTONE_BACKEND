package daviderocca.CAPSTONE_BACKEND.services;

import daviderocca.CAPSTONE_BACKEND.DTO.NewServiceItemDTO;
import daviderocca.CAPSTONE_BACKEND.DTO.ServiceItemResponseDTO;
import daviderocca.CAPSTONE_BACKEND.entities.Category;
import daviderocca.CAPSTONE_BACKEND.entities.ServiceItem;
import daviderocca.CAPSTONE_BACKEND.exceptions.BadRequestException;
import daviderocca.CAPSTONE_BACKEND.exceptions.ResourceNotFoundException;
import daviderocca.CAPSTONE_BACKEND.repositories.ServiceItemRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ServiceItemService {

    @Autowired
    private ServiceItemRepository serviceItemRepository;

    @Autowired
    private CategoryService categoryService;


    public Page<ServiceItemResponseDTO> findAllServiceItems(int pageNumber, int pageSize, String sort) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sort));
        Page<ServiceItem> page = this.serviceItemRepository.findAll(pageable);

        return page.map(serviceItem -> new ServiceItemResponseDTO(
                serviceItem.getServiceId(),
                serviceItem.getTitle(),
                serviceItem.getDurationMin(),
                serviceItem.getPrice(),
                serviceItem.getShortDescription(),
                serviceItem.getDescription(),
                serviceItem.getImages(),
                serviceItem.getCategory() != null ? serviceItem.getCategory().getCategoryId() : null));
    }

    public ServiceItem findServiceItemById(UUID serviceItemId) {
        return this.serviceItemRepository.findById(serviceItemId).orElseThrow(()-> new ResourceNotFoundException(serviceItemId));
    }

    public ServiceItemResponseDTO findServiceItemByIdAndConvert(UUID serviceItemId) {
        ServiceItem found = this.serviceItemRepository.findById(serviceItemId).orElseThrow(()-> new ResourceNotFoundException(serviceItemId));

        return new ServiceItemResponseDTO(
                found.getServiceId(),
                found.getTitle(),
                found.getDurationMin(),
                found.getPrice(),
                found.getShortDescription(),
                found.getDescription(),
                found.getImages(),
                found.getCategory() != null ? found.getCategory().getCategoryId() : null);
    }

    public ServiceItemResponseDTO saveServiceItem(NewServiceItemDTO payload) {

        if (serviceItemRepository.existsByTitle(payload.title())) {
            throw new BadRequestException("Esiste già un servizio con questo titolo!");
        }

        Category relatedCategory = categoryService.findCategoryById(payload.categoryId());

        ServiceItem newServiceItem = new ServiceItem(payload.title(), payload.durationMin(), payload.price(), payload.shortDescription(), payload.description(), payload.images(), relatedCategory);
        ServiceItem savedServiceItem = serviceItemRepository.save(newServiceItem);

        log.info("Servizio {} ({} - categoria {}) creato", savedServiceItem.getServiceId(), savedServiceItem.getTitle(), relatedCategory.getCategoryId());

        return new ServiceItemResponseDTO(savedServiceItem.getServiceId(), savedServiceItem.getTitle(),
                savedServiceItem.getDurationMin(), savedServiceItem.getPrice(), savedServiceItem.getShortDescription(),
                savedServiceItem.getDescription(), savedServiceItem.getImages(), relatedCategory.getCategoryId());
    }

    @Transactional
    public ServiceItemResponseDTO findServiceItemByIdAndUpdate(UUID serviceItemId, NewServiceItemDTO payload) {
        ServiceItem found = findServiceItemById(serviceItemId);

        if (serviceItemRepository.existsByTitleAndServiceIdNot(payload.title(), serviceItemId)) {
            throw new BadRequestException("Esiste già un altro servizio con questo titolo!");
        }

        Category relatedCategory = categoryService.findCategoryById(payload.categoryId());

        found.setTitle(payload.title());
        found.setDurationMin(payload.durationMin());
        found.setPrice(payload.price());
        found.setShortDescription(payload.shortDescription());
        found.setDescription(payload.description());
        found.setImages(payload.images());
        found.setCategory(relatedCategory);

        ServiceItem modifiedServiceItem = serviceItemRepository.save(found);

        log.info("Servizio {} aggiornato (categoria: {})", modifiedServiceItem.getServiceId(), relatedCategory.getCategoryKey());

        return new ServiceItemResponseDTO(modifiedServiceItem.getServiceId(), modifiedServiceItem.getTitle(),
                modifiedServiceItem.getDurationMin(), modifiedServiceItem.getPrice(), modifiedServiceItem.getShortDescription(),
                modifiedServiceItem.getDescription(), modifiedServiceItem.getImages(), relatedCategory.getCategoryId());
    }

    @Transactional
    public void findServiceItemByIdAndDelete(UUID serviceItemId) {
        ServiceItem found = findServiceItemById(serviceItemId);
        serviceItemRepository.delete(found);
        log.info("Servizio {} è stato eliminato!", found.getServiceId());
    }

}
