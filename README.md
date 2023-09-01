Lutung - Java Mandrill API
======

NOTE: This project is a fork since the original project no longer maintained.

Lutung - a Java interface to the [Mandrill](http://www.mandrill.com/) API. 
Check out Mandrill's API [Documentation]
(https://mandrillapp.com/api/docs/) to see all the possible magic.

Features:

*  all public API calls are implemented.
*  easy library set up; just provide your api 
   key that you got from Mandrill.
*  all API calls are exposed through one simple interface: 
   the [MandrillApi](src/main/java/com/microtripit/mandrillapp/lutung/MandrillApi.java) 
   class.
*  easy, intuitive naming scheme. All function-names are derived from the 
   Mandrill API calls: if there is a call with the address 
   '/messages/send.json', then we have a function for that 
   called 'MandrillApi.messages().send(...)'.
*  API request errors are exposed to the user (you!) as a 
   [MandrillApiError](src/main/java/com/microtripit/mandrillapp/lutung/model/MandrillApiError.java). 

Installation
------------
If you're using Maven, just add this [dependency](https://search.maven.org/search?q=g:io.github.dnovitski%20AND%20a:lutung) to your pom.xml:
```
<dependency>
    <groupId>io.github.dnovitski</groupId>
    <artifactId>lutung</artifactId>
    <version>0.0.13</version>
</dependency>
```
If you're not using Maven, see [Dependencies](#dependencies) below.

Examples
--------
**The 'whoami' of Mandrill:**

```java
MandrillApi mandrillApi = new MandrillApi("<put ur Mandrill API key here>");

MandrillUserInfo user = mandrillApi.users().info();

// pretty-print w/ gson
Gson gson = new GsonBuilder().setPrettyPrinting().create();
System.out.println(gson.toJson(user));
```


**Send a 'Hello World!' email**
```java
MandrillApi mandrillApi = new MandrillApi("<put ur Mandrill API key here>");

// create your message
MandrillMessage message = new MandrillMessage();
message.setSubject("Hello World!");
message.setHtml("<h1>Hi pal!</h1><br />Really, I'm just saying hi!");
message.setAutoText(true);
message.setFromEmail("kitty@yourdomain.com");
message.setFromName("Kitty Katz");

// add recipients
List<Recipient> recipients = new ArrayList<>();

Recipient recipient = new Recipient();
recipient.setEmail("claireannette@someotherdomain.com");
recipient.setName("Claire Annette");
recipients.add(recipient);

Recipient recipient2 = new Recipient();
recipient2.setEmail("terrybull@yetanotherdomain.com");
recipients.add(recipient2);

message.setTo(recipients);
message.setPreserveRecipients(true);

List<String> tags = new ArrayList<>();
tags.add("test");
tags.add("helloworld");
message.setTags(tags);

// ... add more message details if you want to!
// then ... send
MandrillMessageStatus[] messageStatusReports = mandrillApi
		.messages().send(message, false);
```


**Error handling for Mandrill API errors**
```java
MandrillApi mandrillApi = new MandrillApi("<put ur Mandrill API key here>");

try {
	MandrillUserInfo user = mandrillApi.users().info();
} catch(MandrillApiError e) {
	log.error(e.getMandrillErrorAsJson(), e);
}
```


**Create a new template**
```java
MandrillApi mandrillApi = new MandrillApi("<put ur Mandrill API key here>");

MandrillTemplate newTemplate = mandrillApi.templates().add(
		"test_template_001", 
		"<html><body><h1>Hello World!</h1></body></html>",
		false);
```

<a name="dependencies"></a>
Dependencies
------------
If you're not using Maven, here's a list of dependencies. Just make sure these jar files are on your classpath:
* [google-gson](https://code.google.com/p/google-gson/)
* [Apache Http Components](http://hc.apache.org/index.html)
* [Apache Commons IO](http://commons.apache.org/proper/commons-io/)
* [Apache Commons Logging](http://commons.apache.org/proper/commons-logging/)

Known Issues
------------
*  The metadata returned by the mandrill api on 
   [/messages/search.json](https://mandrillapp.com/api/docs/messages.html#method=search)
   does not get mapped to a member of [MandrillMessageInfo](src/main/java/com/microtripit/mandrillapp/lutung/view/MandrillMessageInfo.java)
   
*  So far, I failed to successfully use Mandrills [/messages/send-raw.json](https://mandrillapp.com/api/docs/messages.html#method=send-raw)
   call. I'm not sure if I fail to create valid MIME contents, but lemme know if 
   you make any experience with this call.

*  Also, I have no inbound-emailing set up with Mandrill. Would be great if anyone 
   out there could test the implemented 'inbound' functionalities.

Lutung? Huh?
------------
**A monkey!!!** The [Javan Lutung](http://en.wikipedia.org/wiki/Javan_lutung) is the name giver 
for this project; hat tip to [MailChimp's](http://mailchimp.com/) naming scheme.

License
-------
This library is released under the GNU Lesser General Public 
License [http://www.gnu.org/licenses/lgpl.html](http://www.gnu.org/licenses/lgpl.html).

Release 0.0.13 - Release Notes
-------
* Bumped dependency versions of everything to more recent versions
* Removed wrapper around commons logger
* Clean up

Release 0.0.12 - Release Notes
-------
* Java 8 is now minimum version (instead of 1.6)
* commons-io updated from 2.5 to 2.7

Release 0.0.11 - Release Notes
-------
* Add support for configurable client timeouts. These can be configured via Java system properties (eg passing `-Dxxx=yyy` to the JVM args):
  * `mandrill.socket.timeout` (socket timeout in millis, default `5000`)
  * `mandrill.connection.timeout` (connection timeout in millis, default `5000`)
  * `mandrill.socket.linger.timeout` (socket linger timeout in seconds, default `0`)
* Default timeouts have been changed from no timeout to a more sensible default timeout described above to prevent the HTTP client from blocking indefinitely

Release 0.0.10 - Release Notes
-------
* Fixed CVE CVE-2020-13956

Release 0.0.9 - Release Notes
-------
* Forked project under new groupid: io.github.dnovitski
* Fixed NPE in httpClient.getParams due to modern httpclient
* Prevent NPE if response does not exist in MandrillRequestDispatcher.java

Release 0.0.8 - Release Notes
-------
* Fixed thread safety issue with `SimpleDateFormat`, create a new one each time.
[look](https://github.com/rschreijer/lutung/pull/77/files?w=1) thanks [chrisburrell](https://github.com/chrisburrell)

* Added additional fields to smtp data
[look](https://github.com/rschreijer/lutung/pull/81) thanks [lvogelzang](https://github.com/lvogelzang)

* Make the root URL configurable
[look](https://github.com/rschreijer/lutung/pull/82)

Release 0.0.7 - Release Notes
-------
* Fixed custom_quota field in the submarkets API [look](https://github.com/rschreijer/lutung/commit/f47e91fa0fb8c87d3afdc9e7c80298c653197703) thanks [Lokesh-Github123](https://github.com/Lokesh-Github123)
* Re-enabled headers on MandrillMessageContent [look](https://github.com/rschreijer/lutung/commit/26cc3c5b7f8e47abba63db8360876bd0219b37eb)
* Re-enabled metadata on MandrillMessageInfo [look](https://github.com/rschreijer/lutung/commit/3ad452da8fe98513ff93b325f4ed09158ed4313a)
* Catch Json parsing exceptions and throw a MandrillError with the body of the response as the message [look](https://github.com/rschreijer/lutung/commit/5f6b766faffdc803932f313d9a18f75f6e7db9cf)


Release 0.0.6 - Release Notes
-------
* Support to specify the merge-language when using templates, [look](https://github.com/rschreijer/lutung/commit/293627b9e0c81a4704922bca8f2b9f700d848152)
* Fixing endpoint URI for 'parse', [look](https://github.com/rschreijer/lutung/commit/e303934a53260697af0f7c88de0f367435e2ff2c)
* Better Android compatibility, [look](https://github.com/rschreijer/lutung/commit/381c34e014b6a12810e370eb90edd6c1308b9a83)
* Added lables for templates, [look](https://github.com/rschreijer/lutung/commit/d06dc36702cb629e1b8183ef2e278028e86c5f1a)

Thanks [billoneil](https://github.com/billoneil)

Release 0.0.5 - Release Notes
-------
* Added [messages/content.json](https://mandrillapp.com/api/docs/messages.JSON.html#method=content) (thanks @benfastmodel)
* Now regarding JVM proxy parameters (thanks @joseanibl138)
* Made commons-logging dependency optional (thanks @aldenquimby)

Release 0.0.3 - Release Notes
-------
*  Added support for Mandrill [sub accounts](https://mandrillapp.com/api/docs/subaccounts.JSON.html) 
   in [MandrillApi.subaccounts](src/main/java/com/microtripit/mandrillapp/lutung/controller/MandrillSubaccountsApi.java).
*  More support for **rejects** (blacklists): [add](https://mandrillapp.com/api/docs/rejects.JSON.html#method=add) 
   and [delete](https://mandrillapp.com/api/docs/rejects.JSON.html#method=delete); 
   also, list rejects for sub accounts. 
*  Added support for [whitelists](https://mandrillapp.com/api/docs/whitelists.JSON.html) 
   in [MandrillApi.whitelists](src/main/java/com/microtripit/mandrillapp/lutung/controller/MandrillWhitelistsApi.java).
*  Added support to manage [sender-domains](https://mandrillapp.com/api/docs/senders.JSON.html#method=add-domain) 
   in [MandrillApi.senders](src/main/java/com/microtripit/mandrillapp/lutung/controller/MandrillSendersApi.java).
*  Added support to [update templates](https://mandrillapp.com/api/docs/templates.JSON.html#method=update) 
   in [MandrillApi.templates](src/main/java/com/microtripit/mandrillapp/lutung/controller/MandrillTemplatesApi.java).
*  Added support for [exports](https://mandrillapp.com/api/docs/exports.JSON.html) 
   in [MandrillApi.exports](src/main/java/com/microtripit/mandrillapp/lutung/controller/MandrillExportsApi.java).
*  Added support for [dedicated IPs and IP Pools](https://mandrillapp.com/api/docs/ips.JSON.html) 
   in [MandrillApi.ips](src/main/java/com/microtripit/mandrillapp/lutung/controller/MandrillIpsApi.java).
