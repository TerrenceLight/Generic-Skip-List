//Terrence Light TE965355
//COP 3503 University of Central Florida

import java.io.*;
import java.util.*;
import java.lang.*;

class Node<T extends Comparable<T>>
{
	//Create a new node of the given height, with null data, and null next references
	Node(int height)
	{
		this.height = height;
		this.data = null;
		this.next = new ArrayList<Node<T>>();
		
		//Set all next references to null
		for(int i = 0; i <= height; i++)
		{
			this.next.add(null);
		}
	}
	
	//Create a new node of the given height and containing the given data
	Node(T data, int height)
	{
		this.height = height;
		this.data = data;
		this.next = new ArrayList<Node<T>>();
		
		//Set all next references to null
		for(int i = 0; i <= height; i++)
		{
			this.next.add(null);
		}
	}
	
	int height;
	
	T data;
	
	ArrayList<Node<T>> next;
	
	//Returns the data contained at this node
	public T value()
	{
		return data;
	}

	//Returns the height of this node
	public int height()
	{
		return height;
	}
	
	//If the next reference is out of bounds of the node's height, return null. Otherwise, return the refernce to the next node at a given level of the skip list
	public Node<T> next(int level)
	{
		if(level < 0 || level > this.height() - 1)
		{
			return null;
		}
		
		else
		{
			return this.next.get(level);
		}
			
		
	}
	
	//Force the node to grow 1 level, and add a NULL reference to the top of the node
	public void grow()
	{
		this.height++;
		this.next.add(null);
	}
	
	//Randomly generate 0 or 1. If we generate 1, then call grow(). Otherwise do nothing
	//Return true if the node does grow, otherwise return false
	public boolean maybeGrow()
	{
		if((int)(2 * Math.random()) == 1)
		{
			this.grow();
			return true;
		}
		
		return false;
	}
	
	//Continuously remove the top reference and decrement the height until the correct height is achieved
	public void trim(int height)
	{
		//We can't trim down to a height if it's taller or equal to our current height
		if(height >= this.height())
		{
			return;
		}
		
		for(int i = this.height() - 1; i > height; i--)
		{
			this.next.remove(i);
			this.height--;
		}
	}
	
	//Since the SkipList requires the given data type to implement Comparable, we can just use their predefined compareTo()
	public int compareTo(Node<T> n)
	{
		return this.data.compareTo(n.data);
	}
}

public class SkipList<T extends Comparable<T>>
{
	//Minimum height of skip list will be 1
	public static final int MIN_HEIGHT = 1;
	
	private Node<T> head;
	
	public int numNodes;
	
	//Create a new SkipList with the head node height set to 1. numNodes set to 0 since there's nothing in it yet
	SkipList()
	{
		head = new Node<T>(MIN_HEIGHT);
		numNodes = 0;
	}
	
	//Create a new SkipList with the head node set to the given height. numNodes set to 0 since there's nothing in it yet
	SkipList(int height)
	{
		head = new Node<T>(height);
		numNodes = 0;
	}
	
	//Return the number of nodes contained in the skip list
	public int size()
	{
		return numNodes;
	}
	
	//Return the height of the head node
	public int height()
	{
		return head.height;
	}
	
	//Return the reference to the head node
	public Node<T> head()
	{
		return head;
	}
	
	//Insert a new item into the skip list with a randomly generated height
	public void insert(T data)
	{
		//Just generate a random height and call the insert function that takes a given height
		insert(data, generateRandomHeight(height()));
	}
	
	//Insert a new item into the skip list with a predetermined height
	//The given height should never be less than 1 or greater than the height of the head node + 1
	public void insert(T data, int height)
	{
		//Before we start inserting the node, increment the number of nodes and see if we need to grow the skip list
		this.numNodes++;
		if(getMaxHeight(numNodes) > head.height())
		{
			this.growSkipList();
		}
		
		//We start insertion with the top reference of the head
		//Create a new node to insert and a new node to travserse the list
		int curLevel = head.height();
		Node<T> traverse = head;
		Node<T> temp = new Node<T>(data, height);
		
		//Continue trying to insert while we haven't tried to go below the bottom reference
		while(curLevel >= 0)
		{
			//If the reference at the given level points to null, then we are safe to insert at this level and drop to the next
			if(traverse.next.get(curLevel) == null)
			{	
				//If we're at a height that is in the reference height of the node we're inserting, insert it
				if(curLevel <= height - 1)
				{
					traverse.next.set(curLevel, temp);
				}
				
				//Drop a level and continue to search
				curLevel--;
				continue;
			}
			
			//If the data we're trying to insert is greater than the reference at the given level, traverse to the next node	
			if(temp.compareTo(traverse.next.get(curLevel)) > 0)
			{
				traverse = traverse.next.get(curLevel);
				continue;
			}
			
			//If the data we're trying to insert is less than the reference at the given level then insert the new node into the given reference level
			if(temp.compareTo(traverse.next.get(curLevel)) < 0)
			{
				//If we're at a height that is within the height range of the node to be inserted, insert it
				if(curLevel <= height - 1)
				{
					temp.next.set(curLevel, traverse.next.get(curLevel));
					traverse.next.set(curLevel, temp);
				}
				
				//Drop a level and continue to search
				curLevel--;
				continue;
			}
			
			//If the data we're trying to insert is equal to the reference at the given level, then insert it and drop a level
			if(temp.compareTo(traverse.next.get(curLevel)) == 0)
			{
				//If we're at a height that is within the height range of the node to be inserted, insert it
				if(curLevel <= height - 1)
				{
					temp.next.set(curLevel, traverse.next.get(curLevel));
					traverse.next.set(curLevel, temp);
				}
				
				//Drop a level and continue
				curLevel--;
				continue;
			}
			
		}
		
	}
	
	//Attempt to delete a node, if we never find it, return
	public void delete(T data)
	{
		//Create an array list to harvest the next references we need to store. We won't know which we actually need to pass forward
		//Until we look at the node that is after the node we delete
		Node<T> traverse = head;
		
		ArrayList<Node<T>> previous = new ArrayList<Node<T>>();
		for(int i = 0; i <= head.height(); i++)
		{
			previous.add(null);
		}
		
		int curLevel = head.height() - 1;
		
		//Keep searching until we find what we're looking for
		while(curLevel >= 0)
		{
			//If we reached the end of a level and haven't found what we're looking for, drop down a level
			if(traverse.next.get(curLevel) == null)
			{
				previous.set(curLevel, traverse);
				curLevel--;
				continue;
			}
			
			
			//If what we're looking for is larger than what we're at, continue forward
			if(((traverse.next.get(curLevel)).value()).compareTo(data) < 0)
			{
				traverse = traverse.next.get(curLevel);
				continue;
			}
			
			//If what we're looking for is smaller than what we're at, drop a level
			if(((traverse.next.get(curLevel)).value()).compareTo(data) > 0)
			{
				previous.set(curLevel, traverse);
				curLevel--;
				continue;
			}
			
			//If we find what we're looking for and we're at the bottom level, delete it
			if(((traverse.next.get(curLevel)).value()).compareTo(data) == 0)
			{
				if(curLevel == 0)
				{
					//Move the previous nodes' references to traverse's next references
					//Record bottom level reference
					previous.set(curLevel, traverse);
					
					//Traverse to the node that is going to be deleted
					traverse = traverse.next.get(curLevel);
					
					int delHeight = traverse.height() - 1;
					Node<T> transfer;
					while(delHeight >= 0)
					{
						transfer = previous.get(delHeight);
						if(traverse.next.size() < delHeight)
						{
							traverse.next.add(null);
						}
						transfer.next.set(delHeight, traverse.next.get(delHeight));
						delHeight--;
					}
					
					//If the skip list needs to decrease in height, trim it
					this.numNodes--;
					if(getMaxHeight(this.size()) < head.height())
					{
						this.trimSkipList();
					}
					
					//Once we have deleted what we're looking for, decrement curLevel to -1 so we don't keep trying to delete more occurrences of data
					curLevel--;
				}
				
				//If we're not at the bottom level, we need to drop, so we can find the leftmost node
				else
				{
					previous.set(curLevel, traverse);
					curLevel--;
					continue;
				}
			}
		}
		
		//If we exit the loop, then we never found what we're looking for
	}
	
	//Search for the given value, return true if it's found. False otherwise
	public boolean contains(T data)
	{
		Node<T> previous;
		Node<T> traverse = head;
		int curLevel = head.height() - 1;
		
		//Keep searching until we find what we're looking for
		while(curLevel >= 0)
		{
			//If we reached the end of a level and haven't found what we're looking for, drop down a level
			if(traverse.next.get(curLevel) == null)
			{
				curLevel--;
				continue;
			}
			
			//If the node we're at is smaller than what we're looking for, continue to the next node
			if(((traverse.next.get(curLevel)).value()).compareTo(data) < 0)
			{
				traverse = traverse.next.get(curLevel);
				continue;
			}
			
			//If the node we're at is larger than what we're looking for, drop a level
			if(((traverse.next.get(curLevel)).value()).compareTo(data) > 0)
			{
				curLevel--;
				continue;
			}
			
			//If we find what we're looking for, return true
			if(((traverse.next.get(curLevel)).value()).compareTo(data) == 0)
			{
				return true;
			}
		}
		
		//If we exit the loop, then we never found what we're looking for
		return false;
		
	}
	
	//Returns the maximum height of a skip list based upon the number of nodes it contains
	private static int getMaxHeight(int n)
	{
		//Calculate the max height. However, if the calculated height is less than one, set it to one
		int maxHeight = (int)Math.ceil(Math.log10(n)/Math.log10(2));
		
		if(maxHeight < 1)
			maxHeight = 1;
		
		return maxHeight;
		
	}
	
	//Generates a random height from 1 to the max height for the skip list
	private static int generateRandomHeight(int maxHeight)
	{
		int retVal = 1;
		int i;
		//Coin flip until failure, or until you reach max height
		for(i = 0; i < maxHeight; i++)
		{
			//If you generate 1, increment the random height
			if(((int)(2 * Math.random())) == 1)
				retVal++;
			
			//Otherwise break
			else
				break;
		}
		
		return retVal;
	}
	
	//If the height of the skip list ever grows, we call this method
	private void growSkipList()
	{
		//Start by growing the head of the skip list
		head.grow();
		Node<T> previous = head;
		Node<T> traverse = head;
		
		//Now start attempting to grow the current highest height nodes
		//Since we insert nodes 1 at a time and check to grow the skip list each time we finish inserting, we don't need to worry about
		//Incrementing the height by more than 1
		int newMax = head.height() - 1;
		int oldMax = head.height() - 2;
		
		//Keep trying to grow the nodes of the oldMax height until we reach the end
		while(traverse.next.get(oldMax) != null)
		{
			traverse = traverse.next.get(oldMax);
			
			//If the node does grow, connect it to the prior node
			if(traverse.maybeGrow())
			{
				//Add a reference to the newly grown node and move "head" forward. Head just represents the previous node of the new max height
				previous.next.set(newMax, traverse);
				previous = traverse;
			}
		}
	}
	
	//If we need to reduce the size of the skip list, remove all of the top level references and shrink their heights
	private void trimSkipList()
	{
		//Create nodes to track the references to be deleted and the next node
		int delHeight = getMaxHeight(this.size()) - 1;
		Node<T> temp1 = head;
		Node<T> temp2;
		
		//While we still have next references at the given height, trim the nodes at the given height
		while(temp1 != null)
		{
			//Use a system simimlar to a swap to continue to have access to the given height to be deleted
			temp2 = temp1;
			temp1 = temp1.next.get(delHeight);
			temp2.trim(delHeight);
		}

	}
	
	//Hardest one yet, but the satisfaction I had when I finally finished it was so great
	public static double difficultyRating()
	{
		return 4.0;
	}
	
	//Took lots of time, more hours than I work in a week lol
	public static double hoursSpent()
	{
		return 30.0;
	}
}