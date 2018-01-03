// Created by Guo Shi, at 171226
// TODO: nodes of FPTree
import java.util.Vector;

public class Node<T> {
	public T data = null;
	public Vector<Node<T>> children = new Vector<>();
	public Node<T> nextLink = null;
	public Node<T> lastLink = null;
	public Node<T> parent = null;
	public int num = 0;
	public int layers = 0;
	
	public Node(T data) {
		this.data = data;
		this.num = 1;
		this.layers = 1;
	}
	
	public Node(Node<T> temp) {
		this.data = temp.getData();
		this.num = temp.getNum();
		this.layers = temp.layers;
	}
	
	// TODO: child
	public Node<T> addChild(Node<T> child) {
		child.setParent(this);
	    child.layers = this.layers + 1;
		this.children.add(child);
		return child;
	}
	
	public Node<T> linkNode(Node<T> child) {
		this.nextLink = child;
		child.lastLink = this;
		return child;
	}
	
	public Vector<Node<T>> getChild() {
		return this.children;
	}
	
	public int isChild(T data) {
		for (int i = 0; i < this.children.size(); i++) {
			if(this.isEqual(data, this.children.elementAt(i).data) == 0)	return i;
		}
		return -1;
	}
	
	private int isEqual(T a, T b) {
		Integer data1 = (Integer)a;
		Integer data2 = (Integer)b;
		return data1.compareTo(data2);
	}
	// TODO: data
	public T getData() {
		return this.data;
	}
	
	public void setData(T data) {
		this.data = data;
	}
	
	public void addNum(int num) {
		this.num += num;
	}
	
	public int getNum() {
		return this.num;
	}
	// TODO: parent
	private void setParent(Node<T> parent) {
		this.parent = parent;
	}
	
	public Node<T> getParent() {
		return this.parent;
	}
	
	public Node<T> getNextLink() {
		return this.nextLink;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
