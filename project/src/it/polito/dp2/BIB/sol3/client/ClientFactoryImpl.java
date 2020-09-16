package it.polito.dp2.BIB.sol3.client;


import java.math.BigInteger;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;



import it.polito.dp2.BIB.ass3.Bookshelf;
import it.polito.dp2.BIB.ass3.Client;

import it.polito.dp2.BIB.ass3.DestroyedBookshelfException;
import it.polito.dp2.BIB.ass3.ItemReader;
import it.polito.dp2.BIB.ass3.ServiceException;


 
public class ClientFactoryImpl implements Client {
	javax.ws.rs.client.Client client;
	WebTarget target;
	static String uri = "http://localhost:8080/BiblioSystem/rest";
	static String urlProperty = "it.polito.dp2.BIB.ass3.URL";
	static String portProperty = "it.polito.dp2.BIB.ass3.PORT";
	
	
	public ClientFactoryImpl(URI uri) {
		this.uri = uri.toString();
		client = ClientBuilder.newClient();
		target = client.target(uri).path("biblio");
	}

	
	@Override
	public Bookshelf createBookshelf(String name) throws ServiceException {
		try{
			it.polito.dp2.BIB.sol3.client.Bookshelf bookshelf= new it.polito.dp2.BIB.sol3.client.Bookshelf();
			bookshelf.setName(name);
			bookshelf.setNreader(BigInteger.ZERO);
			
			Response r= target.path("/bookshelves")
				 	.request(MediaType.APPLICATION_JSON_TYPE)
				 	.post(Entity.json(bookshelf));
			
			if(r.getStatus()!=201)
				throw new ServiceException();
			
			bookshelf=(it.polito.dp2.BIB.sol3.client.Bookshelf) r.readEntity(it.polito.dp2.BIB.sol3.client.Bookshelf.class);
			System.out.println("CREAZIONE BOOKSHELF  =  <"+bookshelf.getName()+"> and  link:  "+bookshelf.getSelf());
			return new BookshelfReaderImpl(bookshelf);
		}catch (Exception e) {
			throw new ServiceException();
		}
	}

	@Override
	public Set<? extends Bookshelf> getBookshelfs(String name) throws ServiceException {
		try{
			if(name==null)
				throw new ServiceException();
			Set<Bookshelf> bookshelf=new HashSet<>();
			Response r= target.path("/bookshelves")
							 	.request(MediaType.APPLICATION_JSON_TYPE)
							 	.get();
			if(r.getStatus()!=200)
				throw new ServiceException();
			
			Bookshelves bookshelves = (it.polito.dp2.BIB.sol3.client.Bookshelves) r.readEntity(it.polito.dp2.BIB.sol3.client.Bookshelves.class);
			 for (it.polito.dp2.BIB.sol3.client.Bookshelves.Bookshelf b : bookshelves.getBookshelf()) {
				if(b.getName().contains(name))
					bookshelf.add(new BookshelfReaderImpl(b));
			}
			System.out.println("GET BOOKSHELF ");
			return bookshelf;
		}catch (Exception e) {
			throw new ServiceException();
		}
	}

	@Override
	public Set<ItemReader> getItems(String keyword, int since, int to) throws ServiceException {
		Set<ItemReader> itemSet=new HashSet<>();
		try{
			Response r = target.path("/items")
					.queryParam("keyword", keyword)
					.queryParam("beforeInclusive", to)
					.queryParam("afterInclusive", since)
				 	  .request(MediaType.APPLICATION_JSON_TYPE)
				 	  .get();
			if(r.getStatus()!=200)
				throw new ServiceException();
			
			Items items =(Items) r.readEntity( Items.class);
			for (it.polito.dp2.BIB.sol3.client.Items.Item i : items.getItem()) {
				itemSet.add(new ItemReaderImpl(i));
			}
		}catch (Exception e) {
			throw new ServiceException();
		}
		return itemSet;
	}
			
}
