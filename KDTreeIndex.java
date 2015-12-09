/*****************************************************************************
 * AMOS
 *
 * Author: (c) 2011 Thanh Truong, UDBL
 *
 * Description: Index manager based on KDTree
 *
 ****************************************************************************/
import edu.wlu.cs.levy.CG.*;

import java.util.List;
import java.util.Iterator;
import callin.*;
import callout.*;

import java.util.*;

public class KDTreeIndex 
{
    // A list of KD-tree(s) with unique id for each.
    private  static Hashtable<Integer, 	KDTree<Oid>> m_lkdtrees;

    // Id base number starts from 10
    private  int idgen = 10;

    // Number of dimensions = 1.
    // It will be overwrite at the first PUT
    private  int dim = 1;

    // Main entry
    public static void main(String argv[]) throws AmosException 
    {
	// Initialize something if needed.
    }

    /**.
       Default constructor.
    */
    public KDTreeIndex() 
    {
	// Put initializations here
    }
    
    /*-----------------------------------------------------------------
      Find KD-tree given its identifier
      -----------------------------------------------------------------*/
    private KDTree<Oid> locateKdtree(int id) throws AmosException
    {
	if (id == 0) return null;

	// Initialize the list if needed.
	if (m_lkdtrees == null) 
	    m_lkdtrees = new Hashtable<Integer, KDTree<Oid>>();

	Integer ID = new Integer(id);
	
	KDTree<Oid> m = m_lkdtrees.get(ID);

	// If there is no such KD-tree
	if (m == null) 
	    {
		// Construct a new one
		m = new KDTree<Oid>(dim); 
		// Put it into our list.
		m_lkdtrees.put(ID, m);
	    }
	return m_lkdtrees.get(ID);
    }
    /*-----------------------------------------------------------------
      Extract Vector of Number stored in tpl to an array of double(s)
      -----------------------------------------------------------------*/
    private double[] toArray(Tuple tpl) throws AmosException 
    {
	if (dim != tpl.getArity()) 
	    dim = tpl.getArity();

	double[] key = new double[dim];
	
	for (int i = 0; i < dim; i++)
	    key[i] = tpl.getDoubleElem(i);

	return key;
    }
    /*-----------------------------------------------------------------
      kdtree_make simply returns an id     
      -----------------------------------------------------------------*/
    public void kdtree_make(CallContext cxt, Tuple tpl)throws AmosException
    {
		// Increase idgen by 1
    	idgen += 1;

		// Return the current value of idgen 
		tpl.setElem(0,idgen);
    }

    /*-----------------------------------------------------------------
      kdtree_put puts <key, val> into a KD-tree given its Id.
      The first PUT always constructs the KD-tree
      -----------------------------------------------------------------*/
    public void kdtree_put(CallContext cxt, Tuple tpl) 
	throws AmosException, KeySizeException, KeyDuplicateException 
    {
	//Get the id as Integer at position 0
	int id = tpl.getIntElem(0);
	
	// Extract feature vector f as key. Convert from sequence of numbers to array of floats.
	double [] key  = toArray(tpl.getSeqElem(1));
	
	// Get Amos object to val type of Oid 
	Oid val =  tpl.getOidElem(2);
	
	// Get the KD-tree whose id = id	
	KDTree<Oid>  m = locateKdtree(id);
	
	if (m != null)
	    {
		// Insert to KD-tree	
		m.insert(key,val);    
		// Emit val to tpl 
		tpl.setElem(3, val);
		// Emit
		cxt.emit(tpl);
	    }
    }
    /*-----------------------------------------------------------------
      GET returns val associated with the given key
      -----------------------------------------------------------------*/
    public void kdtree_get(CallContext cxt, Tuple tpl)throws AmosException, 
	KeyDuplicateException, KeySizeException 
    {
	//Get the id as Integer at position 0
	int id = tpl.getIntElem(0);

	// Extract feature vector f as key. Convert from sequence of numbers to array of floats.
	double [] key  = toArray(tpl.getSeqElem(1));

	// Amos object 
	Oid val = null;

	// Get the KD-tree whose id = id	
	KDTree<Oid>  m = locateKdtree(id);

	if (m != null)
	    {
		// Search in KD-tree val associated with key
	    val = m.search(key);
	    
		if (val != null) 
		    {
			// Set the return val at position 2
		    tpl.setElem(2,val);		
			// Emit tpl
			cxt.emit(tpl);
		    }
	    }
    }
    /*-----------------------------------------------------------------
      kdtree_delete deletes (key,val) pair
      -----------------------------------------------------------------*/
    public void kdtree_delete(CallContext cxt, Tuple tpl)
	throws AmosException, KeyDuplicateException, KeySizeException,
	KeyMissingException
    {
    // Get the id as Integer at position 0
	int id = tpl.getIntElem(0);
	
	// Extract feature vector f as key. Convert from sequence of numbers to array of floats.
	double [] key  = toArray(tpl.getSeqElem(1));
	
	// Get Amos object to val type of Oid 
	Oid val =  tpl.getOidElem(2);
	
	// Get the KD-tree whose id = id	
	KDTree<Oid>  m = locateKdtree(id);

	if (m != null)
	    {
		m.delete(key);
		tpl.setElem(3,val);	 // is this step required?
		cxt.emit(tpl);
	    }
    }

    /*-----------------------------------------------------------------
      KDDTree does not support iterating over all keys - Index Full Scan
      -----------------------------------------------------------------*/
    
    /*-----------------------------------------------------------------
      kdtree_clear flushes away entire KD-tree given its Id
      -----------------------------------------------------------------*/
    public void kdtree_clear(CallContext cxt, Tuple tpl)
	throws AmosException
    {
	// Get the id as Integer at position 0
	int id = tpl.getIntElem(0);

	// Get the KD-tree whose id = id	
	// KDTree<Oid>  m = locateKdtree(id);

	if (m != null)
	    {
	    m_lkdtrees.remove(id)
		tpl.setElem(1,val);	 // is this step required?
		cxt.emit(tpl);
	    }
    }    
    
    /*-----------------------------------------------------------------
      Find KD-tree nodes whose keys are closer within a distance to key. 
      -----------------------------------------------------------------*/
    public void kdtreeProximitySearch(CallContext cxt, Tuple tpl)
	throws AmosException,
	KeySizeException,
	java.lang.IllegalArgumentException 
    {
	// Get the id as Integer at position 0
	int id = tpl.getIntElem(0);

	// Extract feature vector f as key. Convert from sequence of numbers to array of floats.
	double [] key  = toArray(tpl.getSeqElem(1));

	// TODO Get the distance 
    
	// Get the KD-tree whose id = id	
	KDTree<Oid>  m = locateKdtree(id);
	
	if (m != null && m.size() > 0)
	    {
		// TODO Find all values whose keys are within distance dist. 
		List<Oid> ln = null; // list of values 

		if (ln != null && ln.size() > 0) 
		    {		
			// Loop through and emit the found values
			for(Oid val : ln) 
			    {
				// TODO set val to tpl and emit
			    }
		    }
	    }
    }
}
