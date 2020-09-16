package it.polito.dp2.BIB.sol3.client;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriBuilder;

import it.polito.dp2.BIB.ass3.Client;
import it.polito.dp2.BIB.ass3.ClientException;

public class ClientFactory extends it.polito.dp2.BIB.ass3.ClientFactory{

	@Override
	public Client newClient() throws ClientException {
		URI  uri = null;
		ClientFactoryImpl myclient;
		try {
			/*
			String uriProp = System.getProperty("it.polito.dp2.BIB.ass3.URL");
			String uriPort= System.getProperty("it.polito.dp2.BIB.ass3.PORT");
			System.out.println("propertyURI: "+uriProp);
			System.out.println("propertyPort: "+uriPort);
			if(uriProp!=null) {
				uri= new URI(uriProp);
			}else {
				uri= new URI("http://localhost:8080/BiblioSystem/rest/");
				System.setProperty("it.polito.dp2.BIB.ass3.URL","http://localhost:8080/BiblioSystem/rest/");
			}
			if(uriPort==null)
				System.setProperty("it.polito.dp2.BIB.ass3.PORT","8080");
				*/
			uri=getBaseURI();
			myclient= new ClientFactoryImpl(uri);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ClientException();
		}
		return myclient;
		
	}
	
	private URI getBaseURI() {
		if(System.getProperty("it.polito.dp2.BIB.ass3.URL") == null) {
			if(System.getProperty("it.polito.dp2.BIB.ass3.PORT") == null)
				return UriBuilder.fromUri("http://localhost:8080/BiblioSystem/rest").build();
			else
				return UriBuilder.fromUri("http://localhost:" + System.getProperty("it.polito.dp2.BIB.ass3.PORT") + "/BiblioSystem/rest/").build();
		}
		return UriBuilder.fromUri(System.getProperty("it.polito.dp2.BIB.ass3.URL")).build();
	}


}
