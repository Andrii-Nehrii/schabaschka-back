package schabaschka.offer.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import schabaschka.offer.dto.OfferDto;
import schabaschka.offer.model.Offer;
import schabaschka.offer.service.OfferService;
import schabaschka.security.SecurityUtils;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping("/job/{jobId}")
    public List<OfferDto> getByJobId(@PathVariable("jobId") long jobId){
        return offerService.findByJobId(jobId);
    }

    @GetMapping("/me/worker")
    public List<OfferDto> getMyOffersAsWorker(){
        long workerId = SecurityUtils.getCurrentUserId();
        return offerService.findByWorkerId(workerId); 
    }

    @GetMapping("/worker/{workerId}")
    public List<OfferDto> getByWorkerId(@PathVariable("workerId") long workerId){
        return offerService.findByWorkerId(workerId);
    }
    @GetMapping("/{id}")
    public ResponseEntity<OfferDto> getById(@PathVariable("id") long id){
        Optional<OfferDto> offerOpt = offerService.findById(id);
        return offerOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OfferDto create(

            @RequestParam("jobId") long jobId,
            @RequestParam(value = "message", required = false) String message
    ) {
        long workerId = SecurityUtils.getCurrentUserId();
        return offerService.create(workerId, jobId, message);
    }
    @PatchMapping("/{id}/status")
    public OfferDto changeStatus(
            @PathVariable("id") long id,
            @RequestParam("status") String status
    ) {
        Offer.Status newStatus = parseStatus(status);
        if (newStatus == null) {
            throw new IllegalArgumentException("Unknown offer status: " + status);
        }

        if (newStatus == Offer.Status.PENDING) {
            throw new IllegalArgumentException("Status PENDING cannot be set manually");
        }

        return offerService.changeStatus(id, newStatus);
    }






    private Offer.Status parseStatus(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        String upper = trimmed.toUpperCase();
        for (Offer.Status status : Offer.Status.values()) {
            if (status.name().equals(upper)) {
                return status;
            }
        }
        return null;
    }


}
