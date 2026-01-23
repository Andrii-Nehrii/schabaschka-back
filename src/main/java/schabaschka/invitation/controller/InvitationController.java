package schabaschka.invitation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import schabaschka.invitation.dto.InvitationDto;
import schabaschka.invitation.dto.NewInvitationDto;
import schabaschka.invitation.model.Invitation;
import schabaschka.invitation.service.InvitationService;
import schabaschka.security.SecurityUtils;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invitations")
public class InvitationController {
    private final InvitationService invitationService;


    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping("/worker/{workerId}")
    public List<InvitationDto> getByWorkerId(@PathVariable("workerId") long workerId) {
        return invitationService.findByWorkerId(workerId);
    }
    @GetMapping("/employer/{employerId}")
    public List<InvitationDto> getByEmployerId(@PathVariable("employerId") long employerId) {
        return invitationService.findByEmployerId(employerId);
    }
    @GetMapping("/me/worker")
    public List<InvitationDto> getMyInvitationsAsWorker() {
        long workerId = SecurityUtils.getCurrentUserId();
        return invitationService.findByWorkerId(workerId);
    }

    @GetMapping("/me/employer")
    public List<InvitationDto> getMyInvitationsAsEmployer() {
        long employerId = SecurityUtils.getCurrentUserId();
        return invitationService.findByEmployerId(employerId);
    }



    @GetMapping("/{id}")
    public ResponseEntity<InvitationDto> getById(@PathVariable("id") long id) {
        Optional<InvitationDto> invitationOpt = invitationService.findById(id);
        return invitationOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvitationDto create(@RequestBody NewInvitationDto newInvitationDto) {
        return invitationService.create(newInvitationDto);
    }

    @PatchMapping("/{id}/status")
    public InvitationDto changeStatus(@PathVariable("id") long id, @RequestParam("status") String status) {
        Invitation.Status newStatus = parseStatus(status);
        if (newStatus == null) {
            throw new IllegalArgumentException("Unknown invitation status: " + status);
        }
        return invitationService.changeStatus(id, newStatus);
    }



    private Invitation.Status parseStatus(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        String upper = trimmed.toUpperCase();
        for (Invitation.Status status : Invitation.Status.values()) {
            if (status.name().equals(upper)) {
                return status;
            }
        }
        return null;
    }


}
