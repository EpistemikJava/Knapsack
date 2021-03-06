/* 
 * $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
  
  Mark Sattolo (epistemik@gmail.com)
 -----------------------------------------------
   $File: //depot/Eclipse/Java/workspace/KnapsackNew/src/mhs/knapsack/KnapPriQue.java $
   $Revision: #6 $
   $Change: 58 $
   $DateTime: 2011/02/02 11:56:15 $
   
  git version (~/dev/git/mhs-git/KnapsackNew) created Mar 22, 2014
  DrJava version (~/dev/Java/DrJava/projects/Knapsack) created Feb 13, 2015
  new git version (~/dev/git/Knapsack) created Feb 27, 2015
   
$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ */

package mhs.knapsack;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;

/**
 * A collection of {@link KnapNode}s, ordered by highest bound, used by the state space tree in
 * {@link KnapItemList#bestFirstSearch(KnapNode, int)}.
 * @author MARK SATTOLO
 * @version $Revision: #6 $
 */
@SuppressWarnings("serial")
public class KnapPriQue extends Vector<KnapNode> {
    /**
     * add the given node at the proper priority position
     * @see java.util.Vector#add(java.lang.Object)
     */
    @Override
    public boolean add(KnapNode k) {
        if( isEmpty() ) return super.add(k);

        int index = 0;

        /* INSTEAD OF calling Collections.sort() - find the proper position to insert this Node to maintain a sorted Vector */
        while( index < size() && k.getBound() < elementAt(index).getBound() )
            index++;

        if( index >= size() )
            addElement(k);
        else
            add(index, k);

        // sort myself according to Comparator BOUND_ORDER
        // Collections.sort( this, BOUND_ORDER );

        return contains(k);
    }

    /**
     * Log each {@link KnapNode} in the list
     * @param k - logger to print to
     * @param lev - level to print at
     * @param s - extra info to print
     */
    public void log(KnapLogger k, Level lev, String s) {
        k.appendln(" The '" + s + "' KnapPriQue:");

        int j = 1;
        Iterator<KnapNode> it = iterator();

        while( it.hasNext() ) {
            k.append(" #" + j + " ");
            k.append(it.next().display());
            j++;
        }

        k.send(lev);
    }

    /**
     * Log the head {@link KnapNode} in the list
     * @param k - logger to print to
     * @param lev - level to print at
     */
    public void logHead(KnapLogger k, Level lev) {
        k.appendln("\nHead of PriorityQueue:\n");
        k.appendln(get(0).display());
        k.send(lev);
    }

}// class KnapPriQue
