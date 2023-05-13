package pathsearch;

import java.util.Deque;
import java.util.LinkedList;

/**
 *
 * @author Kuroha
 */
public class DijkstraSP {
    //索引代表顶点，值表示从顶点s到当前顶点的最短路径上的最后一条边
    private DirectedEdge[] edgeTo;
    //索引代表顶点，值表示从顶点s到当前顶点的最短路径的总权重
    private double[] distTo;
    //存放树中顶点与非树中顶点之间的有效横切边
    private IndexMinPriorityQueue<Double> pq;

    //根据一副加权有向图G和起点s，创建一个计算起点为s的最短路径树对象
    public DijkstraSP(EdgeWeightedDigraph G, int s){
        //初始化edgeTo
        this.edgeTo = new DirectedEdge[G.V()];
        //创建一个和图的顶点数一样大小的double数组，表示权重，并且初始化数组中的内容为无穷大，无穷大即表示不存在这样的边
        this.distTo = new double[G.V()];
        for (int i = 0; i < distTo.length; i++) {
            distTo[i] = Double.POSITIVE_INFINITY;
        }
        //创建一个和图的顶点数一样大小的索引最小优先队列，存储有效横切边
        this.pq = new IndexMinPriorityQueue<>(G.V());
        //默认让顶点s进入树中，但s顶点目前没有与树中其他的顶点相连接，因此初始化distTo[s]=0.0
        distTo[s] = 0.0;
        //使用顶点s和权重0.0初始化pq
        pq.insert(s,0.0);

        //遍历pq
        while(!pq.isEmpty()){
            //松弛图G中的顶点
            relax(G, pq.delMin());
        }
    }



    //松弛图G中的顶点v
    private void relax(EdgeWeightedDigraph G, int v){
        //松弛顶点v就是松弛顶点v邻接表中的每一条边，遍历邻接表
        for (DirectedEdge edge : G.adj(v)) {
            //获取到该边的终点w
            int w = edge.to();
            //通过松弛技术，判断从起点s到顶点w的最短路径是否需要先从顶点s到顶点v再由顶点v到顶点w
            if (distTo(v) + edge.weight() < distTo(w)){
                distTo[w] = distTo[v] + edge.weight();
                edgeTo[w] = edge;

                //如果顶点w已经存在于索引最小优先队列pq中，则重置顶点w的权重
                if (pq.contains(w)){
                    pq.changeItem(w, distTo(w));
                }else{
                    //如果顶点w没有出现在优先队列pq中，则把顶点w及其权重加入到pq中
                    pq.insert(w, distTo(w));
                }
            }
        }
    }

    //获取从顶点s到顶点v的最短路径的总权重
    public double distTo(int v){
        return distTo[v];
    }

    //判断从顶点s到顶点v是否可达
    public boolean hasPathTo(int v){
        return distTo[v]<Double.POSITIVE_INFINITY;
    }

    //查询从起点s到顶点v的最短路径中所有的边
    public Deque<DirectedEdge> pathTo(int v){
        //如果顶点s到v不可达，则返回null
        if (!hasPathTo(v)){
            return null;
        }
        //创建队列保存最短路径的边
        Deque<DirectedEdge> allEdges = new LinkedList<>();
        //从顶点v开始，逆向寻找，一直找到顶点s为止，而起点s为最短路径树的根结点，所以edgeTo[s]=null
        while (true){
            DirectedEdge e = edgeTo[v];
            if (e==null){
                break;
            }
            allEdges.offer(e);
            v = e.from();
        }
        return allEdges;
    }
}
