//My implementation of a linked list
class LinkedListInt {
    //Each linked list has a head node.
    Node head;
    LinkedListInt(){
        head = new Node();
    }

    //Inner class for nodes inside the linked list
    class Node {
        //Each node has an integer value and a link to the next Node.
        int val;
        Node next;

        //Constructor when only an integer is supplied.
        Node() {
            next = null;
        }

        //Constructor when both an integer and a next node is supplied.
        Node(int val, Node next) {
            this.val = val;
            this.next = next;
        }
    }
    //Given an integer, insert a Node with that integer value to the list.
    void add(int val){//Adding a node takes constant time.
        //Create a new node with head.next as the next connection.
        //Then make head.next be the new node.
        Node ret = new Node(val, head.next);
        head.next = ret;
    }
    //Given an integer and a Node, insert a Node with that integer value after the Node given.
    void add(int val, Node n){//Again, adding takes constant time.
        Node ret = new Node();
        ret.val = val;
        n.next = ret;
    }
    //Removes the first item in the list
    int remove(){//Removing takes constant time.
        Node ret = head.next;
        head.next = ret.next;
        return ret.val;
    }//Function to return length of the LL
    int length(){//Finding the length takes worst-case O(n) time.
        int count = 0;
        Node tmp = head;
        while(tmp.next != null){
            count++;
            tmp = tmp.next;
        }
        return count;
    }
    int peek() {
        if (head.next != null) {
            return head.next.val;
        }
        return -1;
    }


    @Override
    public String toString(){
        Node tmp = head;
        String ret = "";
        while (tmp.next != null){
            ret += tmp.next.val + " ";
            tmp = tmp.next;
        }
        return ret;
    }
}