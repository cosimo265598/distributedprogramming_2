package it.polito.dp2.BIB.sol3.service;

import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;



import it.polito.dp2.BIB.sol3.db.BadRequestInOperationException;
import it.polito.dp2.BIB.sol3.db.ConflictInOperationException;
import it.polito.dp2.BIB.sol3.db.DB;
import it.polito.dp2.BIB.sol3.db.ItemPage;
import it.polito.dp2.BIB.sol3.db.Neo4jDB;
import it.polito.dp2.BIB.sol3.service.jaxb.Bookshelf;
import it.polito.dp2.BIB.sol3.service.jaxb.Bookshelves;
import it.polito.dp2.BIB.sol3.service.jaxb.Citation;
import it.polito.dp2.BIB.sol3.service.jaxb.Item;
import it.polito.dp2.BIB.sol3.service.jaxb.Items;
import it.polito.dp2.BIB.sol3.service.util.ResourseUtils;


public class BiblioService {
	private DB n4jDb = Neo4jDB.getNeo4jDB();
	ResourseUtils rutil;

	private ConcurrentHashMap<BigInteger, Bookshelf> map_bs= new ConcurrentHashMap<BigInteger, Bookshelf>();
	private BigInteger id= BigInteger.ZERO;
			
			
	private BiblioService(UriInfo uriInfo) {
		rutil = new ResourseUtils((uriInfo.getBaseUriBuilder()));
	}
	
	private static BiblioService single_instance = null; 
	  

	public static BiblioService getInstance(UriInfo uri) 
    { 
        if (single_instance == null) 
            single_instance = new BiblioService(uri); 
  
        return single_instance; 
    }
	

	
	public Items getItems(SearchScope scope, String keyword, int beforeInclusive, int afterInclusive, BigInteger page) throws Exception {
		ItemPage itemPage = n4jDb.getItems(scope,keyword,beforeInclusive,afterInclusive,page);

		Items items = new Items();
		List<Item> list = items.getItem();
		
		Set<Entry<BigInteger,Item>> set = itemPage.getMap().entrySet();
		for(Entry<BigInteger,Item> entry:set) {
			Item item = entry.getValue();
			rutil.completeItem(item, entry.getKey());
			list.add(item);
		}
		items.setTotalPages(itemPage.getTotalPages());
		items.setPage(page);
		return items;
	}

	public Item getItem(BigInteger id) throws Exception {
			Item item = n4jDb.getItem(id);
			if (item!=null)
				rutil.completeItem(item, id);
			return item;
	}

	public Item updateItem(BigInteger id, Item item) throws Exception {
		Item ret = n4jDb.updateItem(id, item);
		if (ret!=null) {
			rutil.completeItem(item, id);
			return item;
		} else
			return null;
	}

	public Item createItem(Item item) throws Exception {
		BigInteger id = n4jDb.createItem(item);
		if (id==null)
			throw new Exception("Null id");
		rutil.completeItem(item, id);
		return item;
	}

	public BigInteger deleteItem(BigInteger id) throws ConflictServiceException, Exception {
		try {
			//return n4jDb.deleteItem(id);   

			Item item=getItem(id);
			for ( Entry<BigInteger,Bookshelf>  iter : map_bs.entrySet()) {
				iter.getValue().getItem().removeIf(x-> x.contentEquals(item.getSelf()));
			}
			
			return n4jDb.deleteItem(id);
		} catch (ConflictInOperationException e) {
			throw new ConflictServiceException();
		}catch (Exception e) {
			throw new InternalServerErrorException();
		}
	}

	public  Citation createItemCitation(BigInteger id, BigInteger tid, Citation citation) throws Exception {
		try {
			return n4jDb.createItemCitation(id, tid, citation);
		} catch (BadRequestInOperationException e) {
			throw new BadRequestServiceException();
		}
	}

	public  Citation getItemCitation(BigInteger id, BigInteger tid) throws Exception {
		Citation citation = n4jDb.getItemCitation(id,tid);
		if (citation!=null)
			rutil.completeCitation(citation, id, tid);
		return citation;
	}

	public  boolean deleteItemCitation(BigInteger id, BigInteger tid) throws Exception {
		return n4jDb.deleteItemCitation(id, tid);
	}

	public  Items getItemCitations(BigInteger id) throws Exception {
		ItemPage itemPage = n4jDb.getItemCitations(id, BigInteger.ONE);
		if (itemPage==null)
			return null;

		Items items = new Items();
		List<Item> list = items.getItem();
		
		Set<Entry<BigInteger,Item>> set = itemPage.getMap().entrySet();
		for(Entry<BigInteger,Item> entry:set) {
			Item item = entry.getValue();
			rutil.completeItem(item, entry.getKey());
			list.add(item);
		}
		items.setTotalPages(itemPage.getTotalPages());
		items.setPage(BigInteger.ONE);
		return items;
	}

	public  Items getItemCitedBy(BigInteger id) throws Exception {
		ItemPage itemPage = n4jDb.getItemCitedBy(id, BigInteger.ONE);
		if (itemPage==null)
			return null;

		Items items = new Items();
		List<Item> list = items.getItem();
		
		Set<Entry<BigInteger,Item>> set = itemPage.getMap().entrySet();
		for(Entry<BigInteger,Item> entry:set) {
			Item item = entry.getValue();
			rutil.completeItem(item, entry.getKey());
			list.add(item);
		}
		items.setTotalPages(itemPage.getTotalPages());
		items.setPage(BigInteger.ONE);
		return items;
	}
	
	/*
	 * *******************
	 * PART BOOKSHELVES
	 * *******************
	 */
	public synchronized Bookshelf createBookshelves(Bookshelf item) throws Exception {
		try{
			if(item.getItem().size()>20) 
				throw new BadRequestServiceException();
	
			map_bs.put(id, item);
			rutil.completeBookshelves(item, id);
			id=id.add(BigInteger.ONE);
			
			return item;
		}catch (Exception e) {
			throw new InternalServerErrorException();
		}
	}
	public Bookshelves getBookshelves(String name) throws Exception {
		try{
			Bookshelves b= new Bookshelves();
			b.setNext("");
			b.setPage(BigInteger.ONE);
			b.setTotalPages(BigInteger.ONE);
	
			Bookshelf[] mylist=map_bs.values().stream().filter(z-> z.getName().startsWith(name)).toArray(Bookshelf[]::new);
			b.getBookshelf().addAll(Arrays.asList(mylist));
			return b;
		}catch (Exception e) {
			throw new InternalServerErrorException();
		}
	}
	
	public boolean deleteAll_Bookshelves() throws Exception {
		try{
			map_bs.clear();
			return true;
		}
		catch (Exception e) {
			throw new InternalServerErrorException();
		}	
	}
	
	public BigInteger getSingleBookshelfRead(BigInteger id) throws Exception {
		try{
			Bookshelf bookshelf= map_bs.get(id);
			if(bookshelf==null)
				throw new NotFoundException();

			BigInteger retBig=null;
			retBig=bookshelf.getNreader();
			if(retBig==null)
				return null;
			return retBig;
			
		}catch (NotFoundException e) {
			throw new NotFoundException();
		}catch (Exception e) {
			throw new InternalServerErrorException();
		}
	}
	public Bookshelf getSingleBookshelById(BigInteger id) throws Exception {
		try{
			return map_bs.get(id);
		}catch (Exception e) {
			throw new InternalServerErrorException();
		}
	}
	public Boolean deleteSingleBookshelf(BigInteger id) throws Exception{
		try{
			if(map_bs.remove(id)!=null)
				return true;
			return false;
		}catch (Exception e) {
			throw new InternalServerErrorException();
		}
	}
	
	public List<Item> getItemsFromBookshelf(BigInteger id) throws Exception{
		try{
			Bookshelf bookshelf= map_bs.get(id);
			if(bookshelf==null)
				throw new NotFoundException();

			
			List<String> list_i=bookshelf.getItem();
			String[] ls = list_i.stream().toArray(String[] ::new);
			List<Item> m=new ArrayList<>();
			
			for (String item : ls) {
				String[] a=item.split("BiblioSystem/rest/biblio/items/");
				try{
					m.add(getItem(new BigInteger(a[1])));
				}catch (Exception e) {
					throw new NotFoundException();
				}
			}
			
			BigInteger b=bookshelf.getNreader();		
			synchronized (bookshelf.getNreader()) {	
				bookshelf.setNreader(b.add(BigInteger.ONE));   
			}
			
			return m;

		}catch(NotFoundException e){
			throw new NotFoundException();
		}catch (Exception e ){
			e.printStackTrace();
			throw new InternalServerErrorException();
		}
	}
	
	public synchronized Items addItemsToBookshelf(BigInteger id,String items) throws Exception{
		try{
			Bookshelf bookshelf= map_bs.get(id);
			if(bookshelf==null)
				throw new NotFoundException();
			
			int size = bookshelf.getItem().size();
			if(size+1>20)
				throw new NotAcceptableException();
			
			//test the exist item 
			String[] a=items.split("BiblioSystem/rest/biblio/items/");
			if(getItem(new BigInteger(a[1])) == null)
				throw new NotFoundException();
			
			List<String> sl= bookshelf.getItem();
				if(sl.contains(items.toString())!=true)
					bookshelf.getItem().add(items.toString());
				else
					throw new ConflictServiceException();
			
			Items i=new Items();
			List<Item> m=new ArrayList<>();
			for (String item : bookshelf.getItem()) {
				String[] b=item.split("BiblioSystem/rest/biblio/items/");
				m.add(getItem(new BigInteger(b[1])));
			}
			i.getItem().addAll(m);
			
			return i;
			
		}catch (BadRequestServiceException e) {
			throw new BadRequestException();
		} catch (ConflictServiceException e) {
			throw new ConflictServiceException();
		} catch(NotFoundException e){
			throw new NotFoundException();
		}catch (NotAcceptableException e ){
			throw new NotAcceptableException();
		}catch (Exception e ){
			throw new InternalServerErrorException();
		}
		
	}
	
	public synchronized void deleteItemsBookshelf(BigInteger id){
		try{
			Bookshelf bookshelf= map_bs.get(id);
			if(bookshelf==null)
				throw new NotFoundException();

			bookshelf.getItem().clear();
			
		}catch(NotFoundException e){
			throw new NotFoundException();
		}catch(Exception e){
			throw new InternalServerErrorException();
		}
	}

	public Item getSingleItemFormList(BigInteger id, BigInteger iditem) throws Exception {
		try{
			Bookshelf bookshelf= map_bs.get(id);
			if(bookshelf==null)
				throw new NotFoundException();

			
			List<String> list_i=bookshelf.getItem();
			String[] ls = list_i.stream().toArray(String[] ::new);
			Item i= null;	
			
			for (String s : ls) {
				String[] a=s.split("BiblioSystem/rest/biblio/items/");
				BigInteger b= new BigInteger(a[1]);
				if(b.equals(iditem)){		
					try{
						i=getItem(b);
					}
					catch (Exception e) { throw new NotFoundException(); }
					break;
				}
			}
			
			BigInteger b=bookshelf.getNreader();		
			synchronized (bookshelf.getNreader()) {	
				bookshelf.setNreader(b.add(BigInteger.ONE));   
			}
			if(i!=null)
				return i;
			
			throw new NotFoundException();
			
		}catch(NotFoundException e){
			throw new NotFoundException();
		}catch (Exception e){
			throw new InternalServerErrorException();
		}		
	}


	public synchronized void deleteSingleItemFormList(BigInteger id, BigInteger iditem) {
		try{
			Bookshelf bookshelf= map_bs.get(id);
			if(bookshelf==null)
				throw new NotFoundException();
			
			int index=0;
			for (String s : bookshelf.getItem()) {
				String[] a=s.split("BiblioSystem/rest/biblio/items/");
				BigInteger b= new BigInteger(a[1]);
				index++;
				if(b.equals(iditem)){
					bookshelf.getItem().remove(index-1);
					return ;
				}
			}
			throw new InternalServerErrorException();
		}catch(NotFoundException e){
			throw new NotFoundException();
		}catch (Exception e ){
			throw new InternalServerErrorException();
		}	
	}

}
