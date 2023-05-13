package pathsearch;

public class IndexMinPriorityQueue<T extends Comparable<T>> {

    //存储元素的数组
    private T[] items;

    //存储数组items中的索引作为元素，此数组需要实现最小堆有序
    private int pq[];

    //此数组为pq数组的逆序 pq数组的索引作为该数组的值，pq数组的值作为该数组的索引
    private int[] qp;

    //记录数组 堆中存储的元素个数
    private int N;


    //构造方法
    public IndexMinPriorityQueue(int capacity){
        this.items= (T[]) new Comparable[capacity];
        this.pq=new int[capacity+1];
        this.qp=new int[capacity];
        this.N=0;

        //初始化队列时，队列中没有存储任何元素 让qp中的所有元素全部都为-1
        for(int i=0;i<qp.length;i++){
            qp[i]=-1;
        }
    }

    /**
     * 获取队列中存储元素的个数
     * @return
     */
    public int size(){return N;}

    /**
     * 判断队列是否为空
     * @return
     */
    public boolean isEmpty(){return N==0;}

    /**
     * 判断堆数组pq中索引i处的值是否小于索引j处的值
     * @param i
     * @param j
     * @return
     */
    private boolean less(int i,int j){
        return items[pq[i]].compareTo(items[pq[j]])<0;
    }

    /**
     *交换pq堆中 索引i处与索引j处的值
     * @param i
     * @param j
     */
    private void exchange(int i,int j){
        //交换pq中的数据
        int temp=pq[i];
        pq[i]=pq[j];
        pq[j]=temp;
        //更新改逆序数组qp中的对应的数据
        qp[pq[i]]=i;
        qp[pq[j]]=j;
    }

    /**
     * 索引k处是否存在元素
     * @param k
     * @return true 存在 false不存在
     */
    public boolean contains(int k){
        //真正存储元素的数组items中的索引与逆序数组qp中是对应的，qp[k]!=-1代表 items中该索引处有元素存在
        //初始情况下没有存储元素是qp中的元素全部为-1,当有存储元素,元素就为pq数组中的索引
        return qp[k]!=-1;
    }

    /**
     * 返回最小元素关联的索引
     * @return
     */
    public int minIndex(){
        //最小索引优先队列基于最小堆实现，最小元素即数组items[pq[1]]对应的元素，索引为pq[1]的值
        return pq[1];
    }


    /**
     * 往队列中指定位置处插入元素
     * @param i
     * @param t
     */
    public void insert(int i,T t){
        //先判断该位置在qp数组是否已经存在关联元素
        if(contains(i)){
            //已经存在元素,不做操作
            return;
        }
        //不存在关联 元素个数+1
        N++;
        //将数据存储到items数组的i索引处
        items[i]=t;
        //将新数据的索引i存入pq堆中
        pq[N]=i;
        //qp数组记录pq中新增的元素i
        qp[i]=N;
        //对pq堆中新增的元素进行上浮，使其位于正确的位置
        swim(N);
        //
    }

    /**
     * 上浮算法，使索引k的元素调整到正确位置，保持最小堆有序
     * @param k
     */
    public void swim(int k){
        while (k/2>=1){
            //当前结点存在父结点，与父结点进行比较大小
            if (!less(k,k/2)){
                //当前结点的值大于父结点的值，则此位置就为正确位置结束循环
                break;
            }
            //当前结点小于父结点的值，则交换元素 继续上浮寻找正确的位置
            exchange(k,k/2);
            k=k/2;
        }
        //循环结束条件
        //1.没有父结点了 已经上浮到根结点为最小元素
        //2.结束循环前找到了正确位置
    }

    /**
     * 删除队列中最小的元素，并返回该元素关联的索引
     * @return
     */
    public int delMin(){
        //获取最小元素关联的索引
        int minIndex=pq[1];
        //与pq堆中的最后一个元素交换
        exchange(1,N);
        //删除qp中对应的内容
        qp[pq[N]]=-1;
        //删除items的内容
        items[pq[N]]=null;
        //删除pq处最大索引处的内容
        pq[N]=-1;
        //元素个数-1
        N--;
        //对根结点的元素进行下沉操作寻找其正确的位置
        sink(1);
        return minIndex;
    }
    /**
     * 下沉算法，对索引k处的元素进行下沉 调整正确位置 保持最小堆pq有序
     * @param k
     */
    private void sink(int k){
        while (2*k<=N){
            //当前结点有左子结点
            int min;//记录左右子结点中较小值的索引
            if(2*k+1<=N){
                //当前节点有右子结点
                if (less(2*k,2*k+1)){
                    //左子结点的值小于右子结点的值
                    min=2*k;
                }else {
                    //左子结点的值大于右子结点的值
                    min=2*k+1;
                }
            }else {
                //当前结点没有右子结点
                min=2*k;
            }
            //比较当前节点与min的大小
            if(less(k,min)){
                //当前结点小于min，此位置为合适位置停止循环
                break;
            }
            //当前结点的值大于min，交换元素 继续下沉寻找正确位置
            exchange(k,min);
            k=min;
        }
    }

    /**
     * 删除数组items中索引i关联的元素
     * @param i
     */
    public void delete(int i){
        //找到i在pq堆中的索引
        int k=qp[i];
        //将k索引与pq堆中左后一个元素进行交换
        exchange(k,N);
        //将qp中对应的索引删除
        qp[pq[N]]=-1;
        //删除pq中的关联的索引i
        pq[N]=-1;
        //items中元素删除
        items[i]=null;
        //元素个数-1
        N--;
        //pq堆进行调整保持堆有序
        swim(k);
        //两种情况
        //1.索引k的元素比父结点的值小上浮到了正确位置，对交换下来原先父结点元素进行下沉调整(该元素肯定会比k索引的左右子结点小，不会下沉)
        //2.索引k的元素比父结点大无法上浮，对其进行下沉调整位置
        sink(k);

    }

    /**
     * 将数组items中索引i处的元素修改为t
     * @param i
     * @param t
     */
    public void changeItem(int i,T t){
        items[i]=t;
        //获取i在pq堆中的随意
        int k=qp[i];
        //对该索引的元素进行调整
        swim(k);
        sink(k);
    }

}
