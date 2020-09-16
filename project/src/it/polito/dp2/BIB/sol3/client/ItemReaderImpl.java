package it.polito.dp2.BIB.sol3.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import it.polito.dp2.BIB.ass3.BookReader;
import it.polito.dp2.BIB.ass3.ItemReader;
import it.polito.dp2.BIB.sol3.client.Items.Item;

public class ItemReaderImpl implements it.polito.dp2.BIB.ass3.ItemReader{

	private Integer id;
	private String title;
	private String subtitle = null;
	private List<String> authors;
	private String self;
	private Set<it.polito.dp2.BIB.ass3.ItemReader> citingItems;
	private BookType book= null;
	private ArticleType article= null;
	private BookReader br=null;
	private String citation;
	private String citedby;
	private String target;
	
	public ItemReaderImpl(Item i) {
		// common field
		String[] s=i.getSelf().split("/items/");
		this.id=Integer.valueOf(s[1]);
		this.self=i.getSelf();
		this.title = i.getTitle();
		if (i.getSubtitle() != null)
			this.subtitle = i.getSubtitle();
		this.authors = new ArrayList<>();
		if (i.getAuthor() != null) {
			if (! i.getAuthor().isEmpty())
				this.authors.addAll(i.getAuthor());
		}
		// field different
		if(i.getBook() instanceof BookType){
			book= i.getBook();
		}
		if(i.getArticle() instanceof ArticleType){
			article= i.getArticle();
		}
		citation= i.getCitations();
		target= i.getTargets();
		citedby= i.getCitedBy();
		citingItems = new HashSet<>();		
	}
	
	public BookType getBook(){
		return book;
	}

	public ArticleType getArticle(){
		return article;
	}

	public Integer getId()  {
		return id;
	}
	public String getSelf()  {
		return self;
	}
	
	@Override
	public String[] getAuthors() {
		String[] a = new String[authors.size()];
		for (int i = 0; i < authors.size(); i++)
			a[i] = authors.get(i);
		return a;
	}

	@Override
	public Set<it.polito.dp2.BIB.ass3.ItemReader> getCitingItems() {
		if (citingItems == null)
			citingItems = new HashSet<>();
		return citingItems;
	}

	@Override
	public String getSubtitle() {
		return subtitle;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n***********************************************\nItem id= "+id+"\nTitle= "+title);
		if (subtitle != null)
			sb.append("\nSubtitle= "+subtitle);
		sb.append("\nAuthors= "+authors);
		sb.append("\nCiting Items=\n");
		for (ItemReader i : this.getCitingItems())  {
			sb.append(" -"+i.getTitle()+" \n");
		}
		if(book != null)
			sb.append("Isbn = "+book.getISBN()+ " year= "+book.getYear().getYear()+" publisher= "+book.getPublisher());
		if(article != null)
			sb.append("Journal = "+article.getJournal()+ " number= "+article.getNumber()+" volume= "+article.getVolume().getYear());
		return sb.toString();
	}
}
