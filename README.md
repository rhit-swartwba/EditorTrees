# EditorTrees
Created methods of a class that could be the "behind the scenes" data structure used by a text editor. Each piece of text will be represented as a height-balanced tree with rank and balance codes. Each node contains a single character. Note that an EditTree is not an AVL tree in the traditional sense, since the order of nodes reflects their position within the tree's text rather than an alphabetical ordering of the characters in the tree. 

There were certain efficency requirements:

These methods must run in O(log N) time, where N is the size (number of nodes) in the largest tree involved in the operation.

- add(char c)
- add(char c, int pos).  
- delete(int pos)
- get(int pos)
- fastHeight()

These methods must run in O(N) time, where N is the size (number of nodes) in the tree involved in the operation.

- toString( )
- EditTree(String s)
- EditTree(EditTree e)
- slowHeight()
- slowSize()
- ranksMatchLeftSubtreeSize()
- balanceCodesAreCorrect()

The method get(int pos, int length) must run in O(length) time, where length is the parameter given.

