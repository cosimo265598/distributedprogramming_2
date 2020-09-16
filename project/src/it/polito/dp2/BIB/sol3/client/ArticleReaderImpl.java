package it.polito.dp2.BIB.sol3.client;

import java.util.Set;

import it.polito.dp2.BIB.ass3.ArticleReader;
import it.polito.dp2.BIB.ass3.ItemReader;
import it.polito.dp2.BIB.sol3.client.Items.Item;

public class ArticleReaderImpl extends ItemReaderImpl implements ArticleReader {

	private ItemReaderImpl i=null;
	private String journal;
	private int number;
	private int volume;
	
	
	public ArticleReaderImpl(Item i, ItemReaderImpl i2, String journal, int number, int volume) {
		super(i);
		this.i = i2;
		this.journal = journal;
		this.number = number;
		this.volume = volume;
	}
	/*
	public ArticleReaderImpl(ItemReaderImpl i) {
		this.journal = i.getArticle().getJournal();
		this.number = i.getArticle().getNumber().intValue();
		this.volume = i.getArticle().getVolume().getYear();
		this.i=i;
	}*/

	@Override
	public String[] getAuthors() {
		// TODO Auto-generated method stub
		return i.getAuthors();
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return i.getTitle();
	}

	@Override
	public String getSubtitle() {
		// TODO Auto-generated method stub
		return i.getSubtitle();
	}

	@Override
	public Set<ItemReader> getCitingItems() {
		// TODO Auto-generated method stub
		return i.getCitingItems();
	}

	@Override
	public String getJournal() {
		// TODO Auto-generated method stub
		return journal;
	}

	@Override
	public int getYear() {
		// TODO Auto-generated method stub
		return volume;
	}

	@Override
	public int getNumber() {
		// TODO Auto-generated method stub
		return number;
	}

}
