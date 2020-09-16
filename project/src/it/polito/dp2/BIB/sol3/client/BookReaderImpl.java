package it.polito.dp2.BIB.sol3.client;

import java.util.Set;

import it.polito.dp2.BIB.BookReader;
import it.polito.dp2.BIB.ItemReader;
import it.polito.dp2.BIB.sol3.client.Items.Item;

public class BookReaderImpl extends ItemReaderImpl implements it.polito.dp2.BIB.ass3.BookReader{

	//private ItemReaderImpl i=null;
	private String ISBN;
	private String publisher;
	private int year;
	
	
	public BookReaderImpl(Item i,String iSBN, String publisher, int year) {
		super(i);
		ISBN = iSBN;
		this.publisher = publisher;
		this.year = year;
	}
	/*
	public BookReaderImpl(ItemReaderImpl i) {
		this.ISBN = i.getBook().getISBN();
		this.publisher = i.getBook().getPublisher();
		this.year = i.getBook().getYear().getYear();
		this.i=i;
	}*/

	@Override
	public String getISBN() {
		return ISBN;
	}

	@Override
	public String getPublisher() {
		return publisher;
	}

	@Override
	public int getYear() {
		return year;
	}

	@Override
	public String[] getAuthors() {
		return super.getAuthors();
	}


	@Override
	public String getTitle() {
		return super.getTitle();
	}


	@Override
	public String getSubtitle() {
		return super.getSubtitle();
	}


	@Override
	public Set<it.polito.dp2.BIB.ass3.ItemReader> getCitingItems() {
		return super.getCitingItems();
	}


	


}
