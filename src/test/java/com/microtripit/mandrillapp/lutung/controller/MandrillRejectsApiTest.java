/**
 * 
 */
package com.microtripit.mandrillapp.lutung.controller;

import java.io.IOException;

import org.junit.Assert;

import org.junit.Test;

import com.microtripit.mandrillapp.lutung.MandrillTestCase;
import com.microtripit.mandrillapp.lutung.model.MandrillApiError;

/**
 * @author rschreijer
 * @since Mar 21, 2013
 */
public final class MandrillRejectsApiTest extends MandrillTestCase {
	
	@Test(expected=MandrillApiError.class)
	public final void testAdd() throws IOException, MandrillApiError {
		mandrillApi.rejects().add(null, null, null);
		Assert.fail();
	}
	
	@Test
	public final void testList() throws IOException, MandrillApiError {
		Assert.assertNotNull( mandrillApi.rejects().list(null, null) );
		
	}
	
	@Test(expected=MandrillApiError.class)
	public final void testDelete() throws IOException, MandrillApiError {
		mandrillApi.rejects().delete(null);
		Assert.fail();
	}

}
