package it.polito.dp2.BIB.sol3.client;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import it.polito.dp2.BIB.FactoryConfigurationError;
import it.polito.dp2.BIB.ass3.Bookshelf;
import it.polito.dp2.BIB.ass3.Client;
import it.polito.dp2.BIB.ass3.ClientFactory;
import it.polito.dp2.BIB.ass3.DestroyedBookshelfException;
import it.polito.dp2.BIB.ass3.ServiceException;

public class TestMyWebApplication {
	private static Client testClient=null;	                // Client under test

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			testClient =new it.polito.dp2.BIB.sol3.client.ClientFactory().newClient();
		} catch (FactoryConfigurationError fce) {
			fce.printStackTrace();
		}
		assertNotNull("Could not run test: the implementation under test generated a null Client", testClient);

	}

	@Test
	public final void testCreateBookshelf() throws ServiceException, DestroyedBookshelfException  {
		System.out.println("DEBUG: starting testCreateBookshelf");
		Bookshelf first = testClient.createBookshelf("P-primo");
		Bookshelf second = testClient.createBookshelf("P-secondo");
		Bookshelf terzo = testClient.createBookshelf("P-terzo");
		Bookshelf quarto = testClient.createBookshelf("P-quarto");
		Bookshelf toRemove = testClient.createBookshelf("P-primoremove");
		
		//get bookshelves with the specified string
		Set<? extends Bookshelf> bookshelves = testClient.getBookshelfs("P");
		assertNotNull("The client under test returned a null set", bookshelves);
		assertEquals("Wrong number of bookshelves ",5,bookshelves.size());
		
		toRemove.destroyBookshelf();
		bookshelves = testClient.getBookshelfs("P");
		assertNotNull("The client under test returned a null set", bookshelves);
		assertEquals("Wrong number of bookshelves ",4,bookshelves.size());
		
		first.destroyBookshelf();
		second.destroyBookshelf();
		
		bookshelves = testClient.getBookshelfs("P");
		assertNotNull("The client under test returned a null set", bookshelves);
		assertEquals("Wrong number of bookshelves ",2,bookshelves.size());
		
		terzo.destroyBookshelf();
		bookshelves = testClient.getBookshelfs("P");
		assertNotNull("The client under test returned a null set", bookshelves);
		assertEquals("Wrong number of bookshelves ",1,bookshelves.size());


	}
	@Test(expected=ServiceException.class)
	public final void testCreateBookshelf2() throws ServiceException {
		System.out.println("DEBUG: starting testCreateBookshelf whit exception");
		testClient.createBookshelf(null);
	}
	
	@Test(expected=ServiceException.class)
	public final void testGetBookshelves() throws ServiceException {
		System.out.println("DEBUG: starting testCreateBookshelf whit exception");
		Set<? extends Bookshelf> bookshelves = testClient.getBookshelfs(null);
		assertEquals("Wrong number of bookshelves ",0,bookshelves.size());

	}
	@Test
	public final void testGetBookshelvesNonexist() throws ServiceException {
		System.out.println("DEBUG: starting testCreateBookshelf whit exception");
		Set<? extends Bookshelf> bookshelves = testClient.getBookshelfs("TantoNonCiSono");
		assertEquals("Wrong number of bookshelves ",0,bookshelves.size());

	}




}
