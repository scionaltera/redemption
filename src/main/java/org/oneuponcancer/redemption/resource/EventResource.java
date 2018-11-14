package org.oneuponcancer.redemption.resource;

import org.oneuponcancer.redemption.exception.InsufficientPermissionException;
import org.oneuponcancer.redemption.model.Event;
import org.oneuponcancer.redemption.model.Participant;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.transport.EventCreateRequest;
import org.oneuponcancer.redemption.model.transport.EventEditRequest;
import org.oneuponcancer.redemption.repository.EventRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/v1/event")
public class EventResource {
    private EventRepository eventRepository;
    private ParticipantRepository participantRepository;
    private AuditLogService auditLogService;

    @Inject
    public EventResource(EventRepository eventRepository, ParticipantRepository participantRepository, AuditLogService auditLogService) {
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.auditLogService = auditLogService;
    }

    @RequestMapping("")
    @ResponseBody
    public List<Event> fetchEvent(Principal principal) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.LIST_EVENT.name()))) {
            throw new InsufficientPermissionException("Not allowed to list events.");
        }

        return eventRepository.findAll();
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public Event createEvent(@Valid EventCreateRequest eventCreateRequest, BindingResult bindingResult, Principal principal, HttpServletRequest request) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.CREATE_EVENT.name()))) {
            throw new InsufficientPermissionException("Not allowed to create events.");
        }

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("\n"));

            throw new ValidationException(errorMessages);
        }

        Event event = new Event();

        event.setName(eventCreateRequest.getName());
        event.setDescription(eventCreateRequest.getDescription());
        event.setStartDate(eventCreateRequest.getStartDate());
        event.setEndDate(eventCreateRequest.getEndDate());

        if (eventCreateRequest.getParticipants() != null) {
            List<Participant> participants = participantRepository.findAllById(eventCreateRequest.getParticipants());

            event.setParticipants(participants);
        } else {
            event.setParticipants(Collections.emptyList());
        }

        Event savedEvent = eventRepository.save(event);

        auditLogService.log(
                principal.getName(),
                auditLogService.extractRemoteIp(request),
                String.format("Created event: %s (%s)",
                        savedEvent.getName(),
                        savedEvent.getId()));

        return savedEvent;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Event updateEvent(@PathVariable String id, @Valid EventEditRequest eventEditRequest, BindingResult bindingResult, Principal principal, HttpServletRequest request) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.EDIT_EVENT.name()))) {
            throw new InsufficientPermissionException("Not allowed to edit events.");
        }

        UUID uuid = UUID.fromString(id);
        Event event = eventRepository
                .findById(uuid)
                .orElseThrow(() -> new NullPointerException("No such event found."));

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("\n"));

            throw new ValidationException(errorMessages);
        }

        event.setName(eventEditRequest.getName());
        event.setDescription(eventEditRequest.getDescription());
        event.setStartDate(eventEditRequest.getStartDate());
        event.setEndDate(eventEditRequest.getEndDate());

        if (eventEditRequest.getParticipants() != null) {
            List<Participant> participants = participantRepository.findAllById(eventEditRequest.getParticipants());

            event.setParticipants(participants);
        } else {
            event.setParticipants(Collections.emptyList());
        }

        Event savedEvent = eventRepository.save(event);

        auditLogService.log(
                principal.getName(),
                auditLogService.extractRemoteIp(request),
                String.format("Edited event: %s (%s)",
                        event.getName(),
                        event.getId()));

        return savedEvent;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Event deleteEvent(@PathVariable String id, Principal principal, HttpServletRequest request) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.DELETE_EVENT.name()))) {
            throw new InsufficientPermissionException("Not allowed to delete events.");
        }

        UUID uuid = UUID.fromString(id);
        Event event = eventRepository
                .findById(uuid)
                .orElseThrow(() -> new NullPointerException("No such event found."));

        eventRepository.delete(event);

        auditLogService.log(
                principal.getName(),
                auditLogService.extractRemoteIp(request),
                String.format("Deleted event: %s (%s)",
                        event.getName(),
                        event.getId()));

        return event;
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
