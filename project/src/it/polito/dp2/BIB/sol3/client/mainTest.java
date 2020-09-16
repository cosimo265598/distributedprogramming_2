package it.polito.dp2.BIB.sol3.client;


import java.util.Iterator;
import java.util.Set;

import it.polito.dp2.BIB.ass3.BookReader;
import it.polito.dp2.BIB.ass3.Client;
import it.polito.dp2.BIB.ass3.ClientException;
import it.polito.dp2.BIB.ass3.ClientFactory;
import it.polito.dp2.BIB.ass3.DestroyedBookshelfException;
import it.polito.dp2.BIB.ass3.ItemReader;
import it.polito.dp2.BIB.ass3.ServiceException;
import it.polito.dp2.BIB.ass3.TooManyItemsException;
import it.polito.dp2.BIB.ass3.UnknownItemException;

public class mainTest {

	public static void main(String[] args) {
		
		Client mainClient;	               

		try {
			
			mainClient = new it.polito.dp2.BIB.sol3.client.ClientFactory().newClient();
			
			mainClient.getBookshelfs("");
			
			it.polito.dp2.BIB.ass3.Bookshelf bookshelf=mainClient.createBookshelf("hello");
			Set<ItemReader> it =mainClient.getItems("", 0, 10000);
			
			int i=0;
			for (ItemReader itemReader : it) {
				
				bookshelf.addItem(itemReader);
				i++;
				if(i>5)
					break;		
			}
			//bookshelf.removeItem(it.iterator().next());
			it.stream().forEach(y-> {
				try {
					bookshelf.removeItem(y);
				} catch (DestroyedBookshelfException | UnknownItemException | ServiceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} );
			//bookshelf.destroyBookshelf();
			//bookshelf.removeItem(it.iterator().next());
			
			Thread t= new Thread(){
				public void run(){
				       System.out.println("Thread-running 1");
				       try {
				    	   mainClient.getBookshelfs("");
						Set<ItemReader> it =mainClient.getItems("", 0, 10000);
						
						System.out.println(this.getName()+" Size="+it.size());
						int i=0;
						for (ItemReader itemReader : it) {
							bookshelf.addItem(itemReader);
							i++;
							
						}
						System.out.println(this.getName()+" DISTRUGGO");
						
						bookshelf.destroyBookshelf();

					} catch (ServiceException | DestroyedBookshelfException | UnknownItemException | TooManyItemsException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    }
			};
			Thread t2= new Thread(){
				public void run(){
				       System.out.println("Thread-running  2");
				  try{
						Set<ItemReader> set=bookshelf.getItems();
						System.out.println(this.getName()+" Size="+set.size());
						for (ItemReader i : set) {
							bookshelf.removeItem(i);
						}
						System.out.println(this.getName()+" NREADS="+bookshelf.getNumberOfReads());
				  	}catch (Exception e) {
						e.printStackTrace();
					}
				return;
				}
			};
			Thread t3= new Thread(){
				public void run(){
				       System.out.println("Thread-running 3");
				  try{
						Set<ItemReader> set=bookshelf.getItems();
						System.out.println(this.getName()+" Size="+set.size());
						for (ItemReader i : set) {
							bookshelf.removeItem(i);
						}
						System.out.println(this.getName()+" NREADS="+bookshelf.getNumberOfReads());
				  	}catch (Exception e) {
						e.printStackTrace();
					}
				return;
				}
			};
			Thread t4= new Thread(){
				public void run(){
				       System.out.println("Thread-running 4");
				  try{
						it.polito.dp2.BIB.ass3.Bookshelf bookshelf1= mainClient.createBookshelf("SONO nuovo");
						System.out.println(this.getName()+" Size="+bookshelf1.getItems().size());
						System.out.println(this.getName()+" NAME ="+bookshelf1.getName());
						
						Set<ItemReader> set=bookshelf1.getItems();
						System.out.println(this.getName()+" Size="+set.size());
						for (ItemReader i : set) {
							bookshelf1.removeItem(i);
						}
						System.out.println(this.getName()+"---NREADS="+bookshelf1.getNumberOfReads());
				  	}catch (Exception e) {
						e.printStackTrace();
					}
				return;
				}
			};
			Thread t5= new Thread(){
				public void run(){
				       System.out.println("Thread-running 5");
				  try{
						System.out.println(this.getName()+" NAME ="+bookshelf.getName());
						
						bookshelf.getItems();
						System.out.println(this.getName()+"---NREADS="+bookshelf.getNumberOfReads());
						bookshelf.destroyBookshelf();
				  	}catch (Exception e) {
						e.printStackTrace();
					}
				return;
				}
			};

			Thread t6= new Thread(){
				public void run(){
				       System.out.println("Thread-running 6");
				  try{
					  for(int i=0;i<50;i++){
						  it.polito.dp2.BIB.ass3.Bookshelf bookshelf1=mainClient.createBookshelf("hello");
						Set<ItemReader> it=bookshelf1.getItems();
						System.out.println(this.getName()+" NAME ="+bookshelf1.getName());
						System.out.println(this.getName()+"---NREADS="+bookshelf1.getNumberOfReads());

					  }

				  	}catch (Exception e) {
						e.printStackTrace();
					}
				return;
				}
			};
			Thread t7= new Thread(){
				public void run(){
				       System.out.println("Thread-running 7");
				  try{
						Set<ItemReader> it =mainClient.getItems("", 0, 10000);
						System.out.println(this.getName()+" Size="+it.size());
						
						for (ItemReader itemReader : it) {
							bookshelf.addItem(itemReader);							
						}

				  	}catch (Exception e) {
						e.printStackTrace();
					}
				return;
				}
			};

			//t.start();
			
			//t2.sleep(950);
			//t2.start();
			//t3.sleep(1005);
			//t3.start();
			t4.start();
			//t5.sleep(1000);
			//t5.start();
						
			
			//t6.start();
			//t7.sleep(500);
			//t7.start();
			
			/*
			it.polito.dp2.BIB.ass3.Bookshelf bookshelf1=mainClient.createBookshelf("hello");
			 
			Set<ItemReader> it =mainClient.getItems("Unix", 0, 10000);
			System.out.println("SIZE set items = "+it.size());
			
			
			ItemReader itremove= it.iterator().next();
			int i=0;
			for (ItemReader itemReader : it) {
				bookshelf1.addItem(itemReader);
				System.out.println("SIZE BOOKSHELF tot= "+bookshelf1.getItems().size());
				i++;
				if(i>1)
					bookshelf1.removeItem(itremove);
				itremove=itemReader;
				
			}
			
			System.out.println("SIZE BOOKSHELF tot= "+bookshelf.getItems().size());
			System.out.println("Nreader tot= "+bookshelf.getNumberOfReads());
			System.out.println("SIZE BOOKSHELF tot= "+bookshelf.getItems().size());
			System.out.println("Nreader tot= "+bookshelf.getNumberOfReads());
			
	*/
			/*
			ItemReader it3 =mainClient.getItems("", 0, 10000).iterator().next();
			ItemReader it2 =mainClient.getItems("Unix", 0, 10000).iterator().next();
			
			System.out.println("Title:  "+it3.getTitle());
			System.out.println("Subtitle:  "+it3.getSubtitle());
			bookshelf.addItem(it3);
			bookshelf.addItem(it2);

			//bookshelf.destroyBookshelf();

			//System.out.println(bookshelf.toString());
			bookshelf.getItems().stream().forEach(System.out::println);
			/*
			BookReaderImpl bb=  (BookReaderImpl) it3 ;
			if(bb instanceof BookReaderImpl)
				System.out.println("----Instance");
			
			System.out.println(bb.getISBN().toString());
			*/
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

}
