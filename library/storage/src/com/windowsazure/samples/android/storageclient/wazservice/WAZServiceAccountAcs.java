package com.windowsazure.samples.android.storageclient.wazservice;

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.security.auth.login.LoginException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;

import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.CloudClientAccount;
import com.windowsazure.samples.android.storageclient.CloudQueueClient;
import com.windowsazure.samples.android.storageclient.CloudTableClient;
import com.windowsazure.samples.android.storageclient.PathUtility;
import com.windowsazure.samples.android.storageclient.StorageCredentials;
import com.windowsazure.samples.android.storageclient.StorageCredentialsAcs;
import com.windowsazure.samples.android.storageclient.Utility;

public class WAZServiceAccountAcs implements CloudClientAccount {

	private String m_Token;
	private URI m_WazServiceBaseUri;

	public WAZServiceAccountAcs(String userName, String email, String rawToken, URI wazServiceBaseUri) {
		this.m_Token = rawToken;
		this.m_WazServiceBaseUri = wazServiceBaseUri;
		
		try {
			registerOnWAZService(userName, email);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public CloudBlobClient createCloudBlobClient() throws Exception {
		return new CloudBlobClient(getBlobEndpoint(), getCredentials());
	}
	
	public CloudTableClient createCloudTableClient() throws Exception {
		return new CloudTableClient(getTableEndpoint(), getCredentials());
	}
	
	public CloudQueueClient createCloudQueueClient() throws Exception {
		return new CloudQueueClient(getQueueEndpoint(), getCredentials());
	}
	
	public StorageCredentials getCredentials() throws Exception {
		return new StorageCredentialsAcs(this.m_Token);
	}

	private URI getBlobEndpoint() throws URISyntaxException {
    	return PathUtility.appendPathToUri(this.m_WazServiceBaseUri, SHARED_ACCESS_SIGNATURE_SERVICE_PATH);
	}

	private URI getTableEndpoint() throws URISyntaxException {
	 	return PathUtility.appendPathToUri(this.m_WazServiceBaseUri, TABLES_PROXY_SERVICE_PATH);
	}
	
	private URI getQueueEndpoint() throws URISyntaxException {
	    	return PathUtility.appendPathToUri(this.m_WazServiceBaseUri, QUEUES_PROXY_SERVICE_PATH);
	}
	
	public void registerOnWAZService(String userName, String email) throws Exception {
		final String CREDENTIAL_NS = "http://schemas.datacontract.org/2004/07/Microsoft.Samples.WindowsPhoneCloud.StorageClient.Credentials";
		final String INSTANCE_NS = "http://www.w3.org/2001/XMLSchema-instance";
		final String LOGIN_NODE_NAME = "RegistrationUser";
		final String NAME_NODE_NAME = "Name";
		final String EMAIL_NODE_NAME = "EMail";

		StringWriter stringwriter = new StringWriter();
		stringwriter.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		stringwriter.append(String.format("<%s xmlns=\"%s\" xmlns:i=\"%s\">\n", LOGIN_NODE_NAME, CREDENTIAL_NS, INSTANCE_NS));
		stringwriter.append(String.format("<%s>%s</%s>\n", EMAIL_NODE_NAME, email, EMAIL_NODE_NAME));
		stringwriter.append(String.format("<%s>%s</%s>\n", NAME_NODE_NAME, userName, NAME_NODE_NAME));
		stringwriter.append(String.format("</%s>\n", LOGIN_NODE_NAME));

		String loginXmlString = stringwriter.toString();

		HttpPost request = new HttpPost(PathUtility.appendPathToUri(this.m_WazServiceBaseUri, REGISTRATION_PATH));
		request.setEntity(new ByteArrayEntity(loginXmlString.getBytes()));
		request.setHeader("Content-Type", "text/xml");
		request.addHeader("Authorization", "OAuth " + this.m_Token);
		HttpClient client = Utility.getDefaultHttpClient();
		HttpResponse httpResponse = client.execute(request);
		
		if (httpResponse.getStatusLine().getStatusCode() != 200)
			throw new LoginException("Couldn't register to the WAZ Service: The registration service request returned " + httpResponse.getStatusLine().getReasonPhrase());
	}
    
	private static final String REGISTRATION_PATH = "/RegistrationService/register";
	private static final String SHARED_ACCESS_SIGNATURE_SERVICE_PATH = "/SharedAccessSignatureService";
	private static final String QUEUES_PROXY_SERVICE_PATH = "/AzureQueuesProxy.axd";
	private static final String TABLES_PROXY_SERVICE_PATH = "/AzureTablesProxy.axd";
}