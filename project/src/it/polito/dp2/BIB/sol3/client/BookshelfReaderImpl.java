package it.polito.dp2.BIB.sol3.client;

import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import io.swagger.util.Json;
import it.polito.dp2.BIB.ass3.DestroyedBookshelfException;
import it.polito.dp2.BIB.ass3.ItemReader;
import it.polito.dp2.BIB.ass3.ServiceException;
import it.polito.dp2.BIB.ass3.TooManyItemsException;
import it.polito.dp2.BIB.ass3.UnknownItemException;

public class BookshelfReaderImpl implements it.polito.dp2.BIB.ass3.Bookshelf {
	static private int MAX_ITEMS_INSIDE=20;

	private Integer id;
	private String name;
	private String self;
	private BigInteger nreader;
	
	public BookshelfReaderImpl(Bookshelf bookshelf) {
		String[] s=bookshelf.getSelf().split("BiblioSystem/rest/biblio/bookshelves/");
		this.id=Integer.valueOf(s[1]);
		this.name = bookshelf.getName();
		this.self = bookshelf.getSelf();
		this.nreader = bookshelf.getNreader();
	}
	public BookshelfReaderImpl(Bookshelves.Bookshelf bookshelf) {
		String[] s=bookshelf.getSelf().split("BiblioSystem/rest/biblio/bookshelves/");
		this.id=Integer.valueOf(s[1]);
		this.name = bookshelf.getName();
		this.self = bookshelf.getSelf();
		this.nreader = bookshelf.getNreader();
	}
	

	@Override
	public String getName() throws DestroyedBookshelfException {
		if(testExistBookshelf(id)!=null)
			return name;
		throw new DestroyedBookshelfException();
		
	}

	@Override
	public void addItem(ItemReader item) throws DestroyedBookshelfException, UnknownItemException, ServiceException, TooManyItemsException {
		javax.ws.rs.client.Client client = ClientBuilder.newClient();	
		String self=null;

		if (item == null)
			throw new UnknownItemException("Item is a null object!");

		if(!(item instanceof ItemReaderImpl))
			throw new UnknownItemException("Item is an object whit wrong implementation");
		
		ItemReaderImpl a = (ItemReaderImpl) item;
		self= a.getSelf();
		if(self==null)
			throw new UnknownItemException("Item is an object whit wrong implementation");

		System.out.println("Send item con self= " + self);
				
		Response r1= client.target(ClientFactoryImpl.uri.toString())
				.path("biblio/bookshelves/"+id.toString()+"/items")
				.request(MediaType.APPLICATION_XML_TYPE)
				.put(Entity.xml(self));
				
		if(r1.getStatus()==200 || r1.getStatus()==201){
			System.out.println("SEND OK");
			return;
		}
		if(r1.getStatus()==404)
			throw new DestroyedBookshelfException("Bookshelf does not exist!");
		
		if(r1.getStatus()==406)
			throw new TooManyItemsException("Limit of item inside bookshelf reached! MAX= "+MAX_ITEMS_INSIDE);
	
		if(r1.getStatus()==409)
			throw new ServiceException("Item already exist in the list!");
		
		throw new ServiceException();
	}

	@Override
	public void removeItem(ItemReader item) throws DestroyedBookshelfException, UnknownItemException, ServiceException {
		if(item==null )
			throw new UnknownItemException("Item is a null object");
		
		javax.ws.rs.client.Client client = ClientBuilder.newClient();

		if(!(item instanceof ItemReaderImpl))
			throw new UnknownItemException("Item is an object whit wrong implementation");
		
		ItemReaderImpl i = (ItemReaderImpl) item;

		System.out.println("Remove item con self= " + i.getSelf());

		Response response=client.target(ClientFactoryImpl.uri.toString())
				.path("biblio/bookshelves/"+id.toString()+"/items/"+i.getId())
				.request(MediaType.APPLICATION_JSON_TYPE)
				.delete();

		System.out.println("R status " + response.getStatus());
		if(response.getStatus()==204){
			return;
		}

		if(response.getStatus()==404)
			throw new DestroyedBookshelfException("Bookshelf destroyed!");

		throw new ServiceException();
	}

	@Override
	public Set<ItemReader> getItems() throws DestroyedBookshelfException, ServiceException {

		javax.ws.rs.client.Client client = ClientBuilder.newClient();
		Response i=client.target(ClientFactoryImpl.uri.toString())
				.path("biblio/bookshelves/"+id.toString()+"/items")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.get();
		if(i.getStatus()==404)
			throw new DestroyedBookshelfException("Bookshelf destroyed!");

		if(i.getStatus()!=200)
			throw new ServiceException();
		
		Items it=(Items) i.readEntity(Items.class);
		Set<ItemReader> my_set= new HashSet<>();
		for (Items.Item ii : it.getItem()) {
			my_set.add(new ItemReaderImpl(ii));
		}
		return my_set;
	} 

	@Override
	public void destroyBookshelf() throws DestroyedBookshelfException, ServiceException {
		javax.ws.rs.client.Client client = ClientBuilder.newClient();
		Response resp=client.target(ClientFactoryImpl.uri.toString())
				.path("biblio/bookshelves/"+id.toString())
				.request(MediaType.APPLICATION_JSON_TYPE)
				.delete();
		if(resp.getStatus()==204)
			return;
		if(resp.getStatus()==404)
			throw new DestroyedBookshelfException();
		throw new ServiceException();
	}

	@Override
	public int getNumberOfReads() throws DestroyedBookshelfException {
		javax.ws.rs.client.Client client = ClientBuilder.newClient();
		Response resp=client.target(ClientFactoryImpl.uri.toString())
				.path("biblio/bookshelves/"+id.toString()+"/nreader")
				.request(MediaType.APPLICATION_JSON_TYPE)
				.get();
		
		if(resp.getStatus()==200)
			return (int) resp.readEntity(BigInteger.class).intValue();
		else
			throw new DestroyedBookshelfException();
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		Set<ItemReader> setI= null;
		try {
			setI= this.getItems();
		} catch (DestroyedBookshelfException e1) {
			return "Bookshelf destroyed";
		} catch (ServiceException e1) {
			return "Service Error";
		}
		
		sb.append("\n****************************************************\nBookshel id= "+id+" \nname= "+name);
		sb.append("\nSelf= "+self);
		sb.append("\nN-reader= "+nreader);
		sb.append("\nItems= "+setI.size());
		
		try {
			for (it.polito.dp2.BIB.ass3.ItemReader i : setI)  {
				sb.append("\n- "+i.getTitle());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	
		return sb.toString();
	}

	private Bookshelf testExistBookshelf(int id){
		javax.ws.rs.client.Client client = ClientBuilder.newClient();
		Response resp=client.target(ClientFactoryImpl.uri.toString())
				.path("biblio/bookshelves/"+id)
				.request(MediaType.APPLICATION_JSON_TYPE)
				.get();
		if(resp.getStatus()==200)
			return (Bookshelf) resp.readEntity(Bookshelf.class);
		return null;
		
	}
	

}
