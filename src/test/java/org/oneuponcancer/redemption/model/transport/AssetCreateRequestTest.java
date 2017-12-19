package org.oneuponcancer.redemption.model.transport;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class AssetCreateRequestTest {
    private AssetCreateRequest assetCreateRequest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        assetCreateRequest = new AssetCreateRequest();
    }

    @Test
    public void testName() throws Exception {
        assetCreateRequest.setName("Foop");

        assertEquals("Foop", assetCreateRequest.getName());
    }

    @Test
    public void testDescription() throws Exception {
        assetCreateRequest.setDescription("A big bag of foop.");

        assertEquals("A big bag of foop.", assetCreateRequest.getDescription());
    }
}
