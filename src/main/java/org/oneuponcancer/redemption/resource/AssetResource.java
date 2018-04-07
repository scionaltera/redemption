package org.oneuponcancer.redemption.resource;

import org.oneuponcancer.redemption.exception.InsufficientPermissionException;
import org.oneuponcancer.redemption.model.Asset;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.transport.AssetCreateRequest;
import org.oneuponcancer.redemption.model.transport.AssetEditRequest;
import org.oneuponcancer.redemption.repository.AssetRepository;
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
@RequestMapping("/api/v1/asset")
public class AssetResource {
    private AssetRepository assetRepository;
    private AuditLogService auditLogService;

    @Inject
    public AssetResource(AssetRepository assetRepository, AuditLogService auditLogService) {
        this.assetRepository = assetRepository;
        this.auditLogService = auditLogService;
    }

    @RequestMapping("")
    @ResponseBody
    public List<Asset> fetchAsset(Principal principal) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.LIST_ASSET.name()))) {
            throw new InsufficientPermissionException("Not allowed to list assets.");
        }

        return assetRepository.findAll();
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public Asset createAsset(@Valid AssetCreateRequest assetCreateRequest, BindingResult bindingResult, Principal principal, HttpServletRequest request) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.CREATE_ASSET.name()))) {
            throw new InsufficientPermissionException("Not allowed to create assets.");
        }

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("\n"));

            throw new ValidationException(errorMessages);
        }

        Asset asset = new Asset();

        asset.setName(assetCreateRequest.getName());
        asset.setDescription(assetCreateRequest.getDescription());

        Asset savedAsset = assetRepository.save(asset);

        auditLogService.log(
                principal.getName(),
                auditLogService.extractRemoteIp(request),
                String.format("Created asset: %s (%s)",
                        savedAsset.getName(),
                        savedAsset.getId()));

        return savedAsset;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Asset updateAsset(@PathVariable String id, @Valid AssetEditRequest assetEditRequest, BindingResult bindingResult, Principal principal, HttpServletRequest request) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.EDIT_ASSET.name()))) {
            throw new InsufficientPermissionException("Not allowed to edit assets.");
        }

        UUID uuid = UUID.fromString(id);
        Asset asset = assetRepository
                .findById(uuid)
                .orElseThrow(() -> new NullPointerException("No such asset found."));

        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("\n"));

            throw new ValidationException(errorMessages);
        }

        asset.setName(assetEditRequest.getName());
        asset.setDescription(assetEditRequest.getDescription());

        Asset savedAsset = assetRepository.save(asset);

        auditLogService.log(
                principal.getName(),
                auditLogService.extractRemoteIp(request),
                String.format("Edited asset: %s (%s)",
                        asset.getName(),
                        asset.getId()));

        return savedAsset;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Asset deleteAsset(@PathVariable String id, Principal principal, HttpServletRequest request) {
        if (((UsernamePasswordAuthenticationToken)principal).getAuthorities().stream().noneMatch(a -> a.getAuthority().equals(Permission.DELETE_ASSET.name()))) {
            throw new InsufficientPermissionException("Not allowed to delete assets.");
        }

        UUID uuid = UUID.fromString(id);
        Asset asset = assetRepository
                .findById(uuid)
                .orElseThrow(() -> new NullPointerException("No such asset found."));

        assetRepository.delete(asset);

        auditLogService.log(
                principal.getName(),
                auditLogService.extractRemoteIp(request),
                String.format("Deleted asset: %s (%s)",
                        asset.getName(),
                        asset.getId()));

        return asset;
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
