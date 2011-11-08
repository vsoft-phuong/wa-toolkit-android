package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;

import org.apache.http.client.methods.HttpRequestBase;

public class WAZServiceAccountCredentials extends StorageCredentials {

	private String m_AuthorizationToken;

	public WAZServiceAccountCredentials(String authorizationToken) {
		this.m_AuthorizationToken = authorizationToken;
	}

	@Override
	protected boolean canCredentialsComputeHmac()
			throws NotImplementedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean canCredentialsSignRequest()
			throws NotImplementedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean canCredentialsSignRequestLite()
			throws NotImplementedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String computeHmac256(String s) throws NotImplementedException,
			InvalidKeyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String computeHmac512(String s) throws NotImplementedException,
			InvalidKeyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String containerEndpointPostfix() {
		return "container/";
	}

	@Override
	StorageCredentials credentialsForBlobOf(
			CloudBlobContainer cloudBlobContainer)
			throws IllegalArgumentException, UnsupportedEncodingException,
			NotImplementedException, URISyntaxException, StorageException,
			IOException {
		URI uri = cloudBlobContainer.getTransformedAddress();
		String decoded = uri.toString().replace("&amp;", "&");
		HashMap<String, String[]> arguments = PathUtility
				.parseQueryString(decoded);
		return SharedAccessSignatureHelper.parseQuery(arguments);
	}

	@Override
	protected boolean doCredentialsNeedTransformUri()
			throws NotImplementedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getAccountName() {
		return null;
	}

	@Override
	AbstractBlobRequest getBlobRequest() {
		return new BlobWASServiceRequest();
	}

	@Override
	AbstractContainerRequest getContainerRequest() {
		return new ContainerWASServiceRequest();
	}

	@Override
	public void signRequest(HttpRequestBase request, long l)
			throws NotImplementedException, InvalidKeyException,
			StorageException {
		request.addHeader("AuthToken", m_AuthorizationToken);
	}

	@Override
	public void signRequestLite(HttpRequestBase request, long l)
			throws NotImplementedException, StorageException,
			InvalidKeyException {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString(boolean boolean1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI transformUri(URI uri) throws NotImplementedException,
			URISyntaxException, StorageException {
		// TODO Auto-generated method stub
		return null;
	}
}