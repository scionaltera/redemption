package org.oneuponcancer.redemption.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oneuponcancer.redemption.exception.InsufficientPermissionException;
import org.oneuponcancer.redemption.model.Asset;
import org.oneuponcancer.redemption.model.Permission;
import org.oneuponcancer.redemption.model.transport.AssetCreateRequest;
import org.oneuponcancer.redemption.model.transport.AssetEditRequest;
import org.oneuponcancer.redemption.repository.AssetRepository;
import org.oneuponcancer.redemption.service.AuditLogService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class AssetResourceTest {
    @Captor
    private ArgumentCaptor<Asset> assetArgumentCaptor;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private UsernamePasswordAuthenticationToken principal;

    @Mock
    private HttpServletRequest request;

    private List<Asset> allAssets = new ArrayList<>();

    private AssetResource assetResource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        for (int i = 0; i < 3; i++) {
            allAssets.add(mock(Asset.class));
        }

        when(assetRepository.findAll()).thenReturn(allAssets);
        when(assetRepository.save(any(Asset.class))).thenAnswer(i -> {
            Asset asset = i.getArgumentAt(0, Asset.class);

            asset.setId(UUID.randomUUID().toString());

            return asset;
        });

        assetResource = new AssetResource(
                assetRepository,
                auditLogService);
    }

    @Test
    public void testFetchAsset() throws Exception {
        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.LIST_ASSET.name())));

        List<Asset> result = assetResource.fetchAsset(principal);

        assertFalse(result.isEmpty());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testFetchAssetNoPermission() throws Exception {
        assetResource.fetchAsset(principal);
    }

    @Test
    public void testCreateAsset() throws Exception {
        AssetCreateRequest createRequest = mock(AssetCreateRequest.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_ASSET.name())));
        when(createRequest.getName()).thenReturn("Foop");
        when(createRequest.getDescription()).thenReturn("A big bag of foop.");

        Asset response = assetResource.createAsset(
                createRequest,
                bindingResult,
                principal,
                request
        );

        assertNotNull(response);
        verify(assetRepository).save(assetArgumentCaptor.capture());
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(anyString(), anyString(), anyString());

        Asset asset = assetArgumentCaptor.getValue();

        assertEquals("Foop", asset.getName());
        assertEquals("A big bag of foop.", asset.getDescription());
        assertNotNull(asset.getId());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testCreateAssetNoPermission() throws Exception {
        AssetCreateRequest createRequest = mock(AssetCreateRequest.class);

        when(createRequest.getName()).thenReturn("Foop");
        when(createRequest.getDescription()).thenReturn("A big bag of foop.");

        assetResource.createAsset(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testCreateAssetInvalidName() throws Exception {
        AssetCreateRequest createRequest = mock(AssetCreateRequest.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_ASSET.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid name.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(createRequest.getName()).thenReturn("");
        when(createRequest.getDescription()).thenReturn("A big bag of foop.");

        assetResource.createAsset(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testCreateAssetInvalidDescription() throws Exception {
        AssetCreateRequest createRequest = mock(AssetCreateRequest.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.CREATE_ASSET.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid description.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(createRequest.getName()).thenReturn("Foop");
        when(createRequest.getDescription()).thenReturn("");

        assetResource.createAsset(
                createRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test
    public void testUpdateAsset() throws Exception {
        String id = "1";
        AssetEditRequest editRequest = mock(AssetEditRequest.class);
        Asset asset = mock(Asset.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_ASSET.name())));
        when(assetRepository.findOne(eq(id))).thenReturn(asset);
        when(editRequest.getName()).thenReturn("Foop");
        when(editRequest.getDescription()).thenReturn("A big bag of foop.");

        Asset response = assetResource.updateAsset(
                id,
                editRequest,
                bindingResult,
                principal,
                request
        );

        assertNotNull(response);
        verify(asset).setName(eq("Foop"));
        verify(asset).setDescription(eq("A big bag of foop."));
        verify(assetRepository).save(eq(asset));
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(anyString(), anyString(), anyString());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testUpdateAssetNoPermission() throws Exception {
        String id = "1";
        AssetEditRequest editRequest = mock(AssetEditRequest.class);
        Asset asset = mock(Asset.class);

        when(assetRepository.findOne(eq(id))).thenReturn(asset);
        when(editRequest.getName()).thenReturn("Carp");
        when(editRequest.getDescription()).thenReturn("A bucket of carp.");

        assetResource.updateAsset(
                id,
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateAssetNotFound() throws Exception {
        String id = "1";
        AssetEditRequest editRequest = mock(AssetEditRequest.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_ASSET.name())));
        when(editRequest.getName()).thenReturn("Carp");
        when(editRequest.getDescription()).thenReturn("A bucket of carp.");

        assetResource.updateAsset(
                id,
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testUpdateAssetBadName() throws Exception {
        String id = "1";
        AssetEditRequest editRequest = mock(AssetEditRequest.class);
        Asset asset = mock(Asset.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_ASSET.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid name.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(assetRepository.findOne(eq(id))).thenReturn(asset);
        when(editRequest.getName()).thenReturn("");
        when(editRequest.getDescription()).thenReturn("A bucket of carp.");

        assetResource.updateAsset(
                id,
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test(expected = ValidationException.class)
    public void testUpdateAssetBadDescription() throws Exception {
        String id = "1";
        AssetEditRequest editRequest = mock(AssetEditRequest.class);
        Asset asset = mock(Asset.class);
        ObjectError objectError = mock(ObjectError.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.EDIT_ASSET.name())));
        when(objectError.getDefaultMessage()).thenReturn("Invalid description.");
        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(objectError));
        when(assetRepository.findOne(eq(id))).thenReturn(asset);
        when(editRequest.getName()).thenReturn("Carp");
        when(editRequest.getDescription()).thenReturn("");

        assetResource.updateAsset(
                id,
                editRequest,
                bindingResult,
                principal,
                request
        );
    }

    @Test
    public void testDeleteAsset() throws Exception {
        String id = "id";
        Asset asset = mock(Asset.class);

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.DELETE_ASSET.name())));
        when(assetRepository.findOne(eq(id))).thenReturn(asset);

        Asset result = assetResource.deleteAsset(
                id,
                principal,
                request
        );

        assertEquals(asset, result);
        verify(assetRepository).findOne(eq(id));
        verify(assetRepository).delete(eq(asset));
        verify(auditLogService).extractRemoteIp(eq(request));
        verify(auditLogService).log(anyString(), anyString(), anyString());
    }

    @Test(expected = InsufficientPermissionException.class)
    public void testDeleteAssetNoPermission() throws Exception {
        String id = "id";

        assetResource.deleteAsset(
                id,
                principal,
                request
        );
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteAssetNotFound() throws Exception {
        String id = "id";

        when(principal.getAuthorities()).thenReturn(Collections.singletonList(new SimpleGrantedAuthority(Permission.DELETE_ASSET.name())));

        assetResource.deleteAsset(
                id,
                principal,
                request
        );
    }
}
