package editortrees;

import java.util.ArrayList;

import editortrees.EditTree.BooleanContainer;

/**
 * A node in a height-balanced binary tree with rank. Except for the NULL_NODE,
 * one node cannot belong to two different trees.
 * 
 * @author Brian Beasley and Blaise Swartwood
 */
public class Node {

	enum Code {
		SAME, LEFT, RIGHT;

		// Used in the displayer and debug string
		public String toString() {
			switch (this) {
			case LEFT:
				return "/";
			case SAME:
				return "=";
			case RIGHT:
				return "\\";
			default:
				throw new IllegalStateException();
			}
		}
	}

	// The fields would normally be private, but for the purposes of this class,
	// we want to be able to test the results of the algorithms in addition to the
	// "publicly visible" effects

	char data;
	Node left, right; // subtrees
	int rank; // inorder position of this node within its own subtree.
	Code balance;

	public DisplayableNodeWrapper displayableNodeWrapper;
	// Feel free to add other fields that you find useful.
	// You probably want a NULL_NODE, but you can comment it out if you decide
	// otherwise.
	// The NULL_NODE uses the "null character", \0, as it's data and null children,
	// but they could be anything since you shouldn't ever actually refer to them in
	// your code.
	static final Node NULL_NODE = new Node('\0', null, null);
	// Node parent; You may want parent, but think twice: keeping it up-to-date
	// takes effort too, maybe more than it's worth.

	/**
	 * ensures a new Node is contructed due to the parameters itializes parameters
	 * 
	 * @param data
	 * @param left
	 * @param right
	 */
	public Node(char data, Node left, Node right) {
		this.displayableNodeWrapper = new DisplayableNodeWrapper(this);
		this.data = data;
		this.left = left;
		this.right = right;
	}

	/**
	 * ensures a new Node is contructed due to the parameters itializes parameters
	 * 
	 * @param data
	 * @param balance
	 * @param rank
	 */
	public Node(char data, Code balance, int rank) {
		this.displayableNodeWrapper = new DisplayableNodeWrapper(this);
		this.data = data;
		this.left = NULL_NODE;
		this.right = NULL_NODE;
		this.balance = balance;
		this.rank = rank;

	}

	/**
	 * creates a new node with no children initializes the balance code to = and the
	 * rank to 0;
	 * 
	 * @param data
	 */
	public Node(char data) {
		// Make a leaf
		this(data, NULL_NODE, NULL_NODE);
		this.displayableNodeWrapper = new DisplayableNodeWrapper(this);
		this.balance = Code.SAME;
		this.rank = 0;
	}

	// Provided to you to enable testing, please don't change.
	int slowHeight() {
		if (this == NULL_NODE) {
			return -1;
		}
		return Math.max(left.slowHeight(), right.slowHeight()) + 1;
	}

	// Provided to you to enable testing, please don't change.
	public int slowSize() {
		if (this == NULL_NODE) {
			return 0;
		}
		return left.slowSize() + right.slowSize() + 1;
	}

	// You will probably want to add more constructors and many other
	// recursive methods here. I added 47 of them - most were tiny helper methods
	// to make the rest of the code easy to understand. My longest method was
	// delete(): 20 lines of code other than } lines. Other than delete() and one of
	// its helpers, the others were less than 10 lines long. Well-named helper
	// methods are more effective than comments in writing clean code.

	// TODO: By the end of milestone 1, consider if you want to use the graphical
	// debugger. See
	// the unit test throwing an error and the README.txt file.

	/**
	 * adds a char to the end of the list (in-order)
	 * 
	 * @param ch
	 * @return Node
	 */
	public Node add(char ch, BooleanContainer booleanContainer) {
		// creates the new node at a leaf
		if (this == NULL_NODE) {
			return new Node(ch);
		}
		// adds the node to the right most place because of in order transversal
		else {
			right = right.add(ch, booleanContainer);
			// recursing back up
			// if booleancontainer is false, we will not change the balance codes anymore
			if (booleanContainer.value) {
				if (this.balance == Code.SAME) {
					this.balance = Code.RIGHT;
				} else if (this.balance == Code.LEFT) {
					this.balance = Code.SAME;
					booleanContainer.value = false;
				} else // (this.balance == Code.RIGHT)
				{
					// single left rotation
					booleanContainer.value = false;
					// increment the rotation counter of the boolean object
					booleanContainer.rotationCount += 1;
					return this.singleLeftRotation(this, this.right);
				}
			}
		}
		return this;
	}

	/**
	 * ensures a new node is added at the specified index
	 * 
	 * @param ch
	 * @param index
	 * @return Node
	 */
	public Node add(char ch, int index, BooleanContainer booleanContainer) {
		// creates a new node at a leaf
		if (this == NULL_NODE) {
			// walking back up
			return new Node(ch);
		}
		// recurse to the right
		else if (index > this.rank) {
			// since we are adding to the right, no incrementing is needed to rank
			//
			right = right.add(ch, index - (this.rank + 1), booleanContainer);
			// on the way up the tree
			// If the boolean container value is true, it means we still need to edit
			// and change the balance codes, else if it is false we do not want to change
			// anything else. The value is initially true, until we either change a balance
			// code to Code.SAME OR we do some rotation.
			if (booleanContainer.value) {
				if (this.balance == Code.SAME) {
					this.balance = Code.RIGHT;
				} else if (this.balance == Code.LEFT) {
					this.balance = Code.SAME;
					booleanContainer.value = false;
				} else // (this.balance == Code.RIGHT)
				{
					// single left rotation required or double left rotation
					// if the node that needs rotation child is the opposite balance,
					// so that means that if the right child balance code is left, do
					// double left rotation
					booleanContainer.value = false;
					if (this.right.balance == Code.LEFT) {
						booleanContainer.rotationCount += 2;
						return this.doubleLeftRotation(this, this.right.left, this.right);
					} else // (this.right.balance == Code.RIGHT)
					{
						booleanContainer.rotationCount += 1;
						return this.singleLeftRotation(this, this.right);
					}
					// do a single rotation if the right child balance code is right
				}
			}

		}
		// recurse to the left and add one to the current nodes rank
		else {
			// since we are adding to the left, we want to increment the rank
			this.rank++;
			left = left.add(ch, index, booleanContainer);
			// on the way up the tree
			// If the boolean container value is true, it means we still need to edit
			// and change the balance codes, else if it is false we do not want to change
			// anything else. The value is initially true, until we either change a balance
			// code to Code.SAME OR we do some rotation.
			if (booleanContainer.value) {
				if (this.balance == Code.SAME) {
					this.balance = Code.LEFT;
				} else if (this.balance == Code.RIGHT) {
					this.balance = Code.SAME;
					booleanContainer.value = false;
				} else // (this.balance == Code.LEFT)
				{
					// single right rotation required or double right rotation
					// if the node that needs rotation child is the opposite balance,
					// so that means that if the left child balance code is right, do
					// double right rotation
					booleanContainer.value = false;
					if (this.left.balance == Code.RIGHT) {
						booleanContainer.rotationCount += 2;
						return this.doubleRightRotation(this, this.left.right, this.left);

					} else // (this.left.balance == Code.LEFT)
					{
						booleanContainer.rotationCount += 1;
						return this.singleRightRotation(this, this.left);
					}
				}
			}
		}
		// always return the current element to "rebuild" the tree as we go
		return this;
	}

	public Node delete(int index, BooleanContainer booleanContainer) {
		//recurse to node to be deleted
		if (index > this.rank) {
			//keep track of the height before recursing right to delete
			int previousHeight = this.right.fastHeight();
			//recurse right to delete
			right = right.delete(index - (this.rank + 1), booleanContainer);
			//get the height after node has been deleted from the right
			int currentHeight = this.right.fastHeight();
			//check to see if the height of the right subtree changed after deletion
			//if so then make the boolean container value false
			if (previousHeight == currentHeight) {
				booleanContainer.value = false;
			}
			//if the value is true, continue to travel back up the tree and check balance codes
			if (booleanContainer.value) {
				//tip the codes. No rotations needed
				if (this.balance == Code.RIGHT) {
					this.balance = Code.SAME;
				} else if (this.balance == Code.SAME) {
					this.balance = Code.LEFT;
				} else { //rotations needed
					//make a temporary node in order to modify the current node with rotations
					Node temp = NULL_NODE;
					//checks the balance code of the left child to perform correct rotation
					if (this.left.balance == Code.LEFT) {
						temp = this.singleRightRotation(this, this.left);
						booleanContainer.rotationCount += 1;
					} else if (this.left.balance == Code.SAME) {
						temp = this.singleRightRotation(this, this.left);
						booleanContainer.rotationCount += 1;
						temp.balance = Code.RIGHT;
						temp.right.balance = Code.LEFT;
						//no need to continue rotating
						booleanContainer.value = false;
					} else {
						temp = this.doubleRightRotation(this, this.left.right, this.left);
						booleanContainer.rotationCount += 2;
					}
					return temp;
				}
			}
		} else if (index < this.rank) {
			//subract from the rank after deleting from the left subtree
			this.rank--;
			//get the height before deletion
			int previousHeight = this.left.fastHeight();
			left = left.delete(index, booleanContainer);
			//gets the height after the deletion
			int currentHeight = this.left.fastHeight();
			//if the heights are the same before and after rotation then there is no need for more rotations
			if (previousHeight == currentHeight) {
				booleanContainer.value = false;
			}
			//only looks for needed rotations if value is positive
			if (booleanContainer.value) {
				//checks the balance codes to determine if rotation is needed
				if (this.balance == Code.LEFT) {
					this.balance = Code.SAME;
				} else if (this.balance == Code.SAME) {
					this.balance = Code.RIGHT;
				} else {
					//creates a temp node to be used to set this to rotations return value
					Node temp = NULL_NODE;
					//looks at the right childs balance to determine rotation
					if (this.right.balance == Code.RIGHT) {
						temp = this.singleLeftRotation(this, this.right);
						booleanContainer.rotationCount += 1;
					} else if (this.right.balance == Code.SAME) {
						temp = this.singleLeftRotation(this, this.right);
						booleanContainer.rotationCount += 1;
						temp.balance = Code.LEFT;
						temp.left.balance = Code.RIGHT;
						booleanContainer.value = false;
					} else {
						temp = this.doubleLeftRotation(this, this.right.left, this.right);
						booleanContainer.rotationCount += 2;
					}
					return temp;
				}
			}
		} else { // rank == index (at target node)
					// case 1 no children
			if (this.left == NULL_NODE && this.right == NULL_NODE) {
				booleanContainer.data = this.data;
				return NULL_NODE;
			}
			// case 2 one child
			else if (this.left == NULL_NODE && this.right != NULL_NODE) {
				booleanContainer.data = this.data;
				return this.right;
			} else if (this.left != NULL_NODE && this.right == NULL_NODE) {
				booleanContainer.data = this.data;
				return this.left;
			}
			// case 3 two children
			else {
				//go to the in-order successor
				Node current = this.right;
				while (current.left != NULL_NODE) {
					current = current.left;
				}
				//stores the data of the node to delete
				char tempData = this.data;
				//switches the nodes data to the in order successor's data
				this.data = current.data;
				//switches the successors data to the data to delete
				current.data = tempData;
				//checks the height before deletion
				int previousHeight = this.right.fastHeight();
				//delete the in-order successor
				right = right.delete(index - this.rank, booleanContainer);
				//gets the height after deletion
				int currentHeight = this.right.fastHeight();
				//if the height does not change then no rotations are needed
				if (previousHeight == currentHeight) {
					booleanContainer.value = false;
				}
				//same code as above after the right deletion
				if (booleanContainer.value) {
					if (this.balance == Code.RIGHT) {
						this.balance = Code.SAME;
					} else if (this.balance == Code.SAME) {
						this.balance = Code.LEFT;
					} else {
						Node temp = NULL_NODE;
						if (this.left.balance == Code.LEFT) {
							temp = this.singleRightRotation(this, this.left);
							booleanContainer.rotationCount += 1;
						} else if (this.left.balance == Code.SAME) {
							temp = this.singleRightRotation(this, this.left);
							booleanContainer.rotationCount += 1;
							temp.balance = Code.RIGHT;
							temp.right.balance = Code.LEFT;
							booleanContainer.value = false;
						} else {
							temp = this.doubleRightRotation(this, this.left.right, this.left);
							booleanContainer.rotationCount += 2;
						}
						return temp;
					}
					
				}
			}
		}
		//always return the current node to rebuild the tree and building back up
		return this;
	}
	
	

	/**
	 * ensures a single left rotation is carried out to keep the tree height
	 * balanced
	 * 
	 * @param Node A (parent node)
	 * @param Node B (child node)
	 * @return Node
	 */
	public Node singleLeftRotation(Node A, Node B) {
		// switch the nodes according to the rotation
		A.right = B.left;
		B.left = A;
		// updating ranks and balance codes
		A.balance = Code.SAME;
		B.balance = Code.SAME;
		B.rank += A.rank + 1;
		// return the child to be reattached at that point
		return B;
	}

	/**
	 * ensures a single right rotation is carried out to keep the tree height
	 * balanced
	 * 
	 * @param Node A (parent node)
	 * @param Node B (child node)
	 * @return Node
	 */
	public Node singleRightRotation(Node A, Node B) {
		A.left = B.right;
		B.right = A;
		// updating ranks and balance codes
		A.balance = Code.SAME;
		B.balance = Code.SAME;
		A.rank -= B.rank + 1;
		// return the child to be reattached at that point
		return B;
	}

	/**
	 * ensures a double left rotation is carried out to keep the tree height
	 * balanced
	 * 
	 * @param Node A (parent node)
	 * @param Node B (grandchild node)
	 * @param Node C (child node
	 * @return Node
	 */
	public Node doubleLeftRotation(Node A, Node B, Node C) {
		// performing the double rotation movement
		A.right = B.left;
		C.left = B.right;
		B.left = A;
		B.right = C;
		// updating ranks and balance codes
		// similar to right-left changing ranks in that order
		C.rank -= B.rank + 1;
		B.rank += A.rank + 1;
		// setting balance codes based on what B's value was previously
		if (B.balance == Code.LEFT) {
			A.balance = Code.SAME;
			C.balance = Code.RIGHT;
		} else if (B.balance == Code.RIGHT) {
			A.balance = Code.LEFT;
			C.balance = Code.SAME;
		} else // B.balance == Code.SAME
		{
			A.balance = Code.SAME;
			C.balance = Code.SAME;
		}
		B.balance = Code.SAME;
		return B;
	}

	/**
	 * ensures a double right rotation is carried out to keep the tree height
	 * balanced
	 * 
	 * @param Node C (parent node)
	 * @param Node B (grandchild node)
	 * @param Node A (child node)
	 * @return Node
	 */
	public Node doubleRightRotation(Node C, Node B, Node A) {
		A.right = B.left;
		C.left = B.right;
		B.left = A;
		B.right = C;
		// updating ranks and balance codes
		// ranks update in left right fashion
		B.rank += A.rank + 1;
		C.rank -= B.rank + 1;
		// same balance code updating in the other double rotation
		if (B.balance == Code.LEFT) {
			A.balance = Code.SAME;
			C.balance = Code.RIGHT;
		} else if (B.balance == Code.RIGHT) {
			A.balance = Code.LEFT;
			C.balance = Code.SAME;
		} else {
			A.balance = Code.SAME;
			C.balance = Code.SAME;
		}
		B.balance = Code.SAME;
		return B;
	}

	/**
	 * ensures the data of the node at the specified index is returned
	 * 
	 * @param index
	 * @return data of the node
	 */
	public char get(int index) {
		// the node was found
		if (index == this.rank) {
			return this.data;
		}
		// recurse to the right
		else if (index > this.rank) {
			return right.get(index - (this.rank + 1));
		}
		// recurse to the left
		else {
			return left.get(index);
		}

	}

	/**
	 * 
	 * @return boolean if node has left child
	 */
	public boolean hasLeft() {
		return this.left != NULL_NODE;
	}

	/**
	 * 
	 * @return boolean if node has right child
	 */
	public boolean hasRight() {
		return this.right != NULL_NODE;
	}

	/**
	 * 
	 * @return boolean
	 */
	public boolean hasParent() {
		return false;
	}

	/**
	 * 
	 * @return Node
	 */
	public Node getParent() {
		return NULL_NODE;
	}

	/**
	 * recursive to string method in in-order transversal
	 * 
	 * @return String
	 */
	public void toString(StringBuilder string) {
		if (this != NULL_NODE) {
			left.toString(string);
			string.append(this.data);
			right.toString(string);
		}
	}

	/**
	 * recursive method to obtain the height of the tree
	 * 
	 * @return int
	 */
	public int fastHeight() {
		if (this == NULL_NODE) {
			return -1;
		}
		// if the balance code is left, you only need to find the height of the left
		// subtree and continue
		if (this.balance == Code.LEFT) {
			return left.fastHeight() + 1;
		} else // if this.balance is right, then we know to go right; if it
				// is equal, we can go to either side, so just go right
		{
			return right.fastHeight() + 1;
		}
	}

	/**
	 * recursive toArrayList method in pre-order transversal that holds the elements
	 * data and rank
	 */
	public void toRankString(ArrayList<String> list) {
		if (this == NULL_NODE) {
			return;
		}
		String str = data + Integer.toString(rank);
		list.add(str);
		left.toRankString(list);
		right.toRankString(list);

	}

	/**
	 * ensures the rank of a node matches the size of the left subtree
	 * 
	 * @return RankandSize
	 */
	public RankandSize rankMatchLeftSubtreeSize() {
		// return size 0 and true for null nodes
		if (this == NULL_NODE) {
			return new RankandSize(0, true);
		}
		// recurse through the left and right trees
		RankandSize leftChild = left.rankMatchLeftSubtreeSize();
		RankandSize rightChild = right.rankMatchLeftSubtreeSize();

		// increments the size as we walk back up the tree
		int size = leftChild.size + rightChild.size + 1;
		// checks if the size and rank differ
		if (this.rank != leftChild.size) {
			return new RankandSize(size, false);
		}
		// makes sure that if match was false previously it stays false up the tree
		return new RankandSize(size, leftChild.match && rightChild.match);
	}

	/**
	 * container class to hold both the size of a node's left subtree and if it
	 * matches the rank
	 */
	public class RankandSize {
		public boolean match;
		public int size;

		public RankandSize(int size, boolean match) {
			this.match = match;
			this.size = size;
		}
	}

	/**
	 * recursive toArrayList method in pre-order transversal that holds the elements
	 * data, rank, and balance code
	 */
	public void toDebugString(ArrayList<String> list) {
		if (this == NULL_NODE) {
			return;
		}
		String str = data + Integer.toString(rank) + balance;
		list.add(str);
		left.toDebugString(list);
		right.toDebugString(list);

	}

	/**
	 * ensures the balance codes are correct by comparing the height of the left and
	 * right subtree
	 * 
	 * @return RankandSize
	 */
	public BalanceandHeight balanceCodesAreCorrect() {
		// return height -1 and true for null nodes
		if (this == NULL_NODE) {
			return new BalanceandHeight(-1, true);
		}
		// recurse through the left and right trees
		BalanceandHeight leftChild = left.balanceCodesAreCorrect();
		BalanceandHeight rightChild = right.balanceCodesAreCorrect();

		// takes the max height of the left and right child and add 1 to calculate the
		// height for the next node
		int height = Math.max(rightChild.height, leftChild.height) + 1;
		boolean match = true;
		// checking to make sure that the balance direction corresponds to which side is
		// higher or lower
		if (rightChild.height > leftChild.height) {
			match = this.balance == Code.RIGHT;
		} else if (rightChild.height < leftChild.height) {
			match = this.balance == Code.LEFT;
		} else {
			match = this.balance == Code.SAME;
		}
		if (!match) {
			// if the balance is not correct, return false for match
			return new BalanceandHeight(height, false);
		}
		// makes sure that if match was false previously it stays false up the tree
		return new BalanceandHeight(height, leftChild.match && rightChild.match);
	}

	/**
	 * ensures a string starting at the specified index and of the length is returned.
	 * The string will be in-order transversal of the string
	 * @param index to start
	 * @param length to get
	 * @return String
	 */
	public void get(int index, int length, StringBuilder sb) {
		//if you get a null node return an empty string
		if (this == NULL_NODE) {
			return;
		}
		//look for the start index to the right of the current node
		if (index > rank) {
			right.get(index - (this.rank + 1), length, sb);
		}
		//either the start index is to the left of the current node or ends at the current node
		else if (index + length <= rank) {
			left.get(index, length, sb);
		} 
		//start of the string
		else if (index == rank) {
			sb.append(this.data);
			right.get(index - this.rank, length, sb);
		}
		//else will return the nodes to the left within the index and length
		//then returns the current node's data
		//then returns the nodes' data to the left that is within the length
		else {
			this.left.get(index, length, sb);
			sb.append(data);
			this.right.get(index - (this.rank + 1), length, sb);
		}
		
		
	}

	/**
	 * container class to hold both the height of the tree and if it matches the
	 * balance is correct or not
	 */
	public class BalanceandHeight {
		public boolean match;
		public int height;

		public BalanceandHeight(int height, boolean match) {
			this.match = match;
			this.height = height;
		}
	}
}
