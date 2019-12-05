
import java.util.*;

// AKANE SATO
// SkipList

class Node<AnyType extends Comparable<AnyType>>
{
    int nodeHeight;
    AnyType data;
    ArrayList<Node<AnyType>> next = new ArrayList<>();
    
    
    Node(int height)
    {
        this.nodeHeight = height;
        this.data = null;
        
        //This adds "height - 1" number of NULL references.
        for(int i = 0; i < height; i++)
        {
            next.add(null);
        }
        
    }
    
    Node(AnyType data, int height)
    {
        this.nodeHeight = height;
        this.data = data;
        
        //This adds "height - 1" number of NULL references.
        for(int i = 0; i < height; i++)
        {
            next.add(null);
        }
    }
    
    public AnyType value()
    {
        return this.data;
    }
    
    public int height()
    {
        return this.nodeHeight;
        
    }
    
    
    public Node<AnyType> next(int level)
    {
        // Returns null if passed in an invalid level.
        return (level < 0 || level > (this.nodeHeight - 1) ? null : next.get(level));
        
    }
    
    
    public void setNext(int level, Node<AnyType> node)
    {
        next.remove(level);
        next.add(level, node);
    }
    
    
    public void grow()
    {
        next.add(null);
        nodeHeight++;
    }
    
    
    public void maybeGrow()
    {
        // Generates either a 1.0 or a 2.0; so, there's a 50% chance of 1.0, resulting in a 50% chance of growing.
        if(Math.floor(Math.random() * 2 + 1) != 1.0)
        {
            next.add(null);
            nodeHeight++;
        }
    }
    
    
    public void trim(int height)
    {
        // Removes levels from the top until the new height = height.
        while((this.nodeHeight - 1) >= height)
        {
            next.remove(this.nodeHeight - 1);
            this.nodeHeight--;
        }
    }
    
}

public class SkipList<AnyType extends Comparable<AnyType>>
{
    Node<AnyType> head;
    int size;
    
    
    SkipList()
    {
        // I have chosen to initialize an empty SkipList to size 0 (i.e., 0 nodes).
        head = new Node<AnyType>(0);
        size = 0;
    }
    
    
    SkipList(int height)
    {
        // If passed in an invalid height, makes the head node height 0.
        head = (height < 0 ? new Node<AnyType>(0) : new Node<AnyType>(height));
        size = 0;
    }
    
    
    public int size()
    {
        return this.size;
    }
    
    
    public int height()
    {
        return this.head.height();
    }
    
    
    public Node<AnyType> head()
    {
        return this.head;
    }
    
    
    public void insert(AnyType data)
    {
        // If adding the element causes the SkipList to grow too large, grow the list.
        if(Math.ceil(Math.log(size + 1)/Math.log(2)) > head.height() || head.height() == 0)
            growSkipList();
        
        Node<AnyType> newNode = new Node<AnyType>(data, generateRandomHeight(getMaxHeight(this.size)));
        this.size++;
        
        // This holds the highest level.
        int i = head.height() - 1;
        
        Node<AnyType> temp = head;
        
        
        while(i >= 0)
        {
            // If temp's next val >= data (or is null), we need to drop down.
            if(temp.next.get(i) == null || temp.next.get(i).value().compareTo(data) >= 0)
            {
                // If our new node is tall enough, we can give it references on this level.
                if(newNode.height() >= (i+1))
                {
                    newNode.setNext(i, temp.next.get(i));
                    temp.setNext(i, newNode);
                }
                i--;
                
            } else if(temp.next.get(i).value().compareTo(data) < 0)
                // If temp's next val < data, we move on to data's spot.
            {
                temp = temp.next.get(i);
            }
        }
        
    }
    
    
    public void insert(AnyType data, int height)
    {
        // If adding the element causes the SkipList to grow too large, grow the list.
        if(Math.ceil(Math.log(size + 1)/Math.log(2)) > head.height() || head.height() == 0)
            growSkipList();
        
        Node<AnyType> newNode = new Node<AnyType>(data, height);
        this.size++;
        
        // This holds the highest level.
        int i = head.height() - 1;
        
        Node<AnyType> temp = head;
        
        
        while(i >= 0)
        {
            // If temp's next val >= data (or is null), we need to drop down.
            if(temp.next.get(i) == null || temp.next.get(i).value().compareTo(data) >= 0)
            {
                // If our new node is tall enough, we can give it references on this level.
                if(newNode.height() >= (i+1))
                {
                    newNode.setNext(i, temp.next.get(i));
                    temp.setNext(i, newNode);
                }
                i--;
                
            } else if(temp.next.get(i).value().compareTo(data) < 0)
                // If temp's next val < data, we move on to data's spot.
            {
                temp = temp.next.get(i);
            }
        }
    }
    
    
    private void growSkipList()
    {
        head.grow();
        
        if(head.height() > 1)
        {
            int i = head.height() - 2;
            
            Node<AnyType> temp = head;
            Node<AnyType> temp2 = temp.next.get(i);
            
            while(temp2 != null)
            {
                temp2.maybeGrow();
                
                if(temp2.height() == head.height())
                {
                    temp.setNext(i + 1, temp2);
                    
                    temp = temp2;
                }
                
                //else, we continue on until we find a node that reaches head's height.
                temp2 = temp2.next.get(i);
            }
        }
        
    }
    
    private void trimSkipList()
    {
        if(head.height() > 1)
        {
            
            Node<AnyType> temp = head;
            Node<AnyType> temp2 = temp.next.get(head.height() - 1);
            
            
            for(int i = head.height() - 1; i >= (int)Math.ceil(Math.log(size)/Math.log(2)); i--)
            {
                temp = head;
                temp2 = temp.next.get(i);
                
                while(temp2 != null)
                {
                    temp.setNext(i, temp2.next.get(i));
                    temp2.trim(i);
                    
                    temp2 = temp.next.get(i);
                }
            }
        }
        
        // Using the logs here would produce a funky and inaccurate new height, so we're just using constants.
        if(size == 1 || size == 0)
        {
            head.trim(size);
        } else {
            head.trim((int)Math.ceil(Math.log(size)/Math.log(2)));
        }
        
    }
    
    
    public void delete(AnyType data)
    {
        ArrayList<Node<AnyType>> locations = new ArrayList<>();
        ArrayList<Node<AnyType>> foundLocs = new ArrayList<>();
        
        // This holds the highest level.
        int i = head.height() - 1;
        
        // Delete flag will only be 1 if a reference is removed, i.e., a node is deleted.
        int deleteFlag = 0;
        
        
        Node<AnyType> temp = head;
        Node<AnyType> deleteNode;
        
        
        while(i >= 0)
        {
            // If temp's next val >= data (or is null), we need to drop down.
            if(temp.next.get(i) == null || temp.next.get(i).value().compareTo(data) > 0)
            {
                i--;
                
            } else if(temp.next.get(i).value().compareTo(data)  == 0)
            {
                // Keeping track of the nodes pointing to our data...
                foundLocs.add(0, temp);
                // And storing each time we encounter the data, in case we find one closer to the front.
                locations.add(0, temp.next.get(i));
                i--;
                deleteFlag = 1;
            } else if(temp.next.get(i).value().compareTo(data) < 0)
                // If temp's next val < data, we move on to data's spot.
            {
                temp = temp.next.get(i);
            }
        }
        
        // This node stores where we drop to 0 before the data, so we know for sure it's the data node closest to the front.
        deleteNode = temp.next.get(0);
        
        if(deleteFlag == 1)
        {
            this.size--;
            i = 0;
            
            while(i < foundLocs.size() && foundLocs.get(i).next.get(i) == locations.get(0))
            {
                foundLocs.get(i).setNext(i, deleteNode.next.get(i));
                i++;
            }
            
            deleteNode.trim(0);
            
            if(Math.ceil(Math.log(size)/Math.log(2)) < head.height())
                trimSkipList();
        }
        
    }
    
    
    public boolean contains(AnyType data)
    {
        int i = head.height() - 1;
        
        
        Node<AnyType> temp = head;
        while(i >= 0)
        {
            // If temp's next val >= data (or is null), we need to drop down.
            if(temp.next.get(i) == null || temp.next.get(i).value().compareTo(data) > 0)
            {
                i--;
                
            } else if(temp.next.get(i).value().compareTo(data) == 0)
            {
                // We found it! Yay!!
                return true;
            } else if(temp.next.get(i).value().compareTo(data) < 0)
                // If temp's next val < data, we move on to data's spot.
            {
                temp = temp.next.get(i);
            }
        }
        
        //If we've made it this far, the data was never found. :(
        return false;
        
    }
    
    
    public Node<AnyType> get(AnyType data)
    {
        ArrayList<Node<AnyType>> locations = new ArrayList<>();
        
        int i = head.height() - 1;
        int foundFlag = 0;
        
        Node<AnyType> temp = head;
        while(i >= 0)
        {
            // If temp's next val >= data (or is null), we need to drop down.
            if(temp.next.get(i) == null || temp.next.get(i).value().compareTo(data) > 0)
            {
                i--;
            } else if(temp.next.get(i).value().compareTo(data) == 0)
            {
                foundFlag = 1;
                locations.add(0, temp.next.get(i));
                i--;
            } else if(temp.next.get(i).value().compareTo(data) < 0)
                // If temp's next val < data, we move on to data's spot.
            {
                temp = temp.next.get(i);
            }
        }
        
        if(foundFlag == 1)
        {
            return locations.get(0);
        }
        
        //If we've made it this far, the data was never found. :(
        return null;
        
    }
    
    
    private int getMaxHeight(int n)
    {
        int val = (int)Math.ceil(Math.log(n)/Math.log(2));
        
        return (val >= head.height()) ? val : head.height();
    }
    
    
    private static int generateRandomHeight(int maxHeight)
    {
        //Returns 1 with 50% probability, 2 with 25% probability, 3 with 12.5% probability, and so on, without exceeding maxHeight.
        
        int height = 1;
        
        for(int i = 1; i < maxHeight; i++)
        {
            if(Math.floor(Math.random() * 2 + 1) == 1)
            {
                height++;
            }
            else break;
            
        }
        
        return height;
    }
    
    
    public static double difficultyRating()
    {
        //Return a double on the range 1.0 (ridiculously easy) through 5.0 (insanely difficult).
        return 4;
    }
    
    
    public static double hoursSpent()
    {
        //Return an estimate (greater than zero) of the number of hours you spent on this assignment.
        return 30;
    }
    
}
