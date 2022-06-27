/**
 *
 */
package com.microtripit.mandrillapp.lutung;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author rschreijer
 * @since Mar 21, 2013
 */
public final class MandrillApiTest extends MandrillTestCase {

	@Test
	public void testApiKey() {
		final String key = mandrillApi.getKey();
		Assert.assertNotNull(key);
		Assert.assertFalse( key.isEmpty() );
		Assert.assertNotEquals("<put ur Mandrill API key here>", key);
	}

	@Test
	public void testUsers() {
		Assert.assertNotNull(mandrillApi.users());
	}

	@Test
	public void testMessages() {
		Assert.assertNotNull(mandrillApi.messages());
	}

	@Test
	public void testTags() {
		Assert.assertNotNull(mandrillApi.tags());
	}

	@Test
	public void testRejects() {
		Assert.assertNotNull(mandrillApi.rejects());
	}

	@Test
	public void testWhitelists() {
		Assert.assertNotNull(mandrillApi.whitelists());
	}

	@Test
	public void testSenders() {
		Assert.assertNotNull(mandrillApi.senders());
	}

	@Test
	public void testUrls() {
		Assert.assertNotNull(mandrillApi.urls());
	}

	@Test
	public void testTemplates() {
		Assert.assertNotNull(mandrillApi.templates());
	}

	@Test
	public void testWebhooks() {
		Assert.assertNotNull(mandrillApi.webhooks());
	}

	@Test
	public void testSubaccounts() {
		Assert.assertNotNull(mandrillApi.subaccounts());
	}

	@Test
	public void testInbound() {
		Assert.assertNotNull(mandrillApi.inbound());
	}

	@Test
	public void testExports() {
		Assert.assertNotNull(mandrillApi.exports());
	}

	@Test
	public void testIps() {
		Assert.assertNotNull(mandrillApi.ips());
	}
}
