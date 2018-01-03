import java.io.IOException;
import java.util.Vector;
import java.util.Date;
import java.util.Map;
// Created by Guo Shi, at 171226
// TODO: Using FPTree to find frequent itemsets in dataset
public class FPTree {
	// common parameters for data mining
	private Vector<Vector<Integer>> DataSet;
	private int TransNum;
	private int ItemNum;
	private Vector<Vector<Integer>> FreItemSets;
	private int MinSup;
	// parameters for fptree
	private Node<Integer> root;
	private Node<Integer> current;
	private Vector<Vector<Integer>> FreItemSets_1;
	private Vector<Node<Integer>> HeaderTable;
	private Node<Integer> CurrentHeader;
	
	public FPTree(Vector<Vector<Integer>> DataSetInput, int ItemNum, double minsub) {
		this.DataSet = DataSetInput;
		this.TransNum = this.DataSet.size();  // the number of transactions
		this.ItemNum = ItemNum;	// the number of items
		this.MinSup = (int)((double)this.TransNum * minsub);
		//this.MinSup = 1;
		this.RemoveIndexFromDataSet();
		this.FreItemSets = new Vector<Vector<Integer>>();
		this.FreItemSets_1 = new Vector<Vector<Integer>>();
		this.BuildFPTree();
		this.PrintDataSet();
		this.printElementsFromRoot();
		Vector<Integer> buf = new Vector<Integer>();
		this.FreItemSets.clear();
		
		Runtime run = Runtime.getRuntime();
		//System.in.read();   // 暂停程序执行
		// System.out.println("memory> total:" + run.totalMemory() + " free:" + run.freeMemory() + " used:" + (run.totalMemory()-run.freeMemory()) );
		run.gc();
		System.out.println("time: " + (new Date()));
		// 获取开始时内存使用量
		long startMem = run.totalMemory()-run.freeMemory();
		System.out.println("memory> total:" + run.totalMemory() + " free:" + run.freeMemory() + " used:" + startMem );
		long startTime=System.currentTimeMillis();
		
		this.FindAllFrequentItemsetOfFPTree(this, this, this.HeaderTable.size() - 1, buf);
		
		long endTime=System.currentTimeMillis();
		System.out.println("time: " + (new Date()));
		long endMem = run.totalMemory()-run.freeMemory();
		System.out.println("memory> total:" + run.totalMemory() + " free:" + run.freeMemory() + " used:" + endMem );
		System.out.println("memory difference:" + (endMem-startMem));
		System.out.println("running time： "+(endTime-startTime)+"ms");  
		
	}
	
	public FPTree(FPTree temp) {
		this.DataSet = temp.DataSet;
		this.TransNum = temp.TransNum;
		this.ItemNum = temp.ItemNum;
		this.MinSup = temp.MinSup;
		this.FreItemSets = temp.FreItemSets;
		this.FreItemSets_1 = temp.FreItemSets_1;
		// copy FP tree
		this.root = new Node<>(0);
		this.copyFPTree(temp);
		
	}
	
	private void copyFPTree(FPTree temp) {
		temp.current = temp.root;
		this.current = this.root;
		this.InitHeaderTable();
		copyLeafChild(temp.current, this.current);
	}
	
	private boolean copyLeafChild(Node<Integer> temp, Node<Integer> new_temp) {
		Vector<Node<Integer>> buf = temp.getChild();
		if(buf.size() == 0) {
			return false;
		}
		for(int i = 0; i < buf.size(); i++) {
			Node<Integer> node_buf = buf.elementAt(i); 
			Node<Integer> node_buf_2 = new Node<Integer>(node_buf);
			new_temp = new_temp.addChild(node_buf_2);
			this.linkHeaderTable(new_temp);
			this.copyLeafChild(node_buf,new_temp);
			new_temp = new_temp.getParent();
		}
		return true;
	}
	
	// TODO: init Header Table
	private void InitHeaderTable() {
		this.HeaderTable = new Vector<Node<Integer>>();
		for(int i = 0; i < this.FreItemSets_1.size(); i++) {
			 Vector<Integer> buf = this.FreItemSets_1.elementAt(i);
			 Node<Integer> node_buf = new Node<Integer>(buf.elementAt(0));
			 this.HeaderTable.addElement(node_buf);
		}
	}
 	
	// TODO: find all frequent itemset by FPTree algorithm
	private void FindAllFrequentItemsetOfFPTree(FPTree com_tree, FPTree temp,int start_point,Vector<Integer> prefix) {
		int i = start_point;
		Vector<Integer> pre_buf = new Vector<Integer>();
		for(int in = 0; in < prefix.size(); in++) {
			pre_buf.add(prefix.elementAt(in));
		}
		while(i>=0) {
			if(temp.HeaderTable.elementAt(i).nextLink == null) {
				i -= 1;
				continue;
			}
			pre_buf.clear();
			for(int in = 0; in < prefix.size(); in++) {
				pre_buf.add(prefix.elementAt(in));
			}
			
			FPTree fp_temp = new FPTree(temp);
			//System.out.println();
			//temp.printElementsFromRoot();
			//fp_temp.printHeaderTabel();
			//System.out.print("prune i = "+i+" ,"+this.HeaderTable.elementAt(i).data);
			fp_temp.BuildSubTree(i);
			//System.out.println();
			//temp.printElementsFromRoot();
			fp_temp.PruneSubtree();
			if(fp_temp.HeaderTable.elementAt(i).nextLink != null) {
				pre_buf.add(0, (Integer)fp_temp.HeaderTable.elementAt(i).data);
			}
			int j = i - 1;
			//System.out.println();
			//fp_temp.printElementsFromRoot();
			//fp_temp.printHeaderTabel();
			if(fp_temp.root.children.size() == 0)	break;
			if((fp_temp.root.children.size() == 1)&&(fp_temp.root.children.elementAt(0).children.size() == 0)) {
				if(pre_buf.size() != 0) {
					com_tree.addOneFrequentItem(pre_buf);
				}
				break;
			} else {
				//System.out.println();
				//fp_temp.printElementsFromRoot();
				//fp_temp.printHeaderTabel();
				this.FindAllFrequentItemsetOfFPTree(com_tree, fp_temp, j, pre_buf);
			}
			i = i - 1;
		}
	
		
	}
	
	// TODO: build sub tree
	private boolean BuildSubTree(int start_point) {
		if(start_point == (this.HeaderTable.size() - 1)) {
			return true;
		}
		Node<Integer> buf2 = this.HeaderTable.elementAt(start_point);
		for(int i = this.HeaderTable.size() - 1; i >= 0; i--) {
			if(i == start_point)	continue;
			Node<Integer> buf = this.HeaderTable.elementAt(i);
			//this.printHeaderTabel();
			//System.out.println();
			//System.out.print("i now = "+buf.data);
			if(buf.nextLink == null)	continue;
			buf = buf.nextLink;
			int judge = 1;
			while(buf != null) {
				if(buf.children.size() == 0)	judge = 0;
				buf = buf.nextLink;
			}
			if(judge == 1)	continue;
			buf = this.HeaderTable.elementAt(i);
			this.current = this.root;
			
			//System.out.println();
			//this.printElementsFromRoot();
			iterSubTree(this.current, (int)buf.data, (int)buf2.data);
			//System.out.println();
			//this.printElementsFromRoot();
		}
		return true;
	}
	//
	private boolean iterSubTree(Node<Integer> temp, int item, int item2) {
		if((temp.children.size() == 0) && ((int)temp.data == item)) {
			Node<Integer> buf = temp.getParent();
			Node<Integer> buf2 = temp;
			temp.lastLink.nextLink = temp.nextLink;
			for(int i = 0; i < buf.children.size(); i++) {
				Node<Integer> node_buf = buf.children.elementAt(i);
				if((int)buf2.data == (int)node_buf.data) {
					buf.children.remove((int)i);
				}
			}
			//////
			//buf.num -= buf2.num;
			if(buf == null)	return true;
			while((buf.children.size() == 0) && ((int)buf.data != item2)) {
				buf2 = buf;
				buf = buf.parent;
				if(buf == null) break;
				if(buf2.lastLink != null) {
					buf2.lastLink.nextLink = buf2.nextLink;
				}
				for(int i = 0; i < buf.children.size(); i++) {
					if((int)buf.children.elementAt(i).data == (int)buf2.data) {
						buf.children.remove(i);
					}
				}
				buf.num -= buf2.num;
			}
		}
		else {
			for(int i = 0; i < temp.children.size(); i++) {
				iterSubTree(temp.children.elementAt(i), item, item2);
			}
		}
		return true;
	}
	
	private void PruneNonfrequence(int start_point) {
		Node<Integer> buf = this.HeaderTable.elementAt(start_point);
		while(buf.getNextLink() != null) {
			Node<Integer> buf2 = buf.getNextLink();
			buf = this.deleteNode(buf2);
			//buf = buf2;
		}
	}
	
	// TODO: prune non-frequent item from subtree
	private void PruneSubtree(){
		Vector<Node<Integer>> buf = this.HeaderTable;
		for(int i = 0; i < buf.size(); i++) {
			Node<Integer> node_buf = buf.elementAt(i);
			int num_total = 0;
			node_buf = node_buf.nextLink;
			while(node_buf != null) {
				num_total += node_buf.num;
				node_buf = node_buf.nextLink;
			}
			if(num_total < this.MinSup) {
				this.PruneNonfrequence(i);
			}
		}
	}
	
	public Node<Integer> deleteNode(Node<Integer> temp) {
		for (int i = 0; i < temp.parent.children.size(); i++) {
			Integer buf = temp.parent.children.elementAt(i).data;
			if((Integer)buf == (Integer)temp.data) {
				temp.parent.children.remove(i);
				temp.lastLink.nextLink = temp.nextLink;
				//if(temp.children.size() != 0) {
				//	temp.parent.num -= temp.num;
				//}
				break;
			}
		}
		//for(int i = 0; i < temp.children.size(); i++) {
		//	Node<Integer> buf = temp.children.elementAt(i);
		//	this.mergeChildNode(temp.parent,buf);
		//}
		return temp.parent;
	}
	
	private boolean mergeChildNode(Node<Integer> parent_temp, Node<Integer> temp) {
		if(parent_temp.children.size() == 0) {
			parent_temp.addChild(temp);
			this.linkHeaderTable(temp);
			return true;
		}
		for(int i = 0; i < parent_temp.children.size(); i++) {
			Node<Integer> buf = parent_temp.children.elementAt(i);
			if((int)buf.data == (int)temp.data) {
				buf.addNum(temp.num);
				if(temp.children.size() == 0)	return true;
				for(int j = 0; j < temp.children.size(); j++) {
					mergeChildNode(buf, temp.children.elementAt(j));
				}
				return true;
			}
			// if temp not belongs to children of parent_temp
			parent_temp.addChild(temp);
			this.linkHeaderTable(temp);
			//parent_temp.num += temp.num;
		}
		return true;
	}
	
	// TODO: dataset created by IBM Generator has the item index in the first two cols
	// this function is to remove these two cols.
	private void RemoveIndexFromDataSet() {
		for(int i = 0; i < this.TransNum; i++) {
			Vector<Integer> buf = this.DataSet.elementAt(i);
			buf.remove(0);
			buf.remove(0);
		}
	}
	
	// TODO: bulid FPTree
	private void BuildFPTree() {
		// obtain sorted frequent item with length 1
		this.FindFrequentItemsWithOneItem();
		this.reArrangeDataset();
		this.InitHeaderTable();
		this.root = new Node<>(0);
		//////
		for(int i = 0; i < this.TransNum; i++) {
		//for(int i = 0; i < 2; i++) {
			Vector<Integer> buf = this.DataSet.elementAt(i);
			this.addTransToTree(buf);
		}
	}
	
	private void addTransToTree(Vector<Integer> trans) {
		this.current = this.root;
		for(int i = 0; i < trans.size(); i++) {
			int temp = this.current.isChild(trans.elementAt(i));
			if( temp < 0) {
				Node buf = new Node(trans.elementAt(i));
				this.current = this.current.addChild(buf);
				this.linkHeaderTable(this.current);
			} else {
				this.current = this.current.getChild().elementAt(temp);
				this.current.addNum(1);
			}
		}
	}
	// TODO: link to header tabel
	private boolean linkHeaderTable(Node<Integer> temp) {
		for(int i = 0; i < this.HeaderTable.size(); i++) {
			this.CurrentHeader = this.HeaderTable.elementAt(i);
			Node<Integer> buf = this.CurrentHeader.getNextLink();
			while(buf != null) {
				this.CurrentHeader = this.CurrentHeader.getNextLink();
				buf = this.CurrentHeader.getNextLink();
			}
			if((int)temp.getData() == (int)this.CurrentHeader.getData()) {
				this.CurrentHeader = this.CurrentHeader.linkNode(temp);
				return true;
			}
		}
		return false;
	}
	
	// TODO: print tree elements from a node
	public void printElementsFromRoot() {
		this.current = this.root;
		printChildElements(this.current);
	}
	
	public boolean printChildElements(Node<Integer> temp) {
		Vector<Node<Integer>> buf = temp.getChild();
		if(buf.size() == 0) {
			return false;
		}
		for(int i = 0; i < buf.size(); i++) {
			Node<Integer> node_buf = buf.elementAt(i); 
			System.out.print("Layers = "+node_buf.layers+"  data = "+node_buf.getData()+" num = "+node_buf.getNum());
			System.out.println();
			this.printChildElements(node_buf);
		}
		return true;
	}
	
	public void printHeaderTabel() {
		for(int i = 0; i < this.HeaderTable.size(); i++) {
			Node current = this.HeaderTable.elementAt(i);
			System.out.print("data = "+current.getData()+":");
			while(current.getNextLink() != null) {
				current = current.getNextLink();
				System.out.print("Layers = "+current.layers+"-->");
			}
			System.out.println();
		}
	}
	
	private boolean reArrangeDataset() {
		for(int i = 0; i < this.TransNum; i++) {
			Vector<Integer> buf = this.DataSet.elementAt(i);
			this.DataSet.set(i, this.reArrangeItem(buf));
		}
		return true;
	}
	
	private Vector<Integer> reArrangeItem(Vector<Integer> trans) {
		Vector<Integer> buf = new Vector<>();
		for(int i = 0; i < this.FreItemSets_1.size(); i++) {
			Vector<Integer> item_buf = this.FreItemSets_1.elementAt(i);
			if(isContain(trans, item_buf))	buf.addElement(item_buf.elementAt(0));	
		}
		return buf;
	}
	
	// TODO: find frequent items with length 1
	private boolean FindFrequentItemsWithOneItem() {
		// create init one item
		Vector<Vector<Integer>> buf = new Vector<Vector<Integer>>();
		for(int i = 0; i < this.ItemNum; i++) {
			Vector<Integer> buf2 = new Vector<Integer>();
			buf2.addElement(i);
			buf.addElement(buf2);
		}
		if(buf.isEmpty()) {
			return false;
		}
		this.PruneNonFrequent(buf);
		this.addFrequentItem(buf);
		this.FreItemSets_1 = (Vector<Vector<Integer>>)buf.clone();
		return true;
	}
	
	public void addFrequentItem(Vector<Vector<Integer>> items) {
		for(int i = 0; i < items.size(); i++) {
			this.FreItemSets.addElement(items.get(i));
		}
	}
	
	public void addOneFrequentItem(Vector<Integer> items) {
		int judge = 0;
		for(int i = 0; i < this.FreItemSets.size(); i++) {
			if(isTwoItemsEqual(this.FreItemSets.elementAt(i),items)) {
				judge = 1;
				break;
			}
		}
		if(judge == 0) {
			this.FreItemSets.addElement(items);
			//System.out.print("frequent items: ");
			//for(int j = 0; j < items.size(); j++) {
			//	System.out.print(items.elementAt(j)+" ");
			//}
			//System.out.println();
		}
	}
	
	private boolean isTwoItemsEqual(Vector<Integer> a,Vector<Integer> b) {
		if(a.size() != b.size())	return false;
		for(int i = 0; i < a.size(); i++) {
			if((int)a.elementAt(i) != (int)b.elementAt(i))	return false;
		}
		return true;
	}
	
	// TODO: prune non frequent candidates
	private boolean PruneNonFrequent(Vector<Vector<Integer>> item) {
		Integer[] count = new Integer[item.size()];	
		int item_size = 0;
		for(int i = 0; i < item.size(); i++) {
			count[i] = 0;
		}
		for(int i = 0; i < this.TransNum; i++) {
			Vector<Integer> buf = this.DataSet.elementAt(i);
			for(int j = 0; j < item.size(); j++) {
				Vector<Integer> item_buf = item.elementAt(j);
				item_size = item_buf.size();
				if(this.isContain(buf,item_buf))	count[j] += 1;
			}
		}
		
		// if itemset with length equal to 1, sort the itemset with decrease;
		if(item_size == 1) {
			this.SortItem(item,count);
		}
		
		Vector<Integer> count_buf = new Vector<>();
		for(int j = 0; j < count.length; j++) {
			if((int)count[j] < this.MinSup)	{
				count_buf.addElement((Integer)j);
			}
		}
		for(int i = 0; i < count_buf.size(); i++) {
			item.remove((int)(count_buf.elementAt(i)-i));
		}		
		
		if(item.isEmpty()) return false;
		return true;
	}
	
	private void SortItem(Vector<Vector<Integer>> item, Integer[] count) {
		for(int i = 0; i < (item.size()-1); i++) {
			int index = FindMostFItemStart(count, i);
			this.swapVector(item, i, index);
			this.swapInteger(count, i, index);
		}
	}
	
	private void swapVector(Vector<Vector<Integer>> vector, int p1, int p2) {
		Vector<Integer> buf = vector.elementAt(p1);
		vector.setElementAt(vector.elementAt(p2), p1);
		vector.setElementAt(buf, p2);
	}
	
	private void swapInteger(Integer[] integ, int p1, int p2) {
		Integer buf = integ[p1];
		integ[p1] = integ[p2];
		integ[p2] = buf;
	}
	
	private int FindMostFItemStart(Integer[] count, int s_index) {
		int max = 0;
		int mf_index = s_index;
		for(int i = s_index; i < count.length; i++) {
			if((int)count[i] > max) {
				max = count[i];
				mf_index = i;
			}
 		}
		return mf_index;
	}
	
	// TODO: judge if a trans contains the candidate
	private boolean isContain(Vector<Integer> trans, Vector<Integer> itemset) {
		int count = 0;
		for(int j = 0; j < trans.size(); j++) {
			Integer trans_buf = trans.elementAt(j);
			for(int i = 0; i < itemset.size(); i++) {
				Integer itemset_buf = itemset.elementAt(i);
				if(trans_buf.equals(itemset_buf)) {
					count += 1;
					if(count == itemset.size()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	// TODO: print dataset
	public void PrintDataSet() {
		for (int i = 0; i < this.DataSet.size(); i++) {
			Vector<Integer> buf = this.DataSet.elementAt(i);
			for (int j = 0; j < buf.size(); j++) {
				System.out.print(buf.elementAt(j)+" ");
			}
			System.out.println();
		}
	}
	// TODO: print parameters
	public void PrintState() {
		System.out.print("MinSubNum = "+this.MinSup);
		System.out.println();
		System.out.print("TransNum = "+this.TransNum);
		System.out.println();	
		System.out.println("FrequentNum = "+this.FreItemSets.size());
		System.out.println();
	}
	// TODO: print frequent itemsets
	public boolean PrintFrequent() {
		if(this.FreItemSets.isEmpty()) {
			System.out.print("No frequent items");
			System.out.println();
			return false;
		}
		for(int i = 0; i < this.FreItemSets.size(); i++) {
			System.out.print("frequent items: ");
			Vector<Integer> buf = this.FreItemSets.elementAt(i);
			for(int j = 0; j < buf.size(); j++) {
				System.out.print(buf.elementAt(j)+" ");
			}
			System.out.println();
		}
		System.out.print("Frequent num:"+this.FreItemSets.size());
		return true;
	}

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
			String path = "/Users/mac/Desktop/Paper/course/DataMining/IBMGenerator-master/T1000L10Item20P20L4.data";
			//String path = "/Users/mac/Desktop/Paper/course/DataMining/IBMGenerator-master/T1000L20I20.data";
			DataProcess FileData = new DataProcess(path);
			//FileData.PrintDataSet();
			int ItemNum = 20;
			double minsub = 0.20; // minsub = percentage
			FPTree test = new FPTree(FileData.GetDataSet(),ItemNum,minsub);
			//test.PrintDataSet();
			test.PrintState();
			//test.printElementsFromRoot();
			//test.printHeaderTabel();
			//test.PrintFrequent();
			//FPTree copyTree = new FPTree(test);
			//copyTree.printElementsFromRoot();
			//copyTree.printHeaderTabel();
			
	}

}
