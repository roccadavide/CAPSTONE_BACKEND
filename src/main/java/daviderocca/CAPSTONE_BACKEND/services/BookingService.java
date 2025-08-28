package daviderocca.CAPSTONE_BACKEND.services;

import daviderocca.CAPSTONE_BACKEND.DTO.BookingResponseDTO;
import daviderocca.CAPSTONE_BACKEND.DTO.NewBookingDTO;
import daviderocca.CAPSTONE_BACKEND.entities.Booking;
import daviderocca.CAPSTONE_BACKEND.entities.ServiceItem;
import daviderocca.CAPSTONE_BACKEND.entities.User;
import daviderocca.CAPSTONE_BACKEND.exceptions.BadRequestException;
import daviderocca.CAPSTONE_BACKEND.exceptions.DuplicateResourceException;
import daviderocca.CAPSTONE_BACKEND.exceptions.ResourceNotFoundException;
import daviderocca.CAPSTONE_BACKEND.repositories.BookingRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ServiceItemService serviceItemService;

    @Autowired
    private UserService userService;

    public Page<Booking> findAllBookings(int pageNumber, int pageSize, String sort) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sort));
        return this.bookingRepository.findAll(pageable);
    }

    public Booking findBookingById(UUID userId) {
        return this.bookingRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException(userId));
    }

    public Booking findBookingByEmail(String customerEmail) {
        return this.bookingRepository.findByCustomerEmail(customerEmail).orElseThrow(()-> new ResourceNotFoundException(customerEmail));
    }

    public BookingResponseDTO saveBooking(NewBookingDTO payload) {

        if (payload.startTime().isAfter(payload.endTime())) {
            throw new BadRequestException("L'orario di inizio non può essere successivo a quello di fine!");
        }

        if (payload.startTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("L'orario di inizio non può essere nel passato!");
        }

        bookingRepository.findAll().forEach(existing -> {
            if (existing.getService().getServiceId().equals(payload.serviceId()) &&
                    existing.getStartTime().isBefore(payload.endTime()) &&
                    existing.getEndTime().isAfter(payload.startTime())) {
                throw new BadRequestException("Esiste già una prenotazione in questo intervallo per il servizio scelto!");
            }
        });

        ServiceItem relatedServiceItem = serviceItemService.findServiceItemById(payload.serviceId());

        User relatedUser = null;
        if (payload.userId() != null) {
            relatedUser = userService.findUserById(payload.userId());
        }

        Booking newBooking =  new Booking(payload.customerName(), payload.customerEmail(), payload.customerPhone(), payload.startTime(),
                payload.endTime(), payload.notes(), relatedServiceItem, relatedUser);

        Booking savedNewBooking = this.bookingRepository.save(newBooking);
        log.info("La prenotazione {} dell'utente con email {} è stata salvata correttamente!",
                savedNewBooking.getBookingId(), relatedUser.getEmail());

        return new BookingResponseDTO(savedNewBooking.getBookingId(), savedNewBooking.getCustomerName(),
                savedNewBooking.getCustomerEmail(), savedNewBooking.getCustomerPhone(), savedNewBooking.getStartTime(),
                savedNewBooking.getEndTime(), savedNewBooking.getBookingStatus(), savedNewBooking.getNotes(),
                savedNewBooking.getCreatedAt(), payload.serviceId(), payload.userId());
    }

    @Transactional
    public BookingResponseDTO findBookingByIdAndUpdate (UUID idBooking, NewBookingDTO payload) {
        Booking found = findBookingById(idBooking);

        if (found.getBookingStatus().name().equals("CANCELLED") ||
                found.getBookingStatus().name().equals("COMPLETED")) {
            throw new BadRequestException("Non puoi modificare una prenotazione già " + found.getBookingStatus());
        }

        if (payload.startTime().isAfter(payload.endTime())) {
            throw new BadRequestException("L'orario di inizio non può essere successivo a quello di fine!");
        }

        bookingRepository.findAll().forEach(existing -> {
            if (!existing.getBookingId().equals(idBooking) && // escludo se stesso
                    existing.getService().getServiceId().equals(payload.serviceId()) &&
                    existing.getStartTime().isBefore(payload.endTime()) &&
                    existing.getEndTime().isAfter(payload.startTime())) {
                throw new BadRequestException("Esiste già una prenotazione in questo intervallo per il servizio scelto!");
            }
        });

        if (!found.getCustomerEmail().equals(payload.customerEmail()))
            this.bookingRepository.findByCustomerEmail(payload.customerEmail()).ifPresent(booking -> {
                throw new DuplicateResourceException("Esiste già una prenotazione con email " + payload.customerEmail());
            });

        ServiceItem relatedServiceItem = serviceItemService.findServiceItemById(payload.serviceId());

        User relatedUser = null;
        if (payload.userId() != null) {
            relatedUser = userService.findUserById(payload.userId());
        }


        found.setCustomerName(payload.customerName());
        found.setCustomerEmail(payload.customerEmail());
        found.setCustomerPhone(payload.customerPhone());
        found.setStartTime(payload.startTime());
        found.setEndTime(payload.endTime());
        found.setNotes(payload.notes());
        found.setService(relatedServiceItem);
        found.setUser(relatedUser);

        Booking modifiedBooking = this.bookingRepository.save(found);

        log.info("La prenotazione {} è stata aggiornata!", modifiedBooking.getBookingId());

        return new BookingResponseDTO(modifiedBooking.getBookingId(), modifiedBooking.getCustomerName(),
                modifiedBooking.getCustomerEmail(), modifiedBooking.getCustomerPhone(), modifiedBooking.getStartTime(),
                modifiedBooking.getEndTime(), modifiedBooking.getBookingStatus(), modifiedBooking.getNotes(),
                modifiedBooking.getCreatedAt(), payload.serviceId(), payload.userId());
    }


    public void findBookingByIdAndDelete(UUID idBooking) {
        Booking found = findBookingById(idBooking);
        this.bookingRepository.delete(found);
    }

}
