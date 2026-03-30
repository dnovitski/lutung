/**
 *
 */
package com.microtripit.mandrillapp.lutung.view;

/**
 * <p>The sending results for a single recipient.</p>
 * @author rschreijer
 * @since Mar 16, 2013
 */
public class MandrillMessageStatus {
	private String email, status, reject_reason, queued_reason, _id;

	/**
	 * @return The email address of the recipient.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return The sending status of the recipient &ndash;
	 * either 'sent', 'queued', 'rejected', or 'invalid'.
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return The reason for the rejection if the recipient
	 * status is 'rejected'.
	 */
	public String getRejectReason() {
		return reject_reason;
	}

	/**
	 * @return The reason for the email being queued if the response status is "queued"
	 * Possible values: "attachments", "multiple-recipients",
	 * "free-trial-sends-exhausted", "hourly-quota-exhausted",
	 * "monthly-limit-reached", "sending-paused",
	 * "sending-suspended", "account-suspended",
	 * 	or "sending-backlogged".
	 */
	public String getQueuedReason() {
		return queued_reason;
	}

	/**
	 * @return The message's unique id.
	 */
	public String getId() {
		return _id;
	}

}
