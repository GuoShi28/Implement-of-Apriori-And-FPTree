// Created by Guo Shi at 171224
// Implement apriori algorithm
import java.io.IOException;
import java.util.Vector;
import java.util.Date;

public class Apriori {
	private Vector<Vector<Integer>> DataSet;
	private int TransNum;
	private int ItemNum;
	private Vector<Vector<Integer>> FreItemSets;
	private int MinSup;
	
	public Apriori(Vector<Vector<Integer>> DataSetInput, int ItemNum, double minsub) {
		this.DataSet = DataSetInput;
		this.TransNum = this.DataSet.size();  // the number of transactions
		this.ItemNum = ItemNum;	// the number of items
		this.MinSup = (int)((double)this.TransNum * minsub);
		RemoveIndexFromDataSet();
		this.FreItemSets = new Vector<Vector<Integer>>();
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
	
	
	// TODO: Find frequent itensets from dataset
	public boolean FindFrequentItemset() {
		int item_length = 1;
		// find frequent itemsets with length 1
		if(!FindFrequentItemsWithOneItem()) {
			System.out.print("there's no item is frequent item! Please set smaller minsub");
			System.out.println();
			return false;
		}
		Vector<Vector<Integer>> buf = (Vector<Vector<Integer>>)this.FreItemSets.clone(); // used to store (k-1) lenth frequent itemset
		Vector<Vector<Integer>> lastFrequent = new Vector<Vector<Integer>>(); // and is itemsets with length 1 now.
		for(int i = 1; i < (this.ItemNum-1); i++) {
			if(FindNextLengthFrequent(buf, i, lastFrequent) == false) {
				return true;
			}
			buf = (Vector<Vector<Integer>>)lastFrequent.clone();
		}
		return true;		
	}
	
	private boolean FindNextLengthFrequent(Vector<Vector<Integer>> lastCandiate, int k, Vector<Vector<Integer>> candidate_lengthK) {
		// create k+1 length candidate
		if(lastCandiate.size() == 1) {
			System.out.print("there's only one frequent item with length"+k+", no more frequent items");
			System.out.println();
			return false;
		}
		//System.out.println();
		//System.out.print(lastCandiate.size()+" frequent items with length"+k+":");
		//System.out.println();
		//System.out.print(" creating candidate with leanght "+(k+1)+" -->num:");
		//test time
		long startTime=System.currentTimeMillis();   //start  

		candidate_lengthK.clear();
		for(int i = 0; i < lastCandiate.size(); i++) {
			Vector<Integer> buf = lastCandiate.elementAt(i);
			for(int j = (i+1); j < lastCandiate.size(); j++) {
				Vector<Integer> buf2 = lastCandiate.elementAt(j);
				if(isExtendItem(buf,buf2)) {
					candidate_lengthK.addElement(ExtendItem(buf,buf2));
				}
			}
		}
		long endTime=System.currentTimeMillis(); //end
		//System.out.print(candidate_lengthK.size()+"-->costing time: "+(endTime-startTime)+"ms");
		//System.out.println();
		if(candidate_lengthK.isEmpty()) {
			return false;
		}
		startTime=System.currentTimeMillis();
		PruneNonFrequent(candidate_lengthK);
		endTime=System.currentTimeMillis(); 
		//System.out.print("prune candidate, get "+candidate_lengthK.size()+" frequent sets-->costing time: "+(endTime-startTime)+"ms");
		//System.out.println();
		
		addFrequentItem(candidate_lengthK);
		return true;
	}
	
	
	// TODO: if this two items satisfy the rule of Aprior to merge together.
	//       (k-1) items of these two elements are equal
	private boolean isExtendItem(Vector<Integer> item1, Vector<Integer> item2) {
		for(int i = 0; i < (item1.size()-1); i++) {
			if(item1.elementAt(i) != item2.elementAt(i)) {
				return false;
			}
		}
		return true;
	}
	// TODO: merge two (k-1) itemset to (k) itemset
	private Vector<Integer> ExtendItem(Vector<Integer> item1, Vector<Integer> item2) {
		Vector<Integer> buf = (Vector<Integer>)item1.clone();
		buf.addElement(item2.lastElement());
		return buf;
	}
	// TODO: prune non frequent candidates
	private boolean PruneNonFrequent(Vector<Vector<Integer>> item) {
		Integer[] count = new Integer[item.size()];
		for(int i = 0; i < item.size(); i++) {
			count[i] = 0;
		}
		for(int i = 0; i < this.TransNum; i++) {
			Vector<Integer> buf = this.DataSet.elementAt(i);
			for(int j = 0; j < item.size(); j++) {
				Vector<Integer> item_buf = item.elementAt(j);
				if(isContain(buf,item_buf))	count[j] += 1;
			}
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
		PruneNonFrequent(buf);
		addFrequentItem(buf);
		return true;
	}
	
	private void addFrequentItem(Vector<Vector<Integer>> items) {
		for(int i = 0; i < items.size(); i++) {
			this.FreItemSets.addElement(items.get(i));
		}
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

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String path = "/Users/mac/Desktop/Paper/course/DataMining/IBMGenerator-master/T1000L10Item20P20L4.data";
		//String path = "/Users/mac/Desktop/Paper/course/DataMining/IBMGenerator-master/T1000L100I20.data";
		DataProcess FileData = new DataProcess(path);
		//FileData.PrintDataSet();
		int ItemNum = 20;
		double minsub = 0.30; // minsub = percentage
		//double minsub = 0.003; // minsub = percentage
		Apriori test = new Apriori(FileData.GetDataSet(),ItemNum,minsub);
		test.PrintDataSet();
		
		Runtime run = Runtime.getRuntime();
		//System.in.read();   // 暂停程序执行
		// System.out.println("memory> total:" + run.totalMemory() + " free:" + run.freeMemory() + " used:" + (run.totalMemory()-run.freeMemory()) );
		run.gc();
		System.out.println("time: " + (new Date()));
		// 获取开始时内存使用量
		long startMem = run.totalMemory()-run.freeMemory();
		System.out.println("memory> total:" + run.totalMemory() + " free:" + run.freeMemory() + " used:" + startMem );

		test.FindFrequentItemset();
	
		System.out.println("time: " + (new Date()));
		long endMem = run.totalMemory()-run.freeMemory();
		System.out.println("memory> total:" + run.totalMemory() + " free:" + run.freeMemory() + " used:" + endMem );
		System.out.println("memory difference:" + (endMem-startMem));
		test.PrintState();
		//test.PrintFrequent();
	}

}
