package org.oneuponcancer.redemption.resource;

import org.oneuponcancer.redemption.exception.InsufficientPermissionException;
import org.oneuponcancer.redemption.model.Participant;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.transport.ParticipantCreateRequest;
import org.oneuponcancer.redemption.model.transport.ParticipantEditRequest;
import org.oneuponcancer.redemption.repository.ParticipantRepository;
import org.oneuponcancer.redemption.service.AuditLogService;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/v1/participant")
public class ParticipantResource {
    private ParticipantRepository participantRepository;
    private AuditLogService auditLogService;

    @Inject
    public ParticipantResource(ParticipantRepository participantRepository, AuditLogService auditLogService) {
        this.participantRepository = participantRepository;
        this.auditLogService = auditLogService;
    }

    @RequestMapping("")
    @ResponseBody
    public List<Participant> fetchParticipant(Principal principal) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.LIST_PARTICIPANT.name()))) {
            throw new InsufficientPermissionException("Not allowed to list participants.");
        }

        return participantRepository.findAll();
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public Participant createParticipant(@Valid ParticipantCreateRequest participantCreateRequest, BindingResult bindingResult, Principal principal, HttpServletRequest request) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.CREATE_PARTICIPANT.name()))) {
            throw new InsufficientPermissionException("Not allowed to create participants.");
        }

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("\n"));

            throw new ValidationException(errorMessages);
        }

        Participant participant = new Participant();

        participant.setFirstName(participantCreateRequest.getFirstName());
        participant.setLastName(participantCreateRequest.getLastName());
        participant.setEmail(participantCreateRequest.getEmail());

        Participant savedParticipant = participantRepository.save(participant);

        auditLogService.log(
                principal.getName(),
                auditLogService.extractRemoteIp(request),
                String.format("Created participant: %s %s (%s)",
                        savedParticipant.getFirstName(),
                        savedParticipant.getLastName(),
                        savedParticipant.getId()));

        return savedParticipant;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Participant updateParticipant(@PathVariable String id, @Valid ParticipantEditRequest participantEditRequest, BindingResult bindingResult, Principal principal, HttpServletRequest request) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.EDIT_PARTICIPANT.name()))) {
            throw new InsufficientPermissionException("Not allowed to edit participants.");
        }

        UUID uuid = UUID.fromString(id);
        Participant participant = participantRepository
                .findById(uuid)
                .orElseThrow(() -> new NullPointerException("No such participant found."));

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("\n"));

            throw new ValidationException(errorMessages);
        }

        participant.setFirstName(participantEditRequest.getFirstName());
        participant.setLastName(participantEditRequest.getLastName());
        participant.setEmail(participantEditRequest.getEmail());

        Participant savedParticipant = participantRepository.save(participant);

        auditLogService.log(
                principal.getName(),
                auditLogService.extractRemoteIp(request),
                String.format("Edited participant: %s %s (%s)",
                        participant.getFirstName(),
                        participant.getLastName(),
                        participant.getId()));

        return savedParticipant;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Participant deleteParticipant(@PathVariable String id, Principal principal, HttpServletRequest request) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.DELETE_PARTICIPANT.name()))) {
            throw new InsufficientPermissionException("Not allowed to delete participants.");
        }

        UUID uuid = UUID.fromString(id);
        Participant participant = participantRepository
                .findById(uuid)
                .orElseThrow(() -> new NullPointerException("No such participant found."));

        participantRepository.delete(participant);

        auditLogService.log(
                principal.getName(),
                auditLogService.extractRemoteIp(request),
                String.format("Deleted participant: %s %s (%s)",
                        participant.getFirstName(),
                        participant.getLastName(),
                        participant.getId()));

        return participant;
    }

    @ExceptionHandler(NullPointerException.class)
    public void handleNotFoundException(NullPointerException ex, HttpServletResponse response) throws Exception {
        response.sendError(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(InsufficientPermissionException.class)
    public void handleInsufficientPermissionException(InsufficientPermissionException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public void handleValidationException(ValidationException ex, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
}
