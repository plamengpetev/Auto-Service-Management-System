package com.example.Auto_Service_Management_System.service;

import com.example.Auto_Service_Management_System.model.RequestStatus;
import com.example.Auto_Service_Management_System.model.ServiceRequest;
import com.example.Auto_Service_Management_System.repository.ServiceRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private final ServiceRequestRepository requestRepository;

    public ScheduledTasks(ServiceRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void autoCancelOldPendingRequests() {
        LocalDate limitDate = LocalDate.now().minusDays(30);

        List<ServiceRequest> oldPending = requestRepository
                .findByStatusAndRequestDateBefore(RequestStatus.PENDING, limitDate);

        if (!oldPending.isEmpty()) {
            oldPending.forEach(r -> r.setStatus(RequestStatus.CANCELLED));
            requestRepository.saveAll(oldPending);
            logger.info("Auto-cancelled {} old pending requests", oldPending.size());
        }
    }

    @Scheduled(fixedRate = 300_000)
    public void logRequestsCount() {
        long total = requestRepository.count();
        logger.info("Current number of service requests: {}", total);
    }
}
