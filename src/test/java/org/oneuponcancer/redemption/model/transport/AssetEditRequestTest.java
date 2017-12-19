package org.oneuponcancer.redemption.model.transport;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class AssetEditRequestTest {
    private AssetEditRequest assetEditRequest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        assetEditRequest = new AssetEditRequest();
    }

    @Test
    public void testName() throws Exception {
        assetEditRequest.setName("Foop");

        assertEquals("Foop", assetEditRequest.getName());
    }

    @Test
    public void testDescription() throws Exception {
        assetEditRequest.setDescription("A big bag of foop.");

        assertEquals("A big bag of foop.", assetEditRequest.getDescription());
    }
}
